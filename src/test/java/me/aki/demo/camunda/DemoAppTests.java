package me.aki.demo.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
class DemoAppTests {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    @Test
    void contextLoads() {
    }

    @Test
    void dep() {
        repositoryService.createDeployment()
                .addClasspathResource("demo_processes/generated_diagram_1.bpmn")
                .deploy();
    }

    @Test
    void listProcDef() {
        for (ProcessDefinition processDefinition : repositoryService.createProcessDefinitionQuery().active().latestVersion().list()) {
            log.info("{} {} {}", processDefinition.getId(), processDefinition.getKey(), processDefinition.getName());
        }
    }

    String bk = "some_bk2";

    @Test
    void startProc() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("b011d11d-fea1-11ec-962e-2c16dbac39c7", bk);
        System.out.println(processInstance.getId());
    }

    @Test
    void queryRunningInst() {
        for (Task task : taskService.createTaskQuery().active().list()) {
            log.info("{} {}", task.getId(), task.getName());
        }
    }

    @Test
    void completeTask() {
        String procInstId = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(bk).singleResult().getId();
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInstId).list();
        for (Task task : taskList) {
            taskService.complete(task.getId(), Map.of("approval", false));
        }
    }

    @Test
    void queryHistoryAct() {
        for (HistoricProcessInstance historicProcessInstance : historyService.createHistoricProcessInstanceQuery().completed().processInstanceBusinessKey(bk).list()) {
            log.info("{}", historicProcessInstance);
            for (HistoricActivityInstance historicActivityInstance : historyService.createHistoricActivityInstanceQuery().processInstanceId(historicProcessInstance.getId()).list()) {
                log.info("{} {}", historicActivityInstance.getActivityType(), historicActivityInstance);
            }
        }
    }

    @Test
    void queryHistoricVar() {
        for (HistoricProcessInstance historicProcessInstance : historyService.createHistoricProcessInstanceQuery().completed().processInstanceBusinessKey(bk).list()) {
            log.info("{}", historicProcessInstance);
            List<HistoricVariableInstance> varList = historyService.createHistoricVariableInstanceQuery().processInstanceId(historicProcessInstance.getId()).list();
            for (HistoricVariableInstance v : varList) {
                System.out.println(v);
            }
        }
    }

    @Test
    void history() {
        for (HistoricActivityInstance historicActivityInstance : historyService.createHistoricActivityInstanceQuery().processInstanceId("9891f7a6-d749-11ec-b3e0-2c16dbac39c7").list()) {
            System.out.println(historicActivityInstance);
            historicActivityInstance.getActivityType();
        }
    }
}
