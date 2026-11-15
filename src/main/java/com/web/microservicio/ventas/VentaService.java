
package com.web.microservicio.ventas;

import com.web.microservicio.productos.Producto;
import com.web.microservicio.productos.ProductoRepository;
import com.web.microservicio.ventas.dto.DetalleVentaRequest;
import com.web.microservicio.ventas.dto.VentaRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(
            VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            ProductoRepository productoRepository) {

        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Venta registrarVenta(VentaRequest request) {

        if (request == null || request.detalles() == null
                || request.detalles().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La venta debe incluir productos"
            );
        }

        Venta venta = new Venta();
        BigDecimal total = BigDecimal.ZERO;

        // Primero guardamos la venta
        venta = ventaRepository.save(venta);

        for (DetalleVentaRequest item : request.detalles()) {

            if (item == null || item.productoId() == null
                    || item.cantidad() == null
                    || item.cantidad() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Los datos del producto son invalidos"
                );
            }

            
                Producto producto = productoRepository
                        .buscarPorIdConBloqueo(item.productoId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Producto no encontrado"
                        ));


            if (producto.getStock() < item.cantidad()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Stock insuficiente para: "
                                + producto.getNombre()
                );
            }

            BigDecimal precio = producto.getPrecio();

            BigDecimal subtotal = precio.multiply(
                    BigDecimal.valueOf(item.cantidad())
            );

            // Descontar existencias
            producto.setStock(
                    producto.getStock() - item.cantidad()
            );

            productoRepository.save(producto);

            // Crear detalle de la venta
            DetalleVenta detalle = new DetalleVenta();

            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(subtotal);

            detalleVentaRepository.save(detalle);

            total = total.add(subtotal);
        }

        // Actualizar el total de la venta
        venta.setTotal(total);

        return ventaRepository.saveAndFlush(venta);
    }

    @Transactional(readOnly = true)
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Venta buscarVenta(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Venta no encontrada"
                ));
    }

    @Transactional(readOnly = true)
    public List<DetalleVenta> listarDetalles(Long ventaId) {

        buscarVenta(ventaId);

        return detalleVentaRepository.findByVentaId(ventaId);
    }
}
