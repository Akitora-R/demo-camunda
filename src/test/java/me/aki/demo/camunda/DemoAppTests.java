package me.aki.demo.camunda;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
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
        repositoryService.createDeployment().addClasspathResource("processes/diagram_2.bpmn").deploy();
    }

    @Test
    void completeTask(){
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
