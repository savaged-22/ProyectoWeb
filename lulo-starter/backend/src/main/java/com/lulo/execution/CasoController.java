package com.lulo.execution;

import com.lulo.execution.dto.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/casos")
@RequiredArgsConstructor
public class CasoController {

    private final CasoService casoService;

    @GetMapping("/dashboard")
    public DashboardResponse getDashboard(@RequestParam UUID empresaId) {
        return casoService.getDashboard(empresaId);
    }
    
    @PostMapping("/iniciar")
    public void iniciarCaso(@RequestParam UUID procesoId, @RequestParam UUID usuarioId) {
        casoService.iniciarCaso(procesoId, usuarioId);
    }
}
