package Clase_06.MVN.Entidades.src.main.java.org;

public class App {
   static void main() {
      Persona persona = new Persona("123456789", "Juan", "Perez");
      System.out.println(persona.getCedula());
      System.out.println(persona.getNombre());
      System.out.println(persona.getApellido());
   }
}
