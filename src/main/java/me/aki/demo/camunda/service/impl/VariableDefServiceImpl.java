package me.aki.demo.camunda.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.VariableDef;
import me.aki.demo.camunda.entity.VariableDefProp;
import me.aki.demo.camunda.entity.dto.VariableDefDTO;
import me.aki.demo.camunda.mapper.VariableDefMapper;
import me.aki.demo.camunda.service.VariableDefPropService;
import me.aki.demo.camunda.service.VariableDefService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class VariableDefServiceImpl extends ServiceImpl<VariableDefMapper, VariableDef> implements VariableDefService {
    private final VariableDefPropService variableDefPropService;

    public VariableDefServiceImpl(VariableDefPropService variableDefPropService) {
        this.variableDefPropService = variableDefPropService;
    }

    @Override
    public VariableDef toEntity(VariableDefDTO dto) {
        return BeanUtil.copyProperties(dto, VariableDef.class);
    }

    @Override
    public VariableDefDTO getDTOById(String id) {
        VariableDef variableDef = getById(id);
        VariableDefDTO dto = BeanUtil.copyProperties(variableDef, VariableDefDTO.class);
        List<VariableDefProp> list = variableDefPropService.lambdaQuery().eq(VariableDefProp::getProcDefVariableId, id).list();
        List<VariableDefDTO.VariableDefPropDTO> roots = list.stream()
                .filter(e -> "0".equals(e.getParentPropId()))
                .map(variableDefPropService::toDTO)
                .toList();
        dto.setPropList(roots);
        roots.forEach(root -> root.setChildren(getChildren(root)));
        return dto;
    }

    private List<VariableDefDTO.VariableDefPropDTO> getChildren(VariableDefDTO.VariableDefPropDTO parent) {
        if (parent == null) {
            return Collections.emptyList();
        }
        List<VariableDefDTO.VariableDefPropDTO> children = variableDefPropService.lambdaQuery().eq(VariableDefProp::getParentPropId, parent.getId()).list()
                .stream().map(variableDefPropService::toDTO).toList();
        for (VariableDefDTO.VariableDefPropDTO child : children) {
            child.setChildren(getChildren(child));
        }
        return children;
    }
}
