package me.aki.demo.camunda.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.aki.demo.camunda.entity.FormDef;
import me.aki.demo.camunda.entity.FormItem;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormDefDTO {
    @Valid
    @NotNull
    private FormDef formDef;
    @Valid
    @NotNull
    @NotEmpty
    private List<FormItemDTO> formItemDTOList;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FormItemDTO {
        @Valid
        @NotNull
        private FormItem formItem;
        @Valid
        private List<FormItemPropDTO> formItemPropList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FormItemPropDTO {
        @ApiModelProperty(name = "ID")
        private String id;
        /**
         * 父组件id
         */
        @ApiModelProperty(name = "父属性ID")
        private String parentPropId;
        /**
         * 组件KEY
         */
        @ApiModelProperty(name = "组件KEY")
        private String formItemKey;
        /**
         * 属性KEY
         */
        @ApiModelProperty(name = "属性KEY", required = true)
        @NotBlank
        private String propKey;
        /**
         * 属性值
         */
        @ApiModelProperty(name = "属性值", required = true)
        private String propVal;
        /**
         * 子属性
         */
        @ApiModelProperty(name = "子属性")
        @Valid
        List<FormItemPropDTO> children;
    }
}
