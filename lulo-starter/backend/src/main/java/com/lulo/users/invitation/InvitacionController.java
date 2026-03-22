package com.lulo.users.invitation;

import com.lulo.users.invitation.dto.AceptarInvitacionRequest;
import com.lulo.users.invitation.dto.AceptarInvitacionResponse;
import com.lulo.users.invitation.dto.InvitarUsuarioRequest;
import com.lulo.users.invitation.dto.InvitarUsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitaciones")
@RequiredArgsConstructor
@Tag(name = "Invitaciones", description = "Gestión de invitaciones de usuarios a la empresa")
public class InvitacionController {

    private final InvitacionService invitacionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Invitar usuario",
            description = "El administrador crea una invitación para un correo con un rol asignado")
    public InvitarUsuarioResponse invitar(@Valid @RequestBody InvitarUsuarioRequest request) {
        return invitacionService.invitar(request);
    }

    @PostMapping("/{token}/aceptar")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Aceptar invitación",
            description = "El usuario invitado completa su registro usando el token recibido")
    public AceptarInvitacionResponse aceptar(
            @PathVariable String token,
            @Valid @RequestBody AceptarInvitacionRequest request) {
        return invitacionService.aceptar(token, request);
    }
}
