package me.aki.demo.camunda.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.ProcDefVariable;
import me.aki.demo.camunda.entity.dto.VariableDefDTO;
import me.aki.demo.camunda.mapper.ProcDefVariableMapper;
import me.aki.demo.camunda.service.ProcDefVariableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProcDefVariableServiceImpl extends ServiceImpl<ProcDefVariableMapper, ProcDefVariable> implements ProcDefVariableService {
    @Override
    public ProcDefVariable toEntity(VariableDefDTO dto) {
        return BeanUtil.copyProperties(dto, ProcDefVariable.class);
    }
}
