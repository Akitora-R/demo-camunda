package me.aki.demo.camunda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.FormItem;
import me.aki.demo.camunda.entity.dto.FormDTO;
import me.aki.demo.camunda.mapper.FormItemMapper;
import me.aki.demo.camunda.service.FormItemPropService;
import me.aki.demo.camunda.service.FormItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FormItemServiceImpl extends ServiceImpl<FormItemMapper, FormItem> implements FormItemService {
    private final FormItemPropService formItemPropService;

    public FormItemServiceImpl(FormItemPropService formItemPropService) {
        this.formItemPropService = formItemPropService;
    }

    @Override
    public void saveDTO(FormDTO.FormItemDTO dto) {
        save(dto.getFormItem());
        formItemPropService.saveBatch(dto.getFormItemPropList());
    }
}
