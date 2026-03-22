package com.lulo.diagram;

import com.lulo.diagram.activity.dto.CrearActividadRequest;
import com.lulo.diagram.activity.dto.EditarActividadRequest;
import com.lulo.diagram.activity.dto.EliminarActividadRequest;
import com.lulo.diagram.node.dto.NodoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/procesos/{procesoId}")
@RequiredArgsConstructor
@Tag(name = "Diagrama", description = "Gestión de elementos del diagrama: actividades, gateways, arcos y lanes")
public class DiagramController {

    private final DiagramService diagramService;

    @PostMapping("/actividades")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear actividad",
            description = "Agrega una actividad al proceso. Se especifica nombre, tipo y posición en el canvas")
    public NodoResponse crearActividad(
            @PathVariable Integer procesoId,
            @Valid @RequestBody CrearActividadRequest request) {
        return diagramService.crearActividad(procesoId, request);
    }

    @PatchMapping("/actividades/{actividadId}")
    @Operation(
            summary = "Editar actividad",
            description = "Actualiza los campos indicados de la actividad y registra el cambio en el historial del proceso")
    public NodoResponse editarActividad(
            @PathVariable Integer procesoId,
            @PathVariable Integer actividadId,
            @Valid @RequestBody EditarActividadRequest request) {
        return diagramService.editarActividad(procesoId, actividadId, request);
    }

    @DeleteMapping("/actividades/{actividadId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Eliminar actividad",
            description = "Elimina la actividad y todos sus arcos conectados. El flujo se ajusta automáticamente")
    public void eliminarActividad(
            @PathVariable Integer procesoId,
            @PathVariable Integer actividadId,
            @Valid @RequestBody EliminarActividadRequest request) {
        diagramService.eliminarActividad(procesoId, actividadId, request);
    }
}
