# parcel-service

Microservicio de **parcelas** para la red de huertos urbanos comunitarios. Permite registrar y consultar las parcelas (zonas de cultivo) que luego serán monitoreadas con sensores IoT (humedad del suelo, temperatura, pH, NPK y calidad del aire).

Incluye validación y sanitización de entradas, excepciones personalizadas y mensajes de error uniformes en español.

## Stack tecnológico

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.x (Spring Web, Spring Validation) |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | PostgreSQL 16 |
| Build | Maven |
| Contenedores | Docker |

## Requisitos

- JDK 17 o superior
- Maven 3.9+ (o el wrapper `mvnw` incluido)
- Docker Desktop (para PostgreSQL)

## Estructura del proyecto

```
parcel-service/
├── Dockerfile
├── pom.xml
└── src/main/
    ├── resources/
    │   └── application.yml
    └── java/com/huertos/parcel/
        ├── ParcelServiceApplication.java
        ├── controller/   ParcelController
        ├── service/      ParcelService
        ├── repository/   ParcelRepository
        ├── model/        Parcel, GpsLocation, SoilType
        ├── dto/          CreateParcelRequest, LocationRequest,
        │                 ParcelResponse, ParcelDetailResponse
        ├── exception/    ErrorCode, ErrorResponse, BusinessException,
        │                 ParcelNotFoundException, DuplicateParcelException,
        │                 InvalidParcelDataException, GlobalExceptionHandler
        └── util/         InputSanitizer
```

## Configuración

La aplicación lee la conexión a la base de datos desde variables de entorno, con estos valores por defecto:

| Variable | Valor por defecto |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/huertos` |
| `DB_USER` | `huertos` |
| `DB_PASSWORD` | `huertos_pass` |

Otros ajustes relevantes de `application.yml`:

- El servicio escucha en el puerto `8080`.
- Los mensajes y stack traces internos nunca se exponen al cliente.
- Las propiedades desconocidas en el JSON se rechazan (`fail-on-unknown-properties`).
- `ddl-auto: update` crea y actualiza la tabla `parcels` automáticamente (apropiado para desarrollo).

## Ejecución

### 1. Levantar PostgreSQL

```powershell
docker run -d --name huertos-db `
  -e POSTGRES_DB=huertos `
  -e POSTGRES_USER=huertos `
  -e POSTGRES_PASSWORD=huertos_pass `
  -p 5432:5432 `
  postgres:16-alpine
```

### 2. Iniciar la aplicación

```powershell
mvn spring-boot:run
```

O compilando el jar:

```powershell
mvn clean package -DskipTests
java -jar target/parcel-service-1.0.0.jar
```

La API queda disponible en `http://localhost:8080`.

### Con Docker Compose (si cuenta con `docker-compose.yml`)

```powershell
docker compose up --build
```

## API

URL base: `/api/v1/parcels`

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| `POST` | `/api/v1/parcels` | Registrar una nueva parcela | `201 Created` + cabecera `Location` |
| `GET` | `/api/v1/parcels` | Listar todas las parcelas | `200 OK` |
| `GET` | `/api/v1/parcels/{id}` | Detalle de una parcela y su estado actual | `200 OK` |

### Modelo de la parcela

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | UUID | Identificador generado por el sistema |
| `name` | string | Nombre de la parcela (único, sin distinguir mayúsculas) |
| `location.latitude` | number | Latitud GPS (-90 a 90) |
| `location.longitude` | number | Longitud GPS (-180 a 180) |
| `soilType` | string | `arcilloso`, `limoso` o `arenoso` |
| `ownerOrLead` | string | Responsable o líder de la parcela |
| `createdAt` | ISO-8601 | Fecha de creación (la asigna el sistema) |

### Ejemplos (PowerShell)

**Crear una parcela**

```powershell
$body = @{
  name        = "Huerto Norte"
  location    = @{ latitude = 28.6353; longitude = -106.0889 }
  soilType    = "arcilloso"
  ownerOrLead = "Ana Pérez"
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/v1/parcels `
  -ContentType "application/json; charset=utf-8" -Body $body
```

Respuesta `201 Created`:

```json
{
  "id": "5f1c2a4e-8d1b-4c5e-9a67-0b3f6d2e7a10",
  "name": "Huerto Norte",
  "location": { "latitude": 28.6353, "longitude": -106.0889 },
  "soilType": "arcilloso",
  "ownerOrLead": "Ana Pérez",
  "createdAt": "2026-10-14T18:20:11.123Z"
}
```

**Listar parcelas**

```powershell
Invoke-RestMethod http://localhost:8080/api/v1/parcels
```

**Consultar el detalle**

```powershell
Invoke-RestMethod http://localhost:8080/api/v1/parcels/5f1c2a4e-8d1b-4c5e-9a67-0b3f6d2e7a10
```

Respuesta `200 OK`:

```json
{
  "parcel": {
    "id": "5f1c2a4e-8d1b-4c5e-9a67-0b3f6d2e7a10",
    "name": "Huerto Norte",
    "location": { "latitude": 28.6353, "longitude": -106.0889 },
    "soilType": "arcilloso",
    "ownerOrLead": "Ana Pérez",
    "createdAt": "2026-10-14T18:20:11.123Z"
  },
  "currentStatus": "SIN_LECTURAS",
  "statusNote": "Aún no se han recibido lecturas de sensores para esta parcela."
}
```

> El estado actual es `SIN_LECTURAS` mientras no exista la integración con los sensores IoT.

## Sanitización de entradas

`InputSanitizer` se ejecuta en el constructor de `CreateParcelRequest`, **antes** de las validaciones:

1. Normaliza Unicode (NFKC).
2. Elimina bloques `<script>` y `<style>` completos.
3. Elimina etiquetas HTML y los caracteres `<` y `>` sobrantes.
4. Reemplaza caracteres de control e invisibles.
5. Colapsa espacios múltiples y recorta los extremos.

Ejemplo: `"<b>Huerto</b>   Sur<script>alert(1)</script>"` se guarda como `"Huerto Sur"`.

Las consultas a la base de datos usan JPA con parámetros, lo que previene la inyección SQL.

## Validaciones

| Campo | Reglas |
|---|---|
| `name` | Obligatorio, 3 a 100 caracteres, solo letras, números, espacios y `. , ' ( ) _ -` |
| `location` | Obligatoria |
| `location.latitude` | Obligatoria, entre -90 y 90 |
| `location.longitude` | Obligatoria, entre -180 y 180 |
| `soilType` | Obligatorio, uno de: `arcilloso`, `limoso`, `arenoso` (sin distinguir mayúsculas) |
| `ownerOrLead` | Obligatorio, 2 a 100 caracteres, solo letras, espacios y `. ' -` |

Reglas de negocio adicionales:

- Las coordenadas `(0, 0)` se rechazan (suelen indicar un GPS sin señal).
- No se permiten dos parcelas con el mismo nombre. La unicidad también está garantizada por una restricción en la base de datos.

## Manejo de errores

Todos los errores comparten la misma estructura:

```json
{
  "timestamp": "2026-10-14T18:20:11Z",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_ERROR",
  "message": "Los datos enviados no son válidos. Revise los campos indicados.",
  "path": "/api/v1/parcels",
  "details": [
    { "field": "location.latitude", "message": "La latitud debe estar entre -90 y 90 grados." },
    { "field": "soilType", "message": "Tipo de suelo inválido. Valores permitidos: arcilloso, limoso, arenoso." }
  ]
}
```

| Código | HTTP | Cuándo ocurre |
|---|---|---|
| `VALIDATION_ERROR` | 400 | Datos inválidos o regla de negocio incumplida |
| `MALFORMED_REQUEST` | 400 | JSON mal formado o con campos no permitidos |
| `INVALID_IDENTIFIER` | 400 | El `id` de la ruta no es un UUID válido |
| `PARCEL_NOT_FOUND` | 404 | No existe una parcela con ese `id` |
| `RESOURCE_NOT_FOUND` | 404 | La ruta solicitada no existe |
| `METHOD_NOT_ALLOWED` | 405 | Método HTTP no permitido |
| `UNSUPPORTED_MEDIA_TYPE` | 415 | El contenido no es `application/json` |
| `DUPLICATE_PARCEL` | 409 | Ya existe una parcela con ese nombre |
| `DATA_CONFLICT` | 409 | Conflicto de integridad en la base de datos |
| `INTERNAL_ERROR` | 500 | Error inesperado (el detalle solo queda en el log) |

### Excepciones personalizadas

- `BusinessException`: clase base abstracta, con un `ErrorCode`.
- `ParcelNotFoundException`: parcela inexistente.
- `DuplicateParcelException`: nombre de parcela repetido.
- `InvalidParcelDataException`: incumplimiento de reglas de negocio sobre los datos.

`GlobalExceptionHandler` (`@RestControllerAdvice`) convierte cada excepción en la respuesta de error uniforme.

## Pruebas rápidas de error

```powershell
# 400: datos inválidos
$bad = @{ name = "  "; location = @{ latitude = 120; longitude = -106 }; soilType = "roca"; ownerOrLead = "Ana" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/v1/parcels -ContentType "application/json" -Body $bad

# 400: UUID inválido
Invoke-RestMethod http://localhost:8080/api/v1/parcels/abc

# 404: parcela inexistente
Invoke-RestMethod http://localhost:8080/api/v1/parcels/00000000-0000-0000-0000-000000000000
```

En PowerShell 5.1, `Invoke-RestMethod` lanza una excepción en respuestas 4xx. Para ver el cuerpo del error, use `curl.exe -i` en lugar de `Invoke-RestMethod`.

## Flujo de ramas

```
feat/parcel  →  develop  →  main
```

1. El trabajo se realiza en ramas `feat/*`.
2. Se abre un Pull Request de `feat/*` hacia `develop`.
3. Cuando `develop` está estable, se abre un Pull Request hacia `main`.
