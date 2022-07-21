package me.aki.demo.camunda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.aki.demo.camunda.entity.ProcDef;
import me.aki.demo.camunda.entity.dto.ProcDefDTO;

public interface ProcDefService extends IService<ProcDef> {
    ProcDef toEntity(ProcDefDTO dto);
}
