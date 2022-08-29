package me.aki.demo.camunda.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.aki.demo.camunda.entity.dto.node.NodeDTO;
import me.aki.demo.camunda.enums.SourceBizType;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ProcDefDTO {
    private String id;
//    /**
//     * 流程代码
//     * 真的有必要存在吗
//     */
//    @ApiModelProperty(name = "流程代码")
//    @NotBlank
//    private String procDefCode;
    /**
     * 流程名
     */
    @ApiModelProperty(name = "流程名")
    @NotBlank
    private String procDefName;
    /**
     * 原始流程json
     */
    @ApiModelProperty(name = "来源业务类型")
    @NotNull(message = "来源业务类型不可为null")
    private SourceBizType sourceBizType;
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
    private FormDefDTO formDef;

}
