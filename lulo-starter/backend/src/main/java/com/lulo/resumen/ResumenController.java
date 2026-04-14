package com.lulo.resumen;

import com.lulo.resumen.dto.ResumenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resumen")
@RequiredArgsConstructor
@Tag(name = "Resumen", description = "Vista completa de todos los datos organizados por sus relaciones")
public class ResumenController {

    private final ResumenService resumenService;

    @GetMapping
    @Operation(
            summary = "Resumen global de la base de datos",
            description = """
                    Devuelve todos los datos organizados jerárquicamente según las relaciones:
                    Empresa → Usuarios, RolesProceso, Pools → RolesPool (con permisos y miembros),
                    Procesos → Lanes, Nodos (actividades y gateways), Arcos, Compartidos.
                    Incluye estadísticas globales de conteo.
                    """)
    public ResumenResponse obtenerResumen() {
        return resumenService.construir();
    }
}
