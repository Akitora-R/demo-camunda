package me.aki.demo.camunda.entity.vo;

import lombok.Data;
import me.aki.demo.camunda.entity.VariableInst;
import org.camunda.bpm.engine.history.HistoricProcessInstance;

import java.util.List;

@Data
public class ProcInstVO {
    private HistoricProcessInstance camundaProcessInstance;
    private List<VariableInst> variableList;
    private List<TaskVO> taskList;
}
