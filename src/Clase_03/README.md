# Calculadora Cliente-Servidor UDP con JavaFX || `CLASE 03 (15|04|2026)`

Se hizo este proyecto como práctica de la materia de Aplicaciones Distribuidas en Java, evolucionando la calculadora
de la [Clase 01](../Clase_01) hacia una arquitectura **Cliente-Servidor sobre el protocolo UDP** usando JavaFX y programación asíncrona.

## Vista General del Proyecto

Esta es una calculadora con arquitectura distribuida donde la vista y el cliente de red (JavaFX) se comunican mediante sockets UDP con un
servidor remoto que contiene la lógica de negocio. Construida con **Java 26** y **JavaFX 26**. El patrón de diseño implementa una clara
separación de responsabilidades (SOLID):

### Lado del Servidor (`Clase_03.Server`)

| Clase / Archivo | Responsabilidad                                                                 |
|-----------------|---------------------------------------------------------------------------------|
| `ICalculator`   | Contrato/Interfaz matemática moderna de las operaciones.                        |
| `Calculator`    | Implementación de la lógica de negocio (Modelo).                                |
| `Server`        | Servidor UDP que recibe peticiones, invoca al modelo y responde. Usa inyección. |
| `Launcher`      | Punto de entrada del servidor y configuración del *Graceful Shutdown*.          |

### Lado del Cliente (`Clase_03.Client` & `Clase_03.UI`)

| Clase / Archivo        | Responsabilidad                                                                                                                   |
|------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| `Client`               | Cliente de red. Envía datagramas y retorna respuestas usando `CompletableFuture`.                                                 |
| `CalculatorController` | Puente entre la interfaz y la red. Actualiza la UI de manera reactiva y segura.                                                   |
| `CalculatorApp`        | Configuración de la ventana y de la inyección de dependencias con `FXMLLoader`.                                                   |
| `JavaFXManager`        | Reutilización del administrador concurrente para iniciar JavaFX de la [clase 02](../Clase_02/Multithreads/FX/JavaFXManager.java). |
| `CalculatorView.fxml`  | Definición declarativa de la interfaz gráfica.                                                                                    |

---

## Características Implementadas

- **Comunicación Red/UDP:** El cliente y el servidor se comunican mediante paquetes de datagramas (`DatagramPacket`), enviando *payloads*
  estructurados en formato texto (ej. `10.0,20.0,+`) dónde cada elemento es: `primer_número, segundo_número, operación`.
- **Programación Asíncrona Moderna:** El cliente no bloquea el hilo de la interfaz de usuario. Utiliza `CompletableFuture` para la red y
  `Platform.runLater()` para actualizar los resultados gráficamente.
- **Manejo Estructurado de Logs:** Implementación de SLF4J con Logback para registrar eventos, advertencias y errores en consola (tanto en
  el cliente como en el servidor) sin usar `System.out.println`.
- **Inversión de Dependencias (DIP) y DRY:** Reducción de código repetitivo centralizando peticiones en `performRemoteOperation`. Inyección
  de dependencias gestionada tanto en el `Server` como mediante el Factory del controlador de JavaFX.
- **Manejo de Errores Resiliente:** El servidor atrapa errores de formato y divisiones por cero devolviendo un `ERROR: [mensaje]`. El
  cliente maneja *Timeouts* por si el servidor no está en línea.

---

## Cómo Instalar

### Prerrequisitos

Antes de empezar, asegúrate de tener instalado lo siguiente y referenciar los `.jar` necesarios en tu entorno:

| Herramienta / Librería                                                                                | Versión / Enlace                       |
|-------------------------------------------------------------------------------------------------------|----------------------------------------|
| [Amazon Corretto JDK](https://docs.aws.amazon.com/corretto/latest/corretto-26-ug/downloads-list.html) | 26                                     |
| [JavaFX SDK](https://gluonhq.com/products/javafx/)                                                    | 26                                     |
| [IntelliJ IDEA](https://www.jetbrains.com/idea/)                                                      | Cualquier edición (Community/Ultimate) |
| [SceneBuilder](https://gluonhq.com/products/scene-builder/)                                           | Compatible con JavaFX 26               |
| [SLF4J API](https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.17/)                               | Librería de Logs (solo el `.jar`)      | 
| [Logback Classic](https://repo1.maven.org/maven2/ch/qos/logback/logback-classic/1.5.32/)              | Motor SLF4J (solo el `.jar`)           | 
| [Logback Core](https://repo1.maven.org/maven2/ch/qos/logback/logback-core/1.5.32/)                    | Dependencia Logback (solo el `.jar`)   | 

### Pasos

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/NW08/Apps_Distribuidas-GR2.git
   cd src/Clase_03
   ```

2. **Configura el SDK en IntelliJ IDEA**
    - Ve a `File → Project Structure → SDKs`
    - Agrega el JDK de Amazon Corretto 26.

3. **Agrega las Librerías y Módulos**
    - En `File → Project Structure → Libraries`, agrega la carpeta `lib` del JavaFX SDK 26.
    - En esa misma sección, **agrega los 3 `.jar` de logs** (SLF4J API, Logback Classic, Logback Core).
    - Asegúrate de que los módulos necesarios estén en las VM Options al ejecutar el cliente:
   ```
   --module-path /ruta/a/javafx-sdk-26/lib
   --add-modules=javafx.controls,javafx.fxml,javafx.graphics
   ```

4. **Ejecuta la aplicación (¡El orden importa!)**
    - Primero, ejecuta la clase `Clase_03.Server.Launcher` para encender el Servidor UDP. Revisa en la consola que el Log indique que está
      escuchando en el puerto.
    - Segundo, ejecuta la clase `Clase_03.Client.Launcher` para abrir la interfaz gráfica.

---

**Casos de error y red contemplados:**

| Situación en el Cliente          | Mensaje mostrado en UI / Comportamiento          |
|----------------------------------|--------------------------------------------------|
| Texto no numérico en algún campo | `Número inválido` (Validado localmente)          |
| División por cero                | `ERROR: Cannot divide by zero` (Atrapado en red) |
| Servidor apagado / no responde   | `Timeout / Error de Red` (Tras 5 segundos)       |
| Clic en operación                | Muestra `Calculando...` mientras espera la red.  |

---