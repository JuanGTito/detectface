# Guía de Integración Frontend - Sistema de Monitoreo Inteligente

Esta guía detalla cómo conectar cualquier aplicación Frontend (Web, Android, iOS) con el backend de Monitoreo Inteligente.

---

## 1. Direcciones Base (URLs)

* **API REST**: `http://localhost:8080`
* **WebSocket (Tiempo Real)**: `http://localhost:8080/ws` (Soporta protocolo STOMP sobre SockJS)

---

## 2. Flujo de Autenticación (JWT)

El backend utiliza autenticación basada en JSON Web Tokens (JWT) sin estado.

```
Frontend                           Backend
   |                                  |
   |---- POST /auth/login ------------>| (email, password)
   |<--- Retorna tokens --------------| (accessToken 15m + refreshToken 7d)
   |                                  |
   |-- GET /devices ----------------->| (Con Header: Authorization: Bearer <accessToken>)
   |<-- Retorna 200 OK ---------------|
```

### Ejemplo de Interceptor Axios (Manejo Automático de Tokens)

Puedes utilizar este interceptor en JavaScript/TypeScript (para React, Vue, Angular o React Native) para adjuntar el token a cada solicitud y refrescarlo automáticamente si expira:

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

// 1. Interceptor de Solicitudes: Adjuntar accessToken
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 2. Interceptor de Respuestas: Refrescar token automáticamente en error 401
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Si el error es 401 y no hemos intentado reintentar aún
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
          throw new Error('No refresh token available');
        }

        // Llamar al endpoint de refresco
        const res = await axios.post('http://localhost:8080/auth/refresh', { refreshToken });
        const { accessToken, refreshToken: newRefreshToken } = res.data.data;

        // Guardar nuevos tokens
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', newRefreshToken);

        // Actualizar header de la solicitud fallida y reintentar
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Si el refresco falla, desloguear al usuario
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

export default api;
```

---

## 3. Integración de WebSockets (Tiempo Real)

Para recibir alertas y detecciones al instante, el frontend debe conectarse al broker WebSocket.

### Dependencias recomendadas:
* Para Web (React, Angular, Vue): `sockjs-client` y `@stomp/stompjs`
* Para Android: Biblioteca STOMP de Java/Kotlin (como `github.com/NaikSoftware/StompProtocolAndroid`)

### Ejemplo de Conexión en JavaScript (Web)

```javascript
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const connectWebSocket = (onDetectionReceived, onNotificationReceived, userId) => {
  const socket = new SockJS('http://localhost:8080/ws');
  const stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000, // Reconexión automática cada 5 segundos
    debug: (str) => console.log(str),
  });

  stompClient.onConnect = (frame) => {
    console.log('Conectado a WebSocket con éxito');

    // 1. Suscribirse a las detecciones globales de la IA
    stompClient.subscribe('/topic/detections', (message) => {
      const detection = JSON.parse(message.body);
      onDetectionReceived(detection);
    });

    // 2. Suscribirse a notificaciones globales
    stompClient.subscribe('/topic/notifications', (message) => {
      const notification = JSON.parse(message.body);
      onNotificationReceived(notification);
    });

    // 3. Suscribirse a notificaciones exclusivas de un usuario
    if (userId) {
      stompClient.subscribe(`/topic/notifications-${userId}`, (message) => {
        const notification = JSON.parse(message.body);
        onNotificationReceived(notification);
      });
    }
  };

  stompClient.onStompError = (frame) => {
    console.error('Error de STOMP:', frame.headers['message']);
  };

  stompClient.activate();
  return stompClient;
};
```

---

## 4. Referencia de Endpoints (API REST)

Todas las respuestas exitosas y de error siguen el formato unificado:
```json
{
  "success": true,
  "message": "Mensaje informativo",
  "data": { ... },
  "timestamp": "2026-07-14 15:10:00"
}
```

### Tabla de Endpoints

| Método | Endpoint | Rol Mínimo Requerido | Descripción |
| :--- | :--- | :--- | :--- |
| **POST** | `/auth/register` | Público | Registro de nuevos usuarios (Rol por defecto `USUARIO`). |
| **POST** | `/auth/login` | Público | Login. Devuelve JWT y refresco. |
| **POST** | `/auth/refresh` | Público | Obtener nuevo token de acceso a partir del de refresco. |
| **GET** | `/users` | `ADMIN`, `RRHH`, `GERENTE` | Listar todos los usuarios. |
| **GET** | `/users/{id}` | Todos | Obtener datos de un usuario específico. |
| **POST** | `/users` | `ADMIN` | Crear un nuevo usuario asignándole rol. |
| **PUT** | `/users/{id}` | `ADMIN` o el propio usuario | Actualizar datos del usuario. |
| **DELETE** | `/users/{id}` | `ADMIN` | Eliminar un usuario. |
| **GET** | `/roles` | `ADMIN` | Listar todos los roles. |
| **POST** | `/roles` | `ADMIN` | Crear un rol. |
| **GET** | `/devices` | `ADMIN`, `RRHH`, `GERENTE` | Listar todos los dispositivos de captura. |
| **POST** | `/devices` | `ADMIN` | Registrar cámara IP o Webcam. |
| **PUT** | `/devices/{id}` | `ADMIN` | Editar configuración de cámara. |
| **DELETE** | `/devices/{id}` | `ADMIN` | Eliminar cámara de la red. |
| **GET** | `/detections` | `ADMIN`, `RRHH`, `GERENTE`, `PSICOLOGIA` | Listar detecciones históricas. |
| **POST** | `/detections` | `ADMIN`, `USUARIO` | Registrar detección realizada por modelo de IA externa. |
| **GET** | `/notifications` | `ADMIN`, `RRHH`, `GERENTE` | Listar todas las notificaciones enviadas. |
| **GET** | `/notifications/user/{userId}`| `ADMIN` o propio usuario | Listar notificaciones enviadas a un usuario específico. |
| **PATCH** | `/notifications/{id}/read`| `ADMIN` o propio usuario | Marcar notificación como leída. |

---

## 5. Ejemplos de Payloads JSON

### POST `/auth/login` (Petición)
```json
{
  "email": "juan@example.com",
  "password": "mi_clave_secreta"
}
```

### POST `/auth/login` (Respuesta)
```json
{
  "success": true,
  "message": "Autenticación exitosa",
  "data": {
    "accessToken": "ey...",
    "refreshToken": "ey...",
    "id": 1,
    "nombres": "Juan",
    "email": "juan@example.com",
    "role": "ADMIN"
  },
  "timestamp": "2026-07-14 15:10:00"
}
```

### POST `/devices` (Petición - Crear Cámara IP)
```json
{
  "nombre": "Cámara Entrada Principal",
  "tipo": "IP_CAMERA",
  "ip": "192.168.1.50",
  "puerto": 554,
  "usuario": "admin",
  "password": "cam_password",
  "rtspUrl": "rtsp://admin:cam_password@192.168.1.50:554/h264",
  "indiceWebcam": null,
  "estado": "ONLINE"
}
```

### POST `/detections` (Petición - Registrar evento de IA)
```json
{
  "deviceId": 1,
  "emocion": "FELIZ",
  "usoCelular": false,
  "confianza": 0.94,
  "imagen": "data:image/jpeg;base64,/9j/4AAQSkZJRg..."
}
```
