package me.aki.demo.camunda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.ProcInstTaskProp;
import me.aki.demo.camunda.mapper.ProcInstTaskPropMapper;
import me.aki.demo.camunda.service.ProcInstTaskPropService;
import org.springframework.stereotype.Service;

@Service
public class ProcInstTaskPropServiceImpl extends ServiceImpl<ProcInstTaskPropMapper,ProcInstTaskProp> implements ProcInstTaskPropService {
}
