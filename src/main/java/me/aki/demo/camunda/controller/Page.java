package me.aki.demo.camunda.controller;

import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.entity.HistoricProcessInstanceVO;
import me.aki.demo.camunda.entity.HistoricTaskInstanceVO;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class Page {
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    public Page(RepositoryService repositoryService, RuntimeService runtimeService, TaskService taskService, HistoryService historyService) {
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
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

    @RequestMapping("/his")
    public String historyList(Model model) {
        List<HistoricProcessInstanceVO> voList = historyService.createHistoricProcessInstanceQuery().list().stream().map(hpi -> {
            List<HistoricVariableInstance> varList = historyService.createHistoricVariableInstanceQuery().processInstanceId(hpi.getId()).list();
            List<HistoricActivityInstance> actList = historyService.createHistoricActivityInstanceQuery().processInstanceId(hpi.getId()).list();
            List<HistoricTaskInstanceVO> taskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(hpi.getId()).list().stream().map(t->{
                List<HistoricVariableInstance> tvList = historyService.createHistoricVariableInstanceQuery().taskIdIn(t.getId()).list();
                return new HistoricTaskInstanceVO(t,tvList);
            }).toList();
            return new HistoricProcessInstanceVO(hpi, varList, actList, taskList);
        }).toList();
        model.addAttribute("voList",voList);
        return "his";
    }
}
