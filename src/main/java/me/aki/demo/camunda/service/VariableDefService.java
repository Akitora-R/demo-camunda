package me.aki.demo.camunda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.aki.demo.camunda.entity.VariableDef;
import me.aki.demo.camunda.entity.dto.VariableDefDTO;

public interface VariableDefService extends IService<VariableDef> {
    VariableDef toEntity(VariableDefDTO dto);

    VariableDefDTO getDTOById(String id);
}
