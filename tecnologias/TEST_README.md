# Tests Unitarios - Proyecto Tecnologías

Este documento describe la estructura y ejecución de los tests unitarios del proyecto.

## Estructura de Tests

### 1. Tests de Servicios (`src/test/java/gabs/tecnologias/application/service/`)
- **TecnologiaServiceTest**: Tests para el servicio de tecnologías
- **CapacidadTecnologiaServiceTest**: Tests para el servicio de capacidades de tecnologías

### 2. Tests de Handlers (`src/test/java/gabs/tecnologias/infraestructure/adapter/in/`)
- **TecnologiaHandlerTest**: Tests para el handler de tecnologías
- **CapacidadTecnologiaHandlerTest**: Tests para el handler de capacidades de tecnologías

### 3. Tests de Modelos (`src/test/java/gabs/tecnologias/domain/model/`)
- **TecnologiaTest**: Tests para el modelo Tecnologia
- **CapacidadTecnologiaTest**: Tests para el modelo CapacidadTecnologia

### 4. Tests de Repositorios (`src/test/java/gabs/tecnologias/infraestructure/adapter/out/`)
- **TecnologiaRepositoryImplTest**: Tests para el repositorio de tecnologías
- **CapacidadTecnologiaRepositoryImplTest**: Tests para el repositorio de capacidades de tecnologías

### 5. Tests de Integración (`src/test/java/gabs/tecnologias/integration/`)
- **TecnologiaIntegrationTest**: Tests de integración para el flujo completo

## Ejecución de Tests

### Ejecutar todos los tests
```bash
./gradlew test
```

### Ejecutar tests específicos
```bash
# Tests de servicios
./gradlew test --tests "*ServiceTest"

# Tests de handlers
./gradlew test --tests "*HandlerTest"

# Tests de modelos
./gradlew test --tests "*ModelTest"

# Tests de repositorios
./gradlew test --tests "*RepositoryTest"

# Tests de integración
./gradlew test --tests "*IntegrationTest"
```

### Ejecutar tests con cobertura
```bash
./gradlew test jacocoTestReport
```

## Configuración de Tests

### Base de Datos de Test
- Se utiliza H2 en memoria para los tests
- Configuración en `src/test/resources/application-test.properties`

### Dependencias de Test
- **Spring Boot Test**: Framework de testing
- **Reactor Test**: Testing para programación reactiva
- **H2 Database**: Base de datos en memoria para tests
- **R2DBC H2**: Driver R2DBC para H2

## Cobertura de Tests

Los tests cubren las siguientes funcionalidades:

### TecnologiaService
- ✅ Buscar todas las tecnologías
- ✅ Buscar tecnología por ID
- ✅ Verificar existencia por ID
- ✅ Crear nueva tecnología
- ✅ Actualizar tecnología parcialmente
- ✅ Buscar por nombre
- ✅ Eliminar tecnología

### CapacidadTecnologiaService
- ✅ Obtener lista de tecnologías por capacidad
- ✅ Registrar tecnologías para una capacidad
- ✅ Eliminar capacidades por IDs

### Handlers
- ✅ Manejo de peticiones HTTP
- ✅ Respuestas correctas
- ✅ Manejo de errores

### Modelos
- ✅ Creación de objetos
- ✅ Actualización de campos
- ✅ Manejo de valores nulos
- ✅ Validaciones

### Repositorios
- ✅ Operaciones CRUD
- ✅ Búsquedas específicas
- ✅ Eliminaciones en lote

## Casos de Prueba

### Casos Positivos
- Creación exitosa de tecnologías
- Búsqueda de tecnologías existentes
- Actualización correcta de datos
- Eliminación exitosa

### Casos Negativos
- Tecnología no encontrada
- Validaciones de datos
- Manejo de errores
- Operaciones con datos vacíos

### Casos Edge
- Valores nulos
- Strings vacíos
- IDs muy grandes
- Caracteres especiales

## Mejores Prácticas Implementadas

1. **Arrange-Act-Assert**: Estructura clara en todos los tests
2. **Naming Conventions**: Nombres descriptivos para métodos de test
3. **Mocking**: Uso apropiado de mocks para aislar unidades
4. **Reactive Testing**: Uso de StepVerifier para testing reactivo
5. **Test Data**: Datos de prueba realistas y variados
6. **Error Scenarios**: Cobertura de casos de error
7. **Integration Tests**: Tests de flujo completo

## Configuración de CI/CD

Los tests se ejecutan automáticamente en:
- Pull Requests
- Merge a main
- Deployments

## Mantenimiento

### Agregar Nuevos Tests
1. Crear clase de test en el paquete correspondiente
2. Seguir la convención de nombres `*Test`
3. Implementar casos positivos y negativos
4. Agregar documentación si es necesario

### Actualizar Tests Existentes
1. Mantener compatibilidad con cambios en el código
2. Actualizar mocks cuando sea necesario
3. Verificar que todos los tests pasen

## Troubleshooting

### Problemas Comunes
1. **Tests fallando por configuración**: Verificar `application-test.properties`
2. **Problemas de mocks**: Revisar configuración de Mockito
3. **Tests de integración fallando**: Verificar configuración de base de datos

### Debugging
```bash
# Ejecutar tests con debug
./gradlew test --debug

# Ver logs detallados
./gradlew test --info
``` 