package me.aki.demo.camunda.entity.dto.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.aki.demo.camunda.entity.dto.FlowNodeDTO;
import me.aki.demo.camunda.enums.BpmnShape;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.EndEventBuilder;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EndEventFlowNodeDTO implements FlowNodeDTO {
    private String id;
    private String label;

    @Override
    public String toString() {
        return String.format("(EndEvent %s(%s))", id, label);
    }

    @Override
    public BpmnShape getShape() {
        return BpmnShape.END_EVENT;
    }

    @Override
    public void tidyUp() {
        if (id == null || !id.startsWith("endEvent_")) {
            id = "endEvent_" + UUID.randomUUID();
        }
    }

    @Override
    public EndEventBuilder build(AbstractFlowNodeBuilder<?, ?> builder) {
        return builder.endEvent().id(id).name(label);
    }
}
