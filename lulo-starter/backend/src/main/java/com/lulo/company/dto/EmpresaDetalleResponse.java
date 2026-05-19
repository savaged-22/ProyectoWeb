package com.lulo.company.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class EmpresaDetalleResponse {
    private UUID id;
    private String nombre;
    private String nit;
    private String emailContacto;
    private LocalDateTime createdAt;
    private long totalUsuarios;
    private long totalProcesos;
    private long totalPools;
    private long totalRolesPool;
    private List<UsuarioBasicoResponse> usuarios;
}
