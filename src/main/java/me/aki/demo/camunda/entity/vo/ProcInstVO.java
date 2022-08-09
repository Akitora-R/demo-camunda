package me.aki.demo.camunda.entity.vo;

import lombok.Data;
import me.aki.demo.camunda.entity.FormInst;
import org.camunda.bpm.engine.history.HistoricProcessInstance;

import java.util.List;

@Data
public class ProcInstVO {
    private HistoricProcessInstance camundaProcessInstance;
    private FormInst formInst;
    private List<TaskVO> taskList;
}
