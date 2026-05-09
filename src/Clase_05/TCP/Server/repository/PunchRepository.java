package Clase_05.TCP.Server.repository;

import Clase_05.TCP.Server.model.PunchRequest;
import Clase_05.TCP.Server.model.PunchType;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class PunchRepository implements IPunchRepository {
   private final Map<String, List<PunchRequest>> db = new ConcurrentHashMap<>();

   @Override
   public void save(PunchRequest punch) {
      String key = buildKey(punch.employeeId(), punch.timestamp().toLocalDate());
      db.computeIfAbsent(key, _ -> new CopyOnWriteArrayList<>()).add(punch);
      log.debug("Saved punch in DB: {}", punch);
   }

   @Override
   public boolean hasPunchedToday(String employeeId, PunchType type, LocalDate date) {
      return getPunchesForToday(employeeId, date).stream()
            .anyMatch(p -> p.type() == type);
   }

   @Override
   public List<PunchRequest> getPunchesForToday(String employeeId, LocalDate date) {
      return db.getOrDefault(buildKey(employeeId, date), Collections.emptyList());
   }

   private String buildKey(String employeeId, LocalDate date) {
      return employeeId + "_" + date.toString();
   }
}