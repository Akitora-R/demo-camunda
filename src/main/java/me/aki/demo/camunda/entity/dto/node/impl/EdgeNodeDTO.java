package me.aki.demo.camunda.entity.dto.node.impl;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.aki.demo.camunda.constant.IdPattern;
import me.aki.demo.camunda.entity.dto.VariableDefDTO;
import me.aki.demo.camunda.entity.dto.node.NodeDTO;
import me.aki.demo.camunda.enums.JsonNodeShape;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EdgeNodeDTO implements NodeDTO {
    private String id;
    private String label;
    private String condition;
    private String source;
    private String target;
    private List<VariableDefDTO> variableList;

    @Override
    public String toString() {
        return String.format("%s -> %s", source, target);
    }

    @Override
    public JsonNodeShape getShape() {
        return JsonNodeShape.EDGE;
    }

    @Override
    public void tidyUp(BiConsumer<String, String> onIdChange) {
        if (StrUtil.isBlank(id)) {
            id = IdPattern.EDGE_PREFIX + UUID.randomUUID();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeNodeDTO that = (EdgeNodeDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(label, that.label) && Objects.equals(condition, that.condition) && Objects.equals(source, that.source) && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, condition, source, target);
    }
}
