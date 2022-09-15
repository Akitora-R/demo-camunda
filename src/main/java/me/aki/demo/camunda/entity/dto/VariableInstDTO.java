package me.aki.demo.camunda.entity.dto;

import lombok.Data;
import me.aki.demo.camunda.enums.VariableSourceType;

@Data
public class VariableInstDTO {
    String sourceIdentifier;
    VariableSourceType sourceType;
    String val;
}
