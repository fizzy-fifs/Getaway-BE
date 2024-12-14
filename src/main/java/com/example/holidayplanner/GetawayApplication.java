package com.example.holidayplanner;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@EnableScheduling
@OpenAPIDefinition
@EnableCaching
public class GetawayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GetawayApplication.class, args);
	}

	@Bean
	public RedisTemplate<?,?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<?,?> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

}
