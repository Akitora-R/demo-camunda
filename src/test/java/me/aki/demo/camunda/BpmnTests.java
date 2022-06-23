package me.aki.demo.camunda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.aki.demo.camunda.delegate.ApprovedDelegate;
import me.aki.demo.camunda.entity.bpmn.*;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.EndEventBuilder;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.UserTask;
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

    @Test
    void unParseFlow() {
        EndEventBuilder b = Bpmn.createExecutableProcess().name("GENERATED_PROC1")
                .startEvent()
                .userTask().name("主管审核").camundaAssignee("${chargerAssignee}")
                .endEvent().name("finish");
        BpmnModelInstance modelInstance = b.done();
        for (Task t : modelInstance.getModelElementsByType(Task.class)) {
            if (t instanceof UserTask ut) {
                System.out.println("userTask " + ut.getName() + " " + ut.getCamundaAssignee());
            }
        }
        for (Event event : modelInstance.getModelElementsByType(Event.class)) {
            System.out.println("event " + event.getName());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void parseFlow() {
        List<NodeDTO> d = genTestData();
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
            System.out.println(nl);
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
        l.add(new UserTaskFlowNodeDTO("userTask_1", "审核",null));
        l.add(new EdgeNodeDTO(null, null, null, "userTask_1", "exclusiveGateway_1"));
        l.add(new ExclusiveGatewayFlowNodeDTO("exclusiveGateway_1", null));
        l.add(new EdgeNodeDTO(null,null,"a == b","exclusiveGateway_1","endEvent_1"));
        l.add(new EndEventFlowNodeDTO("endEvent_1", "结束"));
        l.add(new EdgeNodeDTO(null,null,null,"exclusiveGateway_1","endEvent_2"));
        l.add(new EndEventFlowNodeDTO("endEvent_2", "结束"));
        return l;
    }

    List<NodeDTO> genTestDataByStr(){
        String j = """
                [
                    {
                        "id": "startEvent_1",
                        "label": null,
                        "shape": "START_EVENT"
                    },
                    {
                        "id": "userTask_1",
                        "label": "用户任务",
                        "shape": "USER_TASK"
                    },
                    {
                        "id": "endEvent_1",
                        "shape": "END_EVENT"
                    },
                    {
                        "shape": "EDGE",
                        "source": "startEvent_1",
                        "target": "userTask_1"
                    },
                    {
                        "shape": "EDGE",
                        "source": "userTask_1",
                        "target": "endEvent_1"
                    }
                ]
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
