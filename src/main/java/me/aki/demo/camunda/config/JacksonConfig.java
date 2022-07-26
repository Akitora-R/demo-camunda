package me.aki.demo.camunda.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
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

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(new CamundaProcessDefinitionSerializer());
        objectMapper.registerModule(module);
        objectMapper.setDefaultPropertyInclusion(include);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}
