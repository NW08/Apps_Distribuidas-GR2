package Clase_14.src;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

   List<Producto> findByCategoria(String categoria);

   List<Producto> findByNombreComercial(String nombre);
}