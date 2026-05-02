package Clase_04.Server.Model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Setter
@Getter
@ToString
public class Usuario {
   @Getter
   private static ArrayList<Usuario> usuariosCreados = new ArrayList<>();
   private final String cedula;
   private final String nombre;
   private String correo;
   private String telefono;
   private boolean preferencial;
   private Tarjeta tarjeta;

   public Usuario(String cedula, String correo, String telefono, String nombre, boolean preferencial) {
      this.nombre = nombre;
      this.correo = correo;
      this.telefono = telefono;
      this.cedula = cedula;
      this.preferencial = preferencial;
      this.tarjeta = null;
      usuariosCreados.add(this);
   }

}
