# Calculadora Básica con JavaFX || `CLASE 01 (03|04|2026)`

Se hizo este proyecto cómo práctica e introducción de la materia de Aplicaciones Distribuidas en Java usando JavaFX.

## Tabla de Contenidos

- [Vista General del Proyecto](#vista-general-del-proyecto)
- [Características Implementadas](#características-implementadas)
- [Capturas de Pantalla](#capturas-de-pantalla)
- [Cómo Instalar](#cómo-instalar)

---

## Vista General del Proyecto

Esta es una calculadora de escritorio con cuatro operaciones básicas (suma, resta, multiplicación y división), construida con **Java 26** y **JavaFX 26**. La arquitectura sigue el patrón MVC con las siguientes capas:

| Clase / Archivo | Responsabilidad |
|---|---|
| `ICalculatorModel` | Contrato/Interfaz de operaciones |
| `CalculatorModel` | Lógica de negocio (operaciones matemáticas) |
| `CalculatorController` | Manejo de eventos de la UI |
| `CalculatorApp` | Punto de entrada y configuración de la ventana |
| `CalculatorView.fxml` | Definición declarativa de la interfaz gráfica |
| `styles.css` | Estilos visuales de los componentes |

---

## Características Implementadas

- **Suma, resta, multiplicación y división:** Las cuatro operaciones están completamente implementadas y conectadas a sus respectivos botones en la UI.
- **Manejo de errores:** Se valida que los inputs sean números válidos y se controla la división por cero mediante excepciones (`IllegalArgumentException`).
- **Principio DRY:** Toda la lógica de parseo (conversión de string a double en este caso), operación y visualización del resultado está centralizada en un único método `performOperation(BinaryOperator<Double>)`.
- **Inversión de dependencias (DIP):** El controlador recibe `ICalculatorModel` en su constructor, no la clase concreta, lo que facilita pruebas unitarias (de ser el caso).
- **Inyección del controlador vía factory:** El `FXMLLoader` instancia el controlador manualmente para permitir inyección de dependencias.
- **Botón de limpieza:** Resetea ambos campos de texto y el label de resultado con un solo click.

---

## Capturas de Pantalla

**Interfaz principal**
![Interfaz principal](https://placehold.co/600x300?text=Vista+Principal "Ventana principal de la calculadora")

**Vista del resultado**
![Resultado](https://placehold.co/600x300?text=Resultado "Calculadora mostrando el resultado de una operación")

---

## 🚀 Cómo Instalar

### Prerrequisitos

Antes de empezar, asegurate de tener instalado lo siguiente:

| Herramienta | Versión recomendada |
|---|---|
| [Amazon Corretto JDK](https://docs.aws.amazon.com/corretto/latest/corretto-26-ug/downloads-list.html) | 26 |
| [JavaFX SDK](https://gluonhq.com/products/javafx/) | 26 |
| [IntelliJ IDEA](https://www.jetbrains.com/idea/) | Cualquier edición (Community o Ultimate) |
| [SceneBuilder](https://gluonhq.com/products/scene-builder/) | Compatible con JavaFX 26 |

### Pasos

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/NW08/Apps_Distribuidas-GR2.git
   cd src/Clase_01
   ```

2. **Configura el SDK en IntelliJ IDEA**
   - Andá a `File → Project Structure → SDKs`
   - Agregá el JDK de Amazon Corretto 26

3. **Agrega el módulo de JavaFX**
   - En `File → Project Structure → Libraries`, agrega la carpeta `lib` del JavaFX SDK 26 descargado y descomprimido
   - Asegurate de que los módulos necesarios estén en las VM Options:
   ```
   --module-path /ruta/a/javafx-sdk-26/lib
   --add-modules=javafx.controls,javafx.fxml,javafx.swing,javafx.graphics,javafx.media,javafx.web
   ```

4. **Ejecuta la aplicación**
   - Corre la clase `CalculatorApp` (o la clase `Main` que llame a `CalculatorApp.launchApp(args)`)

---

**Casos de error contemplados:**

| Situación | Mensaje mostrado |
|---|---|
| Texto no numérico en algún campo | `Please enter valid numbers.` |
| División por cero | `Cannot divide by zero.` |

---
