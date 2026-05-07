package Clase_05.TCP.Server.services;

import java.time.LocalDateTime;

public class ArrivalRegistryService implements IArrivalRegistryService {
   @Override
   public String register(String name) {
      return String.format("%s, registered at %s", name, LocalDateTime.now());
   }
}