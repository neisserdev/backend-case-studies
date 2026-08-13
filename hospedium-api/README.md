# 🏨 Hospedium API - Gestión de Reservas de Hotel

API REST desarrollada con **Spring Boot 4** para reemplazar el control manual de reservas de un hotel: tipos de habitación, habitaciones físicas, huéspedes, reservas y asignación de habitaciones respetando disponibilidad.

> Este ejercicio forma parte de **[`backend-case-studies`](../)**, un monorepo de casos de estudio backend donde cada carpeta resuelve un problema puntual de diseño e implementación. Consigna original: [`CONSIGNA.md`](./CONSIGNA.md).

## 🎯 Aporte técnico

Este caso se centra principalmente en:

- Modelado de relaciones entre entidades (`Reserva` ⇄ `Asignacion` ⇄ `Habitacion` ⇄ `TipoHabitacion`).
- Reglas de negocio implementadas en la capa de servicio.
- Consultas JPQL para detectar solapamientos.
- Cálculo de disponibilidad sin almacenar un estado `ocupada`.
- Prevención de asignaciones conflictivas.
- Uso de transacciones para operaciones críticas.
- Separación entre DTOs, mappers, servicios y repositorios.

## 🛠️ Tecnologías

- **Java 21** + **Spring Boot 4.1.0**
- **Spring Data JPA** - Persistencia de datos
- **PostgreSQL** - Base de datos
- **Spring Validation** (Jakarta Validation) - Validación de requests
- **Spring `ProblemDetail`** - Errores estandarizados (RFC 9457)
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestión de dependencias

## 📋 Requisitos

- Java 21+
- Maven 3.6+
- PostgreSQL 12+

## 🗄️ Configuración de Base de Datos

La configuración de conexión se resuelve por variables de entorno, con valores por defecto para desarrollo local — no hay credenciales fijas en el código.

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/hospedium_db` |
| `DB_USERNAME` | Usuario de la base de datos | `postgres` |
| `DB_PASSWORD` | Contraseña del usuario | `12345678` |

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
CREATE DATABASE hospedium_db;
ALTER USER postgres WITH PASSWORD '12345678';
```

> Si prefieres un usuario dedicado en vez de `postgres`, créalo y exporta `DB_USERNAME` / `DB_PASSWORD` con esos valores antes de levantar la app.

3. (Opcional) Sobreescribir la configuración por defecto:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/hospedium_db
export DB_USERNAME=postgres
export DB_PASSWORD=12345678
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

- **TipoHabitacion**: catálogo de tipos (sencilla, doble, lujo), cada uno con `nombre` y `precioNoche`.
- **Habitacion**: habitación física del hotel, identificada por `numero` y `piso`, y asociada a un único `TipoHabitacion`.
- **Huesped**: `nombre`, `apellidos` y `pasaporte` (único).
- **Reserva**: solicitud de un huésped indicando `tipoHabitacion`, `fechaEntrada` y `fechaSalida`. No se elige una habitación concreta al crearla. Estados: `PENDIENTE`, `ASIGNADA`, `CANCELADA`.
- **Asignacion**: vincula una `Reserva` con una `Habitacion` física concreta una vez que el hotel resuelve la disponibilidad.

### Reglas de negocio implementadas

- `fechaEntrada` debe ser anterior a `fechaSalida` (mínimo una noche).
- La habitación asignada debe pertenecer al tipo solicitado en la reserva.
- No puede existir otra asignación de la misma habitación con fechas solapadas — el solapamiento se calcula en JPQL como `entradaExistente < salidaNueva AND salidaExistente > entradaNueva`, sin necesidad de mantener un campo `ocupada` en `Habitacion`.
- Solo una reserva `PENDIENTE` puede asignarse o cancelarse.
- Una reserva `CANCELADA` no puede volver a asignarse.
- No se puede eliminar una habitación que ya tiene asignaciones registradas (se preserva el historial de ocupación).
- Operaciones que combinan varias escrituras (crear reserva, asignar habitación, cancelar) están marcadas con `@Transactional`; las consultas de solo lectura usan `@Transactional(readOnly = true)`.

### Arquitectura por capas

`Controller` → `Service` (reglas de negocio + `@Transactional`) → `Repository` (JPA/JPQL) → `Model` (entidades), con `DTO` (records) de entrada/salida y `Mapper` dedicados para no exponer las entidades directamente en la API.

## 🔌 Endpoints de la API

Base URL: `http://localhost:8080/api/v1`

### 🛏️ Tipos de habitación

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/tipos-habitacion` | Crear tipo de habitación |
| `GET` | `/tipos-habitacion` | Listar todos los tipos |
| `GET` | `/tipos-habitacion/{id}` | Obtener tipo por ID |
| `PUT` | `/tipos-habitacion/{id}` | Actualizar tipo |
| `DELETE` | `/tipos-habitacion/{id}` | Eliminar tipo |

**Body de creación/actualización:**

```json
{
  "nombre": "Doble",
  "precioNoche": 85.00
}
```

Validaciones: `nombre` obligatorio · `precioNoche` obligatorio, mayor o igual a 0.

### 🚪 Habitaciones

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/habitaciones` | Crear habitación |
| `GET` | `/habitaciones` | Listar todas las habitaciones |
| `GET` | `/habitaciones/{id}` | Obtener habitación por ID |
| `PUT` | `/habitaciones/{id}` | Actualizar habitación |
| `DELETE` | `/habitaciones/{id}` | Eliminar habitación (rechazada si tiene asignaciones) |
| `POST` | `/habitaciones/disponibles` | Buscar habitaciones libres para un tipo y rango de fechas |

**Body de creación/actualización:**

```json
{
  "numero": "204",
  "piso": 2,
  "tipoHabitacionId": 1
}
```

**Body de búsqueda de disponibilidad** (`POST /habitaciones/disponibles`):

```json
{
  "tipoHabitacionId": 1,
  "fechaEntrada": "2026-06-10",
  "fechaSalida": "2026-06-15"
}
```

### 🧑 Huéspedes

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/huespedes` | Registrar huésped |
| `GET` | `/huespedes/{id}` | Obtener huésped por ID |
| `GET` | `/huespedes/{id}/reservas` | Historial de reservas del huésped |

**Body de creación:**

```json
{
  "nombre": "Ana",
  "apellido": "García",
  "pasaporte": "AB123456"
}
```

Validaciones: todos los campos obligatorios · `pasaporte` debe ser único.

### 📅 Reservas

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/reservas` | Crear reserva (queda en estado `PENDIENTE`) |
| `GET` | `/reservas/{id}` | Obtener reserva por ID |
| `PATCH` | `/reservas/{id}/cancelar` | Cancelar reserva pendiente |
| `POST` | `/reservas/{id}/asignar` | Asignar una habitación física a la reserva |

**Body de creación de reserva:**

```json
{
  "fechaEntrada": "2026-06-10",
  "fechaSalida": "2026-06-15",
  "huespedId": 1,
  "tipoHabitacionId": 1
}
```

**Body de asignación** (`POST /reservas/{id}/asignar`):

```json
{
  "habitacionId": 3
}
```

## ⚠️ Manejo de Errores

La API implementa **RFC 9457 (Problem Details for HTTP APIs)** usando `ProblemDetail` de Spring, a través de un `GlobalExceptionHandler` centralizado. Cada error incluye `type` (URI propia del código de error), `title`, `status`, `detail`, `instance`, y las propiedades adicionales `timestamp` y `code`.

**Validación de un DTO con `@Valid` (400 Bad Request, code `VALIDATION_ERROR`)**

```json
{
  "type": "https://neisser.dev/problems/VALIDATION_ERROR",
  "title": "Error de validación",
  "status": 400,
  "detail": "Los datos enviados no son válidos",
  "instance": "/api/v1/reservas",
  "timestamp": "2026-06-01T14:30:45.123456Z",
  "code": "VALIDATION_ERROR",
  "errors": [
    "fechaEntrada: La fecha de entrada es obligatoria."
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
  "detail": "Huésped con id 999 no encontrado",
  "instance": "/api/v1/huespedes/999",
  "timestamp": "2026-06-01T14:30:45.123456Z",
  "code": "RESOURCE_NOT_FOUND",
  "resource": "Huésped",
  "resourceId": "999"
}
```

**Regla de negocio violada — ej. solapamiento de fechas (422 Unprocessable Entity, code `BUSINESS_RULE_VIOLATION`)**

```json
{
  "type": "https://neisser.dev/problems/BUSINESS_RULE_VIOLATION",
  "title": "Regla de negocio no satisfecha",
  "status": 422,
  "detail": "La habitación no está disponible para esas fechas",
  "instance": "/api/v1/reservas/5/asignar",
  "timestamp": "2026-06-01T14:30:45.123456Z",
  "code": "BUSINESS_RULE_VIOLATION"
}
```

**Conflicto de estado — ej. pasaporte duplicado (409 Conflict, code `RESOURCE_CONFLICT`)**

```json
{
  "type": "https://neisser.dev/problems/RESOURCE_CONFLICT",
  "title": "Conflicto con el estado actual del recurso",
  "status": 409,
  "detail": "Ya existe un huesped con ese pasaporte",
  "instance": "/api/v1/huespedes",
  "timestamp": "2026-06-01T14:30:45.123456Z",
  "code": "RESOURCE_CONFLICT"
}
```

## 🧪 Ejemplos con curl

```bash
# Crear tipo de habitación
curl -X POST http://localhost:8080/api/v1/tipos-habitacion \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Doble","precioNoche":85.00}'

# Crear habitación
curl -X POST http://localhost:8080/api/v1/habitaciones \
  -H "Content-Type: application/json" \
  -d '{"numero":"204","piso":2,"tipoHabitacionId":1}'

# Registrar huésped
curl -X POST http://localhost:8080/api/v1/huespedes \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana","apellido":"García","pasaporte":"AB123456"}'

# Crear reserva
curl -X POST http://localhost:8080/api/v1/reservas \
  -H "Content-Type: application/json" \
  -d '{"fechaEntrada":"2026-06-10","fechaSalida":"2026-06-15","huespedId":1,"tipoHabitacionId":1}'

# Buscar habitaciones disponibles
curl -X POST http://localhost:8080/api/v1/habitaciones/disponibles \
  -H "Content-Type: application/json" \
  -d '{"tipoHabitacionId":1,"fechaEntrada":"2026-06-10","fechaSalida":"2026-06-15"}'

# Asignar habitación a una reserva
curl -X POST http://localhost:8080/api/v1/reservas/1/asignar \
  -H "Content-Type: application/json" \
  -d '{"habitacionId":3}'

# Cancelar reserva
curl -X PATCH http://localhost:8080/api/v1/reservas/1/cancelar

# Historial de reservas de un huésped
curl http://localhost:8080/api/v1/huespedes/1/reservas
```

## 📝 Notas Técnicas

- **Tablas**: se crean automáticamente con `hibernate.ddl-auto=update`.
- **Serialización**: `BigDecimal` para precios, `LocalDate` para fechas de entrada/salida.
- **Validación**: DTOs (`record`) con anotaciones de Jakarta Validation.
- **Configuración por entorno**: la conexión a base de datos se resuelve con `${DB_URL}`, `${DB_USERNAME}` y `${DB_PASSWORD}`, con valores por defecto de desarrollo — no hay secretos en el código fuente.

## 👨‍💻 Autor

Neisser Torres Peña - [@neisserdev](https://github.com/neisserdev)
