package me.aki.demo.camunda.entity.bpmn;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.aki.demo.camunda.enums.BpmnShape;
import me.aki.demo.camunda.enums.EventType;

@RequiredArgsConstructor
@Getter
public class EventNode implements Node {
    private final String id;
    private final String label;
    private final EventType eventType;

    @Override
    public BpmnShape getShape() {
        return BpmnShape.EVENT;
    }
}
