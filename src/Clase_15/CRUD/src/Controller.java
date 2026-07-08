package Clase_15.CRUD.src;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class Controller {
   @Autowired
   private ProductoRepository productoRepository;

   @GetMapping("/")
   public String index() {
      return "¡Hola! API de Farmacia conectada a MySQL funcionando correctamente.";
   }

   // ---- READ ----

   @GetMapping("/productos")
   public List<Producto> obtenerTodos() {
      return productoRepository.findAll();
   }

   @GetMapping("/productos/{id}")
   public ResponseEntity<Producto> obtenerPorId(@PathVariable Integer id) {
      return productoRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
   }

   @GetMapping("/productos/categoria/{categoria}")
   public List<Producto> obtenerPorCategoria(@PathVariable String categoria) {
      return productoRepository.findByCategoria(categoria);
   }

   @GetMapping("/productos/buscar")
   public List<Producto> buscarPorNombre(@RequestParam String nombre) {
      return productoRepository.findByNombreComercial(nombre);
   }

   // ---- CREATE ----

   @PostMapping("/productos")
   public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
      producto.setId(null);
      Producto guardado = productoRepository.save(producto);
      return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
   }

   // ---- UPDATE ----

   @PutMapping("/productos/{id}")
   public ResponseEntity<Producto> actualizar(@PathVariable Integer id, @RequestBody Producto datos) {
      Optional<Producto> existente = productoRepository.findById(id);
      if (existente.isEmpty()) return ResponseEntity.notFound().build();
      datos.setId(id);
      Producto actualizado = productoRepository.save(datos);
      return ResponseEntity.ok(actualizado);
   }

   // ---- DELETE ----

   @DeleteMapping("/productos/{id}")
   public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
      if (!productoRepository.existsById(id)) {
         return ResponseEntity.notFound().build();
      }
      productoRepository.deleteById(id);
      return ResponseEntity.noContent().build();
   }
}