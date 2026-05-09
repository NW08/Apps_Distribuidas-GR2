package Clase_05.TCP.Server.config;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

public class JacksonConfig {
   public static ObjectMapper createMapper() {
      return JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
   }
}