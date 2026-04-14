package com.clarityunited.quadtrivia.client.opentrivia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles dual-layer caching for OpenTrivia responses.
 * Layer 1: In-memory (ConcurrentHashMap)
 * Layer 2: Persistent (YAML file)
 */
@Component
public class OpenTriviaCache {

    private static final Logger log = LoggerFactory.getLogger(OpenTriviaCache.class);
    private final Map<Integer, OpenTriviaResponse> memoryCache = new ConcurrentHashMap<>();
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final String cacheFilePath;

    public OpenTriviaCache(@Value("${app.trivia.cache-file:trivia-cache.yaml}") String cacheFilePath) {
        this.cacheFilePath = cacheFilePath;
        loadFromFile();
    }

    public void put(int amount, OpenTriviaResponse response) {
        memoryCache.put(amount, response);
        saveToFile();
    }

    public Optional<OpenTriviaResponse> get(int amount) {
        return Optional.ofNullable(memoryCache.get(amount));
    }

    private synchronized void saveToFile() {
        try {
            yamlMapper.writeValue(new File(cacheFilePath), memoryCache);
        } catch (IOException e) {
            log.error("Failed to save OpenTrivia cache to file: {}", cacheFilePath, e);
        }
    }

    private void loadFromFile() {
        File file = new File(cacheFilePath);
        if (file.exists()) {
            try {
                Map<String, OpenTriviaResponse> loaded = yamlMapper.readValue(file, 
                    yamlMapper.getTypeFactory().constructMapType(Map.class, String.class, OpenTriviaResponse.class));
                
                loaded.forEach((key, value) -> memoryCache.put(Integer.parseInt(key), value));
                log.info("Loaded {} entries from OpenTrivia cache file: {}", memoryCache.size(), cacheFilePath);
            } catch (IOException e) {
                log.error("Failed to load OpenTrivia cache from file: {}", cacheFilePath, e);
            }
        }
    }
}
