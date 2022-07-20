package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("WF_FORM_ITEM_PROP")
public class FormItemProp extends BaseEntity {
    @ApiModelProperty(name = "ID")
    @TableId
    private String id;
    /**
     * 父组件id
     */
    @ApiModelProperty(name = "父属性ID")
    private String parentPropId;
    /**
     * 组件ID
     */
    @ApiModelProperty(name = "组件ID")
    private String formItemId;
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
