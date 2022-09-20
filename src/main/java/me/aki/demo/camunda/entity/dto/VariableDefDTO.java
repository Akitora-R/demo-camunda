package me.aki.demo.camunda.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aki.demo.camunda.enums.VariableSourceType;

import java.util.List;

@Data
public class VariableDefDTO {
    private String variableKey;
    private VariableSourceType sourceType;
    private String sourceIdentifier;
    private Boolean required;
    private List<VariableDefPropDTO> propList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariableDefPropDTO {
        /**
         * id是为了方便遍历树
         */
        private String id;
        private String key;
        private String val;
        private List<VariableDefPropDTO> children;
    }
}
