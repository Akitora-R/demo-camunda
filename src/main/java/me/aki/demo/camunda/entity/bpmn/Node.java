package me.aki.demo.camunda.entity.bpmn;

import me.aki.demo.camunda.enums.BpmnShape;

public interface Node {
    String getId();
    BpmnShape getShape();
    String getLabel();
}
