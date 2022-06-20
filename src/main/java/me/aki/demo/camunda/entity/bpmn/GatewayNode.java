package me.aki.demo.camunda.entity.bpmn;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.aki.demo.camunda.enums.BpmnShape;

@RequiredArgsConstructor
@Getter
public class GatewayNode implements Node{
    private final String id;
    private final String label;

    @Override
    public BpmnShape getShape() {
        return BpmnShape.GATEWAY;
    }
}
