package Clase_06.RMI.Shared;

import java.io.Serializable;

public record Person(
      int id,
      String name,
      String email,
      String role,
      double salary
) implements Serializable {
}
