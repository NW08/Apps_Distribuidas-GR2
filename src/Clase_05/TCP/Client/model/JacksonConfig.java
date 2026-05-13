package Clase_05.TCP.Client.model;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public class JacksonConfig {
   public ObjectMapper mapper = JsonMapper.builder()
         .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
         .build();
}
