Hospedium necesita reemplazar el control manual de reservas por una API que permita gestionar los distintos tipos de habitación, las habitaciones físicas del hotel, los huéspedes y las reservas.

El hotel dispone de diferentes tipos de habitación, como sencilla, doble y de lujo. Cada tipo tiene un nombre y un precio por noche. Cada habitación física pertenece a un único tipo y se identifica mediante su número y piso.

Un huésped puede solicitar una reserva indicando el tipo de habitación que desea, la fecha de entrada y la fecha de salida. La solicitud no selecciona inicialmente una habitación concreta: el hotel asignará posteriormente una habitación física que pertenezca al tipo solicitado y que esté libre durante todo el período de la reserva.

Una habitación no puede ser asignada a dos reservas cuyos períodos de alojamiento se solapen. Una reserva puede encontrarse pendiente de asignación, asignada o cancelada. Una reserva cancelada no ocupa ninguna habitación.

Reglas:
- fechaEntrada debe ser anterior a fechaSalida.
- Una reserva debe tener al menos una noche.
- Una habitación asignada debe pertenecer al tipo solicitado.
- No puede existir otra asignación de la misma habitación con fechas solapadas.
- El solapamiento se determina mediante: entradaExistente < salidaNueva y salidaExistente > entradaNueva
- Una reserva cancelada no puede ser asignada.
- Una reserva asignada no puede volver a asignarse.
- Cancelar una reserva solo es posible mientras esté pendiente.

Endpoints:
- POST   /api/v1/tipos-habitacion
- GET    /api/v1/tipos-habitacion
- GET    /api/v1/tipos-habitacion/{id}
- PUT  /api/v1/tipos-habitacion/{id}
- DELETE /api/v1/tipos-habitacion/{id}


- POST   /api/v1/habitaciones
- GET    /api/v1/habitaciones
- GET    /api/v1/habitaciones/{id}
- PUT  /api/v1/habitaciones/{id}
- DELETE /api/v1/habitaciones/{id}

- POST   /api/v1/huespedes
- GET    /api/v1/huespedes/{id}

- POST   /api/v1/reservas
- GET    /api/v1/reservas/{id}
- PATCH  /api/v1/reservas/{id}/cancelar

- GET    /api/v1/habitaciones/disponibles
- POST   /api/v1/reservas/{id}/asignar

- GET    /api/v1/huespedes/{id}/reservas