package me.aki.demo.camunda.entity.vo;

import lombok.Data;
import me.aki.demo.camunda.entity.ProcDef;
import org.camunda.bpm.engine.repository.ProcessDefinition;

@Data
public class ProcDefVO {
    private ProcDef procDef;
    private FormDefVO formDefVO;
    private ProcessDefinition camundaProcessDefinition;
}
