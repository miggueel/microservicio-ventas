
package com.web.microservicio.productos;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Obtener todos los productos
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // Buscar un producto por ID
    public Producto buscarProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Producto no encontrado"
                ));
    }

    // Registrar un nuevo producto
    public Producto guardarProducto(Producto producto) {
        validarProducto(producto);
        producto.setId(null);
        return productoRepository.save(producto);
    }

    // Actualizar un producto existente
    @Transactional
    public Producto actualizarProducto(Long id, Producto datos) {
        validarProducto(datos);

        Producto producto = buscarProducto(id);

        producto.setNombre(datos.getNombre());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setStock(datos.getStock());

        return productoRepository.save(producto);
    }

    // Eliminar un producto
    public void eliminarProducto(Long id) {
        Producto producto = buscarProducto(id);
        productoRepository.delete(producto);
    }

    // Validar los datos recibidos
    private void validarProducto(Producto producto) {
        if (producto.getNombre() == null ||
                producto.getNombre().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre es obligatorio"
            );
        }

        if (producto.getPrecio() == null ||
                producto.getPrecio().signum() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio debe ser mayor o igual a cero"
            );
        }

        if (producto.getStock() == null ||
                producto.getStock() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El stock no puede ser negativo"
            );
        }
    }
}
