package Clase_05.TCP.Server.model;

import java.time.LocalDateTime;

public record PunchRequest(
      String employeeId,
      PunchType type,
      LocalDateTime timestamp
) {
}