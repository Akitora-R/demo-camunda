package me.aki.demo.camunda.service;

import cn.hutool.core.lang.Assert;
import me.aki.demo.camunda.entity.dto.FlowNodeDTO;
import me.aki.demo.camunda.entity.dto.NodeDTO;
import me.aki.demo.camunda.entity.dto.NodeLink;
import me.aki.demo.camunda.entity.dto.impl.EdgeNodeDTO;
import me.aki.demo.camunda.entity.dto.impl.StartEventFlowNodeDTO;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class BpmnService {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public BpmnModelInstance parse(String procName, List<NodeDTO> nodeList) {
        nodeList.forEach(NodeDTO::tidyUp);
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