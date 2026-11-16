# API REST de CiberMax

Índice de la documentación de los módulos REST implementados en el código actual.

## Navegación

- [README principal](../../../README.md)
- [Etapa de desarrollo](../README.md)
- [Documentación de pruebas](../../pruebas/README.md)

## Documentación interactiva

La aplicación incluye Springdoc OpenAPI y Swagger UI.

- [Swagger UI](http://localhost:8989/swagger-ui.html)
- [Especificación OpenAPI JSON](http://localhost:8989/v3/api-docs)

El puerto `8989` corresponde al valor de referencia definido en `.env.example`; puede cambiar según `SERVER_PORT`.

## Módulos implementados

| Módulo | Estado | Documentación |
|---|---|---|
| Categorías | Implementado | [API de Categorías](categorias.md) |
| Subcategorías | Implementado parcialmente: registrar y listar | [API de Subcategorías](subcategorias.md) |

No se encontraron controladores REST implementados para Productos, Usuarios, Inventario, Pedidos, Pagos, Reportes u otros módulos durante la revisión actual. Sus archivos de documentación existentes se conservan sin contenido hasta que haya código verificable que documentar.
