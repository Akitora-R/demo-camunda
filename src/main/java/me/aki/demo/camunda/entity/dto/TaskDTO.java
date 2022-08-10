package me.aki.demo.camunda.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>流程任务实例的POJO类</p>
 * <p>
 *     针对流程实例中的各种task节点，目前数据库中只持久化了两个固定的属性，分别为 <strong>approval</strong> 和
 *     <strong>comment</strong>，最终的设计目标应当是对应每个task节点提供定制表单的能力，和流程定义一样。
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
     * 表单数据，nullable
     */
    private TaskFormInstDTO form;

    @Data
    public static class TaskFormInstDTO {
        private List<TaskFormInstItemDTO> itemList;
        @Data
        public static class TaskFormInstItemDTO {
            private String formItemKey;
            private String formItemVal;
        }
    }
}
