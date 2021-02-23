package com.spring.batch.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ListToJsonConverter implements AttributeConverter<List<String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attributes) {
        String dbData = null;
        try {
            dbData = objectMapper.writeValueAsString(attributes);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }

        return dbData;
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        List<String> attributes = null;
        try {
            attributes = objectMapper.readValue(dbData, List.class);
        } catch (final IOException e) {
            log.error("JSON reading error", e);
        }

        return attributes;
    }

}
