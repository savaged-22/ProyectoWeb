package com.lulo.company;

import com.lulo.company.dto.RegistroEmpresaRequest;
import com.lulo.company.dto.RegistroEmpresaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}
