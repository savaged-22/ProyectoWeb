package com.lulo.rbac;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class UsuarioRolPoolId implements Serializable {

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "rol_pool_id")
    private Integer rolPoolId;
}
