# API REST de Clasess

## Navegación

- [Índice de APIs](README.md)
- [README principal](../../../README.md)

## Información general

- Etiqueta OpenAPI: `Clasess`.
- Tabla persistente: `Clasess`.
- URL base: `/api/v1/clasess`.
- Cada `Clasess` pertenece a una categoría existente mediante `categoriaId`.
- La respuesta exitosa usa `ApiResult<ClasessResponse>`.
- El campo `codigo` se maneja como `String` y conserva la composición jerárquica.

## Código de clase

El código se genera automáticamente con el código de la categoría como prefijo y una secuencia que aumenta de diez en diez.

| Categoría | Clases generadas |
|---:|---|
| `"10"` | `"1010"`, `"1020"`, `"1030"`, ... |
| `"20"` | `"2010"`, `"2020"`, `"2030"`, ... |

## DTO de entrada

`ClasessCreateRequest`:

| Campo | Tipo | Reglas |
|---|---|---|
| `nombre` | `String` | Obligatorio, no vacío y máximo 100 caracteres |
| `categoriaId` | `UUID` | Obligatorio; debe identificar una categoría existente |

## Registrar Clasess

```http
POST /api/v1/clasess
Content-Type: application/json
```

Solicitud:

```json
{
  "nombre": "Audífonos",
  "categoriaId": "550e8400-e29b-41d4-a716-446655440000"
}
```

Respuesta `201 Created` para una categoría con código `10`:

```json
{
  "success": true,
  "message": "Clasess creada correctamente",
  "data": {
    "id": "7c5e3f2e-6d6c-4c2a-8e9d-2f2e4f8f0b11",
    "codigo": "1010",
    "nombre": "Audífonos",
    "categoria": "Audio",
    "createdAt": "2026-11-17T10:30:00Z",
    "updatedAt": "2026-11-17T10:30:00Z"
  },
  "timestamp": "2026-11-17T10:30:00Z"
}
```

## Listar Clasess

```http
GET /api/v1/clasess
```

Devuelve `200 OK` con una lista ordenada por `codigo` ascendente. Si no existen registros, `data` es una lista vacía.

## Obtener Clasess por ID

```http
GET /api/v1/clasess/{id}
```

El parámetro `id` es un UUID. Devuelve `200 OK` si existe o `404 Not Found` cuando no se encuentra la Clasess.

## DTO para actualizar

`ClasessUpdateRequest`:

| Campo | Tipo | Reglas |
|---|---|---|
| `nombre` | `String` | Obligatorio, no vacío y máximo 100 caracteres |

La categoría y el código no se reciben en la actualización y permanecen sin cambios.

## Actualizar Clasess

```http
PUT /api/v1/clasess/{id}
Content-Type: application/json
```

Solicitud:

```json
{
  "nombre": "Audífonos inalámbricos"
}
```

Devuelve `200 OK` con la Clasess actualizada. El nombre no puede duplicarse dentro de la misma categoría.

## Persistencia

La entidad `Clasess` se persiste en la tabla `Clasess` y contiene UUID, código único, nombre, relación obligatoria con `Categoria`, `created_at` y `updated_at`. El nombre no puede repetirse dentro de la misma categoría.

## Respuestas de error

| Código HTTP | Situación |
|---:|---|
| `400 Bad Request` | Nombre vacío, ausente o mayor de 100 caracteres; `categoriaId` ausente |
| `404 Not Found` | La categoría padre no existe |
| `409 Conflict` | Ya existe una clase con el mismo nombre dentro de la categoría |
