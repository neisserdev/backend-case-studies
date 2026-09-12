# 📚 Biblioteca API - Gestión de Préstamos de la Red de Bibliotecas Municipales

API REST desarrollada con **Spring Boot 4** para reemplazar el control manual de préstamos de la Red de Bibliotecas Municipales: usuarios inscritos y no inscritos, libros con código único por género, ejemplares físicos disponibles o prestados, y préstamos con cupos por usuario, tope mensual por libro y reglas de devolución según su tipo.

> Este ejercicio forma parte de **[`backend-case-studies`](../)**, un monorepo de casos de estudio backend donde cada carpeta resuelve un problema puntual de diseño e implementación. Consigna original: [`CONSIGNA.md`](./CONSIGNA.md).

## 🎯 Aporte técnico

Este caso se centra principalmente en:

- Modelado de relaciones entre entidades (`Usuario`, `Libro` ⇄ `Ejemplar` ⇄ `Prestamo`).
- Reglas de negocio con múltiples cupos concurrentes: máximo de préstamos abiertos por usuario (según su tipo), máximo de préstamos nuevos por libro y mes calendario, un ejemplar con un único préstamo abierto a la vez.
- Generación de un identificador de negocio único y legible (código de libro: dos letras de género + cuatro dígitos), distinto de la clave primaria técnica.
- Validación cruzada entre campos de un mismo DTO con una constraint de Jakarta Validation a medida (`@FechaDevolucionEsperadaValida`), análoga en propósito a la de otros casos de este monorepo pero aplicada a una regla temporal.
- Consulta JPQL de agregación temporal (conteo de préstamos de un libro dentro de un mes calendario) y de existencia (disponibilidad de ejemplares por género).
- Separación entre DTOs, mappers, servicios y repositorios.

## 🛠️ Tecnologías

- **Java 21** + **Spring Boot 4.1.1**
- **Spring Data JPA** - Persistencia de datos
- **PostgreSQL** - Base de datos
- **Spring Validation** (Jakarta Validation) - Validación de requests, incluida una constraint a nivel de clase
- **Spring `ProblemDetail`** - Errores estandarizados (RFC 9457)
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestión de dependencias

## 📋 Requisitos

- Java 21+
- Maven 3.6+
- PostgreSQL 12+

## 🗄️ Configuración de Base de Datos

La configuración de conexión se resuelve exclusivamente por variables de entorno — no hay valores por defecto ni credenciales fijas en `application.yaml` ni en el código. La aplicación no arranca si alguna de las variables no está definida.

| Variable | Descripción |
|---|---|
| `DB_URL` | URL JDBC de PostgreSQL |
| `DB_USERNAME` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña del usuario |

### PostgreSQL Local

1. Crear base de datos y usuario:

```bash
# Conectar a PostgreSQL (Linux/macOS)
sudo -u postgres psql

# O en Windows (abre psql directamente)
psql -U postgres
```

2. Ejecutar en la terminal de PostgreSQL:

```sql
CREATE DATABASE biblioteca_db;
ALTER USER postgres WITH PASSWORD 'tu_password';
```

> Si prefieres un usuario dedicado en vez de `postgres`, créalo y exporta `DB_USERNAME` / `DB_PASSWORD` con esos valores antes de levantar la app.

3. Exportar la configuración antes de levantar la aplicación:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/biblioteca_db
export DB_USERNAME=postgres
export DB_PASSWORD=tu_password
```

Las tablas se crean automáticamente al iniciar la aplicación (`ddl-auto=update`).

## 🚀 Ejecutar la Aplicación

```bash
# Linux/macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

La app estará disponible en `http://localhost:8080`

## 🧩 Modelo de dominio

- **Usuario**: `nombre`, `documento` (único) y `tipoUsuario` (`INSCRITO` o `NO_INSCRITO`), que determina cuántos préstamos puede mantener abiertos simultáneamente.
- **Libro**: `titulo`, `autor` y `genero`. Al crearse recibe un `codigo` único e inmutable: las dos primeras letras del género en mayúsculas más una secuencia de cuatro dígitos (p. ej. `PO0417` para `POLICIACO`).
- **Ejemplar**: copia física de un `Libro`, con `estadoEjemplar` (`DISPONIBLE` o `PRESTADO`). Un libro puede tener varios ejemplares.
- **Prestamo**: vincula un `Ejemplar` con un `Usuario`. Tiene `tipoPrestamo` (`UNICO` o `VARIOS`), `fechaPrestamo`, `fechaDevolucionEsperada` (solo para `VARIOS`) y `fechaDevolucionReal` (`null` hasta que se registra la devolución). El historial de préstamos nunca se edita ni se elimina, solo se crea y se consulta.

### Reglas de negocio implementadas

- Un ejemplar no puede tener dos préstamos abiertos: si ya está `PRESTADO`, un nuevo préstamo sobre él se rechaza.
- `INSCRITO` → máximo 15 préstamos abiertos simultáneos. `NO_INSCRITO` → máximo 5.
- Un mismo libro no puede superar los 10 préstamos **nuevos** registrados durante un mismo mes calendario, sin importar cuántos ejemplares físicos tenga.
- Un préstamo `UNICO` solo puede devolverse el mismo día calendario (UTC) en que se registró.
- Un préstamo `VARIOS` exige `fechaDevolucionEsperada` posterior a la fecha del préstamo; un préstamo `UNICO` no admite esa fecha. Esta coherencia se valida a nivel de DTO con `@FechaDevolucionEsperadaValida`.
- `fechaDevolucionReal` inicia en `null` y solo se completa al devolver.
- Un préstamo ya devuelto no puede volver a devolverse.
- El código de un libro es único; se genera automáticamente al crearlo y es inmutable.
- Un usuario no puede eliminarse si tiene préstamos abiertos. Un ejemplar no puede eliminarse si tiene un préstamo abierto.
- Operaciones que combinan varias escrituras (crear préstamo, devolver) están marcadas con `@Transactional`; las consultas de solo lectura usan `@Transactional(readOnly = true)`. La creación y la devolución de un préstamo bloquean pesimistamente el ejemplar involucrado para evitar condiciones de carrera sobre su disponibilidad.

### Arquitectura por capas

`Controller` → `Service` (reglas de negocio + `@Transactional`) → `Repository` (JPA/JPQL) → `Model` (entidades), con `DTO` (records) de entrada/salida y `Mapper` dedicados para no exponer las entidades directamente en la API.

## 🔌 Endpoints de la API

Base URL: `http://localhost:8080/api/v1`

### 👤 Usuarios

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/usuarios` | Crear usuario (inscrito o no inscrito) |
| `GET` | `/usuarios/{id}` | Obtener usuario por ID |
| `PATCH` | `/usuarios/{id}` | Actualizar parcialmente nombre o documento |
| `DELETE` | `/usuarios/{id}` | Eliminar usuario (rechazado si tiene préstamos abiertos) |
| `GET` | `/usuarios/{id}/prestamos` | Historial de préstamos del usuario |

**Body de creación:**

```json
{
  "nombre": "Ana García",
  "documento": "U-001",
  "tipoUsuario": "INSCRITO"
}
```

Validaciones: `nombre` y `documento` obligatorios y no vacíos · `documento` único · `tipoUsuario` obligatorio.

**Body de actualización (`PATCH`, todos los campos opcionales):** los textos enviados no pueden estar vacíos y el documento continúa siendo único. El `tipoUsuario` no es editable: cambiarlo dinámicamente podría dejar a un usuario por encima del cupo de préstamos abiertos que le correspondería.

```json
{
  "nombre": "Ana García Pérez"
}
```

### 📖 Libros

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/libros` | Crear libro (el `codigo` se genera automáticamente) |
| `GET` | `/libros` | Listar todos los libros |
| `GET` | `/libros/{id}` | Obtener libro por ID |
| `PATCH` | `/libros/{id}` | Actualizar parcialmente título o autor |
| `DELETE` | `/libros/{id}` | Eliminar libro |
| `GET` | `/libros/disponibles?genero=POLICIACO` | Libros de un género con al menos un ejemplar disponible |

**Body de creación:**

```json
{
  "titulo": "El nombre de la rosa",
  "autor": "Umberto Eco",
  "genero": "POLICIACO"
}
```

Validaciones: `titulo`, `autor` y `genero` obligatorios. `genero` es uno de: `POLICIACO`, `ROMANCE`, `FANTASIA`, `HISTORIA`, `CIENCIA`, `TERROR`, `AVENTURA`, `BIOGRAFIA`. El `codigo` y el `genero` son inmutables una vez creado el libro.

**Body de actualización (`PATCH`, todos los campos opcionales):**

```json
{
  "titulo": "El nombre de la rosa (edición revisada)"
}
```

### 📗 Ejemplares

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/libros/{libroId}/ejemplares` | Registrar un nuevo ejemplar físico de un libro |
| `GET` | `/libros/{libroId}/ejemplares` | Listar los ejemplares de un libro |
| `DELETE` | `/ejemplares/{id}` | Eliminar un ejemplar (rechazado si tiene un préstamo abierto) |

El ejemplar se crea `DISPONIBLE` automáticamente; no requiere cuerpo en la petición.

### 🔁 Préstamos

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/prestamos` | Registrar un préstamo |
| `GET` | `/prestamos/{id}` | Obtener préstamo por ID |
| `PATCH` | `/prestamos/{id}/devolver` | Registrar la devolución de un préstamo |

**Body de creación (préstamo `UNICO`, se devuelve el mismo día):**

```json
{
  "ejemplarId": 1,
  "usuarioId": 1,
  "tipoPrestamo": "UNICO"
}
```

**Body de creación (préstamo `VARIOS`, requiere fecha de devolución esperada):**

```json
{
  "ejemplarId": 2,
  "usuarioId": 1,
  "tipoPrestamo": "VARIOS",
  "fechaDevolucionEsperada": "2026-10-15"
}
```

Validaciones: `ejemplarId` y `usuarioId` obligatorios y positivos · `tipoPrestamo` obligatorio · `VARIOS` exige `fechaDevolucionEsperada` posterior a la fecha del préstamo · `UNICO` no admite `fechaDevolucionEsperada`. Reglas de negocio adicionales (ejemplar sin préstamo abierto, cupo del usuario, tope mensual del libro) se validan en el servicio y responden `422`.

## ⚠️ Manejo de Errores

La API implementa **RFC 9457 (Problem Details for HTTP APIs)** usando `ProblemDetail` de Spring, a través de un `GlobalExceptionHandler` centralizado. Cada error incluye `type` (URI propia del código de error), `title`, `status`, `detail`, `instance`, y las propiedades adicionales `timestamp` y `code`.

**Validación de un DTO con `@Valid` (400 Bad Request, code `VALIDATION_ERROR`)**

```json
{
  "type": "https://neisser.dev/problems/VALIDATION_ERROR",
  "title": "Error de validación",
  "status": 400,
  "detail": "Los datos enviados no son válidos",
  "instance": "/api/v1/prestamos",
  "timestamp": "2026-09-12T14:30:45.123456Z",
  "code": "VALIDATION_ERROR",
  "errors": [
    "Un préstamo VARIOS requiere fechaDevolucionEsperada posterior a la fecha del préstamo, y un préstamo UNICO no la admite."
  ],
  "count": 1
}
```

**Recurso no encontrado (404 Not Found, code `RESOURCE_NOT_FOUND`)**

```json
{
  "type": "https://neisser.dev/problems/RESOURCE_NOT_FOUND",
  "title": "Recurso no encontrado",
  "status": 404,
  "detail": "ejemplar con id 999 no encontrado",
  "instance": "/api/v1/prestamos",
  "timestamp": "2026-09-12T14:30:45.123456Z",
  "code": "RESOURCE_NOT_FOUND",
  "resource": "ejemplar",
  "resourceId": "999"
}
```

**Regla de negocio violada — ej. tope mensual del libro alcanzado (422 Unprocessable Entity, code `BUSINESS_RULE_VIOLATION`)**

```json
{
  "type": "https://neisser.dev/problems/BUSINESS_RULE_VIOLATION",
  "title": "Regla de negocio no satisfecha",
  "status": 422,
  "detail": "El libro alcanzó el máximo de 10 préstamos este mes",
  "instance": "/api/v1/prestamos",
  "timestamp": "2026-09-12T14:30:45.123456Z",
  "code": "BUSINESS_RULE_VIOLATION"
}
```

**Conflicto de estado — ej. documento duplicado (409 Conflict, code `RESOURCE_CONFLICT`)**

```json
{
  "type": "https://neisser.dev/problems/RESOURCE_CONFLICT",
  "title": "Conflicto con el estado actual del recurso",
  "status": 409,
  "detail": "Ya existe un usuario con ese documento",
  "instance": "/api/v1/usuarios",
  "timestamp": "2026-09-12T14:30:45.123456Z",
  "code": "RESOURCE_CONFLICT"
}
```

## 🧪 Ejemplos con curl

```bash
# Crear usuario inscrito
curl -X POST http://localhost:8080/api/v1/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana García","documento":"U-001","tipoUsuario":"INSCRITO"}'

# Crear libro (el código se genera automáticamente)
curl -X POST http://localhost:8080/api/v1/libros \
  -H "Content-Type: application/json" \
  -d '{"titulo":"El nombre de la rosa","autor":"Umberto Eco","genero":"POLICIACO"}'

# Registrar un ejemplar del libro
curl -X POST http://localhost:8080/api/v1/libros/1/ejemplares

# Consultar libros disponibles de un género
curl "http://localhost:8080/api/v1/libros/disponibles?genero=POLICIACO"

# Registrar un préstamo UNICO
curl -X POST http://localhost:8080/api/v1/prestamos \
  -H "Content-Type: application/json" \
  -d '{"ejemplarId":1,"usuarioId":1,"tipoPrestamo":"UNICO"}'

# Registrar un préstamo VARIOS
curl -X POST http://localhost:8080/api/v1/prestamos \
  -H "Content-Type: application/json" \
  -d '{"ejemplarId":2,"usuarioId":1,"tipoPrestamo":"VARIOS","fechaDevolucionEsperada":"2026-10-15"}'

# Devolver un préstamo
curl -X PATCH http://localhost:8080/api/v1/prestamos/1/devolver

# Consultar el historial de préstamos de un usuario
curl http://localhost:8080/api/v1/usuarios/1/prestamos

# Actualizar datos del usuario
curl -X PATCH http://localhost:8080/api/v1/usuarios/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana García Pérez"}'

# Eliminar un ejemplar sin préstamo abierto
curl -X DELETE http://localhost:8080/api/v1/ejemplares/2
```

## 📝 Notas Técnicas

- **Tablas**: se crean automáticamente con `hibernate.ddl-auto=update`.
- **Código de libro**: se genera con las dos primeras letras del género en mayúsculas más cuatro dígitos aleatorios, reintentando hasta encontrar uno no usado; es inmutable tras la creación.
- **Tiempo**: `LocalDateTime` en UTC para `fechaPrestamo` y `fechaDevolucionReal`; `LocalDate` para `fechaDevolucionEsperada`. El tope mensual por libro y el "mismo día" de los préstamos `UNICO` se calculan sobre el calendario UTC.
- **Validación**: DTOs (`record`) con anotaciones de Jakarta Validation, incluida una constraint a nivel de clase (`@FechaDevolucionEsperadaValida`) para la regla que depende de dos campos a la vez (`tipoPrestamo` y `fechaDevolucionEsperada`).
- **Configuración por entorno**: la conexión a base de datos se resuelve únicamente con `${DB_URL}`, `${DB_USERNAME}` y `${DB_PASSWORD}`, sin valores por defecto; no hay secretos ni datos de conexión en el código fuente.
- **Concurrencia**: crear y devolver un préstamo bloquean pesimistamente el ejemplar involucrado para no perder actualizaciones simultáneas sobre su disponibilidad.

## ✅ Pruebas automatizadas

La suite incluye pruebas de contexto, servicios y validaciones para: ejemplar con préstamo abierto, cupo de préstamos por tipo de usuario, tope mensual por libro, no reutilización de un préstamo ya devuelto, restricción de devolución same-day para `UNICO`, liberación del ejemplar al devolver, actualización parcial de usuario y unicidad de documento, y la constraint cruzada de `fechaDevolucionEsperada`.

```bash
./mvnw test
```

## 👨‍💻 Autor

Neisser Torres Peña - [@neisserdev](https://github.com/neisserdev)
