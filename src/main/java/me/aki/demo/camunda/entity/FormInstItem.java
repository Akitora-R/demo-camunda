package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("WF_FORM_INST_ITEM")
public class FormInstItem extends BaseEntity {
    /**
     * ID
     */
    @ApiModelProperty(name = "ID")
    @TableId
    private String id;
    /**
     * 表单实例ID
     */
    @ApiModelProperty(name = "表单实例ID")
    private String formInstId;
    /**
     * 表单组件定义ID
     */
    @ApiModelProperty(name = "表单组件定义ID")
    private String formItemId;
    /**
     * 表单组件值
     */
    @ApiModelProperty(name = "表单组件值")
    private String formItemVal;
}
