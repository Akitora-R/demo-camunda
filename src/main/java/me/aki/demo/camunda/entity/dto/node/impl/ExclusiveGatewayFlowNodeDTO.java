package me.aki.demo.camunda.entity.dto.node.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.aki.demo.camunda.constant.IdPattern;
import me.aki.demo.camunda.entity.dto.ProcDefVariableDTO;
import me.aki.demo.camunda.entity.dto.node.FlowNodeDTO;
import me.aki.demo.camunda.enums.JsonNodeShape;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExclusiveGatewayFlowNodeDTO implements FlowNodeDTO {
    private String id;
    private String label;
    private List<ProcDefVariableDTO> variableList;

    @Override
    public String toString() {
        return String.format("<ExclusiveGateway %s(%s)>", id, label);
    }

    @Override
    public JsonNodeShape getShape() {
        return JsonNodeShape.EXCLUSIVE_GATEWAY;
    }

    @Override
    public void tidyUp(BiConsumer<String, String> onIdChange) {
        if (id == null || !IdPattern.EXCLUSIVE_GATEWAY_PATTERN.matcher(id).matches()) {
            String newId = IdPattern.EXCLUSIVE_GATEWAY_PREFIX + UUID.randomUUID();
            onIdChange.accept(id, newId);
            id = newId;
        }
    }

    @Override
    public List<String> build(AbstractFlowNodeBuilder<?, ?> builder) {
        builder.exclusiveGateway().id(id).name(label);
        return Collections.singletonList(id);
    }
}
