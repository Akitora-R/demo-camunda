package me.aki.demo.camunda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.aki.demo.camunda.delegate.ApprovedDelegate;
import me.aki.demo.camunda.entity.bpmn.*;
import me.aki.demo.camunda.parser.NodeParser;
import me.aki.demo.camunda.parser.impl.*;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.instance.*;
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

    Map<Class<? extends FlowNode>, NodeParser<?, ?>> getMap() {
        HashMap<Class<? extends FlowNode>, NodeParser<?, ?>> m = new HashMap<>();
        m.put(UserTask.class, new UserTaskNodeParser());
        m.put(StartEvent.class, new StartEvenNodeParser());
        m.put(EndEvent.class, new EndEventNodeParser());
        m.put(ExclusiveGateway.class, new ExclusiveGatewayNodeParser());
        m.put(ServiceTask.class, new ServiceTaskNodeParser());
        return m;
    }

    @Test
    void unParseFlow() throws JsonProcessingException {
        BpmnModelInstance modelInstance = genInst();
        var p = getMap();
        HashSet<EdgeNodeDTO> edges = new HashSet<>();
        ArrayList<NodeDTO> nodes = new ArrayList<>();
        for (var e : p.entrySet()) {
            for (FlowNode flowNode : modelInstance.getModelElementsByType(e.getKey())) {
                var dto = e.getValue().toDTO(flowNode);
                edges.addAll(dto.getKey().values().stream().flatMap(Collection::stream).map(this::toDTO).toList());
                nodes.add(dto.getVal());
            }
        }
        nodes.addAll(edges);
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(nodes));
    }

    EdgeNodeDTO toDTO(SequenceFlow flow) {
        EdgeNodeDTO dto = new EdgeNodeDTO();
        dto.setCondition(flow.getTextContent());
        dto.setSource(flow.getSource().getId());
        dto.setTarget(flow.getTarget().getId());
        dto.setId(flow.getId());
        dto.setLabel(flow.getName());
        return dto;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void parseFlow() {
        List<NodeDTO> d = genTestDataByStr();
        Map<Boolean, List<NodeDTO>> collect = d.stream().collect(Collectors.groupingBy(e -> e instanceof EdgeNodeDTO));
        List<FlowNodeDTO<?, ?>> flowNodes = (List) collect.get(false);
        List<EdgeNodeDTO> edges = (List) collect.get(true);
        Map<String, FlowNodeDTO<?, ?>> nodeMap = flowNodes.stream().collect(Collectors.toMap(FlowNodeDTO::getId, e -> e));
        List<FlowNodeDTO<?, ?>> startFlowNodeDTOS = flowNodes.stream().filter(e -> e instanceof StartEventFlowNodeDTO).toList();
        Map<String, List<EdgeNodeDTO>> edgeMap = edges.stream().collect(Collectors.groupingBy(EdgeNodeDTO::getSource));
        assert startFlowNodeDTOS.size() == 1;
        FlowNodeDTO<?, ?> start = startFlowNodeDTOS.get(0);
        ProcessBuilder builder = Bpmn.createExecutableProcess().name("GENERATED_PROC1");
        AbstractFlowNodeBuilder<?, ?> s = builder.startEvent(start.getId()).name(start.getLabel());
        HashSet<String> vertexLog = new HashSet<>();
        travelGraph(new NodeLink(null, null, start), edgeMap, nodeMap, nl -> {
            FlowNodeDTO<?, ?> prev = nl.getPrev();
            var ss = s;
            ss = ss.moveToNode(prev.getId());
            if (nl.getEdge().getCondition() != null) {
                ss.condition(null, nl.getEdge().getCondition());
            }
            nl.getCurr().build(ss);
        });
        System.out.println(Bpmn.convertToString(s.done()));
    }

    // TODO: 2022/6/22 循环图尚未解决
    void travelGraph(
            NodeLink nl,
            Map<String, List<EdgeNodeDTO>> edgeMap,
            Map<String, FlowNodeDTO<?, ?>> nodeMap,
            Consumer<NodeLink> onNode
    ) {
        if (nl.getEdge() != null) {
            onNode.accept(nl);
        }
        for (NodeLink nodeLink : getNext(nl.getCurr(), edgeMap, nodeMap)) {
            travelGraph(nodeLink, edgeMap, nodeMap, onNode);
        }
    }

    List<NodeLink> getNext(FlowNodeDTO<?, ?> prev, Map<String, List<EdgeNodeDTO>> edgeMap, Map<String, FlowNodeDTO<?, ?>> nodeMap) {
        List<EdgeNodeDTO> edgeNodes = edgeMap.get(prev.getId());
        if (edgeNodes == null) {
            return Collections.emptyList();
        }
        return edgeNodes.stream().map(e -> new NodeLink(prev, e, nodeMap.get(e.getTarget()))).collect(Collectors.toList());
    }

    //https://x6.antv.vision/zh/examples/showcase/practices#bpmn
    List<NodeDTO> genTestData() {
        ArrayList<NodeDTO> l = new ArrayList<>();
        l.add(new StartEventFlowNodeDTO("startEvent_1", "开始"));
        l.add(new EdgeNodeDTO(null, null, null, "startEvent_1", "userTask_1"));
        l.add(new UserTaskFlowNodeDTO("userTask_1", "审核", null));
        l.add(new EdgeNodeDTO(null, null, null, "userTask_1", "exclusiveGateway_1"));
        l.add(new ExclusiveGatewayFlowNodeDTO("exclusiveGateway_1", null));
        l.add(new EdgeNodeDTO(null, null, "a == b", "exclusiveGateway_1", "endEvent_1"));
        l.add(new EndEventFlowNodeDTO("endEvent_1", "结束"));
        l.add(new EdgeNodeDTO(null, null, null, "exclusiveGateway_1", "endEvent_2"));
        l.add(new EndEventFlowNodeDTO("endEvent_2", "结束"));
        return l;
    }

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
