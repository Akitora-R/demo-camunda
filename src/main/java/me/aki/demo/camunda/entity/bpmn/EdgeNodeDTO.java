package me.aki.demo.camunda.entity.bpmn;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EdgeNodeDTO implements NodeDTO {
    private final String id;
    private final String label;
    private final String condition;
    private final String source;
    private final String target;

    @Override
    public String toString() {
        return String.format("%s -> %s", source, target);
    }
}
