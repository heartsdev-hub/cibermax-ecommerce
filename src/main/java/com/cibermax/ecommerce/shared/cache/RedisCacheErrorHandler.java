package com.cibermax.ecommerce.shared.cache;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.stereotype.Component;


@Component

public class RedisCacheErrorHandler implements CacheErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheErrorHandler.class);
    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn(
                "Error al consultar Redis. Cache: {}, Key: {}",
                cache.getName(), key, exception
        );
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, @Nullable Object value) {
        log.warn(
                "Error al guardar en Redis. Cache: {}, Key: {}",
                cache.getName(), key, exception
        );
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.error(
                "Error al invalidar Redis. Cache: {}, Key: {}",
                cache.getName(), key, exception
        );

        throw exception;
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.error(
                "Error al limpiar Redis. Cache: {}",
                cache.getName(), exception
        );

        throw exception;
    }
}
