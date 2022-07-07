package me.aki.demo.camunda.entity.dto.node;

public interface CombinationNodeDTO extends FlowNodeDTO {
    String getIncomingNodeId();
    String getOutgoingNodeId();
}
