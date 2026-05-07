package Clase_05.Correction.Server.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum FareType {
   REGULAR(new BigDecimal("0.45")),
   PREFERRED(new BigDecimal("0.17"));

   private final BigDecimal price;

   FareType(BigDecimal price) {
      this.price = price;
   }

   public static FareType from(boolean isPreferred) {
      return isPreferred ? PREFERRED : REGULAR;
   }
}