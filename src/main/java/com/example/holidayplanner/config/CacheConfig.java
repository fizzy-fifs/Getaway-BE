package com.example.holidayplanner.config;

import com.example.holidayplanner.helpers.CacheHelper;
import com.example.holidayplanner.models.GroupInvite;
import com.example.holidayplanner.models.HolidayInvite;
import com.example.holidayplanner.models.group.Group;
import com.example.holidayplanner.models.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {
    @Bean
    public RedisTemplate<String,?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String,?> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<Object> jsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jsonRedisSerializer);
        template.setValueSerializer(jsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheHelper<User> userCacheHelper(RedisTemplate<String,Object> redisTemplate, ObjectMapper objectMapper) {
        return new CacheHelper<>(redisTemplate, objectMapper, User.class);
    }
    @Bean
    public CacheHelper<Group> groupCacheHelper(RedisTemplate<String,Object> redisTemplate, ObjectMapper objectMapper) {
        return new CacheHelper<>(redisTemplate, objectMapper, Group.class);
    }
    @Bean
    public CacheHelper<GroupInvite> groupInviteCacheHelper(RedisTemplate<String,Object> redisTemplate, ObjectMapper objectMapper) {
        return new CacheHelper<>(redisTemplate, objectMapper, GroupInvite.class);
    }
    @Bean
    public CacheHelper<HolidayInvite> holidayInviteCacheHelper(RedisTemplate<String,Object> redisTemplate, ObjectMapper objectMapper) {
        return new CacheHelper<>(redisTemplate, objectMapper, HolidayInvite.class);
    }
}
