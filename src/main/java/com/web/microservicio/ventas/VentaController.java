
package com.web.microservicio.ventas;

import com.web.microservicio.ventas.dto.VentaRequest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "http://localhost:5173")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    // Listar todas las ventas
    @GetMapping
    public List<Venta> listarVentas() {
        return ventaService.listarVentas();
    }

    // Buscar una venta por su ID
    @GetMapping("/{id}")
    public Venta buscarVenta(@PathVariable Long id) {
        return ventaService.buscarVenta(id);
    }

    // Registrar una nueva venta
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Venta registrarVenta(
            @Valid @RequestBody VentaRequest request) {

        return ventaService.registrarVenta(request);
    }

    // Consultar los productos de una venta
    @GetMapping("/{id}/detalles")
    public List<DetalleVenta> listarDetalles(
            @PathVariable Long id) {

        return ventaService.listarDetalles(id);
    }
}
