package com.example.holidayplanner.config;

import com.example.holidayplanner.helpers.CacheHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class CacheConfig {
    @Bean
    public RedisTemplate<?,?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<?,?> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

//    @Bean
//    public <T> CacheHelper<T> cacheHelper(RedisTemplate<String,T> redisTemplate) {
//        CacheHelper<T> cacheHelper = new CacheHelper<>();
//        cacheHelper.setCache(redisTemplate);
//        return cacheHelper;
//    }
}
