package me.aki.demo.camunda.parser.impl;

import me.aki.demo.camunda.entity.bpmn.impl.EndEventFlowNodeDTO;
import me.aki.demo.camunda.entity.bpmn.FlowNodeDTO;
import me.aki.demo.camunda.parser.NodeParser;
import org.camunda.bpm.model.bpmn.instance.EndEvent;

public class EndEventNodeParser extends NodeParser<EndEvent> {
    @Override
    protected FlowNodeDTO doConv(EndEvent node) {
        EndEventFlowNodeDTO dto = new EndEventFlowNodeDTO();
        dto.setId(node.getId());
        dto.setLabel(node.getName());
        return dto;
    }
}
