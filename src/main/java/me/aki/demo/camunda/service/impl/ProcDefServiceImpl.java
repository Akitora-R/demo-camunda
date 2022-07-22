package me.aki.demo.camunda.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.ProcDef;
import me.aki.demo.camunda.entity.dto.ProcDefDTO;
import me.aki.demo.camunda.entity.vo.ProcDefVO;
import me.aki.demo.camunda.mapper.ProcDefMapper;
import me.aki.demo.camunda.service.ProcDefService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProcDefServiceImpl extends ServiceImpl<ProcDefMapper, ProcDef> implements ProcDefService {

    @Override
    public ProcDef toEntity(ProcDefDTO dto) {
        return BeanUtil.copyProperties(dto, ProcDef.class);
    }

    @Override
    public IPage<ProcDef> page(int page, int size) {
        return page(new Page<>(page, size));
    }
}
