package me.aki.demo.camunda.entity.bpmn;

import lombok.*;
import me.aki.demo.camunda.enums.BpmnShape;

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
}
