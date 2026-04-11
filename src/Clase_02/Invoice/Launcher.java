package Clase_02.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Launcher {

   static void main() {

      Invoice invoiceOne = Invoice.builder()
            .code("INV-001")
            .client("Test")
            .date(LocalDate.of(2023, 1, 1))
            .amount(new BigDecimal("152.25"))
            .build();

      invoiceOne.display();

      Invoice invoiceTwo = Invoice.builder()
            .code("INV-002")
            .client("Jossu")
            .date(LocalDate.of(2026, 3, 17))
            .amount(new BigDecimal("1478.52"))
            .build();

      invoiceTwo.display();

   }
}