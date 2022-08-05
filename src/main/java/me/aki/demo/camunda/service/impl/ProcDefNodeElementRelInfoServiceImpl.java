package me.aki.demo.camunda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.ProcDefNodeElementRelInfo;
import me.aki.demo.camunda.mapper.ProcDefNodeElementRelInfoMapper;
import me.aki.demo.camunda.service.ProcDefNodeElementRelInfoService;
import org.springframework.stereotype.Service;

@Service
public class ProcDefNodeElementRelInfoServiceImpl extends ServiceImpl<ProcDefNodeElementRelInfoMapper, ProcDefNodeElementRelInfo> implements ProcDefNodeElementRelInfoService {
    @Override
    public void saveRel(String nodeId, String elemId) {
        ProcDefNodeElementRelInfo e = new ProcDefNodeElementRelInfo();
        e.setProcDefNodeId(nodeId);
        e.setCamundaElementId(elemId);
        save(e);
    }
}
