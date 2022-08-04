package me.aki.demo.camunda.constant;

import me.aki.demo.camunda.entity.dto.node.NodeDTO;

import java.util.function.Function;
import java.util.regex.Pattern;

public interface IdPattern {
    String UUID_PATTERN = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
    String START_EVENT_PREFIX = "startEvent_";
    String END_EVENT_PREFIX = "endEvent_";
    String TASK_PREFIX = "userTask_";
    String EXCLUSIVE_GATEWAY_PREFIX = "exclusiveGateway_";

    Pattern START_EVENT_PATTERN = Pattern.compile(START_EVENT_PREFIX + UUID_PATTERN);
    Pattern END_EVENT_PATTERN = Pattern.compile(END_EVENT_PREFIX + UUID_PATTERN);
    Pattern TASK_PATTERN = Pattern.compile(TASK_PREFIX + UUID_PATTERN);
    Pattern EXCLUSIVE_GATEWAY_PATTERN = Pattern.compile(EXCLUSIVE_GATEWAY_PREFIX + UUID_PATTERN);

    Function<String, String> ERR_MSG_TEMPLATE = id -> String.format("id应该以 %s 为前缀并后接UUIDv4", id);
    Function<Msg<? extends NodeDTO>, String> WARN_MSG_TEMPLATE = m -> String.format("%s id: %s 不符合id规则，重新生成id: %s", m.c, m.oldId, m.newId);

    class Msg<C extends NodeDTO> {
        Class<C> c;
        String oldId;
        String newId;

        public static <C extends NodeDTO> Msg<C> of(Class<C> c, String oldId, String newId) {
            Msg<C> m = new Msg<>();
            m.c = c;
            m.oldId = oldId;
            m.newId = newId;
            return m;
        }
    }
}
