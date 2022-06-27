package me.aki.demo.camunda.parser.impl;

import me.aki.demo.camunda.entity.bpmn.FlowNodeDTO;
import me.aki.demo.camunda.entity.bpmn.UserTaskFlowNodeDTO;
import me.aki.demo.camunda.parser.NodeParser;
import org.camunda.bpm.model.bpmn.builder.UserTaskBuilder;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.springframework.stereotype.Component;

public class UserTaskNodeParser extends NodeParser<UserTaskBuilder, UserTask> {
    @Override
    protected FlowNodeDTO<UserTaskBuilder, UserTask> doConv(UserTask node) {
        UserTaskFlowNodeDTO dto = new UserTaskFlowNodeDTO();
        dto.setLabel(node.getName());
        dto.setId(node.getId());
        dto.setAssignee(node.getCamundaAssignee());
        return dto;
    }
}
