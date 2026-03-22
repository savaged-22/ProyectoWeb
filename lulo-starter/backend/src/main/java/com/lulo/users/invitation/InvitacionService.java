package com.lulo.users.invitation;

import com.lulo.common.exception.ApiException;
import com.lulo.company.EmpresaRepository;
import com.lulo.rbac.RolPool;
import com.lulo.rbac.RolPoolRepository;
import com.lulo.rbac.UsuarioRolPool;
import com.lulo.rbac.UsuarioRolPoolId;
import com.lulo.rbac.UsuarioRolPoolRepository;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import com.lulo.users.invitation.dto.AceptarInvitacionRequest;
import com.lulo.users.invitation.dto.AceptarInvitacionResponse;
import com.lulo.users.invitation.dto.InvitarUsuarioRequest;
import com.lulo.users.invitation.dto.InvitarUsuarioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitacionService {

    private static final int HORAS_EXPIRACION = 72;

    private final InvitacionUsuarioRepository invitacionRepository;
    private final EmpresaRepository           empresaRepository;
    private final UsuarioRepository           usuarioRepository;
    private final RolPoolRepository           rolPoolRepository;
    private final UsuarioRolPoolRepository    usuarioRolPoolRepository;

    // ── Invitar usuario ───────────────────────────────────────────────────────

    @Transactional
    public InvitarUsuarioResponse invitar(InvitarUsuarioRequest request) {

        var empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ApiException("Empresa no encontrada", HttpStatus.NOT_FOUND));

        var rolPool = rolPoolRepository.findById(request.getRolPoolId())
                .orElseThrow(() -> new ApiException("Rol no encontrado", HttpStatus.NOT_FOUND));

        // El rol debe pertenecer a la empresa
        if (!rolPool.getPool().getEmpresa().getId().equals(request.getEmpresaId())) {
            throw new ApiException("El rol no pertenece a esta empresa", HttpStatus.FORBIDDEN);
        }

        var invitadoPor = usuarioRepository.findById(request.getInvitadoPorId())
                .orElseThrow(() -> new ApiException("Usuario que invita no encontrado", HttpStatus.NOT_FOUND));

        // No puede haber una invitación pendiente para el mismo correo en la misma empresa
        if (invitacionRepository.existsByEmailAndEstado(request.getEmailInvitado(), "pendiente")) {
            throw new ApiException(
                    "Ya existe una invitación pendiente para: " + request.getEmailInvitado(),
                    HttpStatus.CONFLICT);
        }

        // El correo no puede pertenecer a un usuario ya registrado
        if (usuarioRepository.existsByEmail(request.getEmailInvitado())) {
            throw new ApiException(
                    "El correo ya está registrado en el sistema: " + request.getEmailInvitado(),
                    HttpStatus.CONFLICT);
        }

        // TODO: hashear el token cuando se implemente la capa de seguridad (HU-Auth)
        String token     = UUID.randomUUID().toString();
        LocalDateTime expira = LocalDateTime.now().plusHours(HORAS_EXPIRACION);

        InvitacionUsuario invitacion = new InvitacionUsuario();
        invitacion.setEmpresa(empresa);
        invitacion.setRolPool(rolPool);
        invitacion.setCreatedByUser(invitadoPor);
        invitacion.setEmail(request.getEmailInvitado());
        invitacion.setTokenHash(token);
        invitacion.setEstado("pendiente");
        invitacion.setExpiresAt(expira);
        invitacion = invitacionRepository.save(invitacion);

        return InvitarUsuarioResponse.builder()
                .invitacionId(invitacion.getId())
                .emailInvitado(invitacion.getEmail())
                .rolAsignado(rolPool.getNombre())
                .token(token)   // TODO: en producción va por email, no en la respuesta
                .expiresAt(expira)
                .mensaje("Invitación creada. Comparte el token con el usuario para que complete su registro.")
                .build();
    }

    // ── Aceptar invitación ────────────────────────────────────────────────────

    @Transactional
    public AceptarInvitacionResponse aceptar(String token, AceptarInvitacionRequest request) {

        InvitacionUsuario invitacion = invitacionRepository.findByTokenHash(token)
                .orElseThrow(() -> new ApiException("Token de invitación inválido", HttpStatus.NOT_FOUND));

        if (!"pendiente".equals(invitacion.getEstado())) {
            throw new ApiException("La invitación ya fue utilizada o cancelada", HttpStatus.CONFLICT);
        }

        if (invitacion.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitacion.setEstado("expirada");
            invitacionRepository.save(invitacion);
            throw new ApiException("La invitación ha expirado", HttpStatus.GONE);
        }

        if (usuarioRepository.existsByEmail(invitacion.getEmail())) {
            throw new ApiException("El correo ya está registrado", HttpStatus.CONFLICT);
        }

        // ── Crear usuario ─────────────────────────────────────────────────────
        Usuario usuario = new Usuario();
        usuario.setEmpresa(invitacion.getEmpresa());
        usuario.setEmail(invitacion.getEmail());
        // TODO: reemplazar por BCrypt cuando se implemente la capa de seguridad (HU-Auth)
        usuario.setPasswordHash(request.getPassword());
        usuario.setEstado("activo");
        usuario = usuarioRepository.save(usuario);

        // ── Asignar rol en el pool ────────────────────────────────────────────
        RolPool rolPool = invitacion.getRolPool();

        UsuarioRolPool asignacion = new UsuarioRolPool();
        asignacion.setId(new UsuarioRolPoolId(usuario.getId(), rolPool.getId()));
        asignacion.setUsuario(usuario);
        asignacion.setRolPool(rolPool);
        usuarioRolPoolRepository.save(asignacion);

        // ── Marcar invitación como aceptada ───────────────────────────────────
        invitacion.setEstado("aceptada");
        invitacionRepository.save(invitacion);

        return AceptarInvitacionResponse.builder()
                .usuarioId(usuario.getId())
                .email(usuario.getEmail())
                .empresaNombre(invitacion.getEmpresa().getNombre())
                .rolAsignado(rolPool.getNombre())
                .mensaje("Registro completado exitosamente")
                .build();
    }
}
