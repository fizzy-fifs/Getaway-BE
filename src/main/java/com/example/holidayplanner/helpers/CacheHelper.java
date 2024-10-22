package com.example.holidayplanner.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CacheHelper<T> {

    @Autowired
    private final CacheManager cacheManager;

    private final String cacheName;

    public CacheHelper(CacheManager cacheManager, Class<T> type) {
        this.cacheManager = cacheManager;
        this.cacheName = type.getName().toLowerCase();
    }

    public List<T> getCachedEntries(List<String> cachedKey) {
        Cache cache = cacheManager.getCache(cacheName);
        assert cache != null;

        List<T> cachedEntries = new ArrayList<>();
        for (String key : cachedKey){
            Cache.ValueWrapper cachedValue = cache.get(key);
            if (cachedValue != null) {
                cachedEntries.add((T) cachedValue.get());
            }
        }

        return cachedEntries;
    }

    public void cacheEntries(List<T> itemsToCache, Function<T, String> idExtractor) {
        Cache cache = cacheManager.getCache(cacheName);
        assert cache != null;

        for (T item : itemsToCache) {
            String id = idExtractor.apply(item);
            cache.put(id, item);
        }
    }
}
