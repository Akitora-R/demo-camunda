package me.aki.demo.camunda.entity.vo;

import lombok.Data;
import org.camunda.bpm.engine.task.Task;

@Data
public class TaskVO {
    private Task camundaTask;

}
