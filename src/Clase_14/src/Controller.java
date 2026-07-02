package Clase_14.src;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller {

   @Autowired
   private ProductoRepository productoRepository;

   // ENDPOINT DE INTRODUCCIÓN
   @GetMapping("/")
   public String index() {
      return "¡Hola! API de Farmacia conectada a MySQL funcionando correctamente.";
   }

   // 1: Traer todos los productos
   @GetMapping("/productos")
   public List<Producto> obtenerTodos() {
      return productoRepository.findAll();
   }

   // 2: Buscar un solo producto por su ID
   @GetMapping("/productos/{id}")
   public Producto obtenerPorId(@PathVariable Integer id) {
      return productoRepository.findById(id).orElse(null);
   }

   // 3: Filtrar productos por su categoría
   @GetMapping("/productos/categoria/{categoria}")
   public List<Producto> obtenerPorCategoria(@PathVariable String categoria) {
      return productoRepository.findByCategoria(categoria);
   }

   // 4: Buscador de productos por nombre (coincidencia parcial)
   @GetMapping("/productos/buscar")
   public List<Producto> buscarPorNombre(@RequestParam String nombre) {
      return productoRepository.findByNombreComercial(nombre);
   }
}