package Clase_07.RMI.Shared;

import java.io.Serializable;

public record Person(
      int clave,
      String nombre,
      String correo,
      String cargo,
      double sueldo
) implements Serializable {
}
