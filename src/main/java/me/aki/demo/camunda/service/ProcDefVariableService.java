package me.aki.demo.camunda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.aki.demo.camunda.entity.ProcDefVariable;
import me.aki.demo.camunda.entity.dto.ProcDefVariableDTO;

public interface ProcDefVariableService extends IService<ProcDefVariable> {
    ProcDefVariable toEntity(ProcDefVariableDTO dto);
}
