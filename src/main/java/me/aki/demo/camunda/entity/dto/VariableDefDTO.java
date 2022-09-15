package me.aki.demo.camunda.entity.dto;

import lombok.Data;
import me.aki.demo.camunda.enums.VariableSourceType;

import java.util.List;

@Data
public class VariableDefDTO {
    private String variableKey;
    private VariableSourceType sourceType;
    private String sourceIdentifier;
    private Boolean required;
    private List<VariableDefProp> propList;

    @Data
    public static class VariableDefProp {
        private String key;
        private String val;
    }
}
