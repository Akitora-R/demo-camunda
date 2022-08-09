package me.aki.demo.camunda.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Configuration
@Slf4j
public class JacksonConfig {
    @Value("${spring.jackson.default-property-inclusion}")
    private JsonInclude.Include include;

    private abstract static class BpmnSerializer<T> extends StdSerializer<T> {

        protected BpmnSerializer(Class<T> t) {
            super(t);
        }

        protected String formatISO8601(Date date) {
            return Optional.ofNullable(date).map(t -> t.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().toString()).orElse(null);
        }
    }

    private static class CamundaProcessDefinitionSerializer extends BpmnSerializer<ProcessDefinition> {
        protected CamundaProcessDefinitionSerializer() {
            super(ProcessDefinition.class);
        }

        @Override
        public void serialize(ProcessDefinition processDefinition, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("id", processDefinition.getId());
            jsonGenerator.writeStringField("key", processDefinition.getKey());
            jsonGenerator.writeStringField("name", processDefinition.getName());
            jsonGenerator.writeNumberField("version", processDefinition.getVersion());
            jsonGenerator.writeEndObject();
        }
    }

    private static class CamundaProcessInstanceSerializer extends BpmnSerializer<ProcessInstance> {
        protected CamundaProcessInstanceSerializer() {
            super(ProcessInstance.class);
        }

        @Override
        public void serialize(ProcessInstance value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("id", value.getId());
            jsonGenerator.writeStringField("businessKey", value.getBusinessKey());
            jsonGenerator.writeEndObject();
        }
    }

    private static class CamundaTaskSerializer extends BpmnSerializer<Task> {
        protected CamundaTaskSerializer() {
            super(Task.class);
        }

        @Override
        public void serialize(Task value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("id", value.getId());
            jsonGenerator.writeStringField("name", value.getName());
            jsonGenerator.writeStringField("assignee", value.getAssignee());
            jsonGenerator.writeStringField("createTime", formatISO8601(value.getCreateTime()));
            jsonGenerator.writeEndObject();
        }
    }
    private static class CamundaHistoricTaskSerializer extends BpmnSerializer<HistoricTaskInstance> {
        protected CamundaHistoricTaskSerializer() {
            super(HistoricTaskInstance.class);
        }

        @Override
        public void serialize(HistoricTaskInstance value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("id", value.getId());
            jsonGenerator.writeStringField("name", value.getName());
            jsonGenerator.writeStringField("assignee", value.getAssignee());
            jsonGenerator.writeStringField("startTime", formatISO8601(value.getStartTime()));
            jsonGenerator.writeStringField("endTime", formatISO8601(value.getEndTime()));
            jsonGenerator.writeEndObject();
        }
    }

    private static class CamundaHistoricProcessInstanceSerializer extends BpmnSerializer<HistoricProcessInstance> {
        protected CamundaHistoricProcessInstanceSerializer() {
            super(HistoricProcessInstance.class);
        }

        @Override
        public void serialize(HistoricProcessInstance value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("id", value.getId());
            jsonGenerator.writeStringField("businessKey", value.getBusinessKey());
            jsonGenerator.writeStringField("processDefinitionId", value.getProcessDefinitionId());
            jsonGenerator.writeStringField("processDefinitionKey", value.getProcessDefinitionKey());
            jsonGenerator.writeStringField("startTime", formatISO8601(value.getStartTime()));
            jsonGenerator.writeStringField("endTime", formatISO8601(value.getEndTime()));
            jsonGenerator.writeStringField("state", value.getState());
            jsonGenerator.writeEndObject();
        }
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(new CamundaProcessDefinitionSerializer());
        module.addSerializer(new CamundaProcessInstanceSerializer());
        module.addSerializer(new CamundaTaskSerializer());
        module.addSerializer(new CamundaHistoricProcessInstanceSerializer());
        module.addSerializer(new CamundaHistoricTaskSerializer());
        objectMapper.registerModule(module);
        objectMapper.setDefaultPropertyInclusion(include);
        log.debug("object property inclusion: {}", include);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}
