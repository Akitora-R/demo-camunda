package me.aki.demo.camunda.entity.bpmn;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NodeLink {
    private FlowNodeDTO<?, ?> prev;
    private EdgeNodeDTO edge;
    private FlowNodeDTO<?, ?> curr;
}
