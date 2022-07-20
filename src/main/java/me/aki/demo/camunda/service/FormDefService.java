package me.aki.demo.camunda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.aki.demo.camunda.entity.FormDef;
import me.aki.demo.camunda.entity.dto.FormDefDTO;
import org.springframework.stereotype.Service;

@Service
public interface FormDefService extends IService<FormDef> {
    void saveDTO(FormDefDTO dto);
}
