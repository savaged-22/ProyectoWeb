package com.lulo.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CrearUsuarioDirectoRequest {
    private UUID   empresaId;
    private UUID   creadoPorId;
    private UUID   rolPoolId;
    private String email;
    private String password;
}
