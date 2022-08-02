package me.aki.demo.camunda.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.FormInstItem;
import me.aki.demo.camunda.entity.dto.ProcInstDTO;
import me.aki.demo.camunda.mapper.FormInstItemMapper;
import me.aki.demo.camunda.service.FormInstItemService;
import org.springframework.stereotype.Service;

@Service
public class FormInstItemServiceImpl extends ServiceImpl<FormInstItemMapper, FormInstItem> implements FormInstItemService {

    @Override
    public FormInstItem toEntity(ProcInstDTO.FormInstDTO.FormInstItemDTO dto) {
        return BeanUtil.copyProperties(dto,FormInstItem.class);
    }
}
