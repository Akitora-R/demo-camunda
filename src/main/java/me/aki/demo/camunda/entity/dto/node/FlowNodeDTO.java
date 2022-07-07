package me.aki.demo.camunda.entity.dto.node;

import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;

public interface FlowNodeDTO extends NodeDTO {
    AbstractFlowNodeBuilder<?, ?> build(AbstractFlowNodeBuilder<?, ?> builder);

}
