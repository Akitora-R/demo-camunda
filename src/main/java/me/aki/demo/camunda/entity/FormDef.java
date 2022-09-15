package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("WF_FORM_DEF")
public class FormDef extends BaseEntity {
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
     * 流程定义节点ID，如果此项不为null，则表明该表单关联节点
     */
    @ApiModelProperty(name = "流程定义节点ID")
    private String procDefNodeId;
    /**
     * 表单标题
     */
    @ApiModelProperty(name = "表单标题")
    @NotBlank
    private String title;

}
