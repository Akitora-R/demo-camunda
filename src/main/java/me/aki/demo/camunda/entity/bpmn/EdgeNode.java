package me.aki.demo.camunda.entity.bpmn;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.aki.demo.camunda.enums.BpmnShape;

@RequiredArgsConstructor
@Getter
public class EdgeNode implements Node {
    private final String id;
    private final String label;
    private final String source;
    private final String target;

    @Override
    public BpmnShape getShape() {
        return BpmnShape.EDGE;
    }
}
