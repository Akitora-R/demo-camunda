package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>流程定义实体类</p>
 * <p>
 *     该实体类拥有 {@code camundaProcDefId} 与 {@code camundaProcDefKey} 来指定 camunda 中
 *     某一版本特定的流程，故每次重新部署同一流程的时候应当清除原有的 {@link ProcDef} ，是否是同一流程
 *     的依据是某.bpmn.xml中 {@code bpmn:process} 标签的 {@code id} 属性所指定。
 * </p>
 */
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
     * <p>Camunda process definition ID</p>
     * <p>
     *     对应每个流程定义的每个版本唯一，即使是对同一个流程定义重新部署也会生成不同的id
     * </p>
     */
    @ApiModelProperty(name = "Camunda process definition ID")
    private String camundaProcDefId;
    /**
     * <p>流程代码</p>
     * <p>
     *     对应一种流程定义，同一流程的所有版本定义共享同一个key
     * </p>
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
