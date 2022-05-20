package me.aki.demo.camunda;

import me.aki.demo.camunda.delegate.ApprovedDelegate;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.junit.jupiter.api.Test;

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
}

