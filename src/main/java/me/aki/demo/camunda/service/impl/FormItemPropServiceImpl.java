package me.aki.demo.camunda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.FormItemProp;
import me.aki.demo.camunda.mapper.FormItemPropMapper;
import me.aki.demo.camunda.service.FormItemPropService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FormItemPropServiceImpl extends ServiceImpl<FormItemPropMapper, FormItemProp> implements FormItemPropService {
}
