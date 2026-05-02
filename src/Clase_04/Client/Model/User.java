package Clase_04.Client.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {
   private String cedula;
   private String mail;
   private String phone;
   private String name;
   private boolean isPreferred;
   private double balance = 0.0;

   public User(String cedula, String mail, String phone, String name, boolean isPreferred) {
      this.cedula = cedula;
      this.mail = mail;
      this.phone = phone;
      this.name = name;
      this.isPreferred = isPreferred;
   }
}
