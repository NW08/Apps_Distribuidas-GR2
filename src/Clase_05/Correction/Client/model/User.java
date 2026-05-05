package Clase_05.Correction.Client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
   private final static int MIN_AGE = 60;

   private Long id;
   private String identification;
   private String name;
   private String lastName;
   private String email;
   private String phone;
   private LocalDate birthday;

   public boolean isPreferred() {
      if (birthday == null) return false;
      return Period.between(birthday, LocalDate.now()).getYears() >= MIN_AGE;
   }
}