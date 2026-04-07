package com.lulo.messaging.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmarRecepcionRequest {

    @NotNull
    private Integer procesoDestinoId;
}
