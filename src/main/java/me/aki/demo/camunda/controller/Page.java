package me.aki.demo.camunda.controller;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
public class Page {
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public Page(RepositoryService repositoryService, RuntimeService runtimeService, TaskService taskService) {
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("word", "麻了");
        return "index";
    }

    @RequestMapping("/task")
    public String taskList(Model model) {
        List<Task> taskList = taskService.createTaskQuery().active().list();
        model.addAttribute("taskList", taskList);
        return "task";
    }

    @RequestMapping("/inst")
    public String instanceList(Model model) {
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().active().list();
        model.addAttribute("processInstanceList", processInstanceList);
        return "inst";
    }

    @RequestMapping("/def")
    public String definitionList(Model model) {
        List<ProcessDefinition> pd = repositoryService.createProcessDefinitionQuery().latestVersion().active().list();
        model.addAttribute("pdList", pd);
        return "def";
    }
}
