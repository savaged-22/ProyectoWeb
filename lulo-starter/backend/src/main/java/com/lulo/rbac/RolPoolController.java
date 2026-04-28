package com.lulo.rbac;

import java.util.UUID;

import com.lulo.rbac.dto.ActualizarPermisosRolPoolRequest;
import com.lulo.rbac.dto.AsignarRolPoolRequest;
import com.lulo.rbac.dto.CrearRolPoolRequest;
import com.lulo.rbac.dto.EditarRolPoolRequest;
import com.lulo.rbac.dto.EliminarRolPoolRequest;
import com.lulo.rbac.dto.PermisoResponse;
import com.lulo.rbac.dto.RolPoolResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles-pool")
@RequiredArgsConstructor
@Tag(name = "Roles de pool", description = "Gestión de roles, permisos y asignaciones dentro de un pool")
public class RolPoolController {

    private final RolPoolService rolPoolService;

    @GetMapping
    @Operation(summary = "Listar roles de pool", description = "Consulta los roles activos o históricos de un pool")
    public List<RolPoolResponse> listar(@RequestParam UUID poolId,
                                        @RequestParam UUID usuarioId,
                                        @RequestParam(defaultValue = "true") boolean soloActivos) {
        return rolPoolService.listar(poolId, usuarioId, soloActivos);
    }

    @GetMapping("/permisos")
    @Operation(summary = "Listar permisos", description = "Retorna el catálogo de permisos disponible para roles de pool")
    public List<PermisoResponse> listarPermisos(@RequestParam UUID poolId, @RequestParam UUID usuarioId) {
        return rolPoolService.listarPermisos(poolId, usuarioId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear rol de pool", description = "Crea un rol específico del pool con permisos configurables")
    public RolPoolResponse crear(@Valid @RequestBody CrearRolPoolRequest request) {
        return rolPoolService.crear(request);
    }

    @PatchMapping("/{rolPoolId}")
    @Operation(summary = "Editar rol de pool", description = "Actualiza nombre o descripción del rol")
    public RolPoolResponse editar(@PathVariable UUID rolPoolId,
                                  @Valid @RequestBody EditarRolPoolRequest request) {
        return rolPoolService.editar(rolPoolId, request);
    }

    @DeleteMapping("/{rolPoolId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar rol de pool", description = "Realiza soft delete de un rol de pool sin usuarios asignados")
    public void eliminar(@PathVariable UUID rolPoolId,
                         @Valid @RequestBody EliminarRolPoolRequest request) {
        rolPoolService.eliminar(rolPoolId, request);
    }

    @PatchMapping("/{rolPoolId}/permisos")
    @Operation(summary = "Actualizar permisos del rol", description = "Reemplaza el conjunto de permisos del rol indicado")
    public RolPoolResponse actualizarPermisos(@PathVariable UUID rolPoolId,
                                              @Valid @RequestBody ActualizarPermisosRolPoolRequest request) {
        return rolPoolService.actualizarPermisos(rolPoolId, request);
    }

    @PostMapping("/{rolPoolId}/usuarios")
    @Operation(summary = "Asignar usuario al rol", description = "Asigna un usuario existente a un rol dentro del pool")
    public RolPoolResponse asignarUsuario(@PathVariable UUID rolPoolId,
                                          @Valid @RequestBody AsignarRolPoolRequest request) {
        return rolPoolService.asignarUsuario(rolPoolId, request);
    }
}
