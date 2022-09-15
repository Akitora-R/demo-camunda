package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aki.demo.camunda.enums.VariableSourceType;

/**
 * 该表反映一个流程中的所有需要的变量，该变量是否必填则由该变量的来源定义，
 * 变量的来源{@link ProcDefVariable#sourceType}目前一共有5种：
 * <ul>
 *     <li>BASE</li>
 *     员工的账号/部门等信息
 *     <li>PROC_FORM</li>
 *     流程表单
 *     <li>TASK_FORM</li>
 *     任务表单，来源为该类型时即要求该变量需要在完成任务时提供，
 *     并可以通过以{@link ProcDefVariable#sourceIdentifier}为表单key来获取。
 *     <li>BEAN</li>
 *     通过实现了特定接口的Java Bean来获取数据，调用时可附加额外参数
 *     <li>BIZ</li>
 *     调用外部业务表单接口来获取数据，调用时可附加额外参数
 * </ul>
 */
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
     * 流程节点定义ID
     */
    @ApiModelProperty(name = "流程节点定义ID")
    private String procDefNodeId;
    /**
     * 变量key，即为bpmn中的variable identifier，
     * 例如<code>${user}</code>中的<code>user<code/>
     */
    @ApiModelProperty(name = "变量key")
    private String variableKey;
    /**
     * 来源类型
     * <ul>
     *     <li>BEAN</li>
     *     <li>PROC_FORM</li>
     *     <li>TASK_FORM</li>
     *     <li>BASE</li>
     *     <li>BIZ</li>
     * </ul>
     */
    @ApiModelProperty(name = "来源类型", notes = "BEAN、FORM、BASE、BIZ")
    private VariableSourceType sourceType;
    /**
     * 来源标识;
     * <ul>
     *     <li>form item key</li>
     *     <li>bean enum</li>
     *     <li>biz field id</li>
     * </ul>
     */
    @ApiModelProperty(name = "来源标识", notes = "可能的值：[form item key, bean enum]")
    private String sourceIdentifier;

    /**
     * 表示该变量是否为必填项，如果是必填项要求在创建流程实例/完成任务时必须获取，否则抛出异常。
     * 如果是非必填项则尝试获取，若为null或失败则不生成对应的表单实例数据。
     */
    @ApiModelProperty(name = "是否必填")
    private Boolean required;
}
