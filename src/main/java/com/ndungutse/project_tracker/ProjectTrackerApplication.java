package com.ndungutse.project_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = "com.ndungutse.project_tracker.repository")
public class ProjectTrackerApplication {

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        return RedisCacheConfiguration
                .defaultCacheConfig(Thread.currentThread().getContextClassLoader())
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(java.time.Duration.ofHours(1)) // Set default TTL for cache
                .disableCachingNullValues();
    }

    public static void main(String[] args) {
        // Disable for testing caching
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(ProjectTrackerApplication.class, args);
    }

}
