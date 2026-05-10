package com.lulo.company.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/** Vista resumida de un usuario asociado a una empresa. */
@Getter
@Builder
public class UsuarioBasicoResponse {

    private UUID id;
    private String email;
    private String estado;
    private String rolPrincipal;
    private LocalDateTime createdAt;
}
