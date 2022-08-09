package me.aki.demo.camunda.service;

import org.camunda.bpm.engine.TaskService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WorkflowTaskService {
    private final TaskService taskService;

    public WorkflowTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void completeTask(String taskId, Boolean approval, String comment) {
        taskService.complete(taskId, Map.of("approval", approval, "comment", comment));
    }
}
