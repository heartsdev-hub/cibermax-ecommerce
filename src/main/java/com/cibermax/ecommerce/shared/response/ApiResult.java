package com.cibermax.ecommerce.shared.response;

import java.time.Instant;

public record ApiResult<T>(
        boolean success,
        String message,
        T data,
        Instant timestamp
) {

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(true, message, data, Instant.now());
    }

    public static <T> ApiResult<T> success(T data) {
        return success("Operación realizada correctamente", data);
    }
}
