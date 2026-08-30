# 🏦 BPA API - Gestión de Cuentas del Banco Popular de Ahorro

API REST desarrollada con **Spring Boot 4** para modernizar el sistema del Banco Popular de Ahorro encargado de administrar las cuentas de sus clientes y registrar sus movimientos: clientes particulares y empresariales, cuentas con depósito inicial mínimo, depósitos, extracciones, bloqueo/reactivación automática e interés anual.

> Este ejercicio forma parte de **[`backend-case-studies`](../)**, un monorepo de casos de estudio backend donde cada carpeta resuelve un problema puntual de diseño e implementación. Consigna original: [`CONSIGNA.md`](./CONSIGNA.md).

## 🎯 Aporte técnico

Este caso se centra principalmente en:

- Modelado de relaciones entre entidades (`Cliente` ⇄ `Cuenta` ⇄ `Operacion`).
- Reglas de negocio de dominio bancario: depósito mínimo, bloqueo por saldo negativo, reactivación por depósito, cierre de cuenta.
- Inmutabilidad del historial: `Operacion` solo se crea y se consulta, nunca se edita ni se elimina.
- Cálculo de interés anual diferenciado por tipo de cliente mediante `switch` de patrones.
- Validación cruzada entre campos de un mismo DTO con una constraint de Jakarta Validation a medida (`@ClasificacionEmpresaRequerida`).
- Consulta JPQL para resolver el cliente dueño de una cuenta.
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
CREATE DATABASE bpa_db;
ALTER USER postgres WITH PASSWORD 'tu_password';
```

> Si prefieres un usuario dedicado en vez de `postgres`, créalo y exporta `DB_USERNAME` / `DB_PASSWORD` con esos valores antes de levantar la app.

3. Exportar la configuración antes de levantar la aplicación:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/bpa_db
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

- **Cliente**: `nombre`, `documento` (único) y `tipoCliente` (`PARTICULAR` o `EMPRESA`). Si es `EMPRESA`, requiere además `clasificacionEmpresa` (`RENTABLE` o `NO_RENTABLE`).
- **Cuenta**: pertenece a un `Cliente`, mantiene un `saldo` y un `estadoCuenta` (`ACTIVA`, `BLOQUEADA` o `CERRADA`). Se crea con un depósito inicial mínimo de $50.
- **Operacion**: registro inmutable de cada movimiento sobre una `Cuenta` (`DEPOSITO`, `EXTRACCION` o `INTERES`), con `monto`, `saldoResultante` y `fecha`. Forma parte del historial permanente y no se expone ninguna ruta de edición o borrado.

### Reglas de negocio implementadas

- El depósito inicial debe ser de al menos $50, o la creación de la cuenta se rechaza.
- No se puede operar (depositar, extraer o aplicar interés) sobre una cuenta `CERRADA`.
- Una cuenta `BLOQUEADA` no admite extracciones.
- Una extracción que deja el saldo negativo bloquea la cuenta automáticamente.
- Un depósito sobre una cuenta `BLOQUEADA` la reactiva inmediatamente, incluso si el saldo resultante continúa siendo negativo.
- Las operaciones son inmutables: no existe `PUT`/`PATCH`/`DELETE` sobre `Operacion`, solo registro y consulta.
- El interés anual se aplica a las cuentas `ACTIVA` y queda registrado como una operación más del historial. La operación es idempotente por año: una cuenta no recibe dos intereses durante el mismo año.
  - Particular → 2.5%
  - Empresa rentable → 3.5%
  - Empresa no rentable → 1.5%
- La aplicación del interés se expone mediante `POST /cuentas/interes/anual`; este endpoint debe ser invocado una vez al año por un planificador externo o por el mecanismo de ejecución programada del entorno.
- Operaciones que combinan varias escrituras (crear cuenta, depositar, extraer, aplicar interés) están marcadas con `@Transactional`; las consultas de solo lectura usan `@Transactional(readOnly = true)`.

### Arquitectura por capas

`Controller` → `Service` (reglas de negocio + `@Transactional`) → `Repository` (JPA/JPQL) → `Model` (entidades), con `DTO` (records) de entrada/salida y `Mapper` dedicados para no exponer las entidades directamente en la API.

## 🔌 Endpoints de la API

Base URL: `http://localhost:8080/api/v1`

### 🧑 Clientes

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/clientes` | Crear cliente (particular o empresa) |
| `GET` | `/clientes/{id}` | Obtener cliente por ID |
| `PATCH` | `/clientes/{id}` | Actualizar parcialmente nombre, documento o clasificación empresarial |

**Body de creación:**

```json
{
  "nombre": "Ana García",
  "documento": "P-001",
  "tipoCliente": "PARTICULAR",
  "clasificacionEmpresa": null
}
```

```json
{
  "nombre": "Comercial del Sur S.A.",
  "documento": "E-001",
  "tipoCliente": "EMPRESA",
  "clasificacionEmpresa": "RENTABLE"
}
```

Validaciones: `nombre` y `documento` obligatorios · `documento` debe ser único · `tipoCliente` obligatorio · si `tipoCliente` es `EMPRESA`, `clasificacionEmpresa` es obligatoria.

**Body de actualización (`PATCH`, todos los campos opcionales):**

```json
{
  "nombre": "Ana García Pérez"
}
```

### 💳 Cuentas

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/cuentas` | Crear cuenta con depósito inicial |
| `GET` | `/cuentas/{id}` | Obtener cuenta por ID |
| `PATCH` | `/cuentas/{id}/cerrar` | Cerrar cuenta |
| `POST` | `/cuentas/{id}/depositos` | Registrar un depósito |
| `POST` | `/cuentas/{id}/extracciones` | Registrar una extracción |
| `GET` | `/cuentas/{id}/operaciones` | Historial de operaciones de la cuenta |
| `POST` | `/cuentas/interes/anual` | Aplicar una vez por año el interés a las cuentas activas que aún no lo recibieron |

**Body de creación de cuenta:**

```json
{
  "clienteId": 1,
  "depositoInicial": 100.00
}
```

**Body de depósito/extracción** (`POST /cuentas/{id}/depositos` o `/extracciones`):

```json
{
  "monto": 250.00
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
  "instance": "/api/v1/clientes",
  "timestamp": "2026-06-01T14:30:45.123456Z",
  "code": "VALIDATION_ERROR",
  "errors": [
    "Una empresa debe clasificarse como RENTABLE o NO_RENTABLE."
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
  "detail": "cuenta con id 999 no encontrado",
  "instance": "/api/v1/cuentas/999",
  "timestamp": "2026-06-01T14:30:45.123456Z",
  "code": "RESOURCE_NOT_FOUND",
  "resource": "cuenta",
  "resourceId": "999"
}
```

**Regla de negocio violada — ej. depósito inicial insuficiente (422 Unprocessable Entity, code `BUSINESS_RULE_VIOLATION`)**

```json
{
  "type": "https://neisser.dev/problems/BUSINESS_RULE_VIOLATION",
  "title": "Regla de negocio no satisfecha",
  "status": 422,
  "detail": "El depósito inicial debe ser de al menos $50",
  "instance": "/api/v1/cuentas",
  "timestamp": "2026-06-01T14:30:45.123456Z",
  "code": "BUSINESS_RULE_VIOLATION"
}
```

**Conflicto de estado — ej. documento duplicado (409 Conflict, code `RESOURCE_CONFLICT`)**

```json
{
  "type": "https://neisser.dev/problems/RESOURCE_CONFLICT",
  "title": "Conflicto con el estado actual del recurso",
  "status": 409,
  "detail": "Ya existe un cliente con ese documento",
  "instance": "/api/v1/clientes",
  "timestamp": "2026-06-01T14:30:45.123456Z",
  "code": "RESOURCE_CONFLICT"
}
```

## 🧪 Ejemplos con curl

```bash
# Crear cliente particular
curl -X POST http://localhost:8080/api/v1/clientes \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana García","documento":"P-001","tipoCliente":"PARTICULAR"}'

# Crear cliente empresa rentable
curl -X POST http://localhost:8080/api/v1/clientes \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Comercial del Sur S.A.","documento":"E-001","tipoCliente":"EMPRESA","clasificacionEmpresa":"RENTABLE"}'

# Crear cuenta con depósito inicial
curl -X POST http://localhost:8080/api/v1/cuentas \
  -H "Content-Type: application/json" \
  -d '{"clienteId":1,"depositoInicial":100.00}'

# Registrar un depósito
curl -X POST http://localhost:8080/api/v1/cuentas/1/depositos \
  -H "Content-Type: application/json" \
  -d '{"monto":250.00}'

# Registrar una extracción
curl -X POST http://localhost:8080/api/v1/cuentas/1/extracciones \
  -H "Content-Type: application/json" \
  -d '{"monto":50.00}'

# Consultar historial de operaciones
curl http://localhost:8080/api/v1/cuentas/1/operaciones

# Cerrar cuenta
curl -X PATCH http://localhost:8080/api/v1/cuentas/1/cerrar

# Actualizar datos del cliente
curl -X PATCH http://localhost:8080/api/v1/clientes/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana García Pérez"}'

# Aplicar interés anual a las cuentas activas
curl -X POST http://localhost:8080/api/v1/cuentas/interes/anual
```

## 📝 Notas Técnicas

- **Tablas**: se crean automáticamente con `hibernate.ddl-auto=update`.
- **Serialización**: `BigDecimal` para saldos y montos (comparados siempre con `compareTo`, nunca con `equals`), `LocalDateTime` para la fecha de cada operación.
- **Validación**: DTOs (`record`) con anotaciones de Jakarta Validation, incluida una constraint a nivel de clase (`@ClasificacionEmpresaRequerida`) para reglas que dependen de más de un campo.
- **Configuración por entorno**: la conexión a base de datos se resuelve únicamente con `${DB_URL}`, `${DB_USERNAME}` y `${DB_PASSWORD}`, sin valores por defecto — no hay secretos ni datos de conexión en el código fuente.

## 👨‍💻 Autor

Neisser Torres Peña - [@neisserdev](https://github.com/neisserdev)
