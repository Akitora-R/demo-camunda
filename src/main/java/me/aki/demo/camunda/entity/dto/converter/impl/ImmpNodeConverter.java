package me.aki.demo.camunda.entity.dto.converter.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.aki.demo.camunda.entity.dto.VariableDefDTO;
import me.aki.demo.camunda.entity.dto.converter.NodeConverter;
import me.aki.demo.camunda.entity.dto.node.NodeDTO;
import me.aki.demo.camunda.entity.dto.node.impl.TaskFlowNodeDTO;
import me.aki.demo.camunda.enums.JsonNodeShape;
import me.aki.demo.camunda.enums.VariableSourceType;
import org.springframework.stereotype.Component;

import javax.validation.Validator;
import java.util.*;
import java.util.function.BiFunction;

@Component
public class ImmpNodeConverter implements NodeConverter {
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final Map<String, BiFunction<String, JsonNode, NodeDTO>> converts;

    public ImmpNodeConverter(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.converts = initConverters();
    }

    private Map<String, BiFunction<String, JsonNode, NodeDTO>> initConverters() {
        HashMap<String, BiFunction<String, JsonNode, NodeDTO>> m = new HashMap<>();
        m.put(JsonNodeShape.TASK.toString(), (id, jsonNode) -> {
            TaskFlowNodeDTO node = new TaskFlowNodeDTO();
            JsonNode data = jsonNode.get("data");
            String label = data.get("nodeName").asText();
            node.setLabel(label);
            // TODO: 2022/9/8 会签功能尚未实现，多个审核人将成为或签
            ArrayList<VariableDefDTO> variableList = new ArrayList<>();
            for (JsonNode a : data.get("approvalMan")) {
                VariableDefDTO v = new VariableDefDTO();
                v.setSourceType(VariableSourceType.BEAN);
                v.setVariableKey("");
            }
            node.setVariableList(variableList);
            return node;
        });
        m.put(JsonNodeShape.EDGE.toString(), (id, jsonNode) -> null);
        m.put(JsonNodeShape.END_EVENT.toString(), (id, jsonNode) -> null);
        m.put(JsonNodeShape.START_EVENT.toString(), (id, jsonNode) -> null);
        m.put(JsonNodeShape.EXCLUSIVE_GATEWAY.toString(), (id, jsonNode) -> null);
        return Collections.unmodifiableMap(m);
    }

    @Override
    public List<NodeDTO> convert(JsonNode jsonNode) {
        ArrayList<NodeDTO> l = new ArrayList<>();
        for (JsonNode node : jsonNode) {
            String id = node.get("id").asText();
            String shape = node.get("shape").asText();
            Optional.ofNullable(converts.get(shape)).ifPresent(f -> {
                NodeDTO nodeDTO = f.apply(id, jsonNode);
                validator.validate(nodeDTO);
                l.add(nodeDTO);
            });
        }
        return l;
    }
}
