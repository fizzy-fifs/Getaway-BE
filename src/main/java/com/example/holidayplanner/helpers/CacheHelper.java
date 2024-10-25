package com.example.holidayplanner.helpers;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CacheHelper<T> {

    private final Class<T> cacheEntryType;

    private final Cache cache;

    public CacheHelper(CacheManager cacheManager, String cacheName, Class<T> cacheEntryType) {
        this.cacheEntryType = cacheEntryType;
        this.cache = cacheManager.getCache(cacheName);
        assert cache != null;
    }

    public void cacheEntry(T entryToCache, String cacheKey) {
        cache.put(cacheKey, entryToCache);
    }

    public void cacheEntries(List<T> entriesToCache, Function<T, String> cacheKeyExtractor) {
        for (T entry : entriesToCache) {
            String id = cacheKeyExtractor.apply(entry);
            cache.put(id, entry);
        }
    }
    
    public T getCachedEntry(String cachedKey) {
        return cache.get(cachedKey, cacheEntryType);
    }

    public List<T> getCachedEntries(List<String> cachedKey) {
        List<T> cachedEntries = new ArrayList<>();
        for (String key : cachedKey){
            T cachedValue = cache.get(key, cacheEntryType);
            if (cachedValue != null) {
                cachedEntries.add(cachedValue);
            }
        }

        return cachedEntries;
    }
}
