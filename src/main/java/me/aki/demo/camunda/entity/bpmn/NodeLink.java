package me.aki.demo.camunda.entity.bpmn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeLink {
    private FlowNodeDTO<?, ?> prev;
    private EdgeNodeDTO edge;
    private FlowNodeDTO<?, ?> curr;
}
