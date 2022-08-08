package me.aki.demo.camunda.entity.dto.query;

import lombok.Data;

@Data
public class ProcInstPagedQuery {
    private ProcInstStatus status;

    public enum ProcInstStatus {
        ACTIVE,
        SUSPENDED,
        COMPLETED,
    }
}
