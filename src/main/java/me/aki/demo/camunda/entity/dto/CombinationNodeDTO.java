package me.aki.demo.camunda.entity.dto;

public interface CombinationNodeDTO extends FlowNodeDTO {
    String getIncomingNodeId();
    String getOutgoingNodeId();
}
