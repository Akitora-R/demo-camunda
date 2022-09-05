package me.aki.demo.camunda.entity.dto.converter;

import me.aki.demo.camunda.entity.dto.node.NodeDTO;

import java.util.List;

public interface Converter {
    List<NodeDTO> convert(String json);
}
