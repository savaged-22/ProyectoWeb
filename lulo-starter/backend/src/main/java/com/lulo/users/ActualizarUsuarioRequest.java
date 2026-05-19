package com.lulo.users;

import java.util.UUID;

import lombok.Data;

/**
 * Edición de un usuario existente. Ambos campos son opcionales:
 * se actualiza solo lo que venga informado.
 */
@Data
public class ActualizarUsuarioRequest {

    /** Si viene, reemplaza el rol del usuario por este. */
    private UUID rolPoolId;

    /** Si viene, cambia el estado: activo | suspendido | inactivo | pendiente. */
    private String estado;
}
