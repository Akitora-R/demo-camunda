package me.aki.demo.camunda.entity.dto;

import lombok.Data;

@Data
public class TaskDTO {
    private Boolean approval;
    private String comment;
}
