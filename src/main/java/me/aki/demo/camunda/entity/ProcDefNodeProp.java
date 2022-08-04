package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@TableName("WF_PROC_DEF_NODE_PROP")
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


    public ProcDefNodeProp(String propKey, String propVal) {
        this.propKey = propKey;
        this.propVal = propVal;
    }
}
