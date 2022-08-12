package me.aki.demo.camunda.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aki.demo.camunda.entity.ProcInstTaskProp;
import org.camunda.bpm.engine.history.HistoricTaskInstance;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskVO {
    private HistoricTaskInstance camundaTask;
    private List<ProcInstTaskProp> propList;
}
