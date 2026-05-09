package Clase_05.TCP.Server.core;

import Clase_05.TCP.Server.model.PunchRequest;
import Clase_05.TCP.Server.model.PunchResponse;
import Clase_05.TCP.Server.repository.PunchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ArrivalRegistryService {
   private final PunchRepository repository;

   public PunchResponse processPunch(PunchRequest request) {
      synchronized (request.employeeId().intern()) {
         if (repository.hasPunchedToday(request.employeeId(), request.type(), request.timestamp().toLocalDate())) {
            log.warn("Duplicate punch attempt: {} for {}", request.type(), request.employeeId());
            return new PunchResponse(false, "Punch already registered for today.");
         }

         repository.save(request);
         log.info("Successfully registered {} for {} at {}", request.type(), request.employeeId(), request.timestamp());
         return new PunchResponse(true, "Successfully registered " + request.type());
      }
   }
}