package me.aki.demo.camunda.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.ProcDef;
import me.aki.demo.camunda.entity.dto.ProcDefDTO;
import me.aki.demo.camunda.mapper.ProcDefMapper;
import me.aki.demo.camunda.service.ProcDefService;
import org.springframework.stereotype.Service;

@Service
public class ProcDefServiceImpl extends ServiceImpl<ProcDefMapper, ProcDef> implements ProcDefService {
    @Override
    public ProcDef toEntity(ProcDefDTO dto) {
        return BeanUtil.copyProperties(dto, ProcDef.class);
    }
}
