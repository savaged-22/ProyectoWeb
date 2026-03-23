package com.lulo.rbac;

import com.lulo.rbac.dto.CrearRolProcesoRequest;
import com.lulo.rbac.dto.EditarRolProcesoRequest;
import com.lulo.rbac.dto.EliminarRolProcesoRequest;
import com.lulo.rbac.dto.RolProcesoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles-proceso")
@RequiredArgsConstructor
@Tag(name = "Roles de proceso", description = "Gestión de roles funcionales reutilizables por empresa")
public class RolProcesoController {

    private final RolProcesoService rolProcesoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear rol de proceso",
            description = "Crea un rol funcional reutilizable dentro de una empresa")
    public RolProcesoResponse crear(@Valid @RequestBody CrearRolProcesoRequest request) {
        return rolProcesoService.crear(request);
    }

    @PatchMapping("/{rolProcesoId}")
    @Operation(
            summary = "Editar rol de proceso",
            description = "Actualiza nombre o descripción del rol manteniendo coherencia de referencias")
    public RolProcesoResponse editar(@PathVariable Integer rolProcesoId,
                                     @Valid @RequestBody EditarRolProcesoRequest request) {
        return rolProcesoService.editar(rolProcesoId, request);
    }

    @DeleteMapping("/{rolProcesoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Eliminar rol de proceso",
            description = "Realiza soft delete del rol solo si no está asignado en ninguna lane")
    public void eliminar(@PathVariable Integer rolProcesoId,
                         @Valid @RequestBody EliminarRolProcesoRequest request) {
        rolProcesoService.eliminar(rolProcesoId, request);
    }
}
