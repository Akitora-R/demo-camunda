package me.aki.demo.camunda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.FormDef;
import me.aki.demo.camunda.entity.dto.FormDefDTO;
import me.aki.demo.camunda.entity.vo.FormDefVO;
import me.aki.demo.camunda.mapper.FormDefMapper;
import me.aki.demo.camunda.service.FormDefService;
import me.aki.demo.camunda.service.FormItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FormDefServiceImpl extends ServiceImpl<FormDefMapper, FormDef> implements FormDefService {
    private final FormItemService formItemService;

    public FormDefServiceImpl(FormItemService formItemService) {
        this.formItemService = formItemService;
    }

    @Override
    public void saveDTO(String procDefId, String procDefNodeId, FormDefDTO dto) {
        if (dto == null) {
            return;
        }
        FormDef formDef = dto.getFormDef();
        formDef.setProcDefId(procDefId);
        formDef.setProcDefNodeId(procDefNodeId);
        save(dto.getFormDef());
        dto.getFormItemList().forEach(e -> formItemService.saveDTO(dto.getFormDef().getId(), e));
    }

    @Override
    public FormDefVO getVOByProcDefId(String procDefId) {
        FormDef formDef = lambdaQuery().eq(FormDef::getProcDefId, procDefId).one();
        return toVO(formDef);
    }

    @Override
    public FormDefVO getVOByProcDefNodeId(String procDefNodeId) {
        FormDef formDef = lambdaQuery().eq(FormDef::getProcDefNodeId, procDefNodeId).one();
        return toVO(formDef);
    }

    private FormDefVO toVO(FormDef entity) {
        FormDefVO vo = new FormDefVO();
        vo.setFormDef(entity);
        List<FormDefVO.FormItemVO> items = formItemService.getVOListByFormDefId(entity.getId());
        vo.setFormItemList(items);
        return vo;
    }
}
