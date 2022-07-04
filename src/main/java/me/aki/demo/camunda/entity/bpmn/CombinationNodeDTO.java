package me.aki.demo.camunda.entity.bpmn;

public interface CombinationNodeDTO extends FlowNodeDTO {
    String getIncomingNodeId();
    String getOutgoingNodeId();
}
