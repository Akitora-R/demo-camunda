package me.aki.demo.camunda.entity.dto.converter.impl;

import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.constant.BeanProviderId;
import me.aki.demo.camunda.constant.ImmpConstant;
import me.aki.demo.camunda.entity.dto.VariableDefDTO;
import me.aki.demo.camunda.entity.dto.converter.NodeConverter;
import me.aki.demo.camunda.entity.dto.node.NodeDTO;
import me.aki.demo.camunda.entity.dto.node.impl.*;
import me.aki.demo.camunda.enums.JsonNodeShape;
import me.aki.demo.camunda.enums.VariableSourceType;
import me.aki.demo.camunda.util.BpmnUtil;
import org.springframework.stereotype.Component;

import javax.validation.Validator;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ImmpNodeConverter implements NodeConverter {
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final Map<String, BiFunction<String, JsonNode, NodeDTO>> converts;

    public ImmpNodeConverter(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.converts = initConverters();
    }

    private VariableDefDTO.VariableDefPropDTO getAssigneeListProp(JsonNode assigneeObj) {
        String assigneeType = assigneeObj.get("type").asText();
        switch (assigneeType) {
            case "SUPERIOR", "DEPT_HEAD", "ROLE" -> {
                VariableDefDTO.VariableDefPropDTO propDTO = new VariableDefDTO.VariableDefPropDTO();
                propDTO.setPropKey(assigneeType);
                ArrayList<VariableDefDTO.VariableDefPropDTO> children = new ArrayList<>();
                propDTO.setChildren(children);
                for (JsonNode extParam : assigneeObj.get("data")) {
                    // TODO: 2022/9/16 做校验
                    children.add(new VariableDefDTO.VariableDefPropDTO(null, extParam.get(ImmpConstant.PROP_KEY_FIELD).asText(), extParam.get(ImmpConstant.PROP_KEY_FIELD).asText(), null));
                }
                return propDTO;
            }
            case "DESIGNATION" -> {
                VariableDefDTO.VariableDefPropDTO propDTO = new VariableDefDTO.VariableDefPropDTO();
                propDTO.setPropKey(assigneeType);
                ArrayList<VariableDefDTO.VariableDefPropDTO> children = new ArrayList<>();
                propDTO.setChildren(children);
                for (JsonNode extParam : assigneeObj.get("data")) {
                    if (extParam.get(ImmpConstant.PROP_KEY_FIELD).asText().equals("USER_LIST")) {
                        children.add(new VariableDefDTO.VariableDefPropDTO(null, extParam.get(ImmpConstant.PROP_KEY_FIELD).asText(), null,
                                objectMapper.convertValue(extParam.get(ImmpConstant.PROP_VAL_FIELD), new TypeReference<List<String>>() {
                                }).stream().map(e -> new VariableDefDTO.VariableDefPropDTO(null, null, e, null)).collect(Collectors.toList())));
                    }
                }
                return propDTO;
            }
            case "SELF" -> {
                VariableDefDTO.VariableDefPropDTO propDTO = new VariableDefDTO.VariableDefPropDTO();
                propDTO.setPropKey(assigneeType);
                return propDTO;
            }
            default -> throw new IllegalArgumentException();
        }
    }

    private Map<String, BiFunction<String, JsonNode, NodeDTO>> initConverters() {
        HashMap<String, BiFunction<String, JsonNode, NodeDTO>> m = new HashMap<>();
        m.put(JsonNodeShape.TASK.toString(), (id, jsonNode) -> {
            TaskFlowNodeDTO node = new TaskFlowNodeDTO();
            node.setId(id);
            node.setIncomingNodeId(id);
            JsonNode data = jsonNode.get("data");
            String label = Optional.ofNullable(data.get("nodeName")).map(JsonNode::asText).orElse(null);
            node.setLabel(label);
            node.setCode("userTaskCode_" + UUID.randomUUID().toString().replace("-", ""));
            ArrayList<VariableDefDTO> variableList = new ArrayList<>();
            JsonNode assigneeArray = data.get("approvalMan");
            Assert.isTrue(assigneeArray != null && assigneeArray.isArray() && !assigneeArray.isEmpty(), "审核节点: {} 未配置审核人", id);
            ArrayList<VariableDefDTO.VariableDefPropDTO> subProp = new ArrayList<>();
            //noinspection ConstantConditions
            for (JsonNode assigneeObj : assigneeArray) {
                subProp.add(getAssigneeListProp(assigneeObj));
            }
            if (assigneeArray.size() == 1) {
                node.setIsLoop(false);
                String varKey = node.getAssigneeVarKey();
                node.setAssignee(BpmnUtil.toVarExp(varKey));
                VariableDefDTO v = new VariableDefDTO();
                variableList.add(v);
                v.setVariableKey(varKey);
                v.setSourceType(VariableSourceType.BEAN);
                v.setSourceIdentifier(BeanProviderId.USER_PROVIDER);
                v.setRequired(true);
                v.setPropList(subProp);
            } else if (assigneeArray.size() > 1) {
                node.setIsLoop(true);
                node.setIsCountersign(true);
                VariableDefDTO v = new VariableDefDTO();
                variableList.add(v);
                String varKey = node.getAssigneeListVarKey();
                v.setVariableKey(varKey);
                v.setSourceType(VariableSourceType.BEAN);
                v.setSourceIdentifier(BeanProviderId.USER_PROVIDER);
                v.setRequired(true);
                v.setPropList(subProp);
            }
            node.setVariableList(variableList);
            return node;
        });
        m.put(JsonNodeShape.END_EVENT.toString(), (id, jsonNode) -> {
            EndEventFlowNodeDTO endEvent = new EndEventFlowNodeDTO();
            endEvent.setId(id);
            return endEvent;
        });
        m.put(JsonNodeShape.START_EVENT.toString(), (id, jsonNode) -> {
            StartEventFlowNodeDTO startEvent = new StartEventFlowNodeDTO();
            startEvent.setId(id);
            return startEvent;
        });
        BiFunction<String, JsonNode, NodeDTO> gatewayConverter = (id, jsonNode) -> {
            ExclusiveGatewayFlowNodeDTO exGateway = new ExclusiveGatewayFlowNodeDTO();
            exGateway.setId(id);
            return exGateway;
        };
        m.put("add", gatewayConverter);
        m.put("empty", gatewayConverter);
        return Collections.unmodifiableMap(m);
    }

    private List<EdgeNodeDTO> tidyUpEdges(JsonNode jsonNode, Map<String, JsonNode> idNodeMap) {
        ArrayList<EdgeNodeDTO> edges = new ArrayList<>();
        for (JsonNode node : jsonNode) {
            String id = node.get("id").asText();
            String shape = node.get("shape").asText();
            if (JsonNodeShape.EDGE.toString().equals(shape)) {
                String sourceFiledName = "source";
                String targetFiledName = "target";
                String sourceId = node.get(sourceFiledName).asText();
                String targetId = node.get(targetFiledName).asText();
                JsonNode n;
                EdgeNodeDTO edge = new EdgeNodeDTO();
                edge.setId(id);
                if ((n = idNodeMap.get(sourceId)).get("shape").asText().equals("proviso") || n.get("shape").asText().equals("default")) {
                    sourceId = getReplacement(jsonNode, idNodeMap, sourceFiledName, targetFiledName, sourceId, n, edge);
                } else if ((n = idNodeMap.get(targetId)).get("shape").asText().equals("proviso") || n.get("shape").asText().equals("default")) {
                    targetId = getReplacement(jsonNode, idNodeMap, targetFiledName, sourceFiledName, targetId, n, edge);
                }
                edge.setSource(sourceId);
                edge.setTarget(targetId);
                if (edges.stream().anyMatch(e -> edge.getSource().equals(e.getSource()) && edge.getTarget().equals(e.getTarget()))) {
                    continue;
                }
                edges.add(edge);
            }
        }
        return edges;
    }

    private String getReplacement(JsonNode jsonNode, Map<String, JsonNode> idNodeMap, String sourceFiledName, String targetFiledName, String sourceId, JsonNode n, EdgeNodeDTO edge) {
        for (JsonNode node1 : jsonNode) {
            if (JsonNodeShape.EDGE.toString().equals(node1.get("shape").asText()) && node1.get(targetFiledName).asText().equals(n.get("id").asText())) {
                sourceId = idNodeMap.get(node1.get(sourceFiledName).asText()).get("id").asText();
                JsonNode dataNode = n.get("data");
                if (dataNode != null) {
                    edge.setCondition(Optional.ofNullable(dataNode.get("condition")).map(JsonNode::asText).orElse(null));
                    edge.setVariableList(getVarList(dataNode.get("variableList")));
                }
            }
        }
        return sourceId;
    }

    private List<VariableDefDTO> getVarList(JsonNode jsonNode) {
        ArrayList<VariableDefDTO> varList = new ArrayList<>();
        Optional.ofNullable(jsonNode).ifPresent(vl -> vl.forEach(v -> {
            String variableKey = Optional.ofNullable(v.get("variableKey")).map(JsonNode::asText).orElse(null);
            VariableSourceType sourceType = VariableSourceType.valueOf(v.get("sourceType").asText());
            String sourceIdentifier = Optional.ofNullable(v.get("sourceIdentifier")).map(JsonNode::asText).orElse(null);
            Boolean required = Optional.ofNullable(v.get("required")).map(JsonNode::asBoolean).orElse(false);
            VariableDefDTO dto = new VariableDefDTO();
            dto.setVariableKey(variableKey);
            dto.setSourceType(sourceType);
            dto.setSourceIdentifier(sourceIdentifier);
            dto.setRequired(required);
            dto.setPropList(getVarPropList(v.get("propList")));
            varList.add(dto);
        }));
        return varList;
    }

    private List<VariableDefDTO.VariableDefPropDTO> getVarPropList(JsonNode jsonNode) {
        if (jsonNode == null || !jsonNode.isArray()) {
            return Collections.emptyList();
        }
        ArrayList<VariableDefDTO.VariableDefPropDTO> l = new ArrayList<>();
        for (JsonNode node : jsonNode) {
            VariableDefDTO.VariableDefPropDTO prop = new VariableDefDTO.VariableDefPropDTO();
            prop.setPropKey(Optional.ofNullable(node.get("propKey")).map(JsonNode::asText).orElse(null));
            prop.setPropVal(Optional.ofNullable(node.get("propVal")).map(JsonNode::asText).orElse(null));
            prop.setChildren(getVarPropList(node.get("children")));
            l.add(prop);
        }
        return l;
    }

    private Map<String, JsonNode> toNodeMap(JsonNode jsonNode) {
        HashMap<String, JsonNode> m = new HashMap<>();
        for (JsonNode node : jsonNode) {
            m.put(node.get("id").asText(), node);
        }
        return m;
    }

    @Override
    public List<NodeDTO> convert(JsonNode jsonNode) {
        Map<String, JsonNode> idNodeMap = toNodeMap(jsonNode);
        List<EdgeNodeDTO> edges = tidyUpEdges(jsonNode, idNodeMap);
        ArrayList<NodeDTO> l = new ArrayList<>(edges);
        for (JsonNode node : jsonNode) {
            JsonNode idNode = node.get("id");
            Assert.notNull(idNode, "节点id不存在");
            String id = idNode.asText();
            Assert.notBlank(id, "存在空id");
            String shape = node.get("shape").asText();
            Optional.ofNullable(converts.get(shape)).ifPresent(f -> {
                NodeDTO nodeDTO = f.apply(id, node);
                validator.validate(nodeDTO);
                l.add(nodeDTO);
            });
        }
        return l;
    }
}