package me.aki.demo.camunda.entity.dto.converter;

import com.fasterxml.jackson.databind.JsonNode;
import me.aki.demo.camunda.entity.dto.node.NodeDTO;

import java.util.List;

public interface NodeConverter {
    List<NodeDTO> convert(JsonNode jsonNode);
}
