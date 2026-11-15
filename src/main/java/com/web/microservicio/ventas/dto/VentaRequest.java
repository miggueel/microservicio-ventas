
package com.web.microservicio.ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record VentaRequest(

    @NotEmpty(message = "La venta debe incluir productos")
    List<@Valid DetalleVentaRequest> detalles

) {}
