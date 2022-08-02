package me.aki.demo.camunda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.FormInst;
import me.aki.demo.camunda.mapper.FormInstMapper;
import me.aki.demo.camunda.service.FormInstService;
import org.springframework.stereotype.Service;

@Service
public class FormInstServiceImpl extends ServiceImpl<FormInstMapper, FormInst> implements FormInstService {
}
