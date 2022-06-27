package me.aki.demo.camunda.parser.impl;

import me.aki.demo.camunda.entity.bpmn.EndEventFlowNodeDTO;
import me.aki.demo.camunda.entity.bpmn.FlowNodeDTO;
import me.aki.demo.camunda.parser.NodeParser;
import org.camunda.bpm.model.bpmn.builder.EndEventBuilder;
import org.camunda.bpm.model.bpmn.instance.EndEvent;

public class EndEventNodeParser extends NodeParser<EndEventBuilder, EndEvent> {
    @Override
    protected FlowNodeDTO<EndEventBuilder, EndEvent> doConv(EndEvent node) {
        EndEventFlowNodeDTO dto = new EndEventFlowNodeDTO();
        dto.setId(node.getId());
        dto.setLabel(node.getName());
        return dto;
    }
}
