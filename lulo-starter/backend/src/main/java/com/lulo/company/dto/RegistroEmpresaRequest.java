package com.lulo.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEmpresaRequest {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    private String nombreEmpresa;

    @NotBlank(message = "El NIT es obligatorio")
    private String nit;

    @NotBlank(message = "El correo de contacto es obligatorio")
    @Email(message = "El correo de contacto no es válido")
    private String emailContacto;

    @NotBlank(message = "El correo del administrador es obligatorio")
    @Email(message = "El correo del administrador no es válido")
    private String emailAdmin;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}
