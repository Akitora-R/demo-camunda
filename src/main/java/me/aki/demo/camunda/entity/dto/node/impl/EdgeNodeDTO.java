package me.aki.demo.camunda.entity.dto.node.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.aki.demo.camunda.entity.dto.node.NodeDTO;
import me.aki.demo.camunda.enums.BpmnShape;

import java.util.Objects;

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

    @Override
    public String toString() {
        return String.format("%s -> %s", source, target);
    }

    @Override
    public BpmnShape getShape() {
        return BpmnShape.EDGE;
    }

    @Override
    public void tidyUp() {

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
