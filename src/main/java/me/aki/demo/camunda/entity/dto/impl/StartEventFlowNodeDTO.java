package me.aki.demo.camunda.entity.dto.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.aki.demo.camunda.entity.dto.FlowNodeDTO;
import me.aki.demo.camunda.enums.BpmnShape;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.StartEventBuilder;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StartEventFlowNodeDTO implements FlowNodeDTO {
    private String id;
    private String label;

    @Override
    public String toString() {
        return String.format("(StartEvent %s(%s))", id, label);
    }

    @Override
    public BpmnShape getShape() {
        return BpmnShape.START_EVENT;
    }

    @Override
    public void tidyUp() {
        if (id == null || !id.startsWith("startEvent_")) {
            id = "startEvent_" + UUID.randomUUID();
        }
    }

    @Override
    public StartEventBuilder build(AbstractFlowNodeBuilder<?, ?> builder) {
        assert builder instanceof StartEventBuilder;
        return ((StartEventBuilder) builder).id(id).name(label);
    }
}
