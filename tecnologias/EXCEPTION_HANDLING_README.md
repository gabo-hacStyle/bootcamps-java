# Sistema de Manejo de Excepciones

Este documento describe el sistema de manejo de excepciones implementado en la aplicación Spring Boot WebFlux.

## Estructura del Sistema

### 1. Excepciones del Dominio

Las excepciones personalizadas se encuentran en el paquete `gabs.tecnologias.domain.exception`:

- **`TecnologiaNotFoundException`**: Se lanza cuando no se encuentra una tecnología por ID o nombre
- **`CapacidadTecnologiaNotFoundException`**: Se lanza cuando no se encuentran tecnologías para una capacidad específica
- **`ValidationException`**: Se lanza cuando hay errores de validación en los datos de entrada

### 2. DTOs de Respuesta de Error

El DTO `ErrorResponse` en `gabs.tecnologias.infraestructure.dto` proporciona una estructura estandarizada para las respuestas de error:

```json
{
  "message": "Mensaje descriptivo del error",
  "error": "CÓDIGO_DE_ERROR",
  "status": 404,
  "timestamp": "2024-01-15 10:30:45",
  "path": "/api/tecnologias/999",
  "fieldErrors": [
    {
      "field": "nombre",
      "message": "El nombre no puede estar vacío",
      "rejectedValue": ""
    }
  ]
}
```

### 3. Manejador Global de Excepciones

El `GlobalExceptionHandler` maneja todas las excepciones y las convierte en respuestas HTTP apropiadas:

- **404 Not Found**: Para recursos no encontrados
- **400 Bad Request**: Para errores de validación
- **500 Internal Server Error**: Para errores no manejados

### 4. DTOs de Validación

Los DTOs de entrada incluyen validaciones usando anotaciones de Bean Validation:

- **`CreateTecnologiaRequest`**: Para crear tecnologías
- **`UpdateTecnologiaRequest`**: Para actualizar tecnologías
- **`RegisterCapacidadTecnologiaRequest`**: Para registrar capacidades de tecnología

## Códigos de Error

| Código | Descripción | HTTP Status |
|--------|-------------|-------------|
| `TECNOLOGIA_NOT_FOUND` | Tecnología no encontrada | 404 |
| `CAPACIDAD_TECNOLOGIA_NOT_FOUND` | Capacidad de tecnología no encontrada | 404 |
| `VALIDATION_ERROR` | Error de validación | 400 |
| `INVALID_ARGUMENT` | Argumento inválido | 400 |
| `INTERNAL_SERVER_ERROR` | Error interno del servidor | 500 |

## Ejemplos de Uso

### Crear una Tecnología

**Petición:**
```bash
POST /api/tecnologias
Content-Type: application/json

{
  "nombre": "",
  "descripcion": "Descripción de prueba"
}
```

**Respuesta de Error:**
```json
{
  "message": "Error de validación en el campo 'nombre': El nombre de la tecnología no puede estar vacío",
  "error": "VALIDATION_ERROR",
  "status": 400,
  "timestamp": "2024-01-15 10:30:45",
  "path": "/api/tecnologias"
}
```

### Buscar Tecnología por ID

**Petición:**
```bash
GET /api/tecnologias/999
```

**Respuesta de Error:**
```json
{
  "message": "Tecnología con ID 999 no encontrada",
  "error": "TECNOLOGIA_NOT_FOUND",
  "status": 404,
  "timestamp": "2024-01-15 10:30:45",
  "path": "/api/tecnologias/999"
}
```

### Registrar Capacidades de Tecnología

**Petición:**
```bash
POST /api/capacidad-tecnologia/1
Content-Type: application/json

{
  "capacidadId": 1,
  "tecnologiaIds": []
}
```

**Respuesta de Error:**
```json
{
  "message": "Error de validación en el campo 'tecnologiaIds': La lista de IDs de tecnologías no puede estar vacía",
  "error": "VALIDATION_ERROR",
  "status": 400,
  "timestamp": "2024-01-15 10:30:45",
  "path": "/api/capacidad-tecnologia/1"
}
```

## Configuración

### Validación

El sistema utiliza Bean Validation para validar los DTOs de entrada. La configuración se encuentra en `ValidationConfig.java`.

### Logging

Todos los errores se registran en el log con el nivel ERROR, incluyendo el stack trace completo para facilitar el debugging.

## Mejores Prácticas

1. **Siempre usar los DTOs de validación** para las peticiones de entrada
2. **Lanzar excepciones específicas** del dominio en lugar de excepciones genéricas
3. **Proporcionar mensajes descriptivos** en las excepciones
4. **Validar los datos de entrada** antes de procesarlos
5. **Usar códigos de error consistentes** para facilitar el manejo en el cliente

## Extensibilidad

Para agregar nuevas excepciones:

1. Crear la excepción en `gabs.tecnologias.domain.exception`
2. Agregar el manejo en `GlobalExceptionHandler`
3. Definir el código de error y el status HTTP apropiado
4. Actualizar este documento con la nueva excepción 