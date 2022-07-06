package me.aki.demo.camunda.entity.dto.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.aki.demo.camunda.entity.dto.FlowNodeDTO;
import me.aki.demo.camunda.enums.BpmnShape;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ExclusiveGatewayBuilder;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExclusiveGatewayFlowNodeDTO implements FlowNodeDTO {
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
    public void tidyUp() {
        if (id == null || !id.startsWith("exclusiveGateway_")) {
            id = "exclusiveGateway_" + UUID.randomUUID();
        }
    }

    @Override
    public ExclusiveGatewayBuilder build(AbstractFlowNodeBuilder<?, ?> builder) {
        return builder.exclusiveGateway().id(id).name(label);
    }
}