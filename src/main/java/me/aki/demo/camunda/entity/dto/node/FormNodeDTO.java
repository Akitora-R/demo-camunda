package me.aki.demo.camunda.entity.dto.node;

import me.aki.demo.camunda.entity.dto.FormDefDTO;

/**
 * 包含表单的节点
 */
public interface FormNodeDTO extends FlowNodeDTO {
    FormDefDTO getForm();
}
