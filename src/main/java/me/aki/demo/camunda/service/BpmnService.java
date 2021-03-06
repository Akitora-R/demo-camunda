package me.aki.demo.camunda.service;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
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
import me.aki.demo.camunda.enums.VariableSourceType;
import me.aki.demo.camunda.provider.UserDataProvider;
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
                       FormDefService formDefService,
                       RepositoryService repositoryService,
                       ProcDefVariableService procDefVariableService,
                       ApplicationContext applicationContext,
                       SysUserService sysUserService,
                       RuntimeService runtimeService,
                       FormInstService formInstService,
                       FormInstItemService formInstItemService, TaskService taskService) {
        this.procDefService = procDefService;
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
        log.debug("start create process definition???{}", dto.getProcDefName());
        BpmnModelInstance instance = parse(dto.getProcDefName(), dto.getNodeList());
        var deploy = repositoryService.createDeployment().name(dto.getProcDefName()).addModelInstance("generatedDef.bpmn", instance).deployWithResult();
        Assert.isTrue(deploy.getDeployedProcessDefinitions().size() == 1, "??????????????????");
        var pd = deploy.getDeployedProcessDefinitions().get(0);
        ProcDef procDef = procDefService.toEntity(dto);
        procDef.setCamundaProcDefId(pd.getId());
        procDef.setCamundaProcDefKey(pd.getKey());
        procDefService.save(procDef);
        formDefService.saveDTO(procDef.getId(), dto.getFormDef());
        List<ProcDefVariable> eL = dto.getNodeList().stream().filter(e -> e.getVariableList() != null).flatMap(e -> e.getVariableList().stream()).map(e -> {
            ProcDefVariable entity = procDefVariableService.toEntity(e);
            entity.setProcDefId(procDef.getId());
            log.debug("create variable entity???{}", entity);
            return entity;
        }).collect(Collectors.toList());
        procDefVariableService.saveBatch(eL);
    }

    public void createProcessInstance(ProcInstDTO dto) {
        ProcDef procDef = procDefService.getById(dto.getProcDefId());
        Assert.notNull(procDef, "procDefId??????");
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
            Assert.notNull(item, "????????????: required id:{} type:{}", varId, e.getSourceType());
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
            Assert.notNull(userBean, "????????????: required key:{} type:{}", varKey, e.getSourceType());
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private BpmnModelInstance parse(String procName, List<NodeDTO> nodeList) {
        nodeList.forEach(NodeDTO::tidyUp);
        // ?????????????????????task code?????????????????????????????????????????????task????????????code????????????
        nodeList.stream()
                .filter(n -> n instanceof TaskFlowNodeDTO)
                .map(n -> (TaskFlowNodeDTO) n)
                .collect(Collectors.groupingBy(TaskFlowNodeDTO::getCode))
                .values()
                .forEach(nList -> Assert.isTrue(nList.size() < 2, "??????Task????????????code?????????????????????????????????:{}", nList));
        Map<Boolean, List<NodeDTO>> collect = nodeList.stream().collect(Collectors.groupingBy(e -> e instanceof EdgeNodeDTO));
        List<FlowNodeDTO> flowNodes = (List) collect.get(false);
        log.debug("num of none-edge node???{}", flowNodes.size());
        List<EdgeNodeDTO> edges = (List) collect.get(true);
        Map<String, FlowNodeDTO> nodeMap = flowNodes.stream().collect(Collectors.toMap(FlowNodeDTO::getId, e -> e));
        List<FlowNodeDTO> startFlowNodeDTOS = flowNodes.stream().filter(e -> e instanceof StartEventFlowNodeDTO).toList();
        Map<String, List<EdgeNodeDTO>> edgeMap = edges.stream().collect(Collectors.groupingBy(EdgeNodeDTO::getSource));
        Assert.isTrue(startFlowNodeDTOS.size() == 1, "????????????????????????");
        FlowNodeDTO start = startFlowNodeDTOS.get(0);
        ProcessBuilder builder = Bpmn.createExecutableProcess().name(procName);
        AbstractFlowNodeBuilder<?, ?> s = builder.startEvent(start.getId()).name(start.getLabel());
//        HashSet<String> vertexLog = new HashSet<>();
        // FIXME: 2022/7/20 ?????????????????????
        travelGraph(new NodeLink(null, null, start), edgeMap, nodeMap, nl -> {
            log.debug("handle node link pair???{}", nl);
            var ss = s;
            ss = ss.moveToNode(nl.getPrevOutgoingNodeId());
            String condition = nl.getEdge().getCondition();
            if (condition != null && !condition.isEmpty()) {
                ss.condition(null, condition);
            }
            nl.getCurr().build(ss);
        });
        return s.done();
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
        List<EdgeNodeDTO> edgeNodes = edgeMap.get(prev.getId());
        if (edgeNodes == null) {
            return Collections.emptyList();
        }
        return edgeNodes.stream().map(e -> new NodeLink(prev, e, nodeMap.get(e.getTarget()))).collect(Collectors.toList());
    }
}
