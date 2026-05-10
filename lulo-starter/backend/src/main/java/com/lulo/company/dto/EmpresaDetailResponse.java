package com.lulo.company.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/** Detalle completo de una empresa con su user registry. */
@Getter
@Builder
public class EmpresaDetailResponse {

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
