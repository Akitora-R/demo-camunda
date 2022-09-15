package me.aki.demo.camunda.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ProcInstDTO {
    /**
     * 流程定义id
     */
    @NotBlank
    private String procDefId;
    /**
     * 表单定义id
     */
    private String formDefId;
    /**
     * camunda 流程实例id，唯一
     */
    private String camundaProcInstId;
    /**
     * camunda 流程实例businessKey，应填来源业务主键，可能不唯一
     */
    @NotBlank
    private String camundaProcInstBusinessKey;
    /**
     * 表单数据，nullable
     */
    private List<VariableInstDTO> variableList;

}
