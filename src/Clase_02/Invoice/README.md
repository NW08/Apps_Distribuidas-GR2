# Sistema de Facturación Básico con Lombok || `CLASE 02 (08|04|2026)`

Se hizo este proyecto como práctica y continuación de la materia de Aplicaciones Distribuidas en Java, introduciendo conceptos fundamentales
de inmutabilidad, patrones de diseño creacionales y reducción de código repetitivo (*boilerplate*) mediante la librería Lombok.

## Vista General del Proyecto

Este es un módulo de gestión de facturas por consola, construido con **Java 26**. La arquitectura se centra en el diseño orientado a objetos
y modelado de dominio limpio, con las siguientes capas:

| Clase / Archivo | Responsabilidad                                                             |
|:----------------|:----------------------------------------------------------------------------|
| `Displayable`   | Interfaz/Contrato que obliga a las clases a definir su propia visualización |
| `Invoice`       | Entidad de negocio (modelo) que representa una factura                      |
| `Launcher`      | Punto de entrada y orquestador para instanciar y probar los objetos         |

---

## Características Implementadas

- **Patrón de Diseño Builder:** Implementado de forma automática mediante la anotación `@Builder` de Lombok. Permite instanciar objetos
  complejos de forma fluida, legible y sin depender del orden de un constructor tradicional.
- **Inmutabilidad absoluta:** Garantizada a través de la anotación `@Value` de Lombok. Convierte todos los campos en `private final` y
  genera los métodos `equals`, `hashCode` y `toString`, haciendo que los objetos `Invoice` sean predecibles y seguros en entornos
  multihilos (*thread-safe*).
- **Manejo seguro de valores monetarios:** Uso estricto de la clase `BigDecimal` (inicializada correctamente mediante `String`) para el
  campo `amount`, evitando la pérdida de precisión y problemas de redondeo inherentes a los tipos primitivos como `double` o `float`.
- **Gestión moderna de fechas:** Integración de la API de fechas de Java 8+ utilizando `LocalDate` para representar el momento de
  facturación sin lidiar con los problemas de la antigua clase `Date`.
- **Abstracción por Contratos:** Cumplimiento de la interfaz `Displayable`, demostrando polimorfismo y delegando la responsabilidad de
  impresión (`System.out.println`) al propio objeto.

---

## Cómo Instalar

### Prerrequisitos

Antes de empezar, asegúrate de tener instalado lo siguiente:

| Herramienta                                                                                           | Versión recomendada                      |
|:------------------------------------------------------------------------------------------------------|:-----------------------------------------|
| [Amazon Corretto JDK](https://docs.aws.amazon.com/corretto/latest/corretto-26-ug/downloads-list.html) | 26                                       |
| [Project Lombok](https://projectlombok.org/download)                                                  | Última versión compatible                |
| [IntelliJ IDEA](https://www.jetbrains.com/idea/)                                                      | Cualquier edición (Community o Ultimate) |

### Pasos

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/NW08/Apps_Distribuidas-GR2.git
   cd src/Clase_02/Invoice
   ```

2. **Configura el SDK en IntelliJ IDEA**
    - Ve a `File → Project Structure → SDKs`
    - Agrega el JDK de Amazon Corretto 26

3. **Configura Lombok en tu IDE**
    - Asegúrate de tener el plugin de Lombok instalado en IntelliJ (`Settings → Plugins → Marketplace → Lombok`).
    - Habilita el procesamiento de anotaciones: Ve a `Settings → Build, Execution, Deployment → Compiler → Annotation Processors` y marca la
      casilla **"Enable annotation processing"**.
    - Descarga el `.jar` y añádelo a las librerías del proyecto.

4. **Ejecuta la aplicación**
    - Corre la clase `Launcher` ejecutando el método `main`. Deberías ver el estado de ambas facturas impreso en la consola.

---

**Casos de error comunes durante la configuración:**

| Situación                                  | Solución                                                                                        |
|:-------------------------------------------|:------------------------------------------------------------------------------------------------|
| El IDE no reconoce `.builder()` o `@Value` | El plugin de Lombok no está instalado o activo.                                                 |
| Errores de compilación en `Launcher`       | Revisa que el procesamiento de anotaciones (*Annotation Processing*) esté habilitado en el IDE. |