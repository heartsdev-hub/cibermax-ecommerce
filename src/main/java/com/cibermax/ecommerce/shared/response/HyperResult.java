package com.cibermax.ecommerce.shared.response;

import java.time.Instant;

public record HyperResult<T>(
        boolean success,
        String message,
        T data,
        Instant timestamp
) {

    public static <T> HyperResult<T> success(String message, T data) {
        return new HyperResult<>(true, message, data, Instant.now());
    }

    public static <T> HyperResult<T> success(T data) {
        return success("Operación realizada correctamente", data);
    }
}
