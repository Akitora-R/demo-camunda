package me.aki.demo.camunda.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.constant.BpmnConstant;
import me.aki.demo.camunda.entity.*;
import me.aki.demo.camunda.entity.dto.ProcDefDTO;
import me.aki.demo.camunda.entity.dto.ProcInstDTO;
import me.aki.demo.camunda.entity.dto.node.FlowNodeDTO;
import me.aki.demo.camunda.entity.dto.node.NodeDTO;
import me.aki.demo.camunda.entity.dto.node.NodeLink;
import me.aki.demo.camunda.entity.dto.node.impl.EdgeNodeDTO;
import me.aki.demo.camunda.entity.dto.node.impl.StartEventFlowNodeDTO;
import me.aki.demo.camunda.entity.dto.node.impl.TaskFlowNodeDTO;
import me.aki.demo.camunda.entity.vo.FormDefVO;
import me.aki.demo.camunda.entity.vo.ProcDefVO;
import me.aki.demo.camunda.entity.vo.ProcInstVO;
import me.aki.demo.camunda.enums.VariableSourceType;
import me.aki.demo.camunda.provider.UserDataProvider;
import me.aki.demo.camunda.util.BpmnUtil;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class BpmnService {

    private final ProcDefService procDefService;
    private final ProcDefNodeService procDefNodeService;
    private final ProcDefNodePropService procDefNodePropService;
    private final ProcDefNodeElementRelInfoService procDefNodeElementRelInfoService;
    private final FormDefService formDefService;
    private final RepositoryService repositoryService;
    private final ProcDefVariableService procDefVariableService;
    private final ApplicationContext applicationContext;
    private final SysUserService sysUserService;
    private final RuntimeService runtimeService;
    private final FormInstService formInstService;
    private final FormInstItemService formInstItemService;
    private final TaskService taskService;

    public BpmnService(ProcDefService procDefService,
                       ProcDefNodeService procDefNodeService,
                       ProcDefNodePropService procDefNodePropService,
                       ProcDefNodeElementRelInfoService procDefNodeElementRelInfoService,
                       FormDefService formDefService,
                       RepositoryService repositoryService,
                       ProcDefVariableService procDefVariableService,
                       ApplicationContext applicationContext,
                       SysUserService sysUserService,
                       RuntimeService runtimeService,
                       FormInstService formInstService,
                       FormInstItemService formInstItemService, TaskService taskService) {
        this.procDefService = procDefService;
        this.procDefNodeService = procDefNodeService;
        this.procDefNodePropService = procDefNodePropService;
        this.procDefNodeElementRelInfoService = procDefNodeElementRelInfoService;
        this.formDefService = formDefService;
        this.repositoryService = repositoryService;
        this.procDefVariableService = procDefVariableService;
        this.applicationContext = applicationContext;
        this.sysUserService = sysUserService;
        this.runtimeService = runtimeService;
        this.formInstService = formInstService;
        this.formInstItemService = formInstItemService;
        this.taskService = taskService;
    }

    public ProcDefVO getProcDefVOById(String id) {
        ProcDef procDef = procDefService.getById(id);
        if (procDef == null) {
            return null;
        }
        ProcDefVO vo = new ProcDefVO();
        vo.setProcDef(procDef);
        var processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(procDef.getCamundaProcDefKey()).singleResult();
        vo.setCamundaProcessDefinition(processDefinition);
        FormDefVO formDef = formDefService.getVOByProcDefId(id);
        vo.setFormDefVO(formDef);
        return vo;
    }

    public void createProcessDefinition(ProcDefDTO dto) {
        log.debug("start create process definition：{}", dto.getProcDefName());
        // parse方法可能会修改dto的内容
        var nodeList = dto.getNodeList();
        var p = parse(dto.getProcDefName(), nodeList);
        // 保存元素关联信息
        var idPair = p.getValue();
        idPair.forEach((nodeId, bpmnIdList) -> bpmnIdList.forEach(bpmnId -> procDefNodeElementRelInfoService.saveRel(nodeId, bpmnId)));
        var instance = p.getKey();
        var deploy = repositoryService.createDeployment().name(dto.getProcDefName()).addModelInstance("generatedDef.bpmn", instance).deployWithResult();
        Assert.isTrue(deploy.getDeployedProcessDefinitions().size() == 1, "部署流程出错");
        var pd = deploy.getDeployedProcessDefinitions().get(0);
        var procDef = procDefService.toEntity(dto);
        procDef.setCamundaProcDefId(pd.getId());
        procDef.setCamundaProcDefKey(pd.getKey());
        procDefService.save(procDef);
        final var procDefId = procDef.getId();
        formDefService.saveDTO(procDefId, dto.getFormDef());
        nodeList.forEach(nodeDTO -> {
            var pair = procDefNodeService.toEntity(nodeDTO);
            ProcDefNode procDefNode = pair.getKey();
            List<ProcDefNodeProp> props = pair.getValue();
            procDefNode.setProcDefId(procDefId);
            procDefNodeService.save(procDefNode);
            props.forEach(e -> e.setProcDefNodeId(procDefNode.getId()));
            procDefNodePropService.saveBatch(props);
            if (nodeDTO.getVariableList() != null) {
                nodeDTO.getVariableList().forEach(v -> {
                    var procDefVariable = procDefVariableService.toEntity(v);
                    procDefVariable.setProcDefId(procDefId);
                    procDefVariable.setProcDefNodeId(procDefNode.getId());
                    procDefVariableService.save(procDefVariable);
                });
            }
        });
    }

    public void createProcessInstance(ProcInstDTO dto) {
        ProcDef procDef = procDefService.getById(dto.getProcDefId());
        Assert.notNull(procDef, "procDefId有误");
        HashMap<String, Object> procVar = new HashMap<>();
        FormInst formInst = new FormInst();
        FormDef formDef = formDefService.lambdaQuery().eq(FormDef::getProcDefId, procDef.getId()).one();
        formInst.setProcDefId(procDef.getId());
        formInst.setFormDefId(formDef.getId());
        // collect form related variables
        var providedFormVar = dto.getForm().getItemList().stream().collect(Collectors.toMap(ProcInstDTO.FormInstDTO.FormInstItemDTO::getFormItemId, e -> e));
        var requiredVar = procDefVariableService.lambdaQuery().eq(ProcDefVariable::getProcDefId, procDef.getId()).list();
        var groupedRequiredVar = requiredVar.stream().collect(Collectors.groupingBy(ProcDefVariable::getSourceType));
        List<ProcDefVariable> requiredFormVar = groupedRequiredVar.getOrDefault(VariableSourceType.FORM, Collections.emptyList());
        List<FormInstItem> items = requiredFormVar.stream().map(e -> {
            String varId = e.getSourceIdentifier();
            var item = providedFormVar.get(varId);
            Assert.notNull(item, "变量缺失: required id:{} type:{}", varId, e.getSourceType());
            procVar.put(varId, item.getFormItemVal());
            return formInstItemService.toEntity(item);
        }).toList();
        // collect the user related variables
        List<ProcDefVariable> requiredBeanVar = groupedRequiredVar.getOrDefault(VariableSourceType.BEAN, Collections.emptyList());
        // FIXME: 2022/7/28 unify it into one single service
        Map<String, UserDataProvider> userBeans = applicationContext.getBeansOfType(UserDataProvider.class);
        requiredBeanVar.forEach(e -> {
            String varId = e.getSourceIdentifier();
            String varKey = e.getVariableKey();
            UserDataProvider userBean = userBeans.get(varId);
            Assert.notNull(userBean, "变量缺失: required key:{} type:{}", varKey, e.getSourceType());
            var varVal = userBean.getUser(sysUserService.getCurrentUser().getUserCode()).getId();
            log.debug("set variable: {} - {}", varKey, varVal);
            procVar.put(varKey, varVal);
        });
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(procDef.getCamundaProcDefKey(), dto.getCamundaProcInstBusinessKey(), procVar);
        formInst.setCamundaProcInstId(processInstance.getId());
        formInst.setCamundaProcInstBusinessKey(dto.getCamundaProcInstBusinessKey());
        formInstService.save(formInst);
        items.forEach(e -> {
            e.setFormInstId(formInst.getId());
            formInstItemService.save(e);
        });
    }

    public IPage<ProcInstVO> procInstPagedQuery() {
        // TODO: 2022/8/3
        return null;
    }

    public ProcInstVO procInstDetail() {
        // TODO: 2022/8/3
        taskService.createTaskQuery().taskDefinitionKey("").singleResult().getTaskDefinitionKey();
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Pair<BpmnModelInstance, Map<String, List<String>>> parse(String procName, List<NodeDTO> nodeList) {
        // 为保证根据每个task code生成的变量不重复，必须校验所有task节点中的code不重复。
        nodeList.stream()
                .filter(n -> n instanceof TaskFlowNodeDTO)
                .map(n -> (TaskFlowNodeDTO) n)
                .collect(Collectors.groupingBy(TaskFlowNodeDTO::getCode))
                .values()
                .forEach(nList -> Assert.isTrue(nList.size() < 2, "所有Task节点中的code不可重复！存在重复节点:{}", nList));
        // 分离出edge和各种node
        Map<Boolean, List<NodeDTO>> collect = nodeList.stream().collect(Collectors.groupingBy(e -> e instanceof EdgeNodeDTO));
        List<FlowNodeDTO> flowNodes = (List) collect.get(false);
        log.debug("num of none-edge node：{}", flowNodes.size());
        List<EdgeNodeDTO> edges = (List) collect.get(true);
        // 整理id的同时保持图连接的正确性
        nodeList.forEach(e -> e.tidyUp((oldId, newId) -> {
            edges.stream().filter(edge -> edge.getSource().equals(oldId)).forEach(edge -> edge.setSource(newId));
            edges.stream().filter(edge -> edge.getTarget().equals(oldId)).forEach(edge -> edge.setTarget(newId));
        }));
        // 获得开始节点
        Map<String, FlowNodeDTO> nodeMap = flowNodes.stream().collect(Collectors.toMap(FlowNodeDTO::getId, e -> e));
        List<FlowNodeDTO> startFlowNodeDTOS = flowNodes.stream().filter(e -> e instanceof StartEventFlowNodeDTO).toList();
        Map<String, List<EdgeNodeDTO>> edgeMap = edges.stream().collect(Collectors.groupingBy(EdgeNodeDTO::getSource));
        Assert.isTrue(startFlowNodeDTOS.size() == 1, "开始节点数量有误");
        FlowNodeDTO start = startFlowNodeDTOS.get(0);
        ProcessBuilder builder = Bpmn.createExecutableProcess().name(procName);
        AbstractFlowNodeBuilder<?, ?> s = builder.startEvent(start.getId()).name(start.getLabel());
        HashMap<String, List<String>> idPair = new HashMap<>();
        // FIXME: 2022/7/20 循环图尚未实现
        travelGraph(new NodeLink(null, null, start), edgeMap, nodeMap, nl -> {
            log.debug("handle node link pair：{}", nl);
            var ss = s;
            ss = ss.moveToNode(nl.getPrevOutgoingNodeId());
            String condition = nl.getEdge().getCondition();
            if (condition != null && !condition.isEmpty()) {
                ss.condition(null, condition);
            }
            FlowNodeDTO curr = nl.getCurr();
            List<String> bpmnId = curr.build(ss);
            idPair.put(curr.getId(), bpmnId);
        });
        BpmnModelInstance instance = s.done();
        var bpmnEdges = BpmnUtil.getElementByName(instance, BpmnConstant.SEQUENCE_FLOW_NAME);
        log.debug("开始处理bpmn sequenceFlow，数量: {}", bpmnEdges.size());
        edges.forEach(edge -> {
            String source = edge.getSource();
            String target = edge.getTarget();
            log.debug("处理edge: {} -> {}", source, target);
            List<String> ids = bpmnEdges.stream().filter(e -> e.getAttribute(BpmnConstant.SEQUENCE_FLOW_ATTR_SOURCE).equals(source) &&
                    e.getAttribute(BpmnConstant.SEQUENCE_FLOW_ATTR_TARGET).equals(target)).map(e -> e.getAttribute(BpmnConstant.ID_ATTR)).toList();
            Assert.isTrue(ids.size() == 1, () -> ids.isEmpty() ?
                    new IllegalArgumentException(String.format("edge匹配元素失败: %s -> %s", source, target)) :
                    new IllegalArgumentException(String.format("路径相同的edge存在多条: %s -> %s", source, target)));
            String bpmnId = ids.get(0);
            idPair.put(edge.getId(), Collections.singletonList(bpmnId));
        });
        return new Pair<>(instance, idPair);
    }

    private void travelGraph(
            NodeLink nl,
            Map<String, List<EdgeNodeDTO>> edgeMap,
            Map<String, FlowNodeDTO> nodeMap,
            Consumer<NodeLink> onNode
    ) {
        if (nl.getEdge() != null) {
            onNode.accept(nl);
        }
        for (NodeLink nodeLink : getNext(nl.getCurr(), edgeMap, nodeMap)) {
            travelGraph(nodeLink, edgeMap, nodeMap, onNode);
        }
    }

    List<NodeLink> getNext(FlowNodeDTO prev, Map<String, List<EdgeNodeDTO>> edgeMap, Map<String, FlowNodeDTO> nodeMap) {
        List<EdgeNodeDTO> edgeNodes = edgeMap.getOrDefault(prev.getId(), Collections.emptyList());
        return edgeNodes.stream().map(e -> new NodeLink(prev, e, nodeMap.get(e.getTarget()))).collect(Collectors.toList());
    }
}
