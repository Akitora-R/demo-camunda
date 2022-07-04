package me.aki.demo.camunda.entity.bpmn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aki.demo.camunda.entity.bpmn.impl.EdgeNodeDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeLink {
    private FlowNodeDTO prev;
    private EdgeNodeDTO edge;
    private FlowNodeDTO curr;

    public String getPrevOutgoingNodeId() {
        if (prev instanceof CombinationNodeDTO cPrev) {
            return cPrev.getOutgoingNodeId();
        }
        return prev.getId();
    }
}
