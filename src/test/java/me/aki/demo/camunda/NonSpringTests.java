package me.aki.demo.camunda;

import me.aki.demo.camunda.delegate.ApprovedDelegate;
import me.aki.demo.camunda.entity.bpmn.ActivityNode;
import me.aki.demo.camunda.entity.bpmn.EdgeNode;
import me.aki.demo.camunda.entity.bpmn.EventNode;
import me.aki.demo.camunda.entity.bpmn.Node;
import me.aki.demo.camunda.enums.ActivityType;
import me.aki.demo.camunda.enums.EventType;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class NonSpringTests {
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
    void parseFlow() {
        List<Node> testData = genTestData();
        ProcessBuilder builder = Bpmn.createExecutableProcess().name("");

    }

    //https://x6.antv.vision/zh/examples/showcase/practices#bpmn
    List<Node> genTestData() {
        ArrayList<Node> l = new ArrayList<>();
        l.add(new EventNode("1", "开始", EventType.START_EVENT));
        l.add(new EdgeNode("2", null, "1", "3"));
        l.add(new ActivityNode("3", "审核", ActivityType.USER_TASK));
        l.add(new EdgeNode("4", null, "3", "5"));
        l.add(new EventNode("5", "结束", EventType.END_EVENT));
        return l;
    }
}
