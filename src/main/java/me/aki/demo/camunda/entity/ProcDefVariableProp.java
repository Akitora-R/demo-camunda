package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("WF_PROC_DEF_VARIABLE_PROP")
public class ProcDefVariableProp extends BaseEntity {
    /**
     * ID
     */
    @ApiModelProperty(name = "ID")
    @TableId
    private String id;
    /**
     * 变量定义ID
     */
    @ApiModelProperty(name = "变量定义ID")
    private String procDefVariableId;
    /**
     * 父属性ID
     */
    @ApiModelProperty(name = "父属性ID")
    private String parentPropId;
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
