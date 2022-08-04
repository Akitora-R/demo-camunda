package me.aki.demo.camunda.service.impl;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.aki.demo.camunda.entity.ProcDefNode;
import me.aki.demo.camunda.entity.ProcDefNodeProp;
import me.aki.demo.camunda.entity.dto.node.NodeDTO;
import me.aki.demo.camunda.entity.dto.node.impl.EdgeNodeDTO;
import me.aki.demo.camunda.entity.dto.node.impl.TaskFlowNodeDTO;
import me.aki.demo.camunda.mapper.ProcDefNodeMapper;
import me.aki.demo.camunda.service.ProcDefNodeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProcDefNodeServiceImpl extends ServiceImpl<ProcDefNodeMapper, ProcDefNode> implements ProcDefNodeService {
    @Override
    public Pair<ProcDefNode, List<ProcDefNodeProp>> toEntity(NodeDTO dto) {
        ProcDefNode pdn = new ProcDefNode();
        pdn.setNodeLabel(dto.getLabel());
        pdn.setNodeShape(dto.getShape());
        pdn.setNodeJsonId(dto.getId());
        ArrayList<ProcDefNodeProp> props = new ArrayList<>();
        if (dto instanceof TaskFlowNodeDTO task) {
            props.add(new ProcDefNodeProp("code", task.getCode()));
            props.add(new ProcDefNodeProp("incomingNodeId", task.getIncomingNodeId()));
            props.add(new ProcDefNodeProp("outgoingNodeId", task.getOutgoingNodeId()));
        } else if (dto instanceof EdgeNodeDTO edge) {
            props.add(new ProcDefNodeProp("source", edge.getSource()));
            props.add(new ProcDefNodeProp("target", edge.getTarget()));
            props.add(new ProcDefNodeProp("condition", edge.getCondition()));
        }
        return new Pair<>(pdn, props);
    }
}
