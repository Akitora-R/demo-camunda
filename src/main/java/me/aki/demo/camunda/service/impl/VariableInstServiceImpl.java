package me.aki.demo.camunda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.VariableInst;
import me.aki.demo.camunda.mapper.VariableInstMapper;
import me.aki.demo.camunda.service.VariableInstService;
import org.springframework.stereotype.Service;

@Service
public class VariableInstServiceImpl extends ServiceImpl<VariableInstMapper, VariableInst> implements VariableInstService {
}
