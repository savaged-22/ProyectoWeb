package com.lulo.company;

import com.lulo.company.dto.EmpresaDetalleResponse;
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

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@Tag(name = "Empresa", description = "Registro y gestión de empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    @Operation(summary = "Listar empresas", description = "Retorna todas las empresas con sus conteos")
    public List<EmpresaListItemResponse> listar() {
        return empresaService.listar();
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar empresa", description = "Crea una empresa con su usuario administrador y pool por defecto")
    public RegistroEmpresaResponse registrar(@Valid @RequestBody RegistroEmpresaRequest request) {
        return empresaService.registrar(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener empresa", description = "Retorna los datos de una empresa con su lista de usuarios")
    public EmpresaDetalleResponse obtener(@PathVariable java.util.UUID id) {
        return empresaService.obtener(id);
    }
}
