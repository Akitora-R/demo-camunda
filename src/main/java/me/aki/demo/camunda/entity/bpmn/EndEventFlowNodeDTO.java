package me.aki.demo.camunda.entity.bpmn;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.aki.demo.camunda.enums.BpmnShape;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.EndEventBuilder;
import org.camunda.bpm.model.bpmn.instance.EndEvent;

@RequiredArgsConstructor
@Getter
public class EndEventFlowNodeDTO implements FlowNodeDTO<EndEventBuilder, EndEvent> {
    private final String id;
    private final String label;

    @Override
    public String toString() {
        return String.format("(EndEvent %s(%s))", id, label);
    }

    @Override
    public BpmnShape getShape() {
        return BpmnShape.END_EVENT;
    }

    @Override
    public EndEventBuilder build(AbstractFlowNodeBuilder<?, ?> builder) {
        return builder.endEvent().id(id).name(label);
    }
}
