package com.lulo.sharing;

import java.util.UUID;

import com.lulo.sharing.dto.ActualizarComparticionProcesoRequest;
import com.lulo.sharing.dto.CompartirProcesoRequest;
import com.lulo.sharing.dto.ProcesoCompartidoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procesos/{procesoId}/compartidos")
@RequiredArgsConstructor
@Tag(name = "Compartición de procesos", description = "Gestión de compartición de procesos entre pools")
public class ProcesoCompartidoController {

    private final ProcesoCompartidoService procesoCompartidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Compartir proceso", description = "Comparte un proceso con otro pool indicando el nivel de acceso")
    public ProcesoCompartidoResponse compartir(@PathVariable UUID procesoId,
                                               @Valid @RequestBody CompartirProcesoRequest request) {
        return procesoCompartidoService.compartir(procesoId, request);
    }

    @GetMapping
    @Operation(summary = "Consultar comparticiones", description = "Lista los pools con los que un proceso está compartido")
    public List<ProcesoCompartidoResponse> listar(@PathVariable UUID procesoId, @RequestParam UUID usuarioId) {
        return procesoCompartidoService.listar(procesoId, usuarioId);
    }

    @PatchMapping("/{poolDestinoId}")
    @Operation(summary = "Actualizar compartición", description = "Actualiza el permiso asociado a un pool destino")
    public ProcesoCompartidoResponse actualizar(@PathVariable UUID procesoId,
                                                @PathVariable UUID poolDestinoId,
                                                @Valid @RequestBody ActualizarComparticionProcesoRequest request) {
        return procesoCompartidoService.actualizar(procesoId, poolDestinoId, request);
    }
}
