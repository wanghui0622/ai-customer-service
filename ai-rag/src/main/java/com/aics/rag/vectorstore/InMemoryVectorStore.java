package com.aics.rag.vectorstore;

import java.util.LinkedHashMap;
import java.util.Map;

public class InMemoryVectorStore {
    private final Map<String, String> store = new LinkedHashMap<>();

    public void save(String id, String text) {
        store.put(id, text);
    }

    public Map<String, String> all() {
        return Map.copyOf(store);
    }
}
