package com.lulo.company;

import com.lulo.company.dto.EmpresaDetailResponse;
import com.lulo.company.dto.EmpresaListItemResponse;
import com.lulo.company.dto.RegistroEmpresaRequest;
import com.lulo.company.dto.RegistroEmpresaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@Tag(name = "Empresa", description = "Registro y gestión de empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

    private final EmpresaService empresaService;

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar empresa",
            description = "Crea una empresa con su usuario administrador y pool por defecto")
    public RegistroEmpresaResponse registrar(@Valid @RequestBody RegistroEmpresaRequest request) {
        return empresaService.registrar(request);
    }

    @GetMapping
    @Operation(summary = "Listar empresas",
            description = "Devuelve todas las empresas con métricas básicas (vista SUPERADMIN)")
    public List<EmpresaListItemResponse> listar() {
        return empresaService.listar();
    }

    @GetMapping("/{empresaId}")
    @Operation(summary = "Detalle de empresa",
            description = "Devuelve la empresa con sus métricas y user registry")
    public EmpresaDetailResponse detalle(@PathVariable UUID empresaId) {
        return empresaService.obtenerDetalle(empresaId);
    }
}
