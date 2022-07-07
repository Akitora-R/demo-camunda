package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("WF_FORM_ITEM")
public class FormItem extends BaseEntity {
    /**
     * ID
     */
    @ApiModelProperty(name = "ID")
    @TableId
    private String id;
    /**
     * 表单定义ID
     */
    @ApiModelProperty(name = "表单定义ID")
    private String formDefId;
    /**
     * 组件标签
     */
    @ApiModelProperty(name = "组件标签")
    private String formItemLabel;
    /**
     * 组件KEY
     */
    @ApiModelProperty(name = "组件KEY")
    private String formItemKey;
    /**
     * 组件类型
     */
    @ApiModelProperty(name = "组件类型")
    private String formItemType;
    /**
     * 是否必填
     */
    @ApiModelProperty(name = "是否必填")
    private Boolean required;
    /**
     * 是否禁用
     */
    @ApiModelProperty(name = "是否禁用")
    private Boolean disabled;
    /**
     * 占位符
     */
    @ApiModelProperty(name = "占位符")
    private String placeholder;
    /**
     * 值类型
     */
    @ApiModelProperty(name = "值类型")
    private String valType;
}
