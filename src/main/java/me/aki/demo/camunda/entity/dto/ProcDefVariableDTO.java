package me.aki.demo.camunda.entity.dto;

import lombok.Data;
import me.aki.demo.camunda.enums.VariableSourceType;

@Data
public class ProcDefVariableDTO {
    private String variableKey;
    private VariableSourceType sourceType;
    private String sourceIdentifier;
}
