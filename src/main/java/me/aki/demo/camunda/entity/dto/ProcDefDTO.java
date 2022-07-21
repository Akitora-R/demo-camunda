package me.aki.demo.camunda.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.aki.demo.camunda.entity.dto.node.NodeDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcDefDTO {
    private String id;
    /**
     * 流程代码
     */
    @ApiModelProperty(name = "流程代码")
    @NotBlank
    private String procDefCode;
    /**
     * 流程名
     */
    @ApiModelProperty(name = "流程名")
    @NotBlank
    private String procDefName;
    /**
     * 原始流程json
     */
    @ApiModelProperty(name = "原始流程json")
    private String originalJson;
    /**
     * 流程定义节点列表
     */
    @ApiModelProperty(name = "流程定义节点列表")
    @NotEmpty
    @NotNull
    @Valid
    private List<NodeDTO> nodeList;
    /**
     * 表单定义
     */
    @ApiModelProperty(name = "表单定义")
    @NotNull
    @Valid
    private FormDefDTO formDefDTO;

}
