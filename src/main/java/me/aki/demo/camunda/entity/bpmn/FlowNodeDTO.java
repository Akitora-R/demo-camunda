package me.aki.demo.camunda.entity.bpmn;

import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.instance.FlowNode;

public interface FlowNodeDTO extends NodeDTO {
    AbstractFlowNodeBuilder<?, ?> build(AbstractFlowNodeBuilder<?, ?> builder);

}
