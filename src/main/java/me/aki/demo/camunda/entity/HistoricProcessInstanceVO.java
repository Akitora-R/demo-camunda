package me.aki.demo.camunda.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;

import java.util.List;

@Data
@AllArgsConstructor
public class HistoricProcessInstanceVO {
    HistoricProcessInstance processInstance;
    List<HistoricVariableInstance> varList;
    List<HistoricActivityInstance> actList;
    List<HistoricTaskInstanceVO> taskList;
}
