package me.aki.demo.camunda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.aki.demo.camunda.entity.FormItem;
import me.aki.demo.camunda.entity.dto.FormDTO;

public interface FormItemService extends IService<FormItem> {
    void saveDTO(FormDTO.FormItemDTO dto);
}
