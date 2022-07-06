package me.aki.demo.camunda.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;

import java.util.List;

@Data
@AllArgsConstructor
public class HistoricTaskInstanceVO {
    private HistoricTaskInstance historicTaskInstance;
    private List<HistoricVariableInstance> varList;
}
