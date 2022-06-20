package me.aki.demo.camunda.entity.bpmn;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.aki.demo.camunda.enums.ActivityType;
import me.aki.demo.camunda.enums.BpmnShape;

@Getter
@RequiredArgsConstructor
public class ActivityNode implements Node{
    private final String id;
    private final String label;
    private final ActivityType activityType;

    @Override
    public BpmnShape getShape() {
        return BpmnShape.ACTIVITY;
    }
}
