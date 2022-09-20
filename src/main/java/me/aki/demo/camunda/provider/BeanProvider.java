package me.aki.demo.camunda.provider;

import me.aki.demo.camunda.entity.dto.VariableDefDTO;

import java.util.List;

public interface BeanProvider<T> {
    T getData(List<VariableDefDTO.VariableDefPropDTO> prop);
}
