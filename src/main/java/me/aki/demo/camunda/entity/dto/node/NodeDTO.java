package me.aki.demo.camunda.entity.dto.node;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.aki.demo.camunda.entity.dto.node.impl.*;
import me.aki.demo.camunda.enums.BpmnShape;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "shape"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EdgeNodeDTO.class, name = "EDGE"),
        @JsonSubTypes.Type(value = EndEventFlowNodeDTO.class, name = "END_EVENT"),
        @JsonSubTypes.Type(value = ExclusiveGatewayFlowNodeDTO.class, name = "EXCLUSIVE_GATEWAY"),
        @JsonSubTypes.Type(value = StartEventFlowNodeDTO.class, name = "START_EVENT"),
        @JsonSubTypes.Type(value = TaskFlowNodeDTO.class, name = "TASK"),
})
public interface NodeDTO {
    String getId();

    String getLabel();

    BpmnShape getShape();

    void tidyUp();
}
