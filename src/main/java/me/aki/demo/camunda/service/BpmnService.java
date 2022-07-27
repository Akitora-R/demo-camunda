package me.aki.demo.camunda.service;

import cn.hutool.core.lang.Assert;
import me.aki.demo.camunda.entity.ProcDef;
import me.aki.demo.camunda.entity.dto.ProcDefDTO;
import me.aki.demo.camunda.entity.dto.node.FlowNodeDTO;
import me.aki.demo.camunda.entity.dto.node.NodeDTO;
import me.aki.demo.camunda.entity.dto.node.NodeLink;
import me.aki.demo.camunda.entity.dto.node.impl.EdgeNodeDTO;
import me.aki.demo.camunda.entity.dto.node.impl.StartEventFlowNodeDTO;
import me.aki.demo.camunda.entity.dto.node.impl.TaskFlowNodeDTO;
import me.aki.demo.camunda.entity.vo.FormDefVO;
import me.aki.demo.camunda.entity.vo.ProcDefVO;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Transactional
public class BpmnService {

    private final ProcDefService procDefService;
    private final FormDefService formDefService;
    private final RepositoryService repositoryService;
    private final ProcDefVariableService procDefVariableService;

    public BpmnService(ProcDefService procDefService, FormDefService formDefService, RepositoryService repositoryService, ProcDefVariableService procDefVariableService) {
        this.procDefService = procDefService;
        this.formDefService = formDefService;
        this.repositoryService = repositoryService;
        this.procDefVariableService = procDefVariableService;
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

    public void createBpmnProcess(ProcDefDTO dto) {
        BpmnModelInstance instance = parse(dto.getProcDefName(), dto.getNodeList());
        var deploy = repositoryService.createDeployment().name(dto.getProcDefName()).addModelInstance("generatedDef.bpmn", instance).deployWithResult();
        Assert.isTrue(deploy.getDeployedProcessDefinitions().size() == 1, "部署流程出错");
        var pd = deploy.getDeployedProcessDefinitions().get(0);
        ProcDef procDef = procDefService.toEntity(dto);
        procDef.setCamundaProcDefId(pd.getId());
        procDef.setCamundaProcDefKey(pd.getKey());
        procDefService.save(procDef);
        formDefService.saveDTO(procDef.getId(), dto.getFormDef());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public BpmnModelInstance parse(String procName, List<NodeDTO> nodeList) {
        nodeList.forEach(NodeDTO::tidyUp);
        // 为保证根据每个task code生成的变量不重复，必须校验所有task节点中的code不重复。
        nodeList.stream()
                .filter(n -> n instanceof TaskFlowNodeDTO)
                .map(n -> (TaskFlowNodeDTO) n)
                .collect(Collectors.groupingBy(TaskFlowNodeDTO::getCode))
                .values()
                .forEach(nList -> Assert.isTrue(nList.size() < 2, "所有Task节点中的code不可重复！存在重复节点:{}", nList));
        Map<Boolean, List<NodeDTO>> collect = nodeList.stream().collect(Collectors.groupingBy(e -> e instanceof EdgeNodeDTO));
        List<FlowNodeDTO> flowNodes = (List) collect.get(false);
        List<EdgeNodeDTO> edges = (List) collect.get(true);
        Map<String, FlowNodeDTO> nodeMap = flowNodes.stream().collect(Collectors.toMap(FlowNodeDTO::getId, e -> e));
        List<FlowNodeDTO> startFlowNodeDTOS = flowNodes.stream().filter(e -> e instanceof StartEventFlowNodeDTO).toList();
        Map<String, List<EdgeNodeDTO>> edgeMap = edges.stream().collect(Collectors.groupingBy(EdgeNodeDTO::getSource));
        Assert.isTrue(startFlowNodeDTOS.size() == 1, "开始节点数量有误");
        FlowNodeDTO start = startFlowNodeDTOS.get(0);
        ProcessBuilder builder = Bpmn.createExecutableProcess().name(procName);
        AbstractFlowNodeBuilder<?, ?> s = builder.startEvent(start.getId()).name(start.getLabel());
//        HashSet<String> vertexLog = new HashSet<>();
        // FIXME: 2022/7/20 循环图尚未实现
        travelGraph(new NodeLink(null, null, start), edgeMap, nodeMap, nl -> {
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

    void travelGraph(
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
