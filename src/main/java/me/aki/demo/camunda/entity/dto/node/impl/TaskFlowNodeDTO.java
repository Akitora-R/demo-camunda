package me.aki.demo.camunda.entity.dto.node.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.constant.IdPattern;
import me.aki.demo.camunda.entity.dto.ProcDefVariableDTO;
import me.aki.demo.camunda.entity.dto.node.CombinationNodeDTO;
import me.aki.demo.camunda.enums.JsonNodeShape;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class TaskFlowNodeDTO implements CombinationNodeDTO {
    @NotBlank
    private String id;
    @NotBlank
    @Pattern(regexp = "^(?!\\d)\\w+$", message = "code只可由字母数字以及下划线组成，并且不能由数字开头。")
    private String code;
    @JsonIgnore
    private String outgoingNodeId;
    @JsonIgnore
    private String incomingNodeId;
    private String label;
    private String assignee;
    private List<ProcDefVariableDTO> variableList;

    @Override
    public String toString() {
        return String.format("[UserTask (%s - %s)]", code, label);
    }

    @Override
    public JsonNodeShape getShape() {
        return JsonNodeShape.TASK;
    }

    /**
     * json id并不需要进行校验
     */
    @Override
    public void tidyUp(BiConsumer<String, String> onIdChange) {
        if (incomingNodeId == null || !IdPattern.TASK_PATTERN.matcher(incomingNodeId).matches()) {
            String newId = IdPattern.TASK_PREFIX + UUID.randomUUID();
            log.warn(IdPattern.WARN_MSG_TEMPLATE.apply(IdPattern.Msg.of(this.getClass(), incomingNodeId, newId)));
//            onIdChange.accept(id, newId);
            incomingNodeId = newId;
        }
        if (outgoingNodeId == null || !IdPattern.EXCLUSIVE_GATEWAY_PATTERN.matcher(outgoingNodeId).matches()) {
            String newId = IdPattern.EXCLUSIVE_GATEWAY_PREFIX + UUID.randomUUID();
            log.warn(IdPattern.WARN_MSG_TEMPLATE.apply(IdPattern.Msg.of(this.getClass(), outgoingNodeId, newId)));
//            onIdChange.accept(id, newId);
            outgoingNodeId = newId;
        }
    }

    @Override
    public AbstractFlowNodeBuilder<?, ?> build(AbstractFlowNodeBuilder<?, ?> builder) {
        String middleGatewayId = "exclusiveGateway_" + UUID.randomUUID();
        String varName = String.format("%s_approval", code);
        return builder
                .userTask().id(incomingNodeId).name(label).camundaAssignee(assignee).camundaOutputParameter(varName, "${approval}")
                .exclusiveGateway().id(middleGatewayId).condition("reject", String.format("${!%s}", varName))
                .endEvent()
                .moveToNode(middleGatewayId).condition("approve", String.format("${%s}", varName))
                .exclusiveGateway().id(outgoingNodeId);
    }
}
