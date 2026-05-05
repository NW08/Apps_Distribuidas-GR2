package Clase_05.Correction.Client.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
public class Card {
   private Long id;
   private Long userID;

   @Builder.Default
   private BigDecimal balance = BigDecimal.ZERO;
}
