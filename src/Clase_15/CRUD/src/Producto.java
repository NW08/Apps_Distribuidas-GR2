package Clase_15.CRUD.src;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Integer id;

   @Column(name = "nombre_comercial")
   private String nombreComercial;

   @Column(name = "principio_activo")
   private String principioActivo;

   private String categoria;

   private String laboratorio;

   private Double precio;

   private Integer stock;

}