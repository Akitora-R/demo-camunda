package me.aki.demo.camunda;

import me.aki.demo.camunda.entity.bpmn.*;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
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

public class NonSpringTests {
    @Test
    void genBpmn() {
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
            s.moveToNode(nl.getPrev().getId());
            if (nl.getEdge().getCondition() != null) {
                s.condition(null, nl.getEdge().getCondition());
            }
            nl.getCurr().build(s);
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
        l.add(new EdgeNodeDTO("2", null, null, "startEvent_1", "userTask_1"));
        l.add(new UserTaskFlowNodeDTO("userTask_1", "审核"));
        l.add(new EdgeNodeDTO("4", null, null, "userTask_1", "endEvent_1"));
        l.add(new EndEventFlowNodeDTO("endEvent_1", "结束"));
        return l;
    }
}
