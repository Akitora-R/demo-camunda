package me.aki.demo.camunda.entity.bpmn;

import lombok.*;
import me.aki.demo.camunda.enums.BpmnShape;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ExclusiveGatewayBuilder;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExclusiveGatewayFlowNodeDTO implements FlowNodeDTO<ExclusiveGatewayBuilder,ExclusiveGateway> {
    private String id;
    private String label;

    @Override
    public String toString() {
        return String.format("<ExclusiveGateway %s(%s)>", id, label);
    }

    @Override
    public BpmnShape getShape() {
        return BpmnShape.EXCLUSIVE_GATEWAY;
    }

    @Override
    public ExclusiveGatewayBuilder build(AbstractFlowNodeBuilder<?, ?> builder) {
        return builder.exclusiveGateway().id(id).name(label);
    }
}
