package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("WF_PROC_DEF_NODE_ELEMENT_REL_INFO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProcDefNodeElementRelInfo extends BaseEntity {
    /**
     * ID
     */
    @ApiModelProperty(name = "ID")
    @TableId
    private String id;
    /**
     * 流程定义节点ID
     */
    @ApiModelProperty(name = "流程定义节点ID")
    private String procDefNodeId;
    /**
     * camunda BPMN 元素ID
     */
    @ApiModelProperty(name = "camunda BPMN 元素ID")
    private String camundaElementId;
}
