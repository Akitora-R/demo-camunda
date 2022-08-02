package me.aki.demo.camunda.entity.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ProcInstDTO {
    private String id;
    @NotBlank
    private String procDefId;
    private String formDefId;
    private String camundaProcInstId;
    private String camundaProcInstBusinessKey;
    @NotNull
    @Valid
    private FormInstDTO form;

    @Data
    public static class FormInstDTO {
        @NotNull
        private List<FormInstItemDTO> itemList;

        @Data
        public static class FormInstItemDTO {
            private String formItemId;
            private String formItemVal;
        }
    }
}
