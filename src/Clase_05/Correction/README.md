# Sistema de Tarjetas de Transporte (Cliente / Servidor UDP) || `CLASE 05 (29|04|2026)`

Proyecto construido como práctica de la materia de Aplicaciones Distribuidas en Java, evolucionando la arquitectura de
la [Prueba 01](../../Clase_04) hacia un sistema completo cliente-servidor (corrección). Cuenta con un servidor UDP con **persistencia real
en base de datos MySQL**,
transacciones e hilos virtuales, interactuando con un cliente de escritorio **JavaFX** asíncrono y reactivo.

## Vista General del Sistema

El sistema gestiona tarjetas de transporte metropolitano. El servidor maneja la base de datos de usuarios y tarjetas (aplicando
automáticamente tarifas preferenciales a mayores de 60 años), mientras que el cliente permite a los usuarios registrarse, consultar
perfiles, recargar saldo y pagar pasajes sin bloquear nunca la interfaz gráfica. Todo está construido sobre **Java 26 (Amazon Corretto)**.

---

## Arquitectura del Sistema

### Lado del Cliente (`Clase_05.Correction.Client`)

| Clase           | Responsabilidad                                                                                                                          |
|-----------------|------------------------------------------------------------------------------------------------------------------------------------------|
| `ClientApp`     | Punto de entrada (instancia simplificada con JEP 477). Configura el DI factory para los controladores e inicializa el `ViewNavigator`.   |
| `UserService`   | Capa de servicio. Construye los payloads UDP, interpreta las respuestas del servidor y expone la API a los controladores vía `Optional`. |
| `UDPService`    | Capa de red. Envía y recibe datagramas con timeout configurable usando virtual threads a través de `CompletableFuture`.                  |
| `UserSession`   | Estado compartido entre vistas. Mantiene el `User` activo con un `ObjectProperty` para reactividad.                                      |
| `ViewNavigator` | Singleton (initialization-on-demand holder). Gestiona la navegación entre vistas con precarga en caché.                                  |
| `JavaFXManager` | Utilitario concurrente. Centraliza el despacho seguro al FX thread con `Platform.runLater()`.                                            |
| `ViewRoute`     | Enum que mapea cada ruta de navegación a su archivo FXML correspondiente.                                                                |

**Controladores (`Clase_05.Correction.Client.UI.controller`)**

| Controlador        | Vista asociada    | Responsabilidad                                                                                                  |
|--------------------|-------------------|------------------------------------------------------------------------------------------------------------------|
| `SearchController` | `SearchView.fxml` | Valida la cédula, consulta el servidor de forma asíncrona y navega a la vista de perfil al encontrar al usuario. |
| `CreateController` | `CreateView.fxml` | Recolecta los datos del formulario y envía el comando de registro al servidor de forma asíncrona.                |
| `ShowController`   | `ShowView.fxml`   | Muestra el perfil y saldo del usuario activo. Gestiona el pago de pasaje y la recarga de saldo.                  |

### Lado del Servidor (`Clase_05.Correction.Server`)

| Paquete      | Clase / Archivo  | Responsabilidad                                                                                   |
|--------------|------------------|---------------------------------------------------------------------------------------------------|
| `config`     | `DatabaseConfig` | Configuración y ciclo de vida del pool de conexiones HikariCP.                                    |
| `model`      | `User` & `Card`  | Entidades con lógica de negocio (tarifa preferencial `isPreferred()`, `chargeCard`, `payTicket`). |
| `model`      | `FareType`       | Enum con los precios de tarifa regular y preferencial.                                            |
| `repository` | `*Repository`    | Acceso a datos de las tablas `client` y `card` (`findByIdentityCard`, `save`, `updateBalance`).   |
| `service`    | `*Service`       | Lógica de recarga, pago, consulta y registro atómico/transaccional.                               |
| `server`     | `Command`        | Enum funcional. Cada entrada encapsula un comando UDP y su formato de respuesta.                  |
| `server`     | `Server`         | Servidor UDP. Recibe paquetes y despacha comandos de forma asíncrona.                             |
| `server`     | `Launcher`       | Punto de entrada. Inyección de dependencias y configuración del Graceful Shutdown.                |

---

## Esquema de Base de Datos (Servidor)

```sql
-- Entidad principal
CREATE TABLE client
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    identity_card VARCHAR(10)  NOT NULL UNIQUE,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    phone         VARCHAR(10) DEFAULT NULL,
    birthday      DATE         NOT NULL
);

-- Relación 1:1 con client
CREATE TABLE card
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    balance   DECIMAL(10, 2) DEFAULT 0.00 NOT NULL,
    client_id BIGINT UNIQUE               NOT NULL,
    CONSTRAINT fk_card_client FOREIGN KEY (client_id)
        REFERENCES client (id) ON DELETE CASCADE ON UPDATE CASCADE
);
```

---

## Protocolo de Comunicación UDP

El payload viaja como texto plano con campos delimitados por `|`. El servidor responde con `SUCCESS|...` o `ERROR|...`.

| Comando    | Payload enviado                                              | Identificador usado      | Respuesta exitosa                                   |
|------------|--------------------------------------------------------------|--------------------------|-----------------------------------------------------|
| `SEARCH`   | `SEARCH\|<cédula>`                                           | Cédula (`identity_card`) | `SUCCESS\|ID:<id>\|NAME:<nombre>\|BALANCE:$<saldo>` |
| `CREATE`   | `CREATE\|cédula\|nombre\|apellido\|email\|phone\|YYYY-MM-DD` | — (registro nuevo)       | `SUCCESS\|USER_ID:<id>`                             |
| `RECHARGE` | `RECHARGE\|<db_user_id>\|<monto>`                            | ID de la BD              | `SUCCESS\|BALANCE:$<nuevo_saldo>`                   |
| `PAY`      | `PAY\|<db_user_id>`                                          | ID de la BD              | `SUCCESS\|BALANCE:$<nuevo_saldo>`                   |

> **Nota:** `SEARCH` es el único comando que identifica al usuario por su cédula. `RECHARGE` y `PAY` usan el ID numérico generado por la
> base de datos.

**Tarifas aplicadas por el servidor al comando `PAY`:**

| Tipo de usuario | Condición        | Tarifa  |
|-----------------|------------------|---------|
| Regular         | Menor de 60 años | `$0.45` |
| Preferencial    | 60 años o más    | `$0.17` |

---

## Características Implementadas

### Compartidas (Cliente y Servidor)

- **Hilos Virtuales (JEP 444):** Ambos usan `Executors.newVirtualThreadPerTaskExecutor()` para manejar I/O de red de forma óptima sin
  consumir platform threads.
- **Manejo Estructurado de Logs:** SLF4J con Logback en todos los componentes para trazas precisas (`INFO`, `DEBUG`, `WARN`, `ERROR`).
- **Instancias Simplificadas (JEP 477):** Uso de la sintaxis de `main()` implícito sin clase envolvente.

### Cliente

- **Comunicación UDP Asíncrona UI:** Las respuestas se despachan de vuelta al FX thread con `JavaFXManager.runOnFxThread()`. Feedback visual
  desactivando botones durante requests.
- **Navegación con Caché y Reactividad:** `ViewNavigator` precarga las vistas en memoria. `ShowController` se suscribe a los cambios del
  `UserSession` para actualizar la UI automáticamente.
- **Optional en API de servicio:** Los métodos que pueden fallar retornan `Optional<T>`, forzando manejo de errores explícito.

### Servidor

- **Pool de Conexiones HikariCP:** Gestión eficiente de conexiones MySQL con cierre ordenado (Graceful Shutdown).
- **Transacciones Atómicas:** El `CREATE` persiste el `client` y su `card` en una sola transacción, haciendo rollback si algo falla.
- **Comandos como Enum Funcional:** Agregar un comando nuevo no requiere tocar la clase principal del `Server`.

---

## Cómo Instalar y Ejecutar

### Prerrequisitos Combinados

| Herramienta / Librería                                                                                | Versión / Enlace                       |
|-------------------------------------------------------------------------------------------------------|----------------------------------------|
| [Amazon Corretto JDK](https://docs.aws.amazon.com/corretto/latest/corretto-26-ug/downloads-list.html) | 26                                     |
| [JavaFX SDK](https://gluonhq.com/products/javafx/)                                                    | 26                                     |
| [IntelliJ IDEA](https://www.jetbrains.com/idea/)                                                      | Cualquier edición (Community/Ultimate) |
| [SceneBuilder](https://gluonhq.com/products/scene-builder/)                                           | Compatible con JavaFX 26               |
| [HikariCP](https://repo1.maven.org/maven2/com/zaxxer/HikariCP/7.0.2/)                                 | Pool de hilos - MySQL (solo el `.jar`) | 
| [MySQL Driver](https://dev.mysql.com/downloads/connector/j/)                                          | Driver JBDC (BDD) (solo el `.jar`)     |

### Pasos de Configuración

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/NW08/Apps_Distribuidas-GR2.git
   cd src/Clase_05/Correction
   ```

2. **Configura la Base de Datos y Variables de Entorno (Servidor)**
    - Crea la BD usando el script del esquema proporcionado más arriba.
    - En IntelliJ (`Run → Edit Configurations → Environment variables`), configura las credenciales del servidor:
        - `DATABASE_PASS`: Tu contraseña de MySQL (obligatoria).
        - `DB_URL`: `jdbc:mysql://localhost:3306/tu_bd?tcpKeepAlive=true` (opcional).
        - `DB_USER`: Tu usuario, ej. `root` (opcional).

3. **Configura el IDE y Librerías**
    - Asegúrate de asignar Amazon Corretto 26 como SDK del proyecto.
    - Agrega todas las dependencias (`.jar` listados y la carpeta `lib` de JavaFX SDK 26) en `File → Project Structure → Libraries`.
        - Si te hacen falta librerías busca en los readme.md de otras clases de este mismo repositorio, ya que se han ido reutilizando a lo
          largo de los proyectos.
    - Agrega las VM Options del cliente:
      `--module-path /ruta/a/javafx-sdk-26/lib --add-modules=javafx.controls,javafx.fxml,javafx.graphics`

4. **Ejecuta la aplicación (el orden importa)**
    - **Primero:** Levanta el Servidor ejecutando `Clase_05.Correction.Server.Launcher`. Verifica en consola: *UDP Server listening on port:
      7541*.
    - **Segundo:** Levanta el Cliente ejecutando `Clase_05.Correction.Client.ClientApp` para abrir la interfaz gráfica.

---

## Flujo de Navegación del Cliente

```
SearchView  ──[usuario encontrado]──▶  ShowView  ──[← Volver]──▶  SearchView
     │
     └──[Nuevo Usuario]──▶  CreateView  ──[Cancelar / Creado]──▶  SearchView
```

---

## Casos de Error Contemplados

### Respuestas de Error del Servidor

| Situación                      | Respuesta del servidor                                     |
|--------------------------------|------------------------------------------------------------|
| Comando desconocido            | `ERROR\|Unknown command: <X>`                              |
| Campos faltantes o mal formato | `ERROR\|Malformed payload...` o `Invalid format`           |
| Usuario/Tarjeta no encontrados | `ERROR\|<mensaje del dominio>`                             |
| Saldo insuficiente para pagar  | `ERROR\|Insufficient balance. Required: $x, Available: $y` |
| Error interno inesperado       | `ERROR\|Internal Server Error`                             |

### Manejo en la Interfaz (Cliente)

| Situación                              | Comportamiento en la UI                                        |
|----------------------------------------|----------------------------------------------------------------|
| Validaciones locales (Cédula / Montos) | Mensaje o rechazo local, sin llamada de red al servidor.       |
| Servidor apagado / timeout de 5s       | `RuntimeException` capturada, mensaje de error en el label.    |
| Respuesta malformada del servidor      | `Optional.empty()` propagado, log de `WARN` con respuesta raw. |
| Error inesperado de I/O en red         | Log de `ERROR` con stack trace completo vía SLF4J.             |
