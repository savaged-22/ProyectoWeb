package com.lulo.company.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class EmpresaListItemResponse {
    private UUID id;
    private String nombre;
    private String nit;
    private String emailContacto;
    private LocalDateTime createdAt;
    private long totalUsuarios;
    private long totalProcesos;
    private long totalPools;
}
