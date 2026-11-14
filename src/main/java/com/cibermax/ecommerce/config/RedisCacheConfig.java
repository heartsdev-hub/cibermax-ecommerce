package com.cibermax.ecommerce.config;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.Duration;

@Configuration
public class RedisCacheConfig {
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheCustomizer() {
        var typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.cibermax.ecommerce")
                .allowIfSubType("java.util")
                .allowIfSubType("java.time")
                .allowIfSubType("java.lang")
                .build();

        var jsonSerializer = GenericJacksonJsonRedisSerializer
                .builder()
                .enableDefaultTyping(typeValidator)
                .build();

        var jsonConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(jsonSerializer)
                )
                .disableCachingNullValues();

        return builder -> builder
                .withCacheConfiguration(
                        "categorias:list:v3",
                        jsonConfig.entryTtl(Duration.ofMinutes(10))
                )
                .withCacheConfiguration(
                        "categorias:id:v3",
                        jsonConfig.entryTtl(Duration.ofMinutes(30))
                );
    }
}
