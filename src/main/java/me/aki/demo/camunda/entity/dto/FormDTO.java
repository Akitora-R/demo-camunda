package me.aki.demo.camunda.entity.dto;

import lombok.Data;
import me.aki.demo.camunda.entity.FormDef;
import me.aki.demo.camunda.entity.FormItem;
import me.aki.demo.camunda.entity.FormItemProp;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class FormDTO {
    @Valid
    @NotNull
    private FormDef formDef;
    @Valid
    @NotNull
    @NotEmpty
    private List<FormItemDTO> formItemDTOList;

    @Data
    public static class FormItemDTO {
        @Valid
        @NotNull
        private FormItem formItem;
        private List<FormItemProp> formItemPropList;
    }
}
