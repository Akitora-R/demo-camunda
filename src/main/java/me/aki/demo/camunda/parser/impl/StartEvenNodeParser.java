package me.aki.demo.camunda.parser.impl;

import me.aki.demo.camunda.entity.bpmn.FlowNodeDTO;
import me.aki.demo.camunda.entity.bpmn.impl.StartEventFlowNodeDTO;
import me.aki.demo.camunda.parser.NodeParser;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

public class StartEvenNodeParser extends NodeParser<StartEvent> {
    @Override
    protected FlowNodeDTO doConv(StartEvent node) {
        StartEventFlowNodeDTO dto = new StartEventFlowNodeDTO();
        dto.setId(node.getId());
        dto.setLabel(node.getName());
        return dto;
    }
}
