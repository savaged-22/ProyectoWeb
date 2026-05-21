package com.lulo.execution;

import com.lulo.execution.dto.*;
import com.lulo.process.Proceso;
import com.lulo.process.ProcesoRepository;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CasoService {

    private final CasoRepository casoRepository;
    private final CasoActividadRepository casoActividadRepository;
    private final CasoLogRepository casoLogRepository;
    private final ProcesoRepository procesoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(UUID empresaId) {
        List<Caso> casos = casoRepository.findByProceso_Empresa_IdOrderByFechaInicioDesc(empresaId);
        
        long totalActivos = casos.stream().filter(c -> c.getEstado().equals("EN_PROGRESO") || c.getEstado().equals("PENDIENTE")).count();
        long errores = casos.stream().filter(c -> c.getEstado().equals("ERROR")).count();
        
        String tasaError = casos.isEmpty() ? "0.00%" : String.format("%.2f%%", (errores * 100.0) / casos.size());
        
        List<CasoResumen> casosResumen = casos.stream().limit(50).map(c -> {
            // Find current activity
            List<CasoActividad> acts = casoActividadRepository.findByCasoIdOrderByFechaInicioDesc(c.getId());
            String currentActivity = acts.isEmpty() ? "Inicio" : acts.get(0).getNodo().getLabel();
            if (currentActivity == null || currentActivity.isEmpty()) {
                currentActivity = "Nodo Anónimo";
            }
            
            return CasoResumen.builder()
                .id(c.getId())
                .procesoNombre(c.getProceso().getNombre())
                .estado(c.getEstado())
                .actividadActual(currentActivity)
                .fechaInicio(c.getFechaInicio())
                .build();
        }).toList();
        
        List<Proceso> procesos = procesoRepository.findByEmpresaId(empresaId);
        List<ProcesoMetrica> metricas = procesos.stream().map(p -> {
            long activos = casos.stream().filter(c -> c.getProceso().getId().equals(p.getId()) && !c.getEstado().equals("COMPLETADO")).count();
            return ProcesoMetrica.builder()
                .nombreProceso(p.getNombre())
                .casosActivos((int) activos)
                .tiempoPromedio("N/A")
                .estadoProceso(p.getEstado())
                .fechaCreacion(p.getCreatedAt())
                .build();
        }).toList();
        
        List<CasoLog> logEntities = casoLogRepository.findByEmpresaIdOrderByFechaDesc(empresaId);
        List<LogEntry> logs = logEntities.stream().limit(20).map(l -> {
            String referencia = "Sistema";
            if (l.getCaso() != null) {
                referencia = l.getCaso().getId().toString();
            } else if (l.getProceso() != null) {
                referencia = l.getProceso().getNombre();
            }
            
            return LogEntry.builder()
                .tiempo(String.format("%02d:%02d:%02d", l.getFecha().getHour(), l.getFecha().getMinute(), l.getFecha().getSecond()))
                .nivel(l.getNivel())
                .claseCss(l.getNivel().equalsIgnoreCase("ERROR") ? "err" : "info")
                .mensaje(l.getMensaje())
                .isErrorBlock(l.getNivel().equalsIgnoreCase("ERROR"))
                .referencia(referencia)
                .build();
        }).toList();
        
        return DashboardResponse.builder()
                .totalActivos((int) totalActivos)
                .tareasPendientes(0) // TODO: Contar CasoActividad en PENDIENTE
                .procesosInstanciadosHoy(casos.size()) // Simplificación para demo
                .tasaError(tasaError)
                .casosActivos(casosResumen)
                .metricasProcesos(metricas)
                .ultimosLogs(logs)
                .build();
    }

    @Transactional
    public Caso iniciarCaso(UUID procesoId, UUID usuarioId) {
        Proceso proceso = procesoRepository.findById(procesoId)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Caso caso = new Caso();
        caso.setProceso(proceso);
        caso.setIniciadoPor(usuario);
        caso.setEstado("EN_PROGRESO");
        caso = casoRepository.save(caso);

        CasoLog log = new CasoLog();
        log.setCaso(caso);
        log.setNivel("INFO");
        log.setMensaje("Caso iniciado para el proceso: " + proceso.getNombre());
        casoLogRepository.save(log);

        return caso;
    }
}
