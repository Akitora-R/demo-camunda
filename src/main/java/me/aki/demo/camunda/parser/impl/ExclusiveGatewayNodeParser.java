package me.aki.demo.camunda.parser.impl;

import me.aki.demo.camunda.entity.bpmn.ExclusiveGatewayFlowNodeDTO;
import me.aki.demo.camunda.entity.bpmn.FlowNodeDTO;
import me.aki.demo.camunda.parser.NodeParser;
import org.camunda.bpm.model.bpmn.builder.ExclusiveGatewayBuilder;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;

public class ExclusiveGatewayNodeParser extends NodeParser<ExclusiveGatewayBuilder, ExclusiveGateway> {
    @Override
    protected FlowNodeDTO<ExclusiveGatewayBuilder, ExclusiveGateway> doConv(ExclusiveGateway node) {
        ExclusiveGatewayFlowNodeDTO dto = new ExclusiveGatewayFlowNodeDTO();
        dto.setId(node.getId());
        dto.setLabel(node.getName());
        return dto;
    }
}
