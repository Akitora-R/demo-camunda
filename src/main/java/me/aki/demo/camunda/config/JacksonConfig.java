package me.aki.demo.camunda.config;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class JacksonConfig {
    @Value("${spring.jackson.default-property-inclusion}")
    private JsonInclude.Include include;

    private static class CamundaProcessDefinitionSerializer extends StdSerializer<ProcessDefinition> {
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

    private static class CamundaProcessInstanceSerializer extends StdSerializer<ProcessInstance> {
        protected CamundaProcessInstanceSerializer() {
            super(ProcessInstance.class);
        }

        @Override
        public void serialize(ProcessInstance processInstance, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("id", processInstance.getId());
            jsonGenerator.writeStringField("businessKey", processInstance.getBusinessKey());
            jsonGenerator.writeEndObject();
        }
    }

    private static class CamundaTaskSerializer extends StdSerializer<Task> {
        protected CamundaTaskSerializer() {
            super(Task.class);
        }

        @Override
        public void serialize(Task task, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("id", task.getId());
            jsonGenerator.writeStringField("name", task.getName());
            jsonGenerator.writeStringField("assignee", task.getAssignee());
            jsonGenerator.writeStringField("createTime", DateUtil.format(task.getCreateTime(), "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
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
        objectMapper.registerModule(module);
        objectMapper.setDefaultPropertyInclusion(include);
        log.debug("object property inclusion: {}", include);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}
