package me.aki.demo.camunda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.aki.demo.camunda.entity.VariableDefProp;
import me.aki.demo.camunda.entity.dto.VariableDefDTO;

import java.util.List;

public interface VariableDefPropService extends IService<VariableDefProp> {
    void saveDTOTree(String parentId, List<VariableDefDTO.VariableDefPropDTO> tree);

    VariableDefDTO.VariableDefPropDTO toDTO(VariableDefProp entity);
}
