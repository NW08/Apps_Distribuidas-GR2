package Clase_05.Correction.Server.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Card {
   public static final BigDecimal TICKET_PRICE = new BigDecimal("0.35");

   private Long id;
   private Long userID;
   private BigDecimal balance = BigDecimal.ZERO;

   public void chargeCard(BigDecimal amount) {
      if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Charge amount must be positive.");
      this.balance = this.balance.add(amount);
   }

   public void payTicket() {
      if (this.balance.compareTo(TICKET_PRICE) < 0) throw new IllegalStateException("Insufficient balance to pay ticket.");
      this.balance = this.balance.subtract(TICKET_PRICE);
   }
}