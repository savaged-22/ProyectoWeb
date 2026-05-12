package com.lulo.users.invitation;

import com.lulo.users.invitation.dto.AceptarInvitacionRequest;
import com.lulo.users.invitation.dto.AceptarInvitacionResponse;
import com.lulo.users.invitation.dto.InvitarUsuarioRequest;
import com.lulo.users.invitation.dto.InvitarUsuarioResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvitacionControllerTest {

    @Mock
    private InvitacionService invitacionService;

    @InjectMocks
    private InvitacionController invitacionController;

    @Test
    void invitar_debeRetornarRespuesta() {
        InvitarUsuarioRequest request = new InvitarUsuarioRequest();

        // Solo usamos campos que seguramente existen en el DTO
        InvitarUsuarioResponse response = InvitarUsuarioResponse.builder()
                .mensaje("Invitación enviada exitosamente")
                .build();

        when(invitacionService.invitar(request))
                .thenReturn(response);

        InvitarUsuarioResponse resultado =
                invitacionController.invitar(request);

        assertNotNull(resultado);
        assertEquals(
                "Invitación enviada exitosamente",
                resultado.getMensaje()
        );

        verify(invitacionService).invitar(request);
    }

    @Test
    void aceptar_debeRetornarRespuesta() {
        String token = "token-123";
        AceptarInvitacionRequest request = new AceptarInvitacionRequest();

        // Solo usamos campos que seguramente existen en el DTO
        AceptarInvitacionResponse response =
                AceptarInvitacionResponse.builder()
                        .mensaje("Invitación aceptada exitosamente")
                        .build();

        when(invitacionService.aceptar(token, request))
                .thenReturn(response);

        AceptarInvitacionResponse resultado =
                invitacionController.aceptar(token, request);

        assertNotNull(resultado);
        assertEquals(
                "Invitación aceptada exitosamente",
                resultado.getMensaje()
        );

        verify(invitacionService).aceptar(token, request);
    }
}