package me.aki.demo.camunda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.aki.demo.camunda.delegate.ApprovedDelegate;
import me.aki.demo.camunda.entity.dto.node.FlowNodeDTO;
import me.aki.demo.camunda.entity.dto.node.NodeDTO;
import me.aki.demo.camunda.entity.dto.node.NodeLink;
import me.aki.demo.camunda.entity.dto.node.impl.EdgeNodeDTO;
import me.aki.demo.camunda.entity.dto.node.impl.EndEventFlowNodeDTO;
import me.aki.demo.camunda.entity.dto.node.impl.StartEventFlowNodeDTO;
import me.aki.demo.camunda.entity.dto.node.impl.TaskFlowNodeDTO;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BpmnTests {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void genBpmn() {
        BpmnModelInstance modelInstance = Bpmn.createExecutableProcess().name("GENERATED_PROC1")
                .startEvent()
                .userTask().name("主管审核").camundaAssignee("${chargerAssignee}").camundaCandidateUsers("${chargerCandidate}")
                .camundaOutputParameter("approval_1", "${approval}")
                .exclusiveGateway()
                .gatewayDirection(GatewayDirection.Diverging)
                .condition("yes", "${approval_1}")
                .serviceTask().name("服务调用任务").camundaClass(ApprovedDelegate.class)
                .endEvent().name("finish")
                .moveToLastGateway()
                .condition("no", "${!approval_2}")
                .endEvent().name("rejected")
                .done();
        System.out.println(Bpmn.convertToString(modelInstance));
    }

    BpmnModelInstance genInst() {
        return Bpmn.createExecutableProcess().name("GENERATED_PROC1")
                .startEvent()
                .userTask().name("主管审核").camundaAssignee("${chargerAssignee}").camundaCandidateUsers("${chargerCandidate}")
                .camundaOutputParameter("approval_1", "${approval}")
                .exclusiveGateway()
                .gatewayDirection(GatewayDirection.Diverging)
                .condition("yes", "${approval_1}")
                .serviceTask().name("服务调用任务").camundaClass(ApprovedDelegate.class)
                .endEvent().name("finish")
                .moveToLastGateway()
                .condition("no", "${!approval_2}")
                .endEvent().name("rejected")
                .done();
    }

    @Test
    void genBpmn2() {
        ProcessBuilder b = Bpmn.createExecutableProcess().name("GENERATED_PROC1");
        AbstractFlowNodeBuilder<?, ?> s = b.startEvent("startEvent_1").name("开始");
        s.userTask("userTask_1").name("审核");
        s = s.moveToNode("userTask_1");
        s.exclusiveGateway("exclusiveGateway_1");
        s = s.moveToNode("exclusiveGateway_1");
        s.condition(null, "a == b");
        s.endEvent("endEvent_1");
        s = s.moveToNode("exclusiveGateway_1");
        s.endEvent("endEvent_2");
        System.out.println(Bpmn.convertToString(s.done()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void parseFlow() {
        List<NodeDTO> nodeList = genTestData();
        nodeList.forEach(NodeDTO::tidyUp);
        Map<Boolean, List<NodeDTO>> collect = nodeList.stream().collect(Collectors.groupingBy(e -> e instanceof EdgeNodeDTO));
        List<FlowNodeDTO> flowNodes = (List) collect.get(false);
        List<EdgeNodeDTO> edges = (List) collect.get(true);
        Map<String, FlowNodeDTO> nodeMap = flowNodes.stream().collect(Collectors.toMap(FlowNodeDTO::getId, e -> e));
        List<FlowNodeDTO> startFlowNodeDTOS = flowNodes.stream().filter(e -> e instanceof StartEventFlowNodeDTO).toList();
        Map<String, List<EdgeNodeDTO>> edgeMap = edges.stream().collect(Collectors.groupingBy(EdgeNodeDTO::getSource));
        assert startFlowNodeDTOS.size() == 1;
        FlowNodeDTO start = startFlowNodeDTOS.get(0);
        ProcessBuilder builder = Bpmn.createExecutableProcess().name("GENERATED_PROC1");
        AbstractFlowNodeBuilder<?, ?> s = builder.startEvent(start.getId()).name(start.getLabel());
        HashSet<String> vertexLog = new HashSet<>();
        travelGraph(new NodeLink(null, null, start), edgeMap, nodeMap, nl -> {
            var ss = s;
            ss = ss.moveToNode(nl.getPrevOutgoingNodeId());
            String condition = nl.getEdge().getCondition();
            if (condition != null && !condition.isEmpty()) {
                ss.condition(null, condition);
            }
            nl.getCurr().build(ss);
        });
        System.out.println(Bpmn.convertToString(s.done()));
    }

    // TODO: 2022/6/22 循环图尚未解决
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

    List<NodeDTO> genTestData() {
        ArrayList<NodeDTO> l = new ArrayList<>();
        l.add(new StartEventFlowNodeDTO("startEvent_1", "开始"));
        l.add(new EdgeNodeDTO(null, null, null, "startEvent_1", "userTask_1"));
        l.add(new TaskFlowNodeDTO("userTask_1", null, "审核1", null));
        l.add(new EdgeNodeDTO(null, null, null, "userTask_1", "userTask_2"));
        l.add(new TaskFlowNodeDTO("userTask_2", null, "审核2", null));
        l.add(new EdgeNodeDTO(null, null, null, "userTask_2", "endEvent_1"));
        l.add(new EndEventFlowNodeDTO("endEvent_1", "结束"));
        return l;
    }

    //https://x6.antv.vision/zh/examples/showcase/practices#bpmn
    List<NodeDTO> genTestDataByStr() {
        String j = """
                [ {
                  "id" : "userTask_1deb5357-ef27-4b24-9b0f-bcb997fc0c37",
                  "label" : "主管审核",
                  "assignee" : "${chargerAssignee}",
                  "shape" : "USER_TASK"
                }, {
                  "id" : "endEvent_6c217e47-029c-4e94-93c2-55fa456f9a38",
                  "label" : "finish",
                  "shape" : "END_EVENT"
                }, {
                  "id" : "endEvent_6b1e7f9b-e5a2-4d8b-bf2c-0ce14773d51c",
                  "label" : "rejected",
                  "shape" : "END_EVENT"
                }, {
                  "id" : "startEvent_e946138b-1986-4aa2-a019-59ec00628dcd",
                  "label" : null,
                  "shape" : "START_EVENT"
                }, {
                  "id" : "serviceTask_f83069b7-77ae-451b-88eb-0cf3ecbc0046",
                  "label" : "服务调用任务",
                  "className" : "me.aki.demo.camunda.delegate.ApprovedDelegate",
                  "shape" : "SERVICE_TASK"
                }, {
                  "id" : "exclusiveGateway_f2e29a82-c529-433b-a299-578710aa7bf5",
                  "label" : null,
                  "shape" : "EXCLUSIVE_GATEWAY"
                }, {
                  "id" : "sequenceFlow_9714db3b-9f80-4426-9c30-b94693ac74f4",
                  "label" : null,
                  "condition" : "",
                  "source" : "startEvent_e946138b-1986-4aa2-a019-59ec00628dcd",
                  "target" : "userTask_1deb5357-ef27-4b24-9b0f-bcb997fc0c37",
                  "shape" : "EDGE"
                }, {
                  "id" : "sequenceFlow_f315ba53-bb4e-49f0-8839-ab60d6c6c97b",
                  "label" : null,
                  "condition" : "",
                  "source" : "userTask_1deb5357-ef27-4b24-9b0f-bcb997fc0c37",
                  "target" : "exclusiveGateway_f2e29a82-c529-433b-a299-578710aa7bf5",
                  "shape" : "EDGE"
                }, {
                  "id" : "sequenceFlow_12872664-8ab8-474f-a5a4-e9559c8d6ba3",
                  "label" : "yes",
                  "condition" : "${approval_1}",
                  "source" : "exclusiveGateway_f2e29a82-c529-433b-a299-578710aa7bf5",
                  "target" : "serviceTask_f83069b7-77ae-451b-88eb-0cf3ecbc0046",
                  "shape" : "EDGE"
                }, {
                  "id" : "sequenceFlow_1f173b7e-c221-432e-aaeb-5707f5417df8",
                  "label" : "no",
                  "condition" : "${!approval_2}",
                  "source" : "exclusiveGateway_f2e29a82-c529-433b-a299-578710aa7bf5",
                  "target" : "endEvent_6b1e7f9b-e5a2-4d8b-bf2c-0ce14773d51c",
                  "shape" : "EDGE"
                }, {
                  "id" : "sequenceFlow_055a23c0-f9a4-4669-ab14-956ff259c488",
                  "label" : null,
                  "condition" : "",
                  "source" : "serviceTask_f83069b7-77ae-451b-88eb-0cf3ecbc0046",
                  "target" : "endEvent_6c217e47-029c-4e94-93c2-55fa456f9a38",
                  "shape" : "EDGE"
                } ]
                """;
        try {
            return objectMapper.readValue(j, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testJackson() {
        System.out.println(genTestDataByStr());
    }
}
