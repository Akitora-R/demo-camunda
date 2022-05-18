package me.aki.demo.camunda.controller;

import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.entity.R;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
public class Api {
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public Api(RepositoryService repositoryService, RuntimeService runtimeService, TaskService taskService) {
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @RequestMapping("/task/complete/{id}")
    public R<?> completeTask(@PathVariable("id") String id, @RequestParam Boolean approval) {
        taskService.complete(id, Map.of("approval", approval));
        return R.ok(null);
    }

    @RequestMapping("/def/upload")
    public R<?> uploadDef(MultipartFile file) {

        return R.ok(null);
    }

    @RequestMapping("/inst/start/{id}")
    public R<String> startProc(@PathVariable("id") String id) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(id, UUID.randomUUID().toString());
        return R.ok(processInstance.getRootProcessInstanceId());
    }

    @RequestMapping("/inst/del/{id}")
    public R<String> delProc(@PathVariable("id") String id) {
        runtimeService.deleteProcessInstance(id, null);
        log.info("删除 {}", id);
        return R.ok(null);
    }
}
