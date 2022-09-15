package me.aki.demo.camunda.entity.dto.node.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
    private String assignee;
    private List<String> candidate;
    private List<VariableDefDTO> variableList;
    private FormDefDTO form;

    @Override
    public String toString() {
        return String.format("[UserTask (%s - %s)]", code, label);
    }

    @Override
    public JsonNodeShape getShape() {
        return JsonNodeShape.TASK;
    }

    /**
     * json id并不需要进行校验
     */
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
        if (variableList.stream().noneMatch(e -> e.getSourceType() == VariableSourceType.TASK_FORM && e.getVariableKey().equals(getApprovalFormKey()))) {
            VariableDefDTO variableDefDTO = new VariableDefDTO();
            variableDefDTO.setVariableKey(getApprovalVarKey());
            variableDefDTO.setSourceType(VariableSourceType.TASK_FORM);
            variableDefDTO.setSourceIdentifier(getApprovalFormKey());
            variableDefDTO.setRequired(true);
            variableList.add(variableDefDTO);
        }
        if (variableList.stream().noneMatch(e -> e.getSourceType() == VariableSourceType.TASK_FORM && e.getVariableKey().equals(getCommentFormKey()))) {
            VariableDefDTO variableDefDTO = new VariableDefDTO();
            variableDefDTO.setVariableKey(getCommentVarKey());
            variableDefDTO.setSourceType(VariableSourceType.TASK_FORM);
            variableDefDTO.setSourceIdentifier(getCommentFormKey());
            variableDefDTO.setRequired(false);
            variableList.add(variableDefDTO);
        }
    }

    @Override
    public List<String> build(AbstractFlowNodeBuilder<?, ?> builder) {
        String middleGatewayId = IdPattern.EXCLUSIVE_GATEWAY_PREFIX + UUID.randomUUID();
        String varName = form.getFormItemList().stream().filter(e -> "审批意见".equals(e.getFormItem().getFormItemLabel())).findAny().orElseThrow().getFormItem().getFormItemKey();
        builder
                .userTask().id(incomingNodeId).name(label).camundaAssignee(assignee)
                .exclusiveGateway().id(middleGatewayId).condition("reject", String.format("${!%s}", varName))
                .endEvent()
                .moveToNode(middleGatewayId).condition("approve", String.format("${%s}", varName))
                .exclusiveGateway().id(outgoingNodeId);
        return Arrays.asList(incomingNodeId, outgoingNodeId, middleGatewayId);
    }

    private boolean hasBasicForm() {
        if (form == null || form.getFormItemList() == null) {
            return false;
        }
        return form.getFormItemList().stream().anyMatch(e -> getApprovalFormKey().equals(e.getFormItem().getFormItemKey())) &&
                form.getFormItemList().stream().anyMatch(e -> getCommentFormKey().equals(e.getFormItem().getFormItemLabel()));
    }

    private List<FormDefDTO.FormItemDTO> getDefaultFormItemDTOList() {
        ArrayList<FormDefDTO.FormItemDTO> itemDTOS = new ArrayList<>();
        FormDefDTO.FormItemDTO e = new FormDefDTO.FormItemDTO();
        FormItem formItem = new FormItem();
        formItem.setFormItemLabel("审批意见");
        formItem.setFormItemKey(getApprovalFormKey());
        formItem.setFormItemType(FormItemType.BOOLEAN);
        formItem.setRequired(true);
        e.setFormItem(formItem);
        itemDTOS.add(e);
        e = new FormDefDTO.FormItemDTO();
        formItem = new FormItem();
        formItem.setFormItemLabel("备注");
        formItem.setFormItemKey(getCommentFormKey());
        formItem.setFormItemType(FormItemType.TEXT_INPUT);
        formItem.setRequired(false);
        e.setFormItem(formItem);
        itemDTOS.add(e);
        return itemDTOS;
    }

    private String getApprovalFormKey() {
        return "approval";
    }

    private String getApprovalVarKey() {
        return String.format("%s_approval", code);
    }

    private String getCommentFormKey() {
        return "comment";
    }

    private String getCommentVarKey() {
        return String.format("%s_comment", code);
    }
}
