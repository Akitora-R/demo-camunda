package me.aki.demo.camunda.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>流程任务实例的POJO类</p>
 * <p>
 *     和流程一样，每个任务都可以有各自的表单。
 * </p>
 */
@Data
public class TaskDTO {
    /**
     * 即 camunda 中的 taskId
     */
    @NotBlank
    private String taskId;
    /**
     * 表示变量数据的列表
     */
    private List<VariableInstDTO> variableList;

}
