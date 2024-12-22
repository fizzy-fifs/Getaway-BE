package com.example.holidayplanner;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;

@SpringBootApplication()
@EnableScheduling
@OpenAPIDefinition
@EnableCaching
@ComponentScan(basePackages = "com.example.holidayplanner")
public class GetawayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GetawayApplication.class, args);
	}

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
				.prefixCacheNameWith(this.getClass().getPackageName() + ".")
				.entryTtl(Duration.ofDays(7))
				.disableCachingNullValues();

		return RedisCacheManager.builder(redisConnectionFactory)
				.cacheDefaults(config)
				.build();
	}
}
