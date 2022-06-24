package me.aki.demo.camunda.entity.bpmn;

import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.instance.FlowNode;

public interface FlowNodeDTO<B extends AbstractFlowNodeBuilder<B, E>, E extends FlowNode> extends NodeDTO {
    B build(AbstractFlowNodeBuilder<?, ?> builder);
}
