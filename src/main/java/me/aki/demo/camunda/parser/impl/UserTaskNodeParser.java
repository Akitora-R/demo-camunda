package me.aki.demo.camunda.parser.impl;

import me.aki.demo.camunda.entity.bpmn.FlowNodeDTO;
import me.aki.demo.camunda.entity.bpmn.impl.TaskFlowNodeDTO;
import me.aki.demo.camunda.parser.NodeParser;
import org.camunda.bpm.model.bpmn.instance.UserTask;

public class UserTaskNodeParser extends NodeParser<UserTask> {
    @Override
    protected FlowNodeDTO doConv(UserTask node) {
        TaskFlowNodeDTO dto = new TaskFlowNodeDTO();
        dto.setLabel(node.getName());
        dto.setId(node.getId());
        dto.setAssignee(node.getCamundaAssignee());
        return dto;
    }
}
