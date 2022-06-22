package me.aki.demo.camunda.entity.bpmn;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.aki.demo.camunda.enums.BpmnShape;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.StartEventBuilder;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

@RequiredArgsConstructor
@Getter
public class StartEventFlowNodeDTO implements FlowNodeDTO<StartEventBuilder, StartEvent> {
    private final String id;
    private final String label;

    @Override
    public String toString() {
        return String.format("(StartEvent %s(%s))", id, label);
    }

    @Override
    public BpmnShape getShape() {
        return BpmnShape.START_EVENT;
    }

    @Override
    public StartEventBuilder build(AbstractFlowNodeBuilder<?, ?> builder) {
        assert builder instanceof StartEventBuilder;
        return ((StartEventBuilder) builder).id(id).name(label);
    }
}
