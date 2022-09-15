package me.aki.demo.camunda.entity.dto.node;

import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;

import java.util.List;

/**
 * 非连接线的流程定义节点
 */
public interface FlowNodeDTO extends NodeDTO {

    /**
     * 根据json node构建bpmn
     *
     * @param builder 使用{@link org.camunda.bpm.model.bpmn.Bpmn}生成
     * @return 生成的bpmn元素的id列表，不含sequenceFlow id
     */
    List<String> build(AbstractFlowNodeBuilder<?, ?> builder);

}
