package me.aki.demo.camunda.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcInstDTO {
    private String procDefId;
    private String formDefId;
    private String camundaProcessInstanceId;
    private String camundaProcessInstanceBusinessKey;
    private FormInstDTO form;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FormInstDTO {
        private List<FormInstItemDTO> itemList;

        @Data
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class FormInstItemDTO {
            private String formItemId;
            private String formItemVal;
        }
    }
}
