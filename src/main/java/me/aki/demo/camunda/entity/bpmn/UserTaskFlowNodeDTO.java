package me.aki.demo.camunda.entity.bpmn;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.aki.demo.camunda.enums.BpmnShape;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.UserTaskBuilder;
import org.camunda.bpm.model.bpmn.instance.UserTask;

@Getter
@Setter
@RequiredArgsConstructor
public class UserTaskFlowNodeDTO implements FlowNodeDTO<UserTaskBuilder, UserTask> {
    private final String id;
    private final String label;
    private String assignee;

    @Override
    public String toString() {
        return String.format("[UserTask %s(%s)]", id, label);
    }

    @Override
    public BpmnShape getShape() {
        return BpmnShape.USER_TASK;
    }

    @Override
    public UserTaskBuilder build(AbstractFlowNodeBuilder<?, ?> builder) {
        return builder.userTask().id(id).name(label).camundaAssignee(assignee);
    }
}
