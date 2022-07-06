package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

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
     * 表单标题
     */
    @ApiModelProperty(name = "表单标题")
    private String title;


}
