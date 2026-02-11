# 🛡️ API Gateway – Intranet Microservices

![Maven](https://img.shields.io/badge/Maven-3.9.6-blue)
![Java](https://img.shields.io/badge/Java-21-brightgreen)
![Docker](https://img.shields.io/badge/Docker-ready-blue)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-Gateway-green)
![Status](https://img.shields.io/badge/Status-Development-orange)

---

## 📌 Descripción General

El **API Gateway** es el punto de entrada único para todos los microservicios de la intranet corporativa.

Su responsabilidad es recibir todas las solicitudes del frontend (Angular) y redirigirlas al microservicio correspondiente, aplicando reglas de seguridad, filtros y validaciones antes de que lleguen al destino final.

En otras palabras, es la **puerta de acceso central** de toda la arquitectura.

---

## 🎯 Rol dentro de la Arquitectura

El API Gateway se encarga de:

- 🔀 Enrutamiento dinámico hacia los microservicios registrados en **Eureka**
- 🔐 Aplicación de seguridad basada en JWT
- 🌍 Gestión centralizada de CORS
- 📊 Exposición de endpoints de monitoreo
- 📘 Consolidación de documentación Swagger

Sin el Gateway, el frontend tendría que comunicarse directamente con cada microservicio, lo que aumentaría la complejidad y los riesgos de seguridad.

---

## 🔀 Enrutamiento Dinámico

Las rutas se configuran utilizando `lb://service-name`, permitiendo balanceo de carga automático gracias a Eureka.

### Ejemplo de funcionamiento:

- `/api/auth/login` → `auth-service`
- `/api/users/**` → `user-service`
- `/api/academy/**` → `academy-service`
- /api/requirement/** → `requirement-service`

El Gateway puede:

- Reescribir rutas
- Agregar headers personalizados
- Aplicar filtros antes de reenviar la petición


---

## 🔐 Seguridad JWT

El Gateway valida los tokens JWT antes de permitir el acceso a rutas protegidas.

### Variable de entorno requerida:

| Variable | Descripción |
|----------|-------------|
| `JWT_PRIVATE_KEY` | Clave privada utilizada para la validación de tokens JWT |
| `EUREKA_SERVER_URL` | URL del servidor Eureka (ej: `http://localhost:8761/eureka`) |

⚠️ La clave nunca debe estar hardcodeada en el código fuente.

---

## 🌍 Configuración CORS

Se gestiona CORS de forma centralizada.

### Entornos configurados:

- **Desarrollo:**  
  `http://localhost:4200`

- **Producción:**  
  `https://intranet.proscience.com`

Se permiten:

- Credenciales
- Headers personalizados
- Métodos HTTP necesarios (GET, POST, PUT, DELETE, etc.)

---

## 📊 Actuator & Monitoreo

El Gateway expone endpoints para supervisión y diagnóstico:

| Endpoint | Descripción |
|----------|-------------|
| `/actuator/health` | Estado general del servicio |
| `/actuator/info` | Información del build |
| `/actuator/gateway` | Información del gateway |
| `/actuator/routes` | Rutas configuradas |

Permite monitoreo centralizado del tráfico y configuración.

---

## 📘 Documentación Unificada (Swagger / OpenAPI)

El Gateway consolida la documentación de los microservicios registrados.

Acceso:

http://localhost:8080/swagger-ui.html


Servicios documentados:

- Auth Service
- User Service (próximamente)
- Academy Service (próximamente)
- Requirement Service (próximamente)

Esto permite tener una **documentación centralizada** sin necesidad de acceder a cada servicio individualmente.

---

## 🚀 Ejecución Local

```bash
mvn spring-boot:run






