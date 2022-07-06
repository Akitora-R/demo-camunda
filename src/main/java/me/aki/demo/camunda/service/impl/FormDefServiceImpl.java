package me.aki.demo.camunda.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.FormDef;
import me.aki.demo.camunda.mapper.FormDefMapper;
import org.springframework.stereotype.Service;

@Service
public class FormDefServiceImpl extends ServiceImpl<FormDefMapper, FormDef> implements IService<FormDef> {
}
