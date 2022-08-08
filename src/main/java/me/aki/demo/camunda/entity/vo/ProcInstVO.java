package me.aki.demo.camunda.entity.vo;

import lombok.Data;
import me.aki.demo.camunda.entity.FormInst;
import org.camunda.bpm.engine.history.HistoricProcessInstance;

@Data
public class ProcInstVO {
    private HistoricProcessInstance camundaProcessInstance;
    private FormInst formInst;
}
