package me.aki.demo.camunda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.ProcDefNodeProp;
import me.aki.demo.camunda.mapper.ProcDefNodePropMapper;
import me.aki.demo.camunda.service.ProcDefNodePropService;
import org.springframework.stereotype.Service;

@Service
public class ProcDefNodePropServiceImpl extends ServiceImpl<ProcDefNodePropMapper, ProcDefNodeProp> implements ProcDefNodePropService {
}
