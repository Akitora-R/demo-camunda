package me.aki.demo.camunda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.VariableDefProp;
import me.aki.demo.camunda.entity.dto.VariableDefDTO;
import me.aki.demo.camunda.mapper.VariableDefPropMapper;
import me.aki.demo.camunda.service.VariableDefPropService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VariableDefPropServiceImpl extends ServiceImpl<VariableDefPropMapper, VariableDefProp> implements VariableDefPropService {
    @Override
    public void saveDTOTree(String parentId, List<VariableDefDTO.VariableDefPropDTO> tree) {
        // TODO: 2022/9/19
    }

    @Override
    public VariableDefDTO.VariableDefPropDTO toDTO(VariableDefProp entity) {
        VariableDefDTO.VariableDefPropDTO dto = new VariableDefDTO.VariableDefPropDTO();
        dto.setId(entity.getId());
        dto.setKey(entity.getPropKey());
        dto.setVal(entity.getPropVal());
        return dto;
    }
}
