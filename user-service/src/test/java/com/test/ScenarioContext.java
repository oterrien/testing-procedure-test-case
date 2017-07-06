package com.test;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

    private final Map<String, Object> Context = new HashMap<>();

    public <T> void put(String key, T object) {
        Context.put(key, object);
    }

    public <T> T get(String type, Class<T> objectClass) {
        return objectClass.cast(Context.get(type));
    }

    public Object get(String type) {
        return Context.get(type);
    }

    public void clear() {
        Context.clear();
    }

}
