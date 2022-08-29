package me.aki.demo.camunda.service;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.extension.service.IService;
import me.aki.demo.camunda.entity.ProcDefNode;
import me.aki.demo.camunda.entity.ProcDefNodeProp;
import me.aki.demo.camunda.entity.dto.node.NodeDTO;

import java.util.List;

public interface ProcDefNodeService extends IService<ProcDefNode> {
    /**
     * 转实体类
     * @param dto dto
     * @return pair of 节点 -> [节点属性]
     */
    Pair<ProcDefNode, List<ProcDefNodeProp>> toEntity(NodeDTO dto);

    ProcDefNode findByCamundaBpmnElemId(String camundaElementId, String camundaProcDefId);
}
