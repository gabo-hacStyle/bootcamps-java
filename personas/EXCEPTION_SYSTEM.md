# Sistema de Excepciones - Microservicio de Personas

## Descripción General

Este documento describe el sistema completo de manejo de excepciones implementado en el microservicio de personas, que proporciona respuestas HTTP consistentes y bien estructuradas para todos los errores posibles.

## Excepciones Personalizadas

### 1. PersonaNotFoundException
- **Propósito**: Se lanza cuando no se encuentra una persona con el ID especificado
- **Código HTTP**: 404 (NOT_FOUND)
- **Casos de uso**: 
  - Buscar persona por ID inexistente
  - Actualizar persona inexistente
  - Eliminar persona inexistente

```java
throw new PersonaNotFoundException(999L);
// Resultado: "Persona con ID 999 no encontrada"
```

### 2. PersonaAlreadyExistsException
- **Propósito**: Se lanza cuando se intenta crear una persona con un correo que ya existe
- **Código HTTP**: 409 (CONFLICT)
- **Casos de uso**:
  - Registrar persona con correo duplicado
  - Actualizar persona con correo ya existente

```java
throw new PersonaAlreadyExistsException("juan@test.com");
// Resultado: "Ya existe una persona con el correo: juan@test.com"
```

### 3. InvalidPersonaDataException
- **Propósito**: Se lanza cuando los datos de la persona son inválidos
- **Código HTTP**: 400 (BAD_REQUEST)
- **Casos de uso**:
  - Nombre vacío o nulo
  - Correo vacío, nulo o sin formato válido
  - Edad fuera del rango válido (0-150)

```java
throw new InvalidPersonaDataException("nombre", "valor inválido");
// Resultado: "Dato inválido para el campo 'nombre': valor inválido"
```

### 4. ExternalServiceException
- **Propósito**: Se lanza cuando hay errores en servicios externos
- **Código HTTP**: 503 (SERVICE_UNAVAILABLE)
- **Casos de uso**:
  - Error en ReportClient
  - Fallos de comunicación con servicios externos

```java
throw new ExternalServiceException("ReportClient", "Service unavailable");
// Resultado: "Error en el servicio ReportClient: Service unavailable"
```

## Estructura de Respuesta de Error

Todas las excepciones devuelven una respuesta JSON consistente con la siguiente estructura:

```json
{
  "message": "Mensaje descriptivo del error",
  "error": "TIPO_ERROR",
  "status": 404,
  "timestamp": "2024-01-15 10:30:45",
  "path": "/api/personas/999"
}
```

### Campos de la Respuesta:
- **message**: Descripción clara del error
- **error**: Tipo de error (NOT_FOUND, CONFLICT, BAD_REQUEST, etc.)
- **status**: Código HTTP numérico
- **timestamp**: Fecha y hora del error
- **path**: Ruta de la petición que causó el error

## Mapeo de Excepciones a Códigos HTTP

| Excepción | Código HTTP | Descripción |
|------------|-------------|-------------|
| PersonaNotFoundException | 404 | Recurso no encontrado |
| PersonaAlreadyExistsException | 409 | Conflicto de datos |
| InvalidPersonaDataException | 400 | Datos de entrada inválidos |
| ExternalServiceException | 503 | Servicio externo no disponible |
| IllegalArgumentException | 400 | Argumento ilegal |
| RuntimeException (genérica) | 500 | Error interno del servidor |

## Validaciones Implementadas

### Validación de Datos de Persona
- **Nombre**: No puede ser null, vacío o solo espacios
- **Correo**: No puede ser null, vacío y debe contener "@"
- **Edad**: Si se proporciona, debe estar entre 0 y 150

### Validaciones de Negocio
- **Correo único**: No se pueden crear dos personas con el mismo correo
- **Existencia**: Verificación de existencia antes de operaciones de actualización/eliminación
- **Integridad referencial**: Validación de relaciones antes de operaciones

## Manejo Global de Excepciones

El `GlobalExceptionHandler` se encarga de:
1. **Capturar excepciones**: Intercepta todas las excepciones lanzadas
2. **Mapear códigos HTTP**: Asigna el código HTTP apropiado
3. **Formatear respuestas**: Crea respuestas JSON consistentes
4. **Logging**: Registra errores para debugging

## Tests Implementados

### Tests Unitarios
- **PersonaServiceTest**: Prueba todas las operaciones del servicio
- **GlobalExceptionHandlerTest**: Prueba el manejo de excepciones
- **PersonaHandlerTest**: Prueba el handler con mocks

### Tests de Integración
- **PersonasApplicationIntegrationTest**: Prueba el flujo completo con base de datos real
- **ExceptionMappingTest**: Valida el mapeo correcto de excepciones a códigos HTTP

## Ejemplos de Uso

### Crear Persona con Datos Inválidos
```bash
POST /api/personas
{
  "nombre": "",
  "correo": "invalid-email"
}
```

**Respuesta:**
```json
{
  "message": "Dato inválido para el campo 'nombre': ",
  "error": "BAD_REQUEST",
  "status": 400,
  "timestamp": "2024-01-15 10:30:45",
  "path": "/api/personas"
}
```

### Buscar Persona Inexistente
```bash
GET /api/personas/999
```

**Respuesta:**
```json
{
  "message": "Persona con ID 999 no encontrada",
  "error": "NOT_FOUND",
  "status": 404,
  "timestamp": "2024-01-15 10:30:45",
  "path": "/api/personas/999"
}
```

### Crear Persona con Correo Duplicado
```bash
POST /api/personas
{
  "nombre": "María García",
  "correo": "juan.perez@test.com"
}
```

**Respuesta:**
```json
{
  "message": "Ya existe una persona con el correo: juan.perez@test.com",
  "error": "CONFLICT",
  "status": 409,
  "timestamp": "2024-01-15 10:30:45",
  "path": "/api/personas"
}
```

## Beneficios del Sistema

1. **Consistencia**: Todas las respuestas de error siguen el mismo formato
2. **Claridad**: Mensajes de error descriptivos y útiles
3. **Trazabilidad**: Incluye timestamp y path para debugging
4. **Escalabilidad**: Fácil agregar nuevas excepciones
5. **Mantenibilidad**: Código bien estructurado y testeado
6. **Experiencia de Usuario**: Respuestas claras para el cliente

## Extensibilidad

Para agregar nuevas excepciones:

1. Crear nueva clase de excepción en `domain.exception`
2. Agregar método de manejo en `GlobalExceptionHandler`
3. Actualizar `PersonaHandler` con el nuevo `onErrorResume`
4. Crear tests unitarios y de integración
5. Documentar en este README 