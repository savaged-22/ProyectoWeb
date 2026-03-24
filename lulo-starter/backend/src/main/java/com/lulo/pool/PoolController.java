package com.lulo.pool;

import com.lulo.pool.dto.CrearPoolRequest;
import com.lulo.pool.dto.EditarPoolRequest;
import com.lulo.pool.dto.PoolResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
@Tag(name = "Pools", description = "Gestión de pools por empresa")
public class PoolController {

    private final PoolService poolService;

    @GetMapping
    @Operation(summary = "Listar pools", description = "Lista los pools de una empresa")
    public List<PoolResponse> listar(@RequestParam Integer empresaId, @RequestParam Integer usuarioId) {
        return poolService.listar(empresaId, usuarioId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear pool", description = "Crea un pool en la empresa y asigna al creador como administrador")
    public PoolResponse crear(@Valid @RequestBody CrearPoolRequest request) {
        return poolService.crear(request);
    }

    @PatchMapping("/{poolId}")
    @Operation(summary = "Editar pool", description = "Actualiza nombre o configuración del pool")
    public PoolResponse editar(@PathVariable Integer poolId, @Valid @RequestBody EditarPoolRequest request) {
        return poolService.editar(poolId, request);
    }
}
