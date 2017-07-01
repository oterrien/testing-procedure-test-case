package com.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JSonUtils {

    public static <T> T parseFromJson(String CredentialPayloadAsJson, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(CredentialPayloadAsJson, clazz);
    }

    public static <T> String serializeToJson(T CredentialPayloadAsJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(CredentialPayloadAsJson);
    }
}