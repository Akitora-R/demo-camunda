package me.aki.demo.camunda.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.constant.BpmnConstant;
import me.aki.demo.camunda.entity.ProcDef;
import me.aki.demo.camunda.entity.VariableDef;
import me.aki.demo.camunda.entity.VariableDefProp;
import me.aki.demo.camunda.entity.VariableInst;
import me.aki.demo.camunda.entity.dto.ProcDefDTO;
import me.aki.demo.camunda.entity.dto.ProcInstDTO;
import me.aki.demo.camunda.entity.dto.VariableDefDTO;
import me.aki.demo.camunda.entity.dto.VariableInstDTO;
import me.aki.demo.camunda.entity.dto.node.*;
import me.aki.demo.camunda.entity.dto.node.impl.EdgeNodeDTO;
import me.aki.demo.camunda.entity.dto.node.impl.StartEventFlowNodeDTO;
import me.aki.demo.camunda.entity.dto.node.impl.TaskFlowNodeDTO;
import me.aki.demo.camunda.entity.dto.query.ProcInstPagedQuery;
import me.aki.demo.camunda.entity.dto.query.ProcInstPagedQueryParam;
import me.aki.demo.camunda.entity.vo.FormDefVO;
import me.aki.demo.camunda.entity.vo.ProcDefVO;
import me.aki.demo.camunda.entity.vo.ProcInstVO;
import me.aki.demo.camunda.entity.vo.TaskVO;
import me.aki.demo.camunda.enums.SourceBizType;
import me.aki.demo.camunda.enums.VariableSourceType;
import me.aki.demo.camunda.provider.BeanProvider;
import me.aki.demo.camunda.provider.DataProvider;
import me.aki.demo.camunda.util.BpmnUtil;
import me.aki.demo.camunda.util.TreeUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class WorkflowProcService {

    private final ProcDefService procDefService;
    private final ProcDefNodeService procDefNodeService;
    private final ProcDefNodePropService procDefNodePropService;
    private final ProcDefNodeElementRelInfoService procDefNodeElementRelInfoService;
    private final FormDefService formDefService;
    private final RepositoryService repositoryService;
    private final VariableDefService variableDefService;
    private final VariableDefPropService variableDefPropService;
    private final SysUserService sysUserService;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final DataProvider dataProvider;
    private final ObjectMapper objectMapper;
    private final VariableInstService variableInstService;

    public WorkflowProcService(ProcDefService procDefService,
                               ProcDefNodeService procDefNodeService,
                               ProcDefNodePropService procDefNodePropService,
                               ProcDefNodeElementRelInfoService procDefNodeElementRelInfoService,
                               FormDefService formDefService,
                               RepositoryService repositoryService,
                               VariableDefService variableDefService,
                               VariableDefPropService variableDefPropService,
                               SysUserService sysUserService,
                               RuntimeService runtimeService,
                               HistoryService historyService,
                               DataProvider dataProvider,
                               ObjectMapper objectMapper,
                               VariableInstService variableInstService) {
        this.procDefService = procDefService;
        this.procDefNodeService = procDefNodeService;
        this.procDefNodePropService = procDefNodePropService;
        this.procDefNodeElementRelInfoService = procDefNodeElementRelInfoService;
        this.formDefService = formDefService;
        this.repositoryService = repositoryService;
        this.variableDefService = variableDefService;
        this.variableDefPropService = variableDefPropService;
        this.sysUserService = sysUserService;
        this.runtimeService = runtimeService;
        this.historyService = historyService;
        this.dataProvider = dataProvider;
        this.objectMapper = objectMapper;
        this.variableInstService = variableInstService;
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
        log.debug("start create process definition: {}", dto.getProcDefName());
        // parse方法可能会修改dto的内容
        var nodeList = dto.getNodeList();
        var p = parse(dto.getProcDefName(), nodeList);
        // 保存元素关联信息
        p.getValue().forEach((nodeId, bpmnIdList) -> bpmnIdList.forEach(bpmnId -> procDefNodeElementRelInfoService.saveRel(nodeId, bpmnId)));
        var instance = p.getKey();
        // 保存camunda流程定义并确保成功
        var deploy = repositoryService.createDeployment().name(dto.getProcDefName()).addModelInstance("generatedDef.bpmn", instance).deployWithResult();
        Assert.isTrue(deploy.getDeployedProcessDefinitions().size() == 1, "部署流程出错");
        var pd = deploy.getDeployedProcessDefinitions().get(0);
        // 保存流程定义
        var procDef = procDefService.toEntity(dto);
        procDef.setCamundaProcDefId(pd.getId());
        procDef.setCamundaProcDefKey(pd.getKey());
        procDefService.save(procDef);
        final var procDefId = procDef.getId();
        // 保存表单信息
        formDefService.saveDTO(procDefId, null, dto.getFormDef());
        // 保存流程定义节点、节点属性、节点变量、节点变量属性
        nodeList.forEach(nodeDTO -> {
            var pair = procDefNodeService.toEntity(nodeDTO);
            var procDefNode = pair.getKey();
            var props = pair.getValue();
            procDefNode.setProcDefId(procDefId);
            procDefNodeService.save(procDefNode);
            String procDefNodeId = procDefNode.getId();
            props.forEach(e -> e.setProcDefNodeId(procDefNodeId));
            procDefNodePropService.saveBatch(props);
            if (nodeDTO instanceof FormNodeDTO formNodeDTO) {
                Optional.ofNullable(formNodeDTO.getForm()).ifPresent(f -> formDefService.saveDTO(procDefId, procDefNodeId, f));
            }
            Optional.ofNullable(nodeDTO.getVariableList()).ifPresent(e -> e.forEach(v -> {
                var procDefVariable = variableDefService.toEntity(v);
                procDefVariable.setProcDefId(procDefId);
                procDefVariable.setProcDefNodeId(procDefNodeId);
                variableDefService.save(procDefVariable);
                Optional.ofNullable(v.getPropList()).ifPresent(varProps -> TreeUtils.travelTreeBFS(varProps, VariableDefDTO.VariableDefPropDTO::getChildren, ni -> {
                    VariableDefProp variableDefProp = new VariableDefProp();
                    String parentId = Optional.ofNullable(ni.getParentNode()).map(VariableDefDTO.VariableDefPropDTO::getId).orElse("0");
                    variableDefProp.setProcDefVariableId(procDefVariable.getId());
                    variableDefProp.setParentPropId(parentId);
                    variableDefProp.setPropKey(ni.getNode().getKey());
                    variableDefProp.setPropVal(ni.getNode().getVal());
                    variableDefPropService.save(variableDefProp);
                    ni.getNode().setId(variableDefProp.getId());
                }));
            }));
        });
    }

    public void createProcessInstance(ProcInstDTO dto) {
        ProcDef procDef = procDefService.getById(dto.getProcDefId());
        Assert.notNull(procDef, "procDefId有误");
        HashMap<String, Object> procVar = new HashMap<>();
        // 从请求dto中收集提供的变量
        Map<String, VariableInstDTO> providedFormVar = Optional.ofNullable(dto.getVariableList()).orElse(Collections.emptyList())
                .stream().collect(Collectors.toMap(VariableInstDTO::getSourceIdentifier, e -> e));
        var requiredVar = variableDefService.lambdaQuery().eq(VariableDef::getProcDefId, procDef.getId()).list();
        var groupedRequiredVar = requiredVar.stream().collect(Collectors.groupingBy(VariableDef::getSourceType));
        // 获取流程定义要求的form变量
        List<VariableDef> requiredFormVar = groupedRequiredVar.getOrDefault(VariableSourceType.PROC_FORM, Collections.emptyList());
        List<VariableInst> items = requiredFormVar.stream().map(e -> {
            String varId = e.getSourceIdentifier();
            String varKey = e.getVariableKey();
            VariableInstDTO item = providedFormVar.get(varId);
            Assert.notNull(item, "变量缺失: required id:{} type:{}", varId, e.getSourceType());
            procVar.put(varKey, item.getVal());
            VariableInst varInst = new VariableInst();
            varInst.setVariableDefId(e.getId());
            varInst.setVariableVal(item.getVal());
            return varInst;
        }).toList();
        ArrayList<VariableInst> varInstList = new ArrayList<>(items);
        // 获取流程定义要求的bean变量
        List<VariableDef> requiredBeanParamVar = groupedRequiredVar.getOrDefault(VariableSourceType.BEAN, Collections.emptyList());
        Map<String, BeanProvider<?>> beanProviderMap = dataProvider.getBeanProviderMap();
        items = requiredBeanParamVar.stream().map(e -> {
            String varId = e.getSourceIdentifier();
            String varKey = e.getVariableKey();
            BeanProvider<?> beanProvider = beanProviderMap.get(varId);
            VariableDefDTO variableDefDTO = variableDefService.getDTOById(e.getId());
            Object data = beanProvider.getData(variableDefDTO.getPropList());
            VariableInst varInst = new VariableInst();
            varInst.setVariableDefId(e.getId());
            procVar.put(varKey, data);
            try {
                varInst.setVariableVal(objectMapper.writeValueAsString(data));
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
            return varInst;
        }).collect(Collectors.toList());
        varInstList.addAll(items);
        // 获取流程定义要求的来源业务变量
        List<VariableDef> requiredBizVar = groupedRequiredVar.getOrDefault(VariableSourceType.BIZ, Collections.emptyList());
        items = requiredBizVar.stream().map(e -> {
            String varId = e.getSourceIdentifier();
            String varKey = e.getVariableKey();
            SourceBizType sourceBizType = procDef.getSourceBizType();
            Object varVal = dataProvider.getBizDataProviderMap().get(sourceBizType).getData(varId);
            procVar.put(varKey, varVal);
            VariableInst varInst = new VariableInst();
            varInst.setVariableDefId(e.getId());
            try {
                varInst.setVariableVal(objectMapper.writeValueAsString(varVal));
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
            return varInst;
        }).collect(Collectors.toList());
        varInstList.addAll(items);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(procDef.getCamundaProcDefKey(), dto.getCamundaProcInstBusinessKey(), procVar);
        varInstList.forEach(v -> {
            v.setCamundaProcInstId(processInstance.getId());
            variableInstService.save(v);
        });
    }

    public IPage<ProcInstVO> procInstPagedQuery(ProcInstPagedQueryParam queryParam) {
        Page<ProcInstVO> p = new Page<>();
        Integer size = queryParam.getSize();
        Integer page = queryParam.getPage();
        HistoricProcessInstanceQuery q = historyService.createHistoricProcessInstanceQuery();
        ProcInstPagedQuery.ProcInstStatus status = queryParam.getQuery().getStatus();
        if (status != null) {
            switch (status) {
                case ACTIVE -> q.active();
                case COMPLETED -> q.completed();
                case SUSPENDED -> q.suspended();
            }
        }
        List<HistoricProcessInstance> rec = q.orderByProcessInstanceStartTime().desc()
                .listPage((page - 1) * size, size);
        long total = q.count();
        p.setPages(page);
        p.setTotal(total);
        p.setSize(size);
        p.setRecords(rec.stream().map(e -> {
            ProcInstVO vo = new ProcInstVO();
            vo.setCamundaProcessInstance(e);
            return vo;
        }).toList());
        return p;
    }

    public ProcInstVO procInstDetailById(String id) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
        if (historicProcessInstance == null) {
            return null;
        }
        ProcInstVO vo = new ProcInstVO();
        vo.setCamundaProcessInstance(historicProcessInstance);
        var taskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(id).orderByHistoricActivityInstanceStartTime().desc().list()
                .stream().map(e -> {
                    TaskVO taskVO = new TaskVO();
                    taskVO.setCamundaTask(e);
                    return taskVO;
                }).toList();
        vo.setTaskList(taskInstanceList);
        return vo;
    }

    /**
     * dto图节点转为 {@link BpmnModelInstance}
     *
     * @param procName 流程定义名
     * @param nodeList dto图节点
     * @return pair of {@link BpmnModelInstance} -> { dto图节点id -> [{@link BpmnModelInstance} 节点id]}
     */
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
        var collect = nodeList.stream().collect(Collectors.groupingBy(e -> e instanceof EdgeNodeDTO));
        List<FlowNodeDTO> flowNodes = (List) collect.get(false);
        log.debug("非边节点数量 ：{}", flowNodes.size());
        List<EdgeNodeDTO> edges = (List) collect.get(true);
        // FIXME: 2022/8/8 既然已经持久化了映射关系，tidyUp过程中是否真的有必要再进行id的修改？
        // 整理id的同时保持图连接的正确性
        nodeList.forEach(e -> e.tidyUp((oldId, newId) -> {
            edges.stream().filter(edge -> edge.getSource().equals(oldId)).forEach(edge -> edge.setSource(newId));
            edges.stream().filter(edge -> edge.getTarget().equals(oldId)).forEach(edge -> edge.setTarget(newId));
        }));
        // 获得开始节点
        var nodeMap = flowNodes.stream().collect(Collectors.toMap(FlowNodeDTO::getId, e -> e));
        var startFlowNodeDTOS = flowNodes.stream().filter(e -> e instanceof StartEventFlowNodeDTO).toList();
        var edgeMap = edges.stream().collect(Collectors.groupingBy(EdgeNodeDTO::getSource));
        Assert.isTrue(startFlowNodeDTOS.size() == 1, "开始节点数量有误");
        var start = startFlowNodeDTOS.get(0);
        var builder = Bpmn.createExecutableProcess().name(procName);
        AbstractFlowNodeBuilder<?, ?> s = builder.startEvent(start.getId()).name(start.getLabel());
        var idPair = new HashMap<String, List<String>>();
        // 用于记录已经走过的节点
        HashSet<String> vertexLog = new HashSet<>();
        // 用于记录已经连接过的节点
        HashSet<Pair<String,String>> linkLog = new HashSet<>();
        // 遍历json图并构建BpmnModelInstance
        travelGraph(new NodeLink(null, null, start), edgeMap, nodeMap, nl -> {
            var curr = nl.getCurr();
            String currId = curr.getId();
            var ss = s;
            String prevId = nl.getPrevOutgoingNodeId();
            ss = ss.moveToNode(prevId);
            var condition = nl.getEdge().getCondition();
            if (condition != null && !condition.isEmpty()) {
                ss.condition(null, condition);
            }
            Pair<String, String> currLinkPair = new Pair<>(prevId, currId);
            if (vertexLog.contains(currId)) {
                if (!linkLog.contains(currLinkPair)){
                    log.debug("已经经过的node: {}, 连接到并跳过", currId);
                    ss.connectTo(currId);
                    linkLog.add(currLinkPair);
                }
                return;
            }
            log.debug("处理 node link pair：{}", nl);
            var bpmnId = curr.build(ss);
            idPair.put(currId, bpmnId);
            vertexLog.add(currId);
            linkLog.add(currLinkPair);
        });
        BpmnModelInstance instance = s.done();
        var bpmnEdges = BpmnUtil.getElementByName(instance, BpmnConstant.SEQUENCE_FLOW_NAME);
        log.debug("bpmn.xml:\n{}", BpmnUtil.toXmlStr(instance));
        log.debug("开始处理bpmn sequenceFlow，数量: {}", bpmnEdges.size());
        // 关联json edge 与bpmn sequenceFlow
        edges.forEach(edge -> {
            var source = nodeMap.get(edge.getSource()) instanceof CombinationNodeDTO c ? c.getOutgoingNodeId() : edge.getSource();
            var target = nodeMap.get(edge.getTarget()) instanceof CombinationNodeDTO c ? c.getIncomingNodeId() : edge.getTarget();
            log.debug("处理edge: {} -> {}", source, target);
            var ids = bpmnEdges.stream().filter(e -> e.getAttribute(BpmnConstant.SEQUENCE_FLOW_ATTR_SOURCE).equals(source) &&
                    e.getAttribute(BpmnConstant.SEQUENCE_FLOW_ATTR_TARGET).equals(target)).map(e -> e.getAttribute(BpmnConstant.ID_ATTR)).toList();
            Assert.isTrue(ids.size() == 1, () -> ids.isEmpty() ?
                    new IllegalArgumentException(String.format("edge匹配元素失败: %s -> %s", source, target)) :
                    new IllegalArgumentException(String.format("路径相同的edge存在多条: %s -> %s", source, target)));
            var bpmnId = ids.get(0);
            idPair.put(edge.getId(), Collections.singletonList(bpmnId));
        });
        // 将CombinationNodeDTO内部的edge也加入集合
        flowNodes.stream()
                .map(e -> e instanceof CombinationNodeDTO c ? c : null)
                .filter(Objects::nonNull)
                .forEach(comb -> idPair.computeIfPresent(comb.getId(), (jsonNodeId, internalElemId) -> {
                    var internalEdgeIds = bpmnEdges.stream().filter(e -> internalElemId.contains(e.getAttribute(BpmnConstant.SEQUENCE_FLOW_ATTR_SOURCE)) &&
                            internalElemId.contains(e.getAttribute(BpmnConstant.SEQUENCE_FLOW_ATTR_TARGET))).map(e -> e.getAttribute(BpmnConstant.ID_ATTR)).toList();
                    // 以防list为immutable类型
                    var i = new ArrayList<>(internalElemId);
                    i.addAll(internalEdgeIds);
                    return i;
                }));
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

    private List<NodeLink> getNext(FlowNodeDTO prev, Map<String, List<EdgeNodeDTO>> edgeMap, Map<String, FlowNodeDTO> nodeMap) {
        List<EdgeNodeDTO> edgeNodes = edgeMap.getOrDefault(prev.getId(), Collections.emptyList());
        return edgeNodes.stream().map(e -> new NodeLink(prev, e, nodeMap.get(e.getTarget()))).collect(Collectors.toList());
    }
}
