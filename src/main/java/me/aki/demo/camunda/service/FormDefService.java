package me.aki.demo.camunda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.aki.demo.camunda.entity.FormDef;
import me.aki.demo.camunda.entity.dto.FormDefDTO;
import me.aki.demo.camunda.entity.vo.FormDefVO;
import org.springframework.stereotype.Service;

@Service
public interface FormDefService extends IService<FormDef> {
    void saveDTO(String procDefId, String procDefNodeId, FormDefDTO dto);

    FormDefVO getVOByProcDefId(String procDefId);

    FormDefVO getVOByProcDefNodeId(String procDefNodeId);
}
