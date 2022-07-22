package me.aki.demo.camunda.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import me.aki.demo.camunda.entity.ProcDef;
import me.aki.demo.camunda.entity.dto.ProcDefDTO;
import me.aki.demo.camunda.entity.vo.ProcDefVO;

public interface ProcDefService extends IService<ProcDef> {

    ProcDef toEntity(ProcDefDTO dto);

    IPage<ProcDef> page(int page,int size);
}
