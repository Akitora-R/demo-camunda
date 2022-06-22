package me.aki.demo.camunda.entity.bpmn;

import me.aki.demo.camunda.enums.BpmnShape;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.instance.FlowNode;

public interface FlowNodeDTO<B extends AbstractFlowNodeBuilder<B, E>, E extends FlowNode> extends NodeDTO {
    BpmnShape getShape();
    B build(AbstractFlowNodeBuilder<?, ?> builder);
}
