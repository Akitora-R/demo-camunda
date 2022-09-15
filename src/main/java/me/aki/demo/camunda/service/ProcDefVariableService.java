package me.aki.demo.camunda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.aki.demo.camunda.entity.ProcDefVariable;
import me.aki.demo.camunda.entity.dto.VariableDefDTO;

public interface ProcDefVariableService extends IService<ProcDefVariable> {
    ProcDefVariable toEntity(VariableDefDTO dto);
}
