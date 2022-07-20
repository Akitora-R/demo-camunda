package me.aki.demo.camunda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.FormDef;
import me.aki.demo.camunda.entity.dto.FormDefDTO;
import me.aki.demo.camunda.mapper.FormDefMapper;
import me.aki.demo.camunda.service.FormDefService;
import me.aki.demo.camunda.service.FormItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FormDefServiceImpl extends ServiceImpl<FormDefMapper, FormDef> implements FormDefService {
    private final FormItemService formItemService;

    public FormDefServiceImpl(FormItemService formItemService) {
        this.formItemService = formItemService;
    }

    @Override
    public void saveDTO(FormDefDTO dto) {
        save(dto.getFormDef());
        dto.getFormItemDTOList().forEach(formItemService::saveDTO);
    }
}
