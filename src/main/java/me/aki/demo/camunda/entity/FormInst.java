package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "表单实例")
@TableName("WF_FORM_INST")
public class FormInst extends BaseEntity {
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
     * 表单定义ID
     */
    @ApiModelProperty(name = "表单定义ID")
    private String formDefId;
    /**
     * Camunda流程实例ID
     */
    @ApiModelProperty(name = "Camunda流程实例ID")
    private String camundaProcInstId;
    /**
     * Camunda流程实例BK
     */
    @ApiModelProperty(name = "Camunda流程实例BK")
    private String camundaProcInstBusinessKey;
}
