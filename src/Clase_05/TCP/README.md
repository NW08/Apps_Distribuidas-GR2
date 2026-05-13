# Sistema de Registro de Horarios (Arquitectura Cliente-Servidor TCP) || `CLASE 05 (29|04|2026)`

Proyecto orientado hacia una arquitectura distribuida sobre TCP. El sistema consta de un **Cliente de escritorio JavaFX** asíncrono y un
**Servidor TCP concurrente**, comunicándose mediante el intercambio de mensajes JSON serializados con Jackson. Todo el ecosistema está
construido sobre **Java 26 (Amazon Corretto)**.

## Vista General del Sistema

El sistema gestiona el registro de horarios de empleados. El cliente permite marcar cuatro tipos de eventos (`ENTRADA`, `RECESO_SALIDA`,
`RECESO_ENTRADA`, `SALIDA`) asociados a un ID de empleado.

* **Cliente:** Proporciona una interfaz reactiva que no se bloquea durante las operaciones de red gracias a hilos virtuales y
  `CompletableFuture`.
* **Servidor:** Escucha conexiones entrantes, procesa la lógica de negocio (previniendo registros duplicados en el mismo día) y persiste la
  información en una estructura de memoria segura para concurrencia. Cada cliente es atendido en su propio hilo virtual.

## Arquitectura del Proyecto

### Componentes del Cliente (`Clase_05.TCP.Client`)

| Clase              | Responsabilidad                                                                                              |
|--------------------|--------------------------------------------------------------------------------------------------------------|
| **Launcher**       | Punto de entrada (JEP 477). Configura el DI factory, instancia dependencias e inicializa el `ClientApp`.     |
| **ClientApp**      | Orquesta el arranque de JavaFX, carga la vista vía `FXMLLoader` y gestiona el *Graceful Shutdown*.           |
| **TCPService**     | Capa de red. Abre un Socket TCP por solicitud, gestiona el envío/recepción de JSON y maneja hilos virtuales. |
| **JacksonConfig**  | Configuración centralizada del `ObjectMapper`.                                                               |
| **FormController** | Controlador de la UI. Valida datos, construye el `PunchRequest` y despacha el comando de forma asíncrona.    |

### Componentes del Servidor (`Clase_05.TCP.Server`)

| Clase                      | Responsabilidad                                                                            |
|----------------------------|--------------------------------------------------------------------------------------------|
| **Launcher**               | Punto de entrada (JEP 477). Ensambla dependencias e inicia el `ServerApp`.                 |
| **ServerApp**              | Núcleo TCP. Abre el `ServerSocket`, acepta conexiones en bucle y delega a hilos virtuales. |
| **ClientHandler**          | Maneja el ciclo de vida de una conexión individual: lectura, proceso y respuesta JSON.     |
| **ArrivalRegistryService** | Capa de negocio. Previene duplicados mediante sincronización por `employeeId.intern()`.    |
| **PunchRepository**        | Capa de persistencia en memoria utilizando `ConcurrentHashMap` y `CopyOnWriteArrayList`.   |

## Protocolo de Comunicación TCP

La comunicación se basa en el intercambio de objetos serializados en JSON sobre una conexión TCP.

### Estructuras de Datos (Records)

| Record            | Dirección          | Campos                                  |
|-------------------|--------------------|-----------------------------------------|
| **PunchRequest**  | Cliente → Servidor | `employeeId`, `type`, `timestamp`       |
| **PunchResponse** | Servidor → Cliente | `success` (boolean), `message` (String) |

### Formato de Mensajes (JSON)

**Estructura del Request:**

```json
{
  "employeeId": "12345",
  "type": "ENTRY",
  "timestamp": "2026-04-29T08:00:00"
}

```

**Tipos de Eventos (PunchType):**

| Tipo          | Descripción                   |
|---------------|-------------------------------|
| **ENTRY**     | Marcación de entrada al turno |
| **LUNCH_OUT** | Inicio de pausa de almuerzo   |
| **LUNCH_IN**  | Fin de pausa de almuerzo      |
| **EXIT**      | Marcación de salida del turno |

## Características Implementadas

* **Hilos Virtuales (JEP 444):** * *Cliente:* `TCPService` usa un executor de hilos virtuales para evitar bloquear el hilo principal de la
  UI.
* *Servidor:* Atiende cada conexión en un hilo virtual, permitiendo alta escalabilidad sin el overhead de los hilos de plataforma.


* **Serialización con Jackson:** Mapeo automático de Records a JSON. Incluye soporte para `JavaTimeModule` para el manejo de fechas (
  `LocalDateTime`).
* **Instancias Simplificadas (JEP 477):** Uso de `main()` implícito en ambos lanzadores (`Launcher`) para reducir código boilerplate.
* **Gestión de Concurrencia y Negocio:** * Sincronización fina por ID de empleado en el servidor para evitar duplicados.
* Uso de colecciones concurrentes para persistencia volátil.


* **Manejo de Logs:** Implementación de SLF4J con Logback en ambos módulos para trazas detalladas del flujo.
* **Graceful Shutdown:** Ambos componentes cuentan con mecanismos para cerrar sockets y ejecutores ordenadamente antes de finalizar la JVM.

## Cómo Instalar y Ejecutar

### Prerrequisitos Combinados

| Herramienta / Librería                                                                                             | Versión / Enlace                       |
|--------------------------------------------------------------------------------------------------------------------|----------------------------------------|
| [Amazon Corretto JDK](https://docs.aws.amazon.com/corretto/latest/corretto-26-ug/downloads-list.html)              | 26                                     |
| [JavaFX SDK](https://gluonhq.com/products/javafx/)                                                                 | 26                                     |
| [IntelliJ IDEA](https://www.jetbrains.com/idea/)                                                                   | Cualquier edición (Community/Ultimate) |
| [SceneBuilder](https://gluonhq.com/products/scene-builder/)                                                        | Compatible con JavaFX 26               |
| [Jackson Core](https://repo.maven.apache.org/maven2/tools/jackson/core/jackson-core/3.0.0-rc2/)                    | 3.0.0-rc2 (solo el `.jar`)             |
| [Jackson Annotations](https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/3.0-rc2/)      | 3.0.0-rc2 (solo el `.jar`)             |
| [Jackson Databind](https://repo.maven.apache.org/maven2/tools/jackson/core/jackson-databind/3.0.0-rc2/)            | 3.0.0-rc2 (solo el `.jar`)             |
| [Jackson Datatype](https://repo.maven.apache.org/maven2/tools/jackson/datatype/jackson-datatype-jsr310/3.0.0-rc2/) | 3.0.0-rc2 (solo el `.jar`)             |

### Pasos de Configuración

1. **Clonar el repositorio:**
    ```bash
    git clone https://github.com/NW08/Apps_Distribuidas-GR2.git
    cd src/Clase_05/TCP
    ```

2. **Configurar Librerías:** * Asignar **Corretto 26** como SDK.
    * Agregar los `.jar` de Jackson y la carpeta `lib` de JavaFX en `Project Structure → Libraries`.
    * *Nota:* Sí faltan librerías como SLF4J o Lombok, consultar readmes de clases anteriores en este repositorio.

3. **Configuración Específica:**
    * **Cliente:** En su `Launcher`, ajustar `SERVER_IP` y `SERVER_PORT` (debe coincidir con el puerto escrito en el servidor). Agregar VM
      Options:
      `--module-path /ruta/a/javafx-sdk-26/lib --add-modules=javafx.controls,javafx.fxml,javafx.graphics`.
    * **Servidor:** En su `Launcher`, ajustar la constante `PORT`.

4. **Ejecución:**
    1. Iniciar primero el **Servidor** (`Clase_05.TCP.Server.Launcher`).
    2. Iniciar el **Cliente** (`Clase_05.TCP.Client.Launcher`) para abrir la interfaz.

## Flujo de Operaciones

### Lógica del Proceso

1. **Cliente (UI):** Usuario ingresa ID y presiona evento → Validación local → Deshabilitar botones → Envío asíncrono vía `TCPService`.
2. **Servidor (Red):** `accept()` detecta conexión → Dispara hilo virtual → `ClientHandler` lee JSON.
3. **Servidor (Negocio):** `ArrivalRegistryService` bloquea por ID de empleado → Verifica duplicado → Persiste en `PunchRepository` → Genera
   respuesta.
4. **Cliente (Red/UI):** Recibe JSON → Deserializa a `PunchResponse` → Despacha a hilo de UI para mostrar resultado (verde/éxito o
   rojo/error) → Re-habilita botones.

## Casos de Error Contemplados

| Situación              | Manejo en Cliente                                | Manejo en Servidor                            |
|------------------------|--------------------------------------------------|-----------------------------------------------|
| **ID Vacío**           | Error local inmediato (sin red).                 | N/A                                           |
| **Servidor Offline**   | Timeout de 5s y mensaje de error en rojo.        | N/A                                           |
| **JSON Malformado**    | Log de error vía SLF4J.                          | Captura excepción y envía `ErrorResponse`.    |
| **Registro Duplicado** | Muestra mensaje de error retornado por servidor. | Detecta conflicto y retorna `success: false`. |
| **Payload Nulo**       | N/A                                              | Cierra conexión silenciosamente.              |
