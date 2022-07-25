package me.aki.demo.camunda.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.FormItem;
import me.aki.demo.camunda.entity.FormItemProp;
import me.aki.demo.camunda.entity.dto.FormDefDTO;
import me.aki.demo.camunda.entity.vo.FormDefVO;
import me.aki.demo.camunda.enums.FormItemType;
import me.aki.demo.camunda.mapper.FormItemMapper;
import me.aki.demo.camunda.service.FormItemPropService;
import me.aki.demo.camunda.service.FormItemService;
import me.aki.demo.camunda.util.TreeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Transactional
public class FormItemServiceImpl extends ServiceImpl<FormItemMapper, FormItem> implements FormItemService {
    private final FormItemPropService formItemPropService;
    private Map<FormItemType, Consumer<List<FormItemProp>>> validators;

    public FormItemServiceImpl(FormItemPropService formItemPropService) {
        this.formItemPropService = formItemPropService;
    }

    @PostConstruct
    void postConstruct() {
        HashMap<FormItemType, Consumer<List<FormItemProp>>> m = new HashMap<>();
        m.put(FormItemType.NUM_INPUT, items -> {
            Map<String, List<FormItemProp>> propMap = items.stream().collect(Collectors.groupingBy(FormItemProp::getPropKey));
            List<FormItemProp> max = propMap.get("max");
            List<FormItemProp> min = propMap.get("min");
            Assert.isTrue(max.size() == 1);
            Assert.isTrue(min.size() == 1);
            Assert.isTrue(StrUtil.isNumeric(max.get(0).getPropVal()));
            Assert.isTrue(StrUtil.isNumeric(min.get(0).getPropVal()));
        });
        m.put(FormItemType.SELECT, items -> {
            Map<String, List<FormItemProp>> propMap = items.stream().collect(Collectors.groupingBy(FormItemProp::getPropKey));

        });
        validators = m;
    }

    @Override
    public void saveDTO(String formDefId, FormDefDTO.FormItemDTO dto) {
        FormItem formItem = dto.getFormItem();
        formItem.setFormDefId(formDefId);
        save(formItem);
        // TODO: 2022/7/19 apply validation
        final var formItemId = formItem.getId();
        formItemPropService.lambdaUpdate().eq(FormItemProp::getFormItemId, formItemId).remove();
        TreeUtils.travelTreeBFS(dto.getFormItemPropList(), FormDefDTO.FormItemPropDTO::getChildren, ni -> {
            String pId = Optional.ofNullable(ni.getParentNode()).map(FormDefDTO.FormItemPropDTO::getId).orElse("0");
            FormItemProp e = toEntity(ni.getNode());
            e.setFormItemId(formItemId);
            e.setParentPropId(pId);
            formItemPropService.save(e);
            ni.getNode().setId(e.getId());
        });
    }

    @Override
    public List<FormDefVO.FormItemVO> getVOListByFormDefId(String formDefId) {
        List<FormItem> items = lambdaQuery().eq(FormItem::getFormDefId, formDefId).list();
        return items.stream().map(this::toVO).collect(Collectors.toList());
    }

    private FormItemProp toEntity(FormDefDTO.FormItemPropDTO dto) {
        // FIXME: 2022/7/19 literal constant
        return BeanUtil.copyProperties(dto, FormItemProp.class, "children");
    }

    private FormDefVO.FormItemVO toVO(FormItem entity) {
        FormDefVO.FormItemVO vo = new FormDefVO.FormItemVO();
        vo.setFormItem(entity);
        var props = formItemPropService.lambdaQuery()
                .eq(FormItemProp::getFormItemId, entity.getId())
                .list().stream().map(this::toVO).collect(Collectors.groupingBy(FormDefVO.FormItemPropVO::getParentPropId));
        var pList = props.getOrDefault("0", Collections.emptyList());
        vo.setFormItemPropList(pList);
        while (!pList.isEmpty()) {
            ArrayList<FormDefVO.FormItemPropVO> vos = new ArrayList<>();
            for (FormDefVO.FormItemPropVO p : pList) {
                List<FormDefVO.FormItemPropVO> children = props.getOrDefault(p.getId(), Collections.emptyList());
                p.setChildren(children);
                vos.addAll(children);
            }
            pList = vos;
        }
        return vo;
    }

    private FormDefVO.FormItemPropVO toVO(FormItemProp entity) {
        return BeanUtil.copyProperties(entity, FormDefVO.FormItemPropVO.class, "children");
    }
}
