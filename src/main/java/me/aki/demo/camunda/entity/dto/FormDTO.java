package me.aki.demo.camunda.entity.dto;

import lombok.Data;
import me.aki.demo.camunda.entity.FormDef;
import me.aki.demo.camunda.entity.FormItem;
import me.aki.demo.camunda.entity.FormItemProp;

import java.util.List;

@Data
public class FormDTO {
    private FormDef formDef;
    private List<FormItemDTO> formItemDTOList;
    @Data
    public static class FormItemDTO{
        private FormItem formItem;
        private List<FormItemProp> formItemPropList;
    }
}
