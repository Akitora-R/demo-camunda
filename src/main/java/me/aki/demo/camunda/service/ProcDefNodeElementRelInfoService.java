package me.aki.demo.camunda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.aki.demo.camunda.entity.ProcDefNodeElementRelInfo;

public interface ProcDefNodeElementRelInfoService extends IService<ProcDefNodeElementRelInfo> {
    void saveRel(String nodeId,String elemId);
}
