package me.aki.demo.camunda.entity.vo;

import lombok.Data;
import me.aki.demo.camunda.entity.FormInst;
import org.camunda.bpm.engine.runtime.ProcessInstance;

@Data
public class ProcInstVO {
    private ProcessInstance camundaProcessInstance;
    private FormInst formInst;
}
