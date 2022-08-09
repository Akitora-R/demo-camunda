package me.aki.demo.camunda.entity.vo;

import lombok.Data;
import org.camunda.bpm.engine.history.HistoricTaskInstance;

@Data
public class TaskVO {
    private HistoricTaskInstance camundaTask;
    private Boolean approval;
    private String comment;

    public TaskVO(HistoricTaskInstance camundaTask, Boolean approval, String comment) {
        this.camundaTask = camundaTask;
        this.approval = approval;
        this.comment = comment;
    }
}
