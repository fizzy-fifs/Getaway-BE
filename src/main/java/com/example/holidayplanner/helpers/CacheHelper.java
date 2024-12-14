package com.example.holidayplanner.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CacheHelper<T> {
    @Autowired
    private RedisTemplate<String, T> cache;

    public void cacheEntry(String cacheKey, T entryToCache) {
        cache.opsForValue().set(cacheKey, entryToCache);
    }

    public void cacheEntries(List<T> entriesToCache, Function<T, String> cacheKeyExtractor) {
        for (T entry : entriesToCache) {
            String id = cacheKeyExtractor.apply(entry);
            cache.opsForValue().set(id, entry);
        }
    }
    
    public T getCachedEntry(String cachedKey) {
        return cache.opsForValue().get(cachedKey);
    }

    public List<T> getCachedEntries(List<String> cachedKey) {
        List<T> cachedEntries = new ArrayList<>();
        for (String key : cachedKey){
            T cachedValue = cache.opsForValue().get(key);
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
