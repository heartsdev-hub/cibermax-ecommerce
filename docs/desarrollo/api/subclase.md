# API REST de Subclases

## Navegación

- [Índice de APIs](README.md)
- [README principal](../../../README.md)

## Información general

- Etiqueta OpenAPI: `Subclases`.
- Tabla persistente: `subclases`.
- URL base: `/api/v1/subclases`.
- Cada subclase pertenece a una `Clasess` existente; la categoría se obtiene de esa relación.
- La respuesta exitosa usa `ApiResult<SubclaseResponse>`.
- Los campos `codigo` de `Categoria`, `Clasess` y `Subclase` se manejan como `String` para conservar la composición jerárquica sin pérdidas de formato.

## Código de subclase

El código se genera usando el código de la `Clasess` como prefijo y una secuencia que aumenta de diez en diez.

| Clasess | Subclases generadas |
|---:|---|
| `1010` | `101010`, `101020`, `101030`, ... |
| `1020` | `102010`, `102020`, `102030`, ... |
| `2010` | `201010`, `201020`, `201030`, ... |

## DTO de entrada

`SubclaseCreateRequest`:

| Campo | Tipo | Reglas |
|---|---|---|
| `nombre` | `String` | Obligatorio, no vacío y máximo 100 caracteres |
| `clasessId` | `UUID` | Obligatorio; debe identificar una Clasess existente |

## Listar subclases

```http
GET /api/v1/subclases
```

Devuelve `200 OK` con todas las subclases ordenadas por `codigo`. Si no existen registros, `data` es una lista vacía.

## Registrar subclase

```http
POST /api/v1/subclases
Content-Type: application/json
```

Solicitud:

```json
{
  "nombre": "Inalámbricos",
  "clasessId": "550e8400-e29b-41d4-a716-446655440000"
}
```

Respuesta `201 Created` para una Clasess con código `1010`:

```json
{
  "success": true,
  "message": "Subclase creada correctamente",
  "data": {
    "id": "7c5e3f2e-6d6c-4c2a-8e9d-2f2e4f8f0b11",
    "codigo": "101010",
    "nombre": "Inalámbricos",
    "categoria": "Audio",
    "clase": "Audífonos",
    "createdAt": "2026-11-17T10:30:00Z",
    "updatedAt": "2026-11-17T10:30:00Z"
  },
  "timestamp": "2026-11-17T10:30:00Z"
}
```

## Persistencia

La entidad `Subclase` se persiste en la tabla `subclases` y contiene UUID, código único, nombre, relación obligatoria con `Clasess`, `created_at` y `updated_at`. El nombre no puede repetirse dentro de la misma `Clasess`.

## Respuestas de error

| Código HTTP | Situación |
|---:|---|
| `400 Bad Request` | Nombre vacío, ausente o mayor de 100 caracteres; `clasessId` ausente |
| `404 Not Found` | La Clasess padre no existe |
| `409 Conflict` | Ya existe una subclase con el mismo nombre dentro de la Clasess |
