package com.lulo.company.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UsuarioBasicoResponse {
    private UUID id;
    private String email;
    private String estado;
    private String rolPrincipal;
    private LocalDateTime createdAt;
}
