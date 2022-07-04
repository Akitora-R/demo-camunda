package me.aki.demo.camunda.parser.impl;

import me.aki.demo.camunda.entity.bpmn.impl.ExclusiveGatewayFlowNodeDTO;
import me.aki.demo.camunda.entity.bpmn.FlowNodeDTO;
import me.aki.demo.camunda.parser.NodeParser;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;

public class ExclusiveGatewayNodeParser extends NodeParser<ExclusiveGateway> {
    @Override
    protected FlowNodeDTO doConv(ExclusiveGateway node) {
        ExclusiveGatewayFlowNodeDTO dto = new ExclusiveGatewayFlowNodeDTO();
        dto.setId(node.getId());
        dto.setLabel(node.getName());
        return dto;
    }
}
