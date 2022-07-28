package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aki.demo.camunda.enums.VariableSourceType;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "流程变量定义")
@TableName("WF_PROC_DEF_VARIABLE")
public class ProcDefVariable extends BaseEntity {
    @ApiModelProperty(name = "ID")
    @TableId
    private String id;
    /**
     * 流程定义ID
     */
    @ApiModelProperty(name = "流程定义ID")
    private String procDefId;
    /**
     * 变量key
     */
    @ApiModelProperty(name = "变量key")
    private String variableKey;
    /**
     * 来源类型;
     * <ul>
     *     <li>BEAN</li>
     *     <li>FORM</li>
     *     <li>BASE</li>
     * </ul>
     */
    @ApiModelProperty(name = "来源类型", notes = "BEAN、FORM、BASE")
    private VariableSourceType sourceType;
    /**
     * 来源标识;
     * <ul>
     *     <li>form item key</li>
     *     <li>bean enum</li>
     * </ul>
     */
    @ApiModelProperty(name = "来源标识", notes = "可能的值：[form item key, bean enum]")
    private String sourceIdentifier;
}
