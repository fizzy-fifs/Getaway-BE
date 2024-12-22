package com.example.holidayplanner.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CacheHelper<T> {
    private final RedisTemplate<String, Object> cache;
    private final ObjectMapper mapper;
    private final Class<T> type;

    public CacheHelper(RedisTemplate<String, Object> cache, ObjectMapper mapper, Class<T> type) {
        this.cache = cache;
        this.mapper = mapper;
        this.type = type;
    }

    public void cacheEntry(String cacheName, String cacheKey, T entryToCache) {
        cache.opsForHash().put(cacheName, cacheKey, entryToCache);
    }

    public void cacheEntry(String cacheKey, T entryToCache, String hashName) {
        cache.opsForHash().put(hashName, cacheKey, entryToCache);
    }

    public void cacheEntries(String cacheName, List<T> entriesToCache, Function<T, String> cacheKeyExtractor) {
        for (T entry : entriesToCache) {
            String id = cacheKeyExtractor.apply(entry);
            cache.opsForHash().put(cacheName, id, entry);
        }
    }

    public void cacheEntries(List<T> entriesToCache, Function<T, String> cacheKeyExtractor, String hashName) {
        System.out.println("Caching entries into " + hashName + " cache...");
        for (T entry : entriesToCache) {
            String id = cacheKeyExtractor.apply(entry);
            cache.opsForHash().put(hashName, id, entry);
        }
    }

    public T getCachedEntry(String cacheName, String cachedKey) {
        System.out.println("Getting entry from " + cacheName + " cache...");
        Object cachedObject = cache.opsForHash().get(cacheName, cachedKey);
        return mapper.convertValue(cachedObject, type);
    }

    public List<T> getCachedEntries(String cacheName, List<String> cachedKey) {
        System.out.println("Getting entries from cache...");
        List<T> cachedEntries = new ArrayList<>();
        for (String key : cachedKey) {
            T cachedValue = (T) cache.opsForHash().get(cacheName, key);
            if (cachedValue != null) {
                cachedEntries.add(cachedValue);
            }
        }

        return cachedEntries;
    }

    public void removeEntryFromCache(String cachedKey) {
        cache.opsForValue().getAndDelete(cachedKey);
    }
}
