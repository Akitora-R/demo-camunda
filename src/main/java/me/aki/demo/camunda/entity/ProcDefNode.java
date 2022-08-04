package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aki.demo.camunda.enums.JsonNodeShape;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("WF_PROC_DEF_NODE")
public class ProcDefNode extends BaseEntity {
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
     * 节点
     */
    @ApiModelProperty(name = "节点JsonId")
    private String nodeJsonId;
    /**
     * 节点标签
     */
    @ApiModelProperty(name = "节点标签")
    private String nodeLabel;
    /**
     * 节点类型
     */
    @ApiModelProperty(name = "节点类型")
    private JsonNodeShape nodeShape;
}
