El Banco Popular de Ahorro necesita modernizar el sistema encargado de administrar las cuentas de sus clientes y registrar sus movimientos.

Un cliente puede ser particular o empresa. Las empresas se clasifican además como rentables o no rentables.

Cada cuenta pertenece a un cliente y comienza con un depósito inicial mínimo de 50 dólares. Una cuenta puede encontrarse activa o bloqueada.

Los depósitos incrementan el saldo. Las extracciones disminuyen el saldo. Si una extracción provoca que el saldo sea negativo, la cuenta queda bloqueada y no puede realizar nuevas operaciones hasta recibir un depósito.

Cada operación registrada forma parte del historial permanente de la cuenta y no puede modificarse ni eliminarse posteriormente.

Una vez al año se aplica automáticamente el interés correspondiente a las cuentas activas.

## Reglas

- Depósito inicial ≥ $50.
- No se puede operar sobre una cuenta cerrada.
- Una cuenta bloqueada no admite extracciones.
- Una extracción que deja saldo negativo bloquea la cuenta.
- Un depósito sobre una cuenta bloqueada la reactiva.
- Las operaciones registradas son inmutables.
- Particular → 2.5%.
- Empresa rentable → 3.5%.
- Empresa no rentable → 1.5%.

## CRUD

| Recurso | Operaciones |
|---|---|
| Cliente | Crear, consultar, actualizar |
| Cuenta | Crear, consultar, cerrar |
| Operación | Registrar depósito/extracción, consultar |
| Interés | Aplicar |

## Endpoints

```
POST   /api/v1/clientes
GET    /api/v1/clientes/{id}
PATCH  /api/v1/clientes/{id}

POST   /api/v1/cuentas
GET    /api/v1/cuentas/{id}
PATCH  /api/v1/cuentas/{id}/cerrar

POST   /api/v1/cuentas/{id}/depositos
POST   /api/v1/cuentas/{id}/extracciones

GET    /api/v1/cuentas/{id}/operaciones

POST   /api/v1/cuentas/interes/anual
```
