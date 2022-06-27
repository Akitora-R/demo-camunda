package me.aki.demo.camunda.entity.bpmn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.aki.demo.camunda.enums.BpmnShape;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ServiceTaskBuilder;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceTaskFlowNodeDTO implements FlowNodeDTO<ServiceTaskBuilder, ServiceTask> {
    private String id;
    private String label;
    private String className;

    @Override
    public ServiceTaskBuilder build(AbstractFlowNodeBuilder<?, ?> builder) {
        return builder.serviceTask().id(id).name(label).camundaClass(className);
    }

    @Override
    public BpmnShape getShape() {
        return BpmnShape.SERVICE_TASK;
    }
}
