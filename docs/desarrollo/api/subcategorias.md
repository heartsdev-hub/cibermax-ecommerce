# API REST de Subcategorías

## Navegación

- [Índice de APIs](README.md)
- [README principal](../../../README.md)

## Información general

- Etiqueta OpenAPI: `Subcategorías`.
- URL base: `/api/v1/subcategorias`.
- Identificador: `UUID`.
- La subcategoría pertenece a una categoría existente mediante `categoriaId`.
- Respuesta exitosa: `ApiResult<T>`.
- Respuesta de error: `ErrorResponse`.

## DTO de creación

`SubCategoryCreateRequestDTO` se utiliza para registrar una subcategoría.

| Campo | Tipo | Reglas |
|---|---|---|
| `nombre` | `String` | Obligatorio, no vacío y máximo 100 caracteres |
| `categoriaId` | `UUID` | Obligatorio; debe identificar una categoría existente |

## Respuesta

`SubCategoryResponse` contiene los datos de la subcategoría y su categoría padre:

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `UUID` | Identificador de la subcategoría |
| `nombre` | `String` | Nombre de la subcategoría |
| `categoriaId` | `UUID` | Identificador de la categoría padre |
| `categoriaNombre` | `String` | Nombre de la categoría padre |
| `createdAt` | `Instant` | Fecha y hora de creación |
| `updatedAt` | `Instant` | Fecha y hora de última actualización |

## Endpoints implementados

### Registrar subcategoría

```http
POST /api/v1/subcategorias
Content-Type: application/json
```

Solicitud:

```json
{
  "nombre": "Laptops",
  "categoriaId": "550e8400-e29b-41d4-a716-446655440000"
}
```

Respuesta `201 Created`:

```json
{
  "success": true,
  "message": "Subcategoría creada correctamente",
  "data": {
    "id": "7c5e3f2e-6d6c-4c2a-8e9d-2f2e4f8f0b11",
    "nombre": "Laptops",
    "categoriaId": "550e8400-e29b-41d4-a716-446655440000",
    "categoriaNombre": "Computadoras",
    "createdAt": "2026-11-16T10:30:00Z",
    "updatedAt": "2026-11-16T10:30:00Z"
  },
  "timestamp": "2026-11-16T10:30:00Z"
}
```

El nombre no puede repetirse dentro de la misma categoría. El mismo nombre puede existir bajo otra categoría.

### Listar subcategorías

```http
GET /api/v1/subcategorias
```

No recibe parámetros.

Respuesta `200 OK`:

```json
{
  "success": true,
  "message": "Subcategorías obtenidas correctamente",
  "data": [
    {
      "id": "7c5e3f2e-6d6c-4c2a-8e9d-2f2e4f8f0b11",
      "nombre": "Laptops",
      "categoriaId": "550e8400-e29b-41d4-a716-446655440000",
      "categoriaNombre": "Computadoras",
      "createdAt": "2026-11-16T10:30:00Z",
      "updatedAt": "2026-11-16T10:30:00Z"
    }
  ],
  "timestamp": "2026-11-16T10:30:00Z"
}
```

Cuando no existen subcategorías, `data` contiene una lista vacía.

### Obtener subcategoría por ID

```http
GET /api/v1/subcategorias/{id}
```

#### Parámetros de ruta

| Parámetro | Tipo | Obligatorio | Descripción |
|---|---|---:|---|
| `id` | `UUID` | Sí | UUID de la subcategoría |

Respuesta `200 OK`:

```json
{
  "success": true,
  "message": "Subcategoría obtenida correctamente",
  "data": {
    "id": "7c5e3f2e-6d6c-4c2a-8e9d-2f2e4f8f0b11",
    "nombre": "Laptops",
    "categoriaId": "550e8400-e29b-41d4-a716-446655440000",
    "categoriaNombre": "Computadoras",
    "createdAt": "2026-11-16T10:30:00Z",
    "updatedAt": "2026-11-16T10:30:00Z"
  },
  "timestamp": "2026-11-16T10:30:00Z"
}
```

## Respuestas de error

| Código HTTP | Situación |
|---:|---|
| `400 Bad Request` | `nombre` vacío, ausente o mayor de 100 caracteres; `categoriaId` ausente |
| `404 Not Found` | La categoría indicada en `categoriaId` o la subcategoría indicada por `id` no existe |
| `409 Conflict` | Ya existe una subcategoría con el mismo nombre dentro de la categoría |
| `500 Internal Server Error` | Error interno no controlado |

Ejemplo de error `404`:

```json
{
  "timestamp": "2026-11-16T10:30:00Z",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "No se encontró la categoría con id: 550e8400-e29b-41d4-a716-446655440000",
  "path": "/api/v1/subcategorias"
}
```

## Pruebas relacionadas

- [Pruebas unitarias de Subcategorías](../../../src/test/java/com/cibermax/ecommerce/modules/subCategoria/)
- [Pruebas de integración de Subcategorías](../../../src/test/java/com/cibermax/ecommerce/modules/subCategoria/integration/SubCategoriaIntegrationTest.java)

El endpoint de actualización se documentará cuando sea implementado.
