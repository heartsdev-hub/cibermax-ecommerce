# API REST de Categorías

## Navegación

- [Índice de APIs](README.md)
- [README principal](../../../README.md)

## Información general

- Etiqueta OpenAPI: `Categorías`.
- Clase controladora: `CategoriaController`.
- URL base: `/api/v1/categorias`.
- Identificador: `UUID`.
- Respuesta exitosa: `ApiResult<T>`.
- Respuesta de error: `ErrorResponse`.
- No existe endpoint `DELETE` implementado.

## DTO

### `CategoryCreateRequestDTO`

Se utiliza en `POST /api/v1/categorias`.

| Campo | Tipo | Reglas |
|---|---|---|
| `nombre` | `String` | Obligatorio, no vacío y máximo 100 caracteres |

### `CategoryUpdateRequestDT`

Se utiliza en `PUT /api/v1/categorias/{id}`.

| Campo | Tipo | Reglas |
|---|---|---|
| `nombre` | `String` | Obligatorio, no vacío y máximo 100 caracteres |

### `CategoryResponse`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `UUID` | Identificador generado |
| `codigo` | `Integer` | Código numérico asignado automáticamente en incrementos de 10: `10`, `20`, `30`... |
| `nombre` | `String` | Nombre de la categoría |
| `createdAt` | `Instant` | Fecha y hora de creación, solo lectura |
| `updatedAt` | `Instant` | Fecha y hora de última actualización, solo lectura |

## Endpoints

### 1. Listar categorías

```http
GET /api/v1/categorias
```

#### Parámetros

No recibe parámetros.

#### Respuesta `200 OK`

Devuelve `ApiResult<List<CategoryResponse>>`.

```json
{
  "success": true,
  "message": "Categorías obtenidas correctamente",
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "codigo": 10,
      "nombre": "Almacenamiento",
      "createdAt": "2026-11-15T10:30:00Z",
      "updatedAt": "2026-11-15T10:30:00Z"
    }
  ],
  "timestamp": "2026-11-15T10:30:00Z"
}
```

El resultado se almacena en la caché `categorias:list:v3` con la clave `all`.

### 2. Obtener categoría por ID

```http
GET /api/v1/categorias/{id}
```

#### Parámetros de ruta

| Parámetro | Tipo | Obligatorio | Descripción |
|---|---|---:|---|
| `id` | `UUID` | Sí | UUID de la categoría |

#### Respuesta `200 OK`

Devuelve `ApiResult<CategoryResponse>`.

```json
{
  "success": true,
  "message": "Categoría obtenida correctamente",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nombre": "Almacenamiento",
    "createdAt": "2026-11-15T10:30:00Z",
    "updatedAt": "2026-11-15T10:30:00Z"
  },
  "timestamp": "2026-11-15T10:30:00Z"
}
```

El resultado se almacena en la caché `categorias:id:v3` usando el UUID como clave.

### 3. Buscar categorías por nombre

```http
GET /api/v1/categorias/buscar?nombre=SSD
```

#### Parámetros de consulta

| Parámetro | Tipo | Obligatorio | Descripción |
|---|---|---:|---|
| `nombre` | `String` | Sí | Texto parcial que se buscará en el nombre |

La búsqueda usa coincidencia parcial e ignora mayúsculas y minúsculas.

#### Respuesta `200 OK`

Devuelve `ApiResult<List<CategoryResponse>>`. Si no hay coincidencias, la lista `data` queda vacía.

### 4. Crear categoría

```http
POST /api/v1/categorias
Content-Type: application/json
```

#### DTO de entrada

```json
{
  "nombre": "Almacenamiento"
}
```

#### Respuesta `201 Created`

Devuelve `ApiResult<CategoryResponse>` con el mensaje `Categoría creada correctamente`.

La creación rechaza nombres duplicados ignorando mayúsculas y minúsculas, y elimina las entradas relacionadas de las cachés de listado y detalle.

### 5. Actualizar categoría

```http
PUT /api/v1/categorias/{id}
Content-Type: application/json
```

#### Parámetros de ruta

| Parámetro | Tipo | Obligatorio | Descripción |
|---|---|---:|---|
| `id` | `UUID` | Sí | UUID de la categoría que se actualizará |

#### DTO de entrada

```json
{
  "nombre": "Computadoras y laptops"
}
```

#### Respuesta `200 OK`

Devuelve `ApiResult<CategoryResponse>` con el mensaje `Categoría actualizada correctamente`.

La actualización rechaza nombres duplicados ignorando mayúsculas y minúsculas, y elimina las entradas relacionadas de las cachés de listado y detalle.

## Respuestas de error

La aplicación usa `ErrorResponse` con esta estructura:

```json
{
  "timestamp": "2026-11-15T15:30:00Z",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "No se encontró la categoría con id: 550e8400-e29b-41d4-a716-446655440000",
  "path": "/api/v1/categorias/550e8400-e29b-41d4-a716-446655440000"
}
```

| Código HTTP | Situación |
|---:|---|
| `400 Bad Request` | UUID inválido o solicitud que incumple `@NotBlank` o `@Size(max = 100)` |
| `404 Not Found` | No existe la categoría indicada |
| `409 Conflict` | Ya existe una categoría con el mismo nombre |
| `500 Internal Server Error` | Error interno no controlado, documentado por el controlador |

Mensajes controlados por el servicio:

- `No se encontró la categoría con id: {id}`
- `Ya existe una categoría con el nombre indicado`
- `El nombre de la categoría es obligatorio`
- `El nombre de la categoría no puede superar los 100 caracteres`

## Persistencia relacionada

La entidad `Categoria` se persiste en la tabla `categorias` con un UUID, código numérico único, nombre único, `created_at` y `updated_at`. El código comienza en `10` y aumenta de diez en diez.

## Pruebas relacionadas

- [Pruebas de integración de Categorías](../../pruebas/categoria-integracion.md)
- [Pruebas unitarias de Categorías](../../pruebas/categoria-unitarias.md)
