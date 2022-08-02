package me.aki.demo.camunda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.aki.demo.camunda.entity.FormInstItem;
import me.aki.demo.camunda.entity.dto.ProcInstDTO;

public interface FormInstItemService extends IService<FormInstItem> {
    FormInstItem toEntity(ProcInstDTO.FormInstDTO.FormInstItemDTO dto);
}
