package me.aki.demo.camunda.entity.dto;

import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;

public interface FlowNodeDTO extends NodeDTO {
    AbstractFlowNodeBuilder<?, ?> build(AbstractFlowNodeBuilder<?, ?> builder);

}
