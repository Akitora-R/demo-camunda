package me.aki.demo.camunda.parser;

import me.aki.demo.camunda.entity.Pair;
import me.aki.demo.camunda.entity.bpmn.FlowNodeDTO;
import me.aki.demo.camunda.enums.FlowDirection;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import java.util.Collection;
import java.util.Map;

public abstract class NodeParser<B extends AbstractFlowNodeBuilder<B, E>, E extends FlowNode> {
    public Pair<Map<FlowDirection, Collection<SequenceFlow>>, FlowNodeDTO<B, E>> toDTO(FlowNode node) {
        E e = conv(node);
        return Pair.Of(getFlowInfo(e), doConv(e));
    }

    private Map<FlowDirection, Collection<SequenceFlow>> getFlowInfo(E node) {
        return Map.of(FlowDirection.IN, node.getIncoming(), FlowDirection.OUT, node.getOutgoing());
    }

    @SuppressWarnings("unchecked")
    private E conv(FlowNode node) {
        return (E) node;
    }

    abstract protected FlowNodeDTO<B, E> doConv(E node);
}
