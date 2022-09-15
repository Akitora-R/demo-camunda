package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "变量实例")
@TableName("WF_VARIABLE_INST")
public class VariableInst extends BaseEntity {
    /**
     * ID
     */
    @ApiModelProperty(name = "ID")
    @TableId
    private String id;
    /**
     * Camunda流程实例ID
     */
    @ApiModelProperty(name = "Camunda流程实例ID")
    private String camundaProcInstId;
    /**
     * 变量定义ID
     */
    @ApiModelProperty(name = "变量定义ID")
    private String variableDefId;
    /**
     * 变量实例值
     */
    @ApiModelProperty(name = "变量实例值")
    private String variableVal;
}
