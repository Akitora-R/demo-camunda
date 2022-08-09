package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "流程实例任务属性")
@TableName("WF_PROC_INST_TASK_PROP")
public class ProcInstTaskProp extends BaseEntity {
    /**
     * ID
     */
    @ApiModelProperty(name = "ID")
    @TableId
    private String id;
    /**
     * 流程定义ID
     */
    @ApiModelProperty(name = "流程定义ID")
    private String procDefId;
    /**
     * 流程定义节点ID
     */
    @ApiModelProperty(name = "流程定义节点ID")
    private String procDefNodeId;
    /**
     * camunda流程实例ID
     */
    @ApiModelProperty(name = "camunda流程实例ID")
    private String camundaProcInstId;
    /**
     * camunda任务实例ID
     */
    @ApiModelProperty(name = "camunda任务实例ID")
    private String camundaTaskInstId;
    /**
     * 属性KEY
     */
    @ApiModelProperty(name = "属性KEY")
    private String propKey;
    /**
     * 属性值
     */
    @ApiModelProperty(name = "属性值")
    private String propVal;
}
