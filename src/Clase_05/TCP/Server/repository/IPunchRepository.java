package Clase_05.TCP.Server.repository;

import Clase_05.TCP.Server.model.PunchRequest;
import Clase_05.TCP.Server.model.PunchType;

import java.time.LocalDate;
import java.util.List;

public interface IPunchRepository {
   void save(PunchRequest punch);

   boolean hasPunchedToday(String employeeId, PunchType type, LocalDate date);

   List<PunchRequest> getPunchesForToday(String employeeId, LocalDate date);
}