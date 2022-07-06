package me.aki.demo.camunda.controller;

import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.entity.dto.R;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@Slf4j
public class Api {
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public Api(RuntimeService runtimeService, TaskService taskService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @GetMapping("/task/complete/{id}")
    public R<?> completeTask(@PathVariable("id") String id, @RequestParam Boolean approval) {
        taskService.setVariable(id,"approval",approval);
        taskService.complete(id);
        return R.ok(null);
    }

    @PostMapping("/def/upload")
    public R<?> uploadDef(MultipartFile file) {

        return R.ok(null);
    }

    @GetMapping("/inst/start/{id}")
    public R<String> startProc(@PathVariable("id") String id) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(id, UUID.randomUUID().toString());
        return R.ok(processInstance.getRootProcessInstanceId());
    }

    @DeleteMapping("/inst/del/{id}")
    public R<String> delProc(@PathVariable("id") String id) {
        runtimeService.deleteProcessInstance(id, null);
        log.info("删除 {}", id);
        return R.ok(null);
    }
}
