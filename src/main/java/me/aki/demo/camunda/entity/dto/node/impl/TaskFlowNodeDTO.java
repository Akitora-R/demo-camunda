package me.aki.demo.camunda.entity.dto.node.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.aki.demo.camunda.entity.dto.node.CombinationNodeDTO;
import me.aki.demo.camunda.enums.BpmnShape;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskFlowNodeDTO implements CombinationNodeDTO {
    private String id;
    @NotBlank
    @javax.validation.constraints.Pattern(regexp = "^(?!\\d)\\w+$", message = "code只可由字母数字以及下划线组成，并且不能由数字开头。")
    private String code;
    @JsonIgnore
    private String outgoingNodeId;
    private String label;
    private String assignee;

    @Override
    public String toString() {
        return String.format("[UserTask (%s - %s)]", code, label);
    }

    @Override
    public BpmnShape getShape() {
        return BpmnShape.TASK;
    }

    @Override
    public void tidyUp() {
        // TODO: 2022/7/4 应当用正则校验id规则
        if (id == null || !id.startsWith("userTask_")) {
            id = "userTask_" + UUID.randomUUID();
        }
        if (outgoingNodeId == null || !outgoingNodeId.startsWith("exclusiveGateway_")) {
            outgoingNodeId = "exclusiveGateway_" + UUID.randomUUID();
        }
    }

    @Override
    public AbstractFlowNodeBuilder<?, ?> build(AbstractFlowNodeBuilder<?, ?> builder) {
        String middleGatewayId = "exclusiveGateway_" + UUID.randomUUID();
        String varName = String.format("%s_approval", code);
        return builder
                .userTask().id(id).name(label).camundaAssignee(assignee).camundaOutputParameter(varName, "${approval}")
                .exclusiveGateway().id(middleGatewayId).condition("reject", String.format("${!%s}", varName))
                .endEvent()
                .moveToNode(middleGatewayId).condition("approve", String.format("${%s}", varName))
                .exclusiveGateway().id(outgoingNodeId);
    }

    @Override
    public String getIncomingNodeId() {
        return id;
    }
}
