package me.aki.demo.camunda.parser.impl;

import me.aki.demo.camunda.entity.bpmn.FlowNodeDTO;
import me.aki.demo.camunda.entity.bpmn.ServiceTaskFlowNodeDTO;
import me.aki.demo.camunda.parser.NodeParser;
import org.camunda.bpm.model.bpmn.builder.ServiceTaskBuilder;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;

public class ServiceTaskNodeParser extends NodeParser<ServiceTaskBuilder, ServiceTask> {
    @Override
    protected FlowNodeDTO<ServiceTaskBuilder, ServiceTask> doConv(ServiceTask node) {
        ServiceTaskFlowNodeDTO dto = new ServiceTaskFlowNodeDTO();
        dto.setLabel(node.getName());
        dto.setId(node.getId());
        dto.setClassName(node.getCamundaClass());
        return dto;
    }
}
