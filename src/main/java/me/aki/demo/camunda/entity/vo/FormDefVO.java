package me.aki.demo.camunda.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.aki.demo.camunda.entity.FormDef;
import me.aki.demo.camunda.entity.FormItem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormDefVO {
    private FormDef formDef;
    private List<FormItemVO> formItemList;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FormItemVO {
        @NotNull
        private FormItem formItem;
        private List<FormItemPropVO> formItemPropList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FormItemPropVO {
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
        List<FormItemPropVO> children;
    }
}
