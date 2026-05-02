package Clase_04.Server.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tarjeta {
   private Usuario usuario;
   private double saldo = 0.0;

   public void asignarTarjeta(Usuario usuario) {
      this.usuario = usuario;
   }

   public void cargarSaldo(double saldo) {
      this.saldo += saldo;
   }

   public boolean pagarPasaje() {
      final double COSTO_PASAJE = 0.35;
      if (saldo >= COSTO_PASAJE) {
         saldo -= COSTO_PASAJE;
         return true;
      }
      return false;
   }
}
