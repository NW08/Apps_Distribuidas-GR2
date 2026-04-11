package Clase_02.Invoice;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;


@Value
@Builder
public class Invoice implements Displayable {
   String code;
   String client;
   LocalDate date;
   BigDecimal amount;

   public void display() {
      System.out.println(this);
   }
}