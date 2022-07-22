package me.aki.demo.camunda.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.camunda.bpm.engine.repository.ProcessDefinition;

import java.io.IOException;

public class CamundaProcessDefinitionSerializer extends StdSerializer<ProcessDefinition> {
    protected CamundaProcessDefinitionSerializer(Class<ProcessDefinition> t) {
        super(t);
    }

    @Override
    public void serialize(ProcessDefinition processDefinition, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("id", processDefinition.getId());
        jsonGenerator.writeEndObject();
    }
}
