package Class_06.MVN.Servicio.src.main.java.org.example;

import Class_06.MVN.Entidades.src.main.java.org.Persona;


import java.util.ArrayList;
import java.util.Iterator;

public class ServicioPersona {
   private final ArrayList<Persona> personas;
   private Persona persona;

   public ServicioPersona() {
      personas = new ArrayList<>();
   }

   public void insertar(Persona persona) {
      personas.add(persona);
   }

   public ArrayList<Persona> mostrar() {
      return personas;
   }

   public Persona buscar(String cedula) {
      for (Persona persona : personas) {
         if (persona.getCedula().equals(cedula)) {
            this.persona = persona;
         }
      }
      return persona;
   }

   public void eliminar(String cedula) {
      for (Iterator<Persona> iterator = personas.iterator(); iterator.hasNext(); ) {
         Persona persona = iterator.next();
         if (persona.getCedula().equals(cedula)) {
            iterator.remove();
            break;
         }
      }
   }
}
