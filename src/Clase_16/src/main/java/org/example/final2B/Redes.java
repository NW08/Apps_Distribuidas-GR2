package Clase_16.src.main.java.org.example.final2B;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "redes")
public class Redes {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id")
   private Integer id;

   @Column(name = "usuario", nullable = false, length = 15)
   private String usuario;

   @Column(name = "accion", nullable = false, length = 10)
   private String accion;

   @Column(name = "fecha", nullable = false)
   private LocalDate fecha;

   @Column(name = "hora", nullable = false)
   private LocalTime hora;

   @Column(name = "short", nullable = false, length = 10)
   private String shortVideo;
}