package com.example.holidayplanner.helpers;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class CacheHelper<T> {
    private final RedisTemplate<String, T> cache;

    public CacheHelper(RedisTemplate<String, T> cache) {
        this.cache = cache;
    }

    public void cacheEntry(String cacheKey, T entryToCache) {
        System.out.println("Caching entry...");
        cache.opsForValue().set(cacheKey, entryToCache);
    }

    public void cacheEntries(List<T> entriesToCache, Function<T, String> cacheKeyExtractor) {
        System.out.println("Caching entries...");
        for (T entry : entriesToCache) {
            String id = cacheKeyExtractor.apply(entry);
            cache.opsForValue().set(id, entry);
        }
    }
    
    public T getCachedEntry(String cachedKey) {
        System.out.println("Getting entry from cache...");
        return cache.opsForValue().get(cachedKey);
    }

    public List<T> getCachedEntries(List<String> cachedKey) {
        System.out.println("Getting entries from cache...");
        System.out.println(this.cache);
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
