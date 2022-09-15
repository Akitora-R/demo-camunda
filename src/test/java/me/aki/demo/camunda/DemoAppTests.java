package me.aki.demo.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.repository.DeploymentWithDefinitions;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashMap;
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
        String time = LocalDateTime.now().toString();
        DeploymentWithDefinitions result = repositoryService.createDeployment()
                .addModelInstance(time + ".bpmn", BpmnTests.genCountersignatureBpm())
                .name("test bpmn when " + time)
                .deployWithResult();
        for (ProcessDefinition definition : result.getDeployedProcessDefinitions()) {
            System.out.printf("deployed: id - %s key - %s\n", definition.getId(), definition.getKey());
        }
    }

    @Test
    void listProcDef() {
        for (ProcessDefinition processDefinition : repositoryService.createProcessDefinitionQuery().latestVersion().list()) {
            log.info("{} {} {}", processDefinition.getId(), processDefinition.getKey(), processDefinition.getName());
        }
    }

    String bk = "multiTaskInst";

    @Test
    void startProc() {
        HashMap<String, Object> m = new HashMap<>();
        m.put("chargerAssigneeList", List.of("1", "3", "5"));
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("64bdd6a0-2f52-11ed-8687-2c16dbac39c7", bk, m);
        System.out.println(processInstance.getId());
    }

    @Test
    void queryRunningTask() {
        for (Task task : taskService.createTaskQuery().active().list()) {
            log.info("id: {} name: {} Assignee: {}", task.getId(), task.getName(), task.getAssignee());
        }
    }

    @Test
    void completeTask() {
        Map<String, Object> param = Map.of("chargerApproval", false);
        taskService.complete("7de1fee9-2f52-11ed-aa2b-2c16dbac39c7", param);
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
        for (HistoricProcessInstance historicProcessInstance : historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(bk).list()) {
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
