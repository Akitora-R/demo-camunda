package me.aki.demo.camunda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.ProcDefVariableProp;
import me.aki.demo.camunda.mapper.ProcDefVariablePropMapper;
import me.aki.demo.camunda.service.ProcDefVariablePropService;
import org.springframework.stereotype.Service;

@Service
public class ProcDefVariablePropServiceImpl extends ServiceImpl<ProcDefVariablePropMapper, ProcDefVariableProp> implements ProcDefVariablePropService {
}
