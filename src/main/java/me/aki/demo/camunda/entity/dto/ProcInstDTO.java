package me.aki.demo.camunda.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ProcInstDTO {
    @NotBlank
    private String procDefId;
    @NotBlank
    private String formDefId;
    private String camundaProcessInstanceId;
    private String camundaProcessInstanceBusinessKey;
    private FormInstDTO form;

    @Data
    public static class FormInstDTO {
        private List<FormInstItemDTO> itemList;

        @Data
        public static class FormInstItemDTO {
            private String formItemId;
            private String formItemVal;
        }
    }
}
