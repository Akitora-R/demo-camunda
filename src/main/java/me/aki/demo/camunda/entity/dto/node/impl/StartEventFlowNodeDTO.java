package me.aki.demo.camunda.entity.dto.node.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.constant.IdPattern;
import me.aki.demo.camunda.entity.dto.VariableDefDTO;
import me.aki.demo.camunda.entity.dto.node.FlowNodeDTO;
import me.aki.demo.camunda.enums.JsonNodeShape;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.StartEventBuilder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class StartEventFlowNodeDTO implements FlowNodeDTO {
    private String id;
    private String label;
    private List<VariableDefDTO> variableList;

    @Override
    public String toString() {
        return String.format("(StartEvent %s(%s))", id, label);
    }

    @Override
    public JsonNodeShape getShape() {
        return JsonNodeShape.START_EVENT;
    }

    @Override
    public void tidyUp(BiConsumer<String, String> onIdChange) {
        if (id == null || !IdPattern.START_EVENT_PATTERN.matcher(id).matches()) {
            String newId = IdPattern.START_EVENT_PREFIX + UUID.randomUUID();
            log.warn(IdPattern.WARN_MSG_TEMPLATE.apply(IdPattern.Msg.of(this.getClass(), id, newId)));
            onIdChange.accept(id, newId);
            id = newId;
        }
    }

    @Override
    public List<String> build(AbstractFlowNodeBuilder<?, ?> builder) {
        assert builder instanceof StartEventBuilder;
        ((StartEventBuilder) builder).id(id).name(label);
        return Collections.singletonList(id);
    }
}
