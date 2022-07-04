package me.aki.demo.camunda.parser.impl;

import me.aki.demo.camunda.entity.bpmn.FlowNodeDTO;
import me.aki.demo.camunda.entity.bpmn.impl.ServiceTaskFlowNodeDTO;
import me.aki.demo.camunda.parser.NodeParser;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;

public class ServiceTaskNodeParser extends NodeParser<ServiceTask> {
    @Override
    protected FlowNodeDTO doConv(ServiceTask node) {
        ServiceTaskFlowNodeDTO dto = new ServiceTaskFlowNodeDTO();
        dto.setLabel(node.getName());
        dto.setId(node.getId());
        dto.setClassName(node.getCamundaClass());
        return dto;
    }
}
