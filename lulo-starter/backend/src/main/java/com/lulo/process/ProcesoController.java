package com.lulo.process;

import com.lulo.process.dto.CrearProcesoRequest;
import com.lulo.process.dto.EditarProcesoRequest;
import com.lulo.process.dto.EliminarProcesoRequest;
import com.lulo.process.dto.ProcesoDetalleResponse;
import com.lulo.process.dto.ProcesoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/procesos")
@RequiredArgsConstructor
@Tag(name = "Procesos", description = "Gestión de procesos organizacionales")
public class ProcesoController {

    private final ProcesoService procesoService;

    @GetMapping
    @Operation(
            summary = "Listar procesos",
            description = "Lista paginada con filtros opcionales por estado, categoría y nombre")
    public Page<ProcesoResponse> listar(
            @RequestParam Integer empresaId,
            @RequestParam Integer usuarioId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String nombre,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return procesoService.listar(empresaId, usuarioId, estado, categoria, nombre, pageable);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener proceso con diagrama",
            description = "Retorna el proceso y todos sus elementos del diagrama: lanes, nodos y arcos")
    public ProcesoDetalleResponse obtener(
            @PathVariable Integer id,
            @RequestParam Integer empresaId,
            @RequestParam Integer usuarioId) {
        return procesoService.obtener(id, empresaId, usuarioId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear proceso",
            description = "Crea un proceso asociado a la empresa y al pool indicado")
    public ProcesoResponse crear(@Valid @RequestBody CrearProcesoRequest request) {
        return procesoService.crear(request);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Editar proceso",
            description = "Actualiza los campos indicados del proceso y registra el cambio en el historial")
    public ProcesoResponse editar(
            @PathVariable Integer id,
            @Valid @RequestBody EditarProcesoRequest request) {
        return procesoService.editar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Eliminar proceso",
            description = "Soft delete: el proceso queda inactivo en BD para mantener trazabilidad histórica")
    public void eliminar(
            @PathVariable Integer id,
            @Valid @RequestBody EliminarProcesoRequest request) {
        procesoService.archivar(id, request);
    }
}
