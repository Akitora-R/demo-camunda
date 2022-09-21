package me.aki.demo.camunda.entity.dto.node.impl;

import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.constant.BeanProviderId;
import me.aki.demo.camunda.constant.IdPattern;
import me.aki.demo.camunda.entity.FormDef;
import me.aki.demo.camunda.entity.FormItem;
import me.aki.demo.camunda.entity.dto.FormDefDTO;
import me.aki.demo.camunda.entity.dto.VariableDefDTO;
import me.aki.demo.camunda.entity.dto.node.CombinationNodeDTO;
import me.aki.demo.camunda.entity.dto.node.FormNodeDTO;
import me.aki.demo.camunda.enums.FormItemType;
import me.aki.demo.camunda.enums.JsonNodeShape;
import me.aki.demo.camunda.enums.VariableSourceType;
import me.aki.demo.camunda.util.BpmnUtil;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class TaskFlowNodeDTO implements CombinationNodeDTO, FormNodeDTO {
    @NotBlank
    private String id;
    @NotBlank
    @Pattern(regexp = "^(?!\\d)\\w+$", message = "code只可由字母数字以及下划线组成，并且不能由数字开头。")
    private String code;
    @JsonIgnore
    private String outgoingNodeId;
    @JsonIgnore
    private String incomingNodeId;
    private String label;
    /**
     * <p>任务分配人。变量or用户id。</p>
     * <p>
     * 如果为会签/或签的情况下会自动替换为一内部变量，实际的任务分配人信息将以约定的identifier{@link TaskFlowNodeDTO#getAssigneeListVarKey()}
     * 的形式储存在{@link TaskFlowNodeDTO#variableList}中，具体的list即为BEAN类型的该identifier的所以prop，而各个prop又有各自的prop来标识
     * 通过该BEAN获取实际数据时所用的参数，具体的数据格式如下：
     * </p>
     * <blockquote><pre>
     *     [
     *        {
     *            "key": "SUPERIOR", //上级
     *            "children": [
     *                {
     *                    "key": "LEVEL",
     *                    "val": "0" //直属上级加x级
     *                }
     *            ]
     *        },
     *        {
     *            "key": "DEPT_HEAD", //部门负责人
     *            "children": [
     *                {
     *                    "key": "LEVEL",
     *                    "val": "0" //直属部门负责人加x级
     *                }
     *            ]
     *        },
     *        {
     *            "key": "ROLE", //角色
     *            "children": [
     *                {
     *                    "key": "ROLE_ID",
     *                    "val": "1" //角色id
     *                }
     *            ]
     *        },
     *        {
     *            "key": "DESIGNATION", //指定成员
     *            "children": [
     *                {
     *                    "key": "USER_LIST",
     *                    "val": [
     *                        "1",
     *                        "2",
     *                        "3"
     *                    ] //用户id
     *                }
     *            ]
     *        },
     *        {
     *            "key": "SELF" //提交人本人
     *        }
     *    ]
     * </pre></blockquote>
     */
    private String assignee;
    /**
     * 包括{@link TaskFlowNodeDTO#assignee}在内的，完成该任务需要的所有变量信息。
     */
    private List<VariableDefDTO> variableList;
    /**
     * 表单定义
     */
    private FormDefDTO form;
    /**
     * 对应bpmn.xml中的<code>multiInstanceLoopCharacteristics</code>元素，表示为或签或会签，默认false。
     */
    private Boolean isLoop;
    /**
     * 是否会签，默认true
     */
    private Boolean isCountersign;

    @Override
    public String toString() {
        return String.format("[UserTask (%s - %s)]", code, label);
    }

    @Override
    public JsonNodeShape getShape() {
        return JsonNodeShape.TASK;
    }

    @Override
    public void tidyUp(BiConsumer<String, String> onIdChange) {
        if (incomingNodeId == null || !IdPattern.TASK_PATTERN.matcher(incomingNodeId).matches()) {
            String newId = IdPattern.TASK_PREFIX + UUID.randomUUID();
            log.warn(IdPattern.WARN_MSG_TEMPLATE.apply(IdPattern.Msg.of(this.getClass(), incomingNodeId, newId)));
//            onIdChange.accept(id, newId);
            incomingNodeId = newId;
        }
        if (outgoingNodeId == null || !IdPattern.EXCLUSIVE_GATEWAY_PATTERN.matcher(outgoingNodeId).matches()) {
            String newId = IdPattern.EXCLUSIVE_GATEWAY_PREFIX + UUID.randomUUID();
            log.warn(IdPattern.WARN_MSG_TEMPLATE.apply(IdPattern.Msg.of(this.getClass(), outgoingNodeId, newId)));
//            onIdChange.accept(id, newId);
            outgoingNodeId = newId;
        }
        // 检查是否已经提供了每个userTask需要的两个基本表单组件，没有的话就加上
        // 通过表单key：approval, comment 来区别出这两个基本表单组件
        if (!hasBasicForm()) {
            FormDefDTO dto = new FormDefDTO();
            FormDef formDef = new FormDef();
            dto.setFormItemList(getDefaultFormItemDTOList());
            dto.setFormDef(formDef);
            form = dto;
        }
        if (variableList == null) {
            variableList = new ArrayList<>();
        }
        // 同样地，检查变量列表中是否有这两个表单组件所对应的变量
        if (variableList.stream().noneMatch(e -> e.getSourceType() == VariableSourceType.TASK_FORM && e.getVariableKey().equals(getDefaultApprovalSourceId()))) {
            VariableDefDTO variableDefDTO = new VariableDefDTO();
            variableDefDTO.setVariableKey(getApprovalVarKey());
            variableDefDTO.setSourceType(VariableSourceType.TASK_FORM);
            variableDefDTO.setSourceIdentifier(getDefaultApprovalSourceId());
            variableDefDTO.setRequired(true);
            variableList.add(variableDefDTO);
        }
        if (variableList.stream().noneMatch(e -> e.getSourceType() == VariableSourceType.TASK_FORM && e.getVariableKey().equals(getDefaultCommentSourceId()))) {
            VariableDefDTO variableDefDTO = new VariableDefDTO();
            variableDefDTO.setVariableKey(getCommentVarKey());
            variableDefDTO.setSourceType(VariableSourceType.TASK_FORM);
            variableDefDTO.setSourceIdentifier(getDefaultCommentSourceId());
            variableDefDTO.setRequired(false);
            variableList.add(variableDefDTO);
        }
        if (isLoop == null) {
            isLoop = Boolean.FALSE;
        }
        if (isCountersign == null) {
            isCountersign = true;
        }
        // 如果是会/或签的情况，校验是否提供了至少一个审核者信息
        if (isLoop) {
            VariableDefDTO assigneeListVarDef = variableList.stream().filter(e -> BeanProviderId.USER_PROVIDER.equals(e.getSourceIdentifier()) && e.getSourceType() == VariableSourceType.BEAN).findAny().orElseThrow();
            List<VariableDefDTO.VariableDefPropDTO> propList = assigneeListVarDef.getPropList();
            Assert.notEmpty(propList, "会/或签节点的审核者列表不可为空！");
        }
    }

    @Override
    public List<String> build(AbstractFlowNodeBuilder<?, ?> builder) {
        String middleGatewayId = IdPattern.EXCLUSIVE_GATEWAY_PREFIX + UUID.randomUUID();
        String varName = getApprovalVarKey();
        String approveVarExp = String.format("${%s}", varName);
        String rejectVarExp = String.format("${!%s}", varName);
        if (isLoop) {
            String generatedAssigneeVarName = "userTaskAssignee_" + UUID.randomUUID().toString().replace("-", "");
            String generatedAssigneeVar = String.format("${%s}", generatedAssigneeVarName);
            builder
                    .userTask().id(incomingNodeId).name(label).camundaAssignee(generatedAssigneeVar)
                    .multiInstance()
                    .parallel().camundaCollection(BpmnUtil.toVarExp(getAssigneeListVarKey())).camundaElementVariable(generatedAssigneeVarName).completionCondition(rejectVarExp)
                    .multiInstanceDone()
                    .exclusiveGateway().id(middleGatewayId).condition("reject", rejectVarExp)
                    .endEvent()
                    .moveToNode(middleGatewayId).condition("approve", approveVarExp)
                    .exclusiveGateway().id(outgoingNodeId);
        } else {
            builder
                    .userTask().id(incomingNodeId).name(label).camundaAssignee(assignee)
                    .exclusiveGateway().id(middleGatewayId).condition("reject", rejectVarExp)
                    .endEvent()
                    .moveToNode(middleGatewayId).condition("approve", approveVarExp)
                    .exclusiveGateway().id(outgoingNodeId);
        }
        return Arrays.asList(incomingNodeId, outgoingNodeId, middleGatewayId);
    }

    private boolean hasBasicForm() {
        if (form == null || form.getFormItemList() == null) {
            return false;
        }
        return form.getFormItemList().stream().anyMatch(e -> getDefaultApprovalSourceId().equals(e.getFormItem().getFormItemKey())) &&
                form.getFormItemList().stream().anyMatch(e -> getDefaultCommentSourceId().equals(e.getFormItem().getFormItemLabel()));
    }

    private List<FormDefDTO.FormItemDTO> getDefaultFormItemDTOList() {
        ArrayList<FormDefDTO.FormItemDTO> itemDTOS = new ArrayList<>();
        FormDefDTO.FormItemDTO e = new FormDefDTO.FormItemDTO();
        FormItem formItem = new FormItem();
        formItem.setFormItemLabel("审批意见");
        formItem.setFormItemKey(getDefaultApprovalSourceId());
        formItem.setFormItemType(FormItemType.BOOLEAN);
        formItem.setRequired(true);
        e.setFormItem(formItem);
        itemDTOS.add(e);
        e = new FormDefDTO.FormItemDTO();
        formItem = new FormItem();
        formItem.setFormItemLabel("备注");
        formItem.setFormItemKey(getDefaultCommentSourceId());
        formItem.setFormItemType(FormItemType.TEXT_INPUT);
        formItem.setRequired(false);
        e.setFormItem(formItem);
        itemDTOS.add(e);
        return itemDTOS;
    }

    /**
     * @return 变量来源中代表审核是否通过的变量的identifier
     */
    private String getDefaultApprovalSourceId() {
        return "approval";
    }

    /**
     * @return bpmn中代表审核是否通过的变量的名称
     */
    public String getApprovalVarKey() {
        return String.format("approval_%s", code);
    }

    /**
     * @return 变量来源中代表备注的变量的identifier
     */
    private String getDefaultCommentSourceId() {
        return "comment";
    }

    /**
     * @return bpmn中代表备注的变量的名称
     */
    private String getCommentVarKey() {
        return String.format("comment_%s", code);
    }

    /**
     * @return bpmn中代表审核列表的变量的名称
     */
    public String getAssigneeListVarKey() {
        return String.format("assigneeList_%s", code);
    }

    public String getAssigneeVarKey() {
        return String.format("assignee_%s", code);
    }
}
