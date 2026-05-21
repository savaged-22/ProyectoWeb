package com.lulo.company;

import com.lulo.company.dto.RegistroEmpresaRequest;
import com.lulo.company.dto.RegistroEmpresaResponse;
import com.lulo.company.dto.EmpresaDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@Tag(name = "Empresa", description = "Registro y gestión de empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar empresa", description = "Crea una empresa con su usuario administrador y pool por defecto")
    public RegistroEmpresaResponse registrar(@Valid @RequestBody RegistroEmpresaRequest request) {
        return empresaService.registrar(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener empresa por ID", description = "Retorna los detalles de la empresa especificada por ID")
    public EmpresaDetailResponse obtenerPorId(@PathVariable UUID id) {
        return empresaService.obtenerDetallePorId(id);
    }
}
