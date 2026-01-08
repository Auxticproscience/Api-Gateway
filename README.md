# API Gateway 🛡️

El **API Gateway** es el punto de entrada principal para todos los microservicios de la intranet.  
Gestiona el enrutamiento, balanceo de carga, seguridad y documentación centralizada de los microservicios registrados en **Eureka**.  

---

## 📌 Características principales

- **Enrutamiento dinámico:**  
  Redirige solicitudes a los microservicios registrados en Eureka usando `lb://service-name`.
  
- **Filtros y reescritura de rutas:**  
  - Ejemplo: `/api/auth/login` → Auth Service  
  - Se agregan cabeceras personalizadas como `X-Gateway-Source` para seguimiento.

- **CORS global y por servicio:**  
  Configurado para Angular (`http://localhost:4200`) y producción (`https://intranet.proscience.com`).  
  Permite credenciales y headers personalizados.

- **Seguridad JWT:**  
  La clave privada se toma de la variable de entorno `JWT_PRIVATE_KEY`.  
  Facilita la integración de autenticación entre servicios sin exponer la clave en el código.

- **Actuator & Health:**  
  - Endpoints disponibles: `/actuator/health`, `/actuator/info`, `/actuator/gateway`, `/actuator/routes`  
  - Permite monitoreo centralizado del gateway y sus rutas.

- **Documentación agregada con Swagger/OpenAPI:**  
  - `/swagger-ui.html` consolida la documentación de los microservicios:  
    - Auth Service  
    - User Service (próximamente)  

---

## ⚙️ Configuración

### 1. Variables de entorno

| Variable | Descripción |
|----------|-------------|
| `JWT_PRIVATE_KEY` | Clave privada para la firma de tokens JWT |
| `EUREKA_SERVER_URL` | URL del Eureka Server (ej: `http://localhost:8761/eureka`) |


### 2. Profiles de Spring

- **Dev:** CORS habilitado para localhost, logging DEBUG.
- **Prod:** CORS limitado al frontend oficial, logging WARN.

### 3. Puertos

- Predeterminado: `8080`  
  Cambiable en `application.yml` o variable de entorno.

---

## 🐳 Docker

### Dockerfile

```dockerfile
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
