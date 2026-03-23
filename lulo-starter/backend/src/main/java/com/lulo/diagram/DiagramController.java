package com.lulo.diagram;

import com.lulo.diagram.activity.dto.CrearActividadRequest;
import com.lulo.diagram.activity.dto.EditarActividadRequest;
import com.lulo.diagram.activity.dto.EliminarActividadRequest;
import com.lulo.diagram.arc.dto.ArcoResponse;
import com.lulo.diagram.arc.dto.CrearArcoRequest;
import com.lulo.diagram.arc.dto.EditarArcoRequest;
import com.lulo.diagram.arc.dto.EliminarArcoRequest;
import com.lulo.diagram.gateway.dto.CrearGatewayRequest;
import com.lulo.diagram.gateway.dto.EditarGatewayRequest;
import com.lulo.diagram.gateway.dto.EliminarGatewayRequest;
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

    @PostMapping("/gateways")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear gateway",
            description = "Agrega un gateway exclusivo, paralelo o inclusivo al proceso")
    public NodoResponse crearGateway(
            @PathVariable Integer procesoId,
            @Valid @RequestBody CrearGatewayRequest request) {
        return diagramService.crearGateway(procesoId, request);
    }

    @PostMapping("/arcos")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear arco",
            description = "Crea una conexión dirigida entre dos nodos válidos del proceso")
    public ArcoResponse crearArco(
            @PathVariable Integer procesoId,
            @Valid @RequestBody CrearArcoRequest request) {
        return diagramService.crearArco(procesoId, request);
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

    @PatchMapping("/gateways/{gatewayId}")
    @Operation(
            summary = "Editar gateway",
            description = "Actualiza tipo, configuración, posición o lane del gateway")
    public NodoResponse editarGateway(
            @PathVariable Integer procesoId,
            @PathVariable Integer gatewayId,
            @Valid @RequestBody EditarGatewayRequest request) {
        return diagramService.editarGateway(procesoId, gatewayId, request);
    }

    @PatchMapping("/arcos/{arcoId}")
    @Operation(
            summary = "Editar arco",
            description = "Actualiza origen, destino o condición del arco manteniendo la consistencia del flujo")
    public ArcoResponse editarArco(
            @PathVariable Integer procesoId,
            @PathVariable Integer arcoId,
            @Valid @RequestBody EditarArcoRequest request) {
        return diagramService.editarArco(procesoId, arcoId, request);
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

    @DeleteMapping("/gateways/{gatewayId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Eliminar gateway",
            description = "Elimina el gateway si no tiene arcos activos conectados")
    public void eliminarGateway(
            @PathVariable Integer procesoId,
            @PathVariable Integer gatewayId,
            @Valid @RequestBody EliminarGatewayRequest request) {
        diagramService.eliminarGateway(procesoId, gatewayId, request);
    }

    @DeleteMapping("/arcos/{arcoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Eliminar arco",
            description = "Realiza soft delete del arco para conservar trazabilidad")
    public void eliminarArco(
            @PathVariable Integer procesoId,
            @PathVariable Integer arcoId,
            @Valid @RequestBody EliminarArcoRequest request) {
        diagramService.eliminarArco(procesoId, arcoId, request);
    }
}
