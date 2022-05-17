package me.aki.demo.camunda.controller;

import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.entity.R;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
public class Index {
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;

    public Index(RepositoryService repositoryService, RuntimeService runtimeService) {
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
    }

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("word", "麻了");
        return "index";
    }

    @RequestMapping("/task")
    public String taskList() {
        return "task";
    }

    @RequestMapping("/inst")
    public String instanceList(Model model) {
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().active().list();
        model.addAttribute("processInstanceList",processInstanceList);
        return "inst";
    }

    @RequestMapping("/def")
    public String definitionList(Model model) {
        List<ProcessDefinition> pd = repositoryService.createProcessDefinitionQuery().latestVersion().active().list();
        model.addAttribute("pdList", pd);
        return "def";
    }

    @RequestMapping("/inst/start/{id}")
    @ResponseBody
    public R<String> startProc(@PathVariable("id") String id) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(id, UUID.randomUUID().toString());
        return R.ok(processInstance.getRootProcessInstanceId());
    }
}
