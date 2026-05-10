package com.lulo.company.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/** Vista resumida de una empresa para listados / portfolio. */
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
