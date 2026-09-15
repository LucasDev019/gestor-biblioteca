Gestor de Biblioteca

Aplicación completa para la gestión de préstamos de una biblioteca: una API REST en Java con Spring Boot, y una interfaz de escritorio en Java Swing.

¿Qué hace?

- Gestión de libros: alta, baja, modificación y listado, con control de ejemplares disponibles.
- Gestión de usuarios de la biblioteca.
- Sistema de préstamos con lógica de negocio real: no permite prestar un libro sin ejemplares disponibles, descuenta stock automáticamente al prestar y lo repone al devolver.
- Interfaz de escritorio que consume la API mediante peticiones HTTP, sin necesidad de Postman ni herramientas externas para usarla.

Tecnologías

- Backend: Java 21, Spring Boot, Spring Data JPA, MySQL
- Cliente: Java Swing, HttpClient (java.net.http), Jackson (parseo de JSON)
- Build: Maven

Arquitectura

Cliente Swing (UI) → HTTP (JSON) → API REST (Spring Boot) → JPA/Hibernate → MySQL



La interfaz de escritorio no accede nunca directamente a la base de datos: todo pasa por la API REST, igual que lo haría una app móvil o una web.


Cómo ejecutarlo

1. Requisitos previos
- Java 21
- MySQL 8 instalado y corriendo
- Maven (o usar el wrapper incluido, `mvnw`)

2. Crear la base de datos

```sql
CREATE DATABASE biblioteca;
```

3. Configurar la conexión

En `src/main/resources/application.properties`, ajusta usuario y contraseña de tu MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/biblioteca
spring.datasource.username=root
spring.datasource.password=TU_CONTRASEÑA
```

4. Arrancar el backend

Ejecuta `GestorBibliotecaApplication.java`. Las tablas se crean automáticamente al arrancar.

5. Arrancar la interfaz gráfica

Con el backend corriendo, ejecuta `ui/MainWindow.java` en un proceso aparte.

Endpoints principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | /libros | Lista todos los libros |
| POST | /libros | Crea un libro |
| PUT | /libros/{id} | Actualiza un libro |
| DELETE | /libros/{id} | Elimina un libro |
| POST | /prestamos | Crea un préstamo (valida ejemplares disponibles) |
| PUT | /prestamos/{id}/devolver | Registra la devolución de un préstamo |


Qué aprendí haciendo este proyecto

- A diseñar y modelar relaciones entre entidades (`@ManyToOne`) a partir de un diagrama UML.
- A construir una API REST completa con Spring Boot y Spring Data JPA, incluyendo lógica de negocio más allá de un CRUD simple.
- A consumir una API propia desde un cliente distinto (Swing), reforzando la diferencia entre backend y frontend/cliente.
- A depurar problemas reales de configuración: paquetes de Java, versiones de librerías, conflictos de credenciales en Git.

Autor

Lucas Gómez García — Estudiante de DAM
