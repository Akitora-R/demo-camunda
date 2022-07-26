package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("WF_PROC_DEF")
public class ProcDef extends BaseEntity {
    /**
     * ID
     */
    @ApiModelProperty(name = "ID")
    @TableId
    private String id;
    /**
     * Camunda process definition ID
     */
    @ApiModelProperty(name = "Camunda process definition ID")
    private String camundaProcDefId;
    /**
     * 流程代码
     */
    @ApiModelProperty(name = "Camunda process definition KEY")
    private String camundaProcDefKey;
    /**
     * 流程名
     */
    @ApiModelProperty(name = "流程名")
    private String procDefName;
    /**
     * 原始流程json
     */
    @ApiModelProperty(name = "原始流程json")
    private String originalJson;

}
