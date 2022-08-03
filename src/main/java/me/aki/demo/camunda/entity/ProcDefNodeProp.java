package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProcDefNodeProp extends BaseEntity {
    /**
     * ID
     */
    @ApiModelProperty(name = "ID")
    @TableId
    private String id;
    /**
     * 流程定义节点ID
     */
    @ApiModelProperty(name = "流程定义节点ID")
    private String procDefNodeId;
    /**
     * 属性KEY
     */
    @ApiModelProperty(name = "属性KEY")
    private String propKey;
    /**
     * 属性VAL
     */
    @ApiModelProperty(name = "属性VAL")
    private String propVal;
}
