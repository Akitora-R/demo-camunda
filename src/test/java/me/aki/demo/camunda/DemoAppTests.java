package me.aki.demo.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
                .addClasspathResource("processes/generated_diagram_1.bpmn")
                .deploy();
    }

    @Test
    void listProcDef() {
        for (ProcessDefinition processDefinition : repositoryService.createProcessDefinitionQuery().active().latestVersion().list()) {
            log.info("{} {}",processDefinition);
        }
    }

    @Test
    void completeTask() {
        taskService.complete("4fbec3f6-d720-11ec-8bc5-2c16dbac39c7");
    }

    @Test
    void history() {
        for (HistoricActivityInstance historicActivityInstance : historyService.createHistoricActivityInstanceQuery().processInstanceId("9891f7a6-d749-11ec-b3e0-2c16dbac39c7").list()) {
            System.out.println(historicActivityInstance);
            historicActivityInstance.getActivityType();
        }
    }
}
