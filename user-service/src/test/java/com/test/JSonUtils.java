package com.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class JSonUtils {

    public static <T> T parseFromJson(String CredentialPayloadAsJson, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(CredentialPayloadAsJson, clazz);
    }

    public static <T> List<T> parseFromJsonList(String CredentialPayloadAsJson, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(CredentialPayloadAsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    public static <T> String serializeToJson(T CredentialPayloadAsJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(CredentialPayloadAsJson);
    }
}