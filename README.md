# Ciber Max — Sistema Integral de Comercio Electrónico

## Descripción

Ciber Max es un sistema integral de comercio electrónico cuyo backend centraliza la API REST y la lógica de negocio para sus canales de atención: tienda web, panel administrativo, aplicación móvil y chatbot.

El proyecto se encuentra en desarrollo incremental. La implementación comprobada actualmente incluye el módulo de categorías, persistencia con PostgreSQL, caché con Redis, documentación OpenAPI y pruebas automatizadas.

## Navegación de la documentación

1. [Planificación](docs/planificacion/README.md)
2. [Análisis de requisitos](docs/analisis-requisitos/analisis-requisitos.md)
3. [Diseño de software](docs/diseno-software/README.md)
4. [Desarrollo](docs/desarrollo/README.md)
5. [Pruebas](docs/pruebas/README.md)
6. [Despliegue](docs/despliegue/README.md)
7. [Mantenimiento](docs/mantenimiento/README.md)

## 1. Tecnologías utilizadas


### Backend

| Tecnología | Descripción |
|---|---|
| Java | Lenguaje de programación principal |
| Spring Boot | Framework para desarrollar la API REST |
| Spring Web | Desarrollo de controladores y endpoints HTTP |
| Spring Data JPA | Persistencia y acceso a datos |
| Hibernate | Implementación ORM para la base de datos |
| Maven | Gestión de dependencias y compilación |
| Lombok | Reducción de código repetitivo |

### Base de datos y caché

| Tecnología | Descripción |
|---|---|
| PostgreSQL | Base de datos relacional |
| Redis | Sistema de caché para optimizar consultas |

### Infraestructura y herramientas

| Tecnología | Descripción |
|---|---|
| Docker | Ejecución de servicios mediante contenedores |
| Docker Compose | Administración de servicios del entorno |
| Git | Control de versiones |
| GitHub | Repositorio de código y colaboración |
| GitHub Actions | Automatización de verificaciones |
| Jira | Gestión y seguimiento de tareas |

### Documentación

| Tecnología | Descripción |
|---|---|
| OpenAPI 3 | Especificación de la API REST |
| Springdoc OpenAPI | Generación automática de documentación |
| Swagger UI | Visualización y prueba de endpoints |
| Markdown | Documentación técnica del proyecto |

### Pruebas automatizadas

| Tecnología | Descripción |
|---|---|
| JUnit 5 | Framework de pruebas |
| Mockito | Simulación de dependencias |
| Spring Boot Test | Pruebas con el contexto de Spring |
| MockMvc | Pruebas de solicitudes HTTP |
| Testcontainers | Contenedores temporales para integración |
| PostgreSQL | Base de datos utilizada en integración |
| Redis | Caché utilizada en pruebas cuando corresponde |

---

## 2. Arquitectura del sistema

El sistema utiliza una **arquitectura monolítica modular en capas**, que permite organizar las funcionalidades en módulos independientes a nivel lógico dentro de una única aplicación Spring Boot.

### 2.1 Arquitectura monolítica

Los módulos se desarrollan, compilan y despliegan como una sola aplicación.

### 2.2 Arquitectura modular

El sistema está dividido en módulos funcionales, como:

- Autenticación
- Usuarios
- Categorías
- Subcategorías
- Productos
- Proveedores
- Inventario
- Carrito
- Pedidos
- Pagos
- Ventas
- Delivery
- Chatbot
- Reportes
- Notificaciones

### 2.3 Arquitectura en capas

| Capa | Responsabilidad |
|---|---|
| Controller | Recibir solicitudes HTTP y devolver respuestas |
| Service | Implementar la lógica de negocio |
| Repository | Gestionar el acceso a la base de datos |
| Entity | Representar entidades persistentes |
| DTO | Definir los datos de entrada y salida |
| Mapper | Convertir entidades a DTO y viceversa |

### 2.4 Flujo de comunicación

```text
Tienda Web / Panel Administrativo / App Móvil / Chatbot
                         |
                         v
                    API REST
                         |
                         v
                     Controller
                         |
                         v
                      Service
                         |
                         v
                     Repository
                         |
                         v
                     PostgreSQL
```

Redis se utiliza en las operaciones que requieren almacenamiento en caché.

---

## 3. Arquitectura de carpetas

La siguiente estructura representa la organización modular prevista del backend.

```text
ecommerce/
├── .github/
│   └── workflows/
├── docs/
│   ├── planificacion/
│   ├── analisis-requisitos/
│   ├── diseno-software/
│   ├── desarrollo/
│   ├── pruebas/
│   ├── despliegue/
│   └── mantenimiento/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/cibermax/ecommerce/
│   │   │       ├── config/
│   │   │       ├── security/
│   │   │       ├── shared/
│   │   │       └── modules/
│   │   │           ├── auth/
│   │   │           ├── usuarios/
│   │   │           ├── categorias/
│   │   │           ├── subcategorias/
│   │   │           ├── productos/
│   │   │           ├── proveedores/
│   │   │           ├── inventario/
│   │   │           ├── carrito/
│   │   │           ├── pedidos/
│   │   │           ├── pagos/
│   │   │           ├── ventas/
│   │   │           ├── delivery/
│   │   │           ├── chatbot/
│   │   │           ├── reportes/
│   │   │           └── notificaciones/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/
│   │           └── migration/
│   └── test/
│       └── java/
├── .env.example
├── .gitignore
├── docker-compose.yml
├── pom.xml
└── README.md
```

**Nota:** Los módulos y directorios pendientes se incorporarán progresivamente. La estructura debe mantenerse alineada con el código real del proyecto.

---

## 4. Requisitos previos

Antes de instalar y ejecutar el sistema, se deben tener las siguientes herramientas.

| Herramienta | Requisito |
|---|---|
| Java JDK | Versión compatible con Spring Boot |
| Maven | Versión compatible con el proyecto |
| Docker | Docker Engine o Docker Desktop |
| Docker Compose | Complemento Docker Compose |
| Git | Control de versiones |
| IntelliJ IDEA | IDE recomendado, opcional |

### Verificar las herramientas instaladas

```bash
java -version
mvn -version
docker --version
docker compose version
git --version
```

---

## 5. Instalación del proyecto

### 5.1 Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
```

Reemplazar `<URL_DEL_REPOSITORIO>` por la URL real de GitHub.

### 5.2 Ingresar al directorio

```bash
cd ecommerce
```

### 5.3 Abrir el proyecto en IntelliJ IDEA

1. Abrir IntelliJ IDEA.
2. Seleccionar **Open**.
3. Seleccionar la carpeta `ecommerce`.
4. Esperar a que Maven cargue el proyecto.
5. Verificar que esté configurado el JDK correspondiente.

---

## 6. Instalación de dependencias

El proyecto utiliza Maven para gestionar sus dependencias.

### 6.1 Descargar y compilar dependencias

Con Maven Wrapper:

```bash
./mvnw clean compile
```

En Windows PowerShell:

```powershell
.\mvnw.cmd clean compile
```

Con Maven instalado:

```bash
mvn clean compile
```

### 6.2 Actualizar dependencias en IntelliJ IDEA

1. Abrir el archivo `pom.xml`.
2. Acceder al panel Maven.
3. Seleccionar **Reload All Maven Projects**.
4. Esperar a que finalice la descarga de dependencias.

### 6.3 Verificar la compilación

```bash
mvn clean verify
```

Este comando compila el proyecto y ejecuta las verificaciones configuradas, incluidas las pruebas que correspondan.

---

## 7. Configuración del sistema

### 7.1 Configuración de variables de entorno

La aplicación utiliza variables de entorno para configurar sus servicios.

Ejemplo de `.env.example`:

```dotenv
DB_HOST=localhost
DB_PORT=5432
DB_NAME=ecommerce
DB_USERNAME=postgres
DB_PASSWORD=CAMBIAR_CONTRASENA

SPRING_PROFILES_ACTIVE=dev
```

Las variables de Redis y otros servicios deberán añadirse de acuerdo con la configuración real del proyecto.

### 7.2 Configuración de PostgreSQL

La base de datos principal del sistema es PostgreSQL.

Ejemplo de configuración en `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:ecommerce}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD}
```

### 7.3 Configuración de Redis

Redis se utiliza como mecanismo de caché para optimizar consultas frecuentes.

Su conexión debe configurarse mediante las propiedades de Spring Boot correspondientes y las variables de entorno definidas en el proyecto.

### 7.4 Iniciar los servicios con Docker

```bash
docker compose up -d
```

### 7.5 Verificar los contenedores

```bash
docker ps
```

Comprobar que los servicios de PostgreSQL y Redis estén funcionando correctamente.

### 7.6 Ejecutar la aplicación

Con Maven Wrapper:

```bash
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

### 7.7 Verificar el funcionamiento

Dirección local habitual:

http://localhost:8080

Swagger UI:

http://localhost:8080/swagger-ui.html

OpenAPI JSON:

http://localhost:8080/v3/api-docs

### 7.8 Seguridad de las configuraciones

No publicar credenciales reales en el repositorio.

Agregar las siguientes reglas a `.gitignore`:

```gitignore
.env
.env.*
!.env.example
```

Mantener el archivo `.env.example` únicamente con valores de referencia.
