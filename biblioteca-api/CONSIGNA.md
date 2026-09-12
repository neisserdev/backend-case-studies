‎La Red de Bibliotecas Municipales necesita reemplazar el control manual de préstamos por una API que permita conocer en todo momento qué ejemplares están disponibles, quién tiene cada ejemplar y cuántos préstamos puede mantener abiertos cada usuario.
‎
‎Los usuarios pueden ser inscritos o no inscritos. Los usuarios inscritos pueden mantener hasta 15 préstamos abiertos simultáneamente, mientras que los no inscritos pueden mantener hasta 5.
‎
‎Cada libro representa una obra bibliográfica y puede tener uno o varios ejemplares físicos. Un ejemplar puede encontrarse prestado o disponible.
‎
‎Cuando se registra un préstamo se debe comprobar que el ejemplar no tenga actualmente otro préstamo abierto. Además, un mismo libro no puede superar los 10 préstamos registrados durante un mismo mes calendario, independientemente de cuántos ejemplares físicos tenga.
‎
‎Cada libro posee un código único formado por las dos primeras letras de su género en mayúsculas y una secuencia de cuatro dígitos correspondiente a ese género.
‎
‎Reglas: 
‎- Un ejemplar no puede tener dos préstamos abiertos.
‎- INSCRITO → máximo 15 préstamos abiertos. 
‎- NO_INSCRITO → máximo 5.
‎- Un libro admite máximo 10 préstamos nuevos por mes calendario.
‎- UNICO se devuelve el mismo día.
‎- VARIOS debe tener una fecha de devolución posterior a la fecha del préstamo.
‎- fechaDevolucionReal inicialmente es null.
‎- Un préstamo ya devuelto no puede volver a devolverse.
‎- El código del libro debe ser único.
‎
‎Endpoints:
‎POST   /api/v1/usuarios
‎GET    /api/v1/usuarios/{id}
‎PATCH  /api/v1/usuarios/{id}
‎DELETE /api/v1/usuarios/{id}
‎
‎POST   /api/v1/libros
‎GET    /api/v1/libros
‎GET    /api/v1/libros/{id}
‎PATCH  /api/v1/libros/{id}
‎DELETE /api/v1/libros/{id}
‎
‎POST   /api/v1/libros/{id}/ejemplares
‎GET    /api/v1/libros/{id}/ejemplares
‎DELETE /api/v1/ejemplares/{id}
‎
‎GET    /api/v1/libros/disponibles?genero=POLICIACO
‎
‎POST   /api/v1/prestamos
‎GET    /api/v1/prestamos/{id}
‎PATCH  /api/v1/prestamos/{id}/devolver
‎
‎GET    /api/v1/usuarios/{id}/prestamos
‎