package Clase_05.Correction.Server.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
public class Card {
   public static final BigDecimal TICKET_PRICE = new BigDecimal("0.35");
   public static final BigDecimal TICKET_PRICE_PREFERRED = new BigDecimal("0.17");
   private Long id;
   private Long userID;
   private BigDecimal balance = BigDecimal.ZERO;

   public void chargeCard(BigDecimal amount) {
      if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Charge amount must be positive.");
      this.balance = this.balance.add(amount);
   }

   public void payTicket(boolean isPreferred) {
      BigDecimal price = isPreferred ? TICKET_PRICE_PREFERRED : TICKET_PRICE;
      if (this.balance.compareTo(price) < 0)
         throw new IllegalStateException("Insufficient balance. Required: $" + price + ", Available: $" + this.balance);
      this.balance = this.balance.subtract(price);
   }
}