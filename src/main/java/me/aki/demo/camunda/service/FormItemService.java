package me.aki.demo.camunda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.aki.demo.camunda.entity.FormItem;
import me.aki.demo.camunda.entity.dto.FormDefDTO;
import me.aki.demo.camunda.entity.vo.FormDefVO;

import java.util.List;

public interface FormItemService extends IService<FormItem> {
    void saveDTO(String formDefId,FormDefDTO.FormItemDTO dto);

    List<FormDefVO.FormItemVO> getVOListByFormDefId(String formDefId);
}
