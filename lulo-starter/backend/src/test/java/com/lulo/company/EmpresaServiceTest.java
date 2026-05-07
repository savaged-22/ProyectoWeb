package com.lulo.company;

import com.lulo.common.exception.ApiException;
import com.lulo.company.dto.RegistroEmpresaRequest;
import com.lulo.company.dto.RegistroEmpresaResponse;
import com.lulo.pool.Pool;
import com.lulo.pool.PoolRepository;
import com.lulo.rbac.PermisoRepository;
import com.lulo.rbac.RolPool;
import com.lulo.rbac.RolPoolRepository;
import com.lulo.rbac.UsuarioRolPool;
import com.lulo.rbac.UsuarioRolPoolRepository;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock private EmpresaRepository        empresaRepository;
    @Mock private UsuarioRepository        usuarioRepository;
    @Mock private PoolRepository           poolRepository;
    @Mock private PermisoRepository        permisoRepository;
    @Mock private RolPoolRepository        rolPoolRepository;
    @Mock private UsuarioRolPoolRepository usuarioRolPoolRepository;

    @InjectMocks private EmpresaService service;

    private RegistroEmpresaRequest buildRequest() {
        return new RegistroEmpresaRequest(
                "Empresa Demo", "900111222-1", "contacto@demo.com",
                "admin@demo.com", "segura123"
        );
    }

    @Test
    void registrar_exitoso_creaEmpresaConAdminYPoolDefault() {
        when(empresaRepository.existsByNit(any())).thenReturn(false);
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        when(permisoRepository.findAll()).thenReturn(List.of());

        Empresa empresa = new Empresa();
        empresa.setId(1);
        empresa.setNombre("Empresa Demo");
        empresa.setNit("900111222-1");
        when(empresaRepository.save(any())).thenReturn(empresa);

        Usuario admin = new Usuario();
        admin.setId(10);
        admin.setEmail("admin@demo.com");
        admin.setEmpresa(empresa);
        when(usuarioRepository.save(any())).thenReturn(admin);

        Pool pool = new Pool();
        pool.setId(5);
        pool.setNombre("Principal");
        pool.setEmpresa(empresa);
        when(poolRepository.save(any())).thenReturn(pool);

        RolPool rolAdmin = new RolPool();
        rolAdmin.setId(3);
        rolAdmin.setNombre("Administrador");
        when(rolPoolRepository.save(any())).thenReturn(rolAdmin);

        when(usuarioRolPoolRepository.save(any())).thenReturn(new UsuarioRolPool());

        RegistroEmpresaResponse response = service.registrar(buildRequest());

        assertThat(response.getEmpresaId()).isEqualTo(1);
        assertThat(response.getEmailAdmin()).isEqualTo("admin@demo.com");
        assertThat(response.getPoolDefault()).isEqualTo("Principal");
        assertThat(response.getMensaje()).isEqualTo("Empresa registrada exitosamente");
    }

    @Test
    void registrar_nitDuplicado_throwsConflict() {
        when(empresaRepository.existsByNit("900111222-1")).thenReturn(true);

        RegistroEmpresaRequest request1 = buildRequest();
        assertThatThrownBy(() -> service.registrar(request1))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void registrar_emailAdminDuplicado_throwsConflict() {
        when(empresaRepository.existsByNit(any())).thenReturn(false);
        when(usuarioRepository.existsByEmail("admin@demo.com")).thenReturn(true);

        RegistroEmpresaRequest request2 = buildRequest();
        assertThatThrownBy(() -> service.registrar(request2))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }
}
