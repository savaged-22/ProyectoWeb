package com.lulo.company;

import com.lulo.company.dto.RegistroEmpresaRequest;
import com.lulo.company.dto.RegistroEmpresaResponse;
import com.lulo.common.exception.ApiException;
import com.lulo.pool.Pool;
import com.lulo.pool.PoolRepository;
import com.lulo.rbac.Permiso;
import com.lulo.rbac.PermisoRepository;
import com.lulo.rbac.RolPool;
import com.lulo.rbac.RolPoolRepository;
import com.lulo.rbac.UsuarioRolPool;
import com.lulo.rbac.UsuarioRolPoolId;
import com.lulo.rbac.UsuarioRolPoolRepository;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class EmpresaService {

    private static final String POOL_DEFAULT_NOMBRE = "Principal";
    private static final String ROL_ADMIN_NOMBRE    = "Administrador";

    @Autowired
    private EmpresaRepository        empresaRepository;
    @Autowired
    private UsuarioRepository        usuarioRepository;
    @Autowired
    private PoolRepository           poolRepository;
    @Autowired
    private PermisoRepository        permisoRepository;
    @Autowired
    private RolPoolRepository        rolPoolRepository;
    @Autowired
    private UsuarioRolPoolRepository usuarioRolPoolRepository;

    @Transactional
    public RegistroEmpresaResponse registrar(RegistroEmpresaRequest request) {

        // ── Validaciones de unicidad ──────────────────────────────────────────
        if (empresaRepository.existsByNit(request.getNit())) {
            throw new ApiException(
                    "Ya existe una empresa registrada con el NIT: " + request.getNit(),
                    HttpStatus.CONFLICT);
        }

        if (usuarioRepository.existsByEmail(request.getEmailAdmin())) {
            throw new ApiException(
                    "El correo de administrador ya está en uso: " + request.getEmailAdmin(),
                    HttpStatus.CONFLICT);
        }

        // ── 1. Crear Empresa ──────────────────────────────────────────────────
        Empresa empresa = new Empresa();
        empresa.setNombre(request.getNombreEmpresa());
        empresa.setNit(request.getNit());
        empresa.setEmailContacto(request.getEmailContacto());
        empresa = empresaRepository.save(empresa);

        // ── 2. Crear Usuario administrador inicial ────────────────────────────
        Usuario admin = new Usuario();
        admin.setEmpresa(empresa);
        admin.setEmail(request.getEmailAdmin());
        // TODO: reemplazar por BCrypt cuando se implemente la capa de seguridad (HU-Auth)
        admin.setPasswordHash(request.getPassword());
        admin.setEstado("activo");
        admin = usuarioRepository.save(admin);

        // ── 3. Crear Pool por defecto ─────────────────────────────────────────
        Pool pool = new Pool();
        pool.setEmpresa(empresa);
        pool.setNombre(POOL_DEFAULT_NOMBRE);
        pool = poolRepository.save(pool);

        // ── 4. Crear Rol propietario con todos los permisos ───────────────────
        List<Permiso> todosLosPermisos = permisoRepository.findAll();

        RolPool rolAdmin = new RolPool();
        rolAdmin.setPool(pool);
        rolAdmin.setNombre(ROL_ADMIN_NOMBRE);
        rolAdmin.setDescripcion("Rol con acceso completo al pool");
        rolAdmin.setActivo(true);
        rolAdmin.setEsPropietario(true);
        rolAdmin.setPermisos(new HashSet<>(todosLosPermisos));
        rolAdmin = rolPoolRepository.save(rolAdmin);

        // ── 5. Asignar usuario → rol en el pool ──────────────────────────────
        UsuarioRolPool asignacion = new UsuarioRolPool();
        asignacion.setId(new UsuarioRolPoolId(admin.getId(), rolAdmin.getId()));
        asignacion.setUsuario(admin);
        asignacion.setRolPool(rolAdmin);
        usuarioRolPoolRepository.save(asignacion);

        // ── Respuesta ─────────────────────────────────────────────────────────
        return RegistroEmpresaResponse.builder()
                .empresaId(empresa.getId())
                .empresaNombre(empresa.getNombre())
                .usuarioId(admin.getId())
                .emailAdmin(admin.getEmail())
                .poolDefault(pool.getNombre())
                .mensaje("Empresa registrada exitosamente")
                .build();
    }
}
