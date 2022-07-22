package me.aki.demo.camunda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.aki.demo.camunda.entity.FormItem;
import me.aki.demo.camunda.entity.dto.FormDefDTO;

public interface FormItemService extends IService<FormItem> {
    void saveDTO(String formDefId,FormDefDTO.FormItemDTO dto);
}
