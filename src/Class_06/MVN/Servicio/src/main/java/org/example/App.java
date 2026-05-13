package Class_06.MVN.Servicio.src.main.java.org.example;

import Class_06.MVN.Entidades.src.main.java.org.Persona;

public class App {
   static void main() {
      ServicioPersona personas = new ServicioPersona();

      Persona persona = new Persona("1234567890", "Jose", "Vargas");
      personas.insertar(persona);

      personas.insertar(new Persona("0987654321", "Juan", "Gomez"));

      personas.insertar(new Persona("0987654321", "Pedro", "Diaz"));
      for (Persona x : personas.mostrar()) {
         System.out.println(x);
      }
   }
}