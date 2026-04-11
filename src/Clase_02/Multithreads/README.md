# Gestor de Concurrencia y Multihilos || `CLASE 02 (08|04|2026)`

Se hizo este proyecto como práctica de la materia de Aplicaciones Distribuidas en Java, con el objetivo de dominar la ejecución paralela de
tareas, la orquestación de hilos modernos mediante la API de concurrencia de Java y la protección de recursos compartidos.

## Vista General del Proyecto

Este módulo simula la ejecución concurrente de cinco procesos de naturaleza completamente distinta, gestionados por un ‘Thread Pool’ (
Piscina de Hilos) en **Java 26**. La arquitectura se divide en los siguientes componentes:

| Clase / Archivo         | Responsabilidad                                                                                                 |
|:------------------------|:----------------------------------------------------------------------------------------------------------------|
| `Printable`             | Interfaz funcional que obliga a las tareas visuales a tener un método de impresión.                             |
| `Launcher`              | Orquestador principal. Crea y gestiona el ciclo de vida de los hilos usando `ExecutorService`.                  |
| `ConsoleMonitor`        | **Árbitro (Monitor):** Protege la consola mediante métodos `synchronized` para evitar mezclas de texto.         |
| `AProcess` & `BProcess` | Tareas de carga iterativa para demostrar el reparto de tiempo de CPU y bloqueos de monitor.                     |
| `CProcess`              | Tarea de cálculo con paso de parámetros por constructor y manejo seguro de excepciones (`ArithmeticException`). |
| `DProcess`              | Tarea de delegación. Lanza una interfaz gráfica pesada (Calculadora JavaFX) en un hilo independiente.           |
| `EProcess`              | Tarea de introspección. Recupera e imprime la identidad/nombre del hilo que la está ejecutando.                 |

---

## Características Implementadas

- **Gestión Moderna de Hilos (Executors):** En lugar de instanciar manualmente (`new Thread()`), se utiliza un `FixedThreadPool` de 5
  hilos, optimizando el uso de CPU y memoria al reciclar “trabajadores” fijos.
- **Prevención de Fugas de Memoria:** Implementación de bloques `try-with-resources` (Java 19+) para asegurar que el `ExecutorService` se
  apague de forma automática y segura (AutoCloseable).
- **Seguridad en Hilos (Thread-Safety):** Resolución de “Condiciones de Carrera" (choques de impresión en consola) implementando el patrón
  Monitor con métodos `synchronized` en `ConsoleMonitor`.
- **Aislamiento de Interfaz Gráfica (GUI):** Prevención de bloqueos en el hilo principal ejecutando la llamada nativa de JavaFX (
  `CalculatorApp.launchApp`) dentro de un `Runnable` asíncrono.
- **Manejo de Excepciones Concurrentes:** El proceso matemático previene la terminación abrupta del hilo capturando divisiones por cero e
  imprimiendo el símbolo `[∞]` sin interrumpir el bucle.

---

## Cómo Instalar y Ejecutar

### Prerrequisitos

Este módulo depende directamente del proyecto de la **Clase 01**. Asegúrate de tenerlo configurado:

| Herramienta                                                                                           | Versión recomendada                |
|:------------------------------------------------------------------------------------------------------|:-----------------------------------|
| [Amazon Corretto JDK](https://docs.aws.amazon.com/corretto/latest/corretto-26-ug/downloads-list.html) | 26                                 |
| [JavaFX SDK](https://gluonhq.com/products/javafx/)                                                    | 26 (Requerido para `DProcess`)     |
| Proyecto [`Clase_01.CalculatorApp`](../../Clase_01)                                                   | Disponible en el mismo repositorio |

### Pasos

1. **Clona el repositorio y navega al módulo**
   ```bash
   git clone https://github.com/NW08/Apps_Distribuidas-GR2.git
   cd src/Clase_02/Multithreads
   ```

2. **Configura el IDE (IntelliJ IDEA)**
    - Asegúrate de que tu proyecto tenga acceso a la librería de JavaFX, ya que el proceso D la invocará.
    - Verifica que las dependencias entre módulos/paquetes estén correctas (que `Clase_02` pueda importar `Clase_01`).

3. **Ejecuta la aplicación**
    - Inicia la clase `Launcher` ejecutando su método `main`.

### Comportamiento Esperado al Ejecutar

Al dar *Play*, observarás tres cosas casi simultáneas:

1. Se abrirá la ventana gráfica de la Calculadora de la Clase 01.
2. La consola imprimirá los bloques de símbolos (`#` y `=`), el saludo con el nombre del hilo, y la cuenta matemática regresiva.
3. El orden exacto de los textos en consola **variará en cada ejecución**. Esto es el comportamiento natural y correcto de la concurrencia,
   ya que depende de qué hilo obtuvo primero el acceso al Monitor del sistema.