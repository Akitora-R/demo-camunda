package me.aki.demo.camunda.entity.dto.node;

/**
 * 可一对应多bpmn节点的流程节点
 */
public interface CombinationNodeDTO extends FlowNodeDTO {
    /**
     * 定义该组节点内接收上一个节点的指向的节点id
     */
    String getIncomingNodeId();

    /**
     * 定义从该组节点处最终实际上连接到下一个节点的节点id
     */
    String getOutgoingNodeId();
}
