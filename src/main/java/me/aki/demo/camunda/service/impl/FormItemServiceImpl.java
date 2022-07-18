package me.aki.demo.camunda.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.FormItem;
import me.aki.demo.camunda.entity.FormItemProp;
import me.aki.demo.camunda.entity.dto.FormDTO;
import me.aki.demo.camunda.enums.FormItemType;
import me.aki.demo.camunda.mapper.FormItemMapper;
import me.aki.demo.camunda.service.FormItemPropService;
import me.aki.demo.camunda.service.FormItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Transactional
public class FormItemServiceImpl extends ServiceImpl<FormItemMapper, FormItem> implements FormItemService {
    private final FormItemPropService formItemPropService;
    private Map<FormItemType, Consumer<List<FormItemProp>>> validators;

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
            // TODO: 2022/7/18
        });
        validators = m;
    }

    public FormItemServiceImpl(FormItemPropService formItemPropService) {
        this.formItemPropService = formItemPropService;
    }

    @Override
    public void saveDTO(FormDTO.FormItemDTO dto) {
        save(dto.getFormItem());
        Optional.ofNullable(validators.get(dto.getFormItem().getFormItemType())).ifPresent(c -> c.accept(dto.getFormItemPropList()));
        formItemPropService.saveBatch(dto.getFormItemPropList());
    }
}
