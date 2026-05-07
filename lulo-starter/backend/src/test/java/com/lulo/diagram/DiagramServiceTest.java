package com.lulo.diagram;

import com.lulo.audit.AuditService;
import com.lulo.common.exception.ApiException;
import com.lulo.company.Empresa;
import com.lulo.diagram.activity.Actividad;
import com.lulo.diagram.activity.ActividadRepository;
import com.lulo.diagram.activity.dto.CrearActividadRequest;
import com.lulo.diagram.activity.dto.EditarActividadRequest;
import com.lulo.diagram.activity.dto.EliminarActividadRequest;
import com.lulo.diagram.arc.Arco;
import com.lulo.diagram.arc.ArcoRepository;
import com.lulo.diagram.arc.dto.ArcoResponse;
import com.lulo.diagram.arc.dto.CrearArcoRequest;
import com.lulo.diagram.arc.dto.EditarArcoRequest;
import com.lulo.diagram.arc.dto.EliminarArcoRequest;
import com.lulo.diagram.gateway.Gateway;
import com.lulo.diagram.gateway.GatewayRepository;
import com.lulo.diagram.gateway.dto.CrearGatewayRequest;
import com.lulo.diagram.gateway.dto.EditarGatewayRequest;
import com.lulo.diagram.gateway.dto.EliminarGatewayRequest;
import com.lulo.diagram.lane.Lane;
import com.lulo.diagram.lane.LaneRepository;
import com.lulo.diagram.lane.dto.LaneResponse;
import com.lulo.diagram.node.Nodo;
import com.lulo.diagram.node.NodoRepository;
import com.lulo.diagram.node.dto.NodoResponse;
import com.lulo.process.Proceso;
import com.lulo.process.ProcesoRepository;
import com.lulo.rbac.RolProceso;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiagramServiceTest {

    @Mock private ProcesoRepository   procesoRepository;
    @Mock private LaneRepository      laneRepository;
    @Mock private NodoRepository      nodoRepository;
    @Mock private ActividadRepository actividadRepository;
    @Mock private GatewayRepository   gatewayRepository;
    @Mock private ArcoRepository      arcoRepository;
    @Mock private UsuarioRepository   usuarioRepository;
    @Mock private AuditService        auditService;

    @InjectMocks private DiagramService service;

    private Empresa empresa;
    private Proceso proceso;
    private Lane lane;
    private Usuario usuario;
    private Actividad actividad;
    private Gateway gateway;
    private Arco arco;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1);
        empresa.setNombre("Demo SA");

        proceso = new Proceso();
        proceso.setId(10);
        proceso.setNombre("Proceso Alfa");
        proceso.setActivo(true);
        proceso.setEmpresa(empresa);

        lane = new Lane();
        lane.setId(5);
        lane.setNombre("Lane 1");
        lane.setProceso(proceso);

        usuario = new Usuario();
        usuario.setId(3);
        usuario.setEmail("user@demo.com");
        usuario.setEmpresa(empresa);

        actividad = new Actividad();
        actividad.setId(20);
        actividad.setLabel("Actividad A");
        actividad.setProceso(proceso);
        actividad.setLane(lane);
        actividad.setTipoActividad("tarea");

        gateway = new Gateway();
        gateway.setId(30);
        gateway.setLabel("GW exclusivo");
        gateway.setProceso(proceso);
        gateway.setTipoGateway("exclusivo");

        arco = new Arco();
        arco.setId(40);
        arco.setProceso(proceso);
        arco.setFromNodo(actividad);
        arco.setToNodo(gateway);
        arco.setActivo(true);
    }

    // ─── crearActividad ──────────────────────────────────────────────────────

    @Test
    void crearActividad_sinLane_exitoso() {
        // CrearActividadRequest(creadoPorId, laneId, label, tipoActividad, posX, posY, propsJson)
        CrearActividadRequest req = new CrearActividadRequest(3, null, "Nueva", "tarea", 1.0f, 2.0f, null);
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(actividadRepository.save(any())).thenReturn(actividad);

        NodoResponse resp = service.crearActividad(10, req);

        assertThat(resp.getId()).isEqualTo(20);
        assertThat(resp.getTipo()).isEqualTo("actividad");
    }

    @Test
    void crearActividad_conLane_exitoso() {
        CrearActividadRequest req = new CrearActividadRequest(3, 5, "Nueva", "tarea", 1.0f, 2.0f, null);
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(laneRepository.findById(5)).thenReturn(Optional.of(lane));
        when(actividadRepository.save(any())).thenReturn(actividad);

        NodoResponse resp = service.crearActividad(10, req);

        assertThat(resp.getLaneId()).isEqualTo(5);
    }

    @Test
    void crearActividad_procesoNoEncontrado_throwsNotFound() {
        when(procesoRepository.findByIdAndActivoTrue(99)).thenReturn(Optional.empty());

        var req = new CrearActividadRequest(3, null, "X", "tarea", 0f, 0f, null);
        assertThatThrownBy(() -> service.crearActividad(99, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void crearActividad_laneNoEncontrada_throwsNotFound() {
        CrearActividadRequest req = new CrearActividadRequest(3, 99, "X", "tarea", 0f, 0f, null);
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(laneRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.crearActividad(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void crearActividad_laneDeProcesoDiferente_throwsForbidden() {
        Proceso otroProceso = new Proceso();
        otroProceso.setId(99);
        Lane laneAjena = new Lane();
        laneAjena.setId(5);
        laneAjena.setProceso(otroProceso);

        CrearActividadRequest req = new CrearActividadRequest(3, 5, "X", "tarea", 0f, 0f, null);
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(laneRepository.findById(5)).thenReturn(Optional.of(laneAjena));

        assertThatThrownBy(() -> service.crearActividad(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    // ─── crearGateway ────────────────────────────────────────────────────────

    @Test
    void crearGateway_exitoso() {
        CrearGatewayRequest req = new CrearGatewayRequest(3, null, "GW", 0f, 0f, "exclusivo", null);
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(gatewayRepository.save(any())).thenReturn(gateway);

        NodoResponse resp = service.crearGateway(10, req);

        assertThat(resp.getTipo()).isEqualTo("gateway");
        assertThat(resp.getTipoGateway()).isEqualTo("exclusivo");
        verify(auditService).registrar(any(), any(), eq("GATEWAY"), any(), eq("CREAR"), eq(null), any());
    }

    @Test
    void crearGateway_conLane_exitoso() {
        CrearGatewayRequest req = new CrearGatewayRequest(3, 5, "GW", 0f, 0f, "paralelo", null);
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(laneRepository.findById(5)).thenReturn(Optional.of(lane));
        when(gatewayRepository.save(any())).thenReturn(gateway);

        NodoResponse resp = service.crearGateway(10, req);

        assertThat(resp).isNotNull();
    }

    @Test
    void crearGateway_procesoNoEncontrado_throwsNotFound() {
        when(procesoRepository.findByIdAndActivoTrue(99)).thenReturn(Optional.empty());

        var req = new CrearGatewayRequest(3, null, "GW", 0f, 0f, "exclusivo", null);
        assertThatThrownBy(() -> service.crearGateway(99, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void crearGateway_usuarioNoEncontrado_throwsNotFound() {
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        var req = new CrearGatewayRequest(99, null, "GW", 0f, 0f, "exclusivo", null);
        assertThatThrownBy(() -> service.crearGateway(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void crearGateway_usuarioDeOtraEmpresa_throwsForbidden() {
        Empresa otra = new Empresa(); otra.setId(99);
        Usuario ajeno = new Usuario(); ajeno.setId(3); ajeno.setEmpresa(otra);
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(ajeno));

        var req = new CrearGatewayRequest(3, null, "GW", 0f, 0f, "exclusivo", null);
        assertThatThrownBy(() -> service.crearGateway(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void crearGateway_laneDeOtroProceso_throwsForbidden() {
        Proceso otroProceso = new Proceso(); otroProceso.setId(99);
        Lane laneAjena = new Lane(); laneAjena.setId(5); laneAjena.setProceso(otroProceso);
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(laneRepository.findById(5)).thenReturn(Optional.of(laneAjena));

        var req = new CrearGatewayRequest(3, 5, "GW", 0f, 0f, "exclusivo", null);
        assertThatThrownBy(() -> service.crearGateway(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    // ─── editarActividad ─────────────────────────────────────────────────────

    @Test
    void editarActividad_exitoso() {
        // EditarActividadRequest(editadoPorId, label, tipoActividad, laneId, posX, posY, propsJson)
        EditarActividadRequest req = new EditarActividadRequest(3, "Nuevo Label", "decision", null, 5.0f, 6.0f, null);
        when(actividadRepository.findById(20)).thenReturn(Optional.of(actividad));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(actividadRepository.save(any())).thenReturn(actividad);

        NodoResponse resp = service.editarActividad(10, 20, req);

        assertThat(resp).isNotNull();
        verify(auditService).registrar(any(), any(), eq("ACTIVIDAD"), eq(20), eq("EDITAR"), any(), any());
    }

    @Test
    void editarActividad_conNuevaLane_actualizaLane() {
        Lane otraLane = new Lane(); otraLane.setId(7); otraLane.setProceso(proceso);
        EditarActividadRequest req = new EditarActividadRequest(3, null, null, 7, null, null, null);
        when(actividadRepository.findById(20)).thenReturn(Optional.of(actividad));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(laneRepository.findById(7)).thenReturn(Optional.of(otraLane));
        when(actividadRepository.save(any())).thenReturn(actividad);

        NodoResponse resp = service.editarActividad(10, 20, req);

        assertThat(resp).isNotNull();
    }

    @Test
    void editarActividad_noEncontrada_throwsNotFound() {
        when(actividadRepository.findById(99)).thenReturn(Optional.empty());

        var req = new EditarActividadRequest(3, null, null, null, null, null, null);
        assertThatThrownBy(() -> service.editarActividad(10, 99, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void editarActividad_deOtroProceso_throwsForbidden() {
        Proceso otroProceso = new Proceso(); otroProceso.setId(99); otroProceso.setEmpresa(empresa);
        actividad.setProceso(otroProceso);
        when(actividadRepository.findById(20)).thenReturn(Optional.of(actividad));

        var req = new EditarActividadRequest(3, null, null, null, null, null, null);
        assertThatThrownBy(() -> service.editarActividad(10, 20, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void editarActividad_usuarioNoEncontrado_throwsNotFound() {
        when(actividadRepository.findById(20)).thenReturn(Optional.of(actividad));
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        var req = new EditarActividadRequest(99, null, null, null, null, null, null);
        assertThatThrownBy(() -> service.editarActividad(10, 20, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void editarActividad_usuarioDeOtraEmpresa_throwsForbidden() {
        Empresa otra = new Empresa(); otra.setId(99);
        Usuario ajeno = new Usuario(); ajeno.setId(3); ajeno.setEmpresa(otra);
        when(actividadRepository.findById(20)).thenReturn(Optional.of(actividad));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(ajeno));

        var req = new EditarActividadRequest(3, null, null, null, null, null, null);
        assertThatThrownBy(() -> service.editarActividad(10, 20, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void editarActividad_laneDeOtroProceso_throwsForbidden() {
        Proceso otroProceso = new Proceso(); otroProceso.setId(99);
        Lane laneAjena = new Lane(); laneAjena.setId(7); laneAjena.setProceso(otroProceso);
        EditarActividadRequest req = new EditarActividadRequest(3, null, null, 7, null, null, null);
        when(actividadRepository.findById(20)).thenReturn(Optional.of(actividad));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(laneRepository.findById(7)).thenReturn(Optional.of(laneAjena));

        assertThatThrownBy(() -> service.editarActividad(10, 20, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    // ─── editarGateway ───────────────────────────────────────────────────────

    @Test
    void editarGateway_exitoso() {
        // EditarGatewayRequest(editadoPorId, laneId, label, posX, posY, tipoGateway, configJson)
        EditarGatewayRequest req = new EditarGatewayRequest(3, null, "Nuevo GW", 1.0f, 2.0f, "exclusivo", null);
        when(gatewayRepository.findByIdAndProcesoId(30, 10)).thenReturn(Optional.of(gateway));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(gatewayRepository.save(any())).thenReturn(gateway);

        NodoResponse resp = service.editarGateway(10, 30, req);

        assertThat(resp).isNotNull();
        verify(auditService).registrar(any(), any(), eq("GATEWAY"), eq(30), eq("EDITAR"), any(), any());
    }

    @Test
    void editarGateway_noEncontrado_throwsNotFound() {
        when(gatewayRepository.findByIdAndProcesoId(99, 10)).thenReturn(Optional.empty());

        var req = new EditarGatewayRequest(3, null, null, null, null, null, null);
        assertThatThrownBy(() -> service.editarGateway(10, 99, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void editarGateway_usuarioDeOtraEmpresa_throwsForbidden() {
        Empresa otra = new Empresa(); otra.setId(99);
        Usuario ajeno = new Usuario(); ajeno.setId(3); ajeno.setEmpresa(otra);
        when(gatewayRepository.findByIdAndProcesoId(30, 10)).thenReturn(Optional.of(gateway));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(ajeno));

        var req = new EditarGatewayRequest(3, null, null, null, null, null, null);
        assertThatThrownBy(() -> service.editarGateway(10, 30, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void editarGateway_cambioAParaleloConCondiciones_throwsConflict() {
        Arco arcoConCondicion = new Arco();
        arcoConCondicion.setCondicionExpr("x > 0");
        arcoConCondicion.setFromNodo(gateway);
        arcoConCondicion.setToNodo(actividad);
        arcoConCondicion.setActivo(true);

        EditarGatewayRequest req = new EditarGatewayRequest(3, null, null, null, null, "paralelo", null);
        when(gatewayRepository.findByIdAndProcesoId(30, 10)).thenReturn(Optional.of(gateway));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(arcoRepository.findByFromNodoIdAndActivoTrue(30)).thenReturn(List.of(arcoConCondicion));

        assertThatThrownBy(() -> service.editarGateway(10, 30, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    // ─── eliminarActividad ───────────────────────────────────────────────────

    @Test
    void eliminarActividad_exitoso_conArcos() {
        EliminarActividadRequest req = new EliminarActividadRequest(3, true);
        when(actividadRepository.findById(20)).thenReturn(Optional.of(actividad));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(arcoRepository.findByFromNodoId(20)).thenReturn(List.of(arco));
        when(arcoRepository.findByToNodoId(20)).thenReturn(List.of());

        service.eliminarActividad(10, 20, req);

        verify(arcoRepository).deleteAll(any());
        verify(actividadRepository).delete(actividad);
        verify(auditService).registrar(any(), any(), eq("ACTIVIDAD"), eq(20), eq("ELIMINAR"), any(), eq(null));
    }

    @Test
    void eliminarActividad_noEncontrada_throwsNotFound() {
        when(actividadRepository.findById(99)).thenReturn(Optional.empty());

        var req = new EliminarActividadRequest(3, true);
        assertThatThrownBy(() -> service.eliminarActividad(10, 99, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void eliminarActividad_deOtroProceso_throwsForbidden() {
        Proceso otroProceso = new Proceso(); otroProceso.setId(99); otroProceso.setEmpresa(empresa);
        actividad.setProceso(otroProceso);
        when(actividadRepository.findById(20)).thenReturn(Optional.of(actividad));

        var req = new EliminarActividadRequest(3, true);
        assertThatThrownBy(() -> service.eliminarActividad(10, 20, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void eliminarActividad_usuarioNoEncontrado_throwsNotFound() {
        when(actividadRepository.findById(20)).thenReturn(Optional.of(actividad));
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        var req = new EliminarActividadRequest(99, true);
        assertThatThrownBy(() -> service.eliminarActividad(10, 20, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void eliminarActividad_usuarioDeOtraEmpresa_throwsForbidden() {
        Empresa otra = new Empresa(); otra.setId(99);
        Usuario ajeno = new Usuario(); ajeno.setId(3); ajeno.setEmpresa(otra);
        when(actividadRepository.findById(20)).thenReturn(Optional.of(actividad));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(ajeno));

        var req = new EliminarActividadRequest(3, true);
        assertThatThrownBy(() -> service.eliminarActividad(10, 20, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    // ─── eliminarGateway ─────────────────────────────────────────────────────

    @Test
    void eliminarGateway_exitoso() {
        EliminarGatewayRequest req = new EliminarGatewayRequest(3, true);
        when(gatewayRepository.findByIdAndProcesoId(30, 10)).thenReturn(Optional.of(gateway));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(arcoRepository.findByToNodoIdAndActivoTrue(30)).thenReturn(List.of());
        when(arcoRepository.findByFromNodoIdAndActivoTrue(30)).thenReturn(List.of());

        service.eliminarGateway(10, 30, req);

        verify(gatewayRepository).delete(gateway);
        verify(auditService).registrar(any(), any(), eq("GATEWAY"), eq(30), eq("ELIMINAR"), any(), eq(null));
    }

    @Test
    void eliminarGateway_noEncontrado_throwsNotFound() {
        when(gatewayRepository.findByIdAndProcesoId(99, 10)).thenReturn(Optional.empty());

        var req = new EliminarGatewayRequest(3, true);
        assertThatThrownBy(() -> service.eliminarGateway(10, 99, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void eliminarGateway_usuarioDeOtraEmpresa_throwsForbidden() {
        Empresa otra = new Empresa(); otra.setId(99);
        Usuario ajeno = new Usuario(); ajeno.setId(3); ajeno.setEmpresa(otra);
        when(gatewayRepository.findByIdAndProcesoId(30, 10)).thenReturn(Optional.of(gateway));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(ajeno));

        var req = new EliminarGatewayRequest(3, true);
        assertThatThrownBy(() -> service.eliminarGateway(10, 30, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void eliminarGateway_conArcosActivos_throwsConflict() {
        when(gatewayRepository.findByIdAndProcesoId(30, 10)).thenReturn(Optional.of(gateway));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(arcoRepository.findByToNodoIdAndActivoTrue(30)).thenReturn(List.of(arco));
        when(arcoRepository.findByFromNodoIdAndActivoTrue(30)).thenReturn(List.of());

        var req = new EliminarGatewayRequest(3, true);
        assertThatThrownBy(() -> service.eliminarGateway(10, 30, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    // ─── crearArco ───────────────────────────────────────────────────────────

    @Test
    void crearArco_exitoso() {
        CrearArcoRequest req = new CrearArcoRequest(3, 20, 30, null, null);
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(nodoRepository.findById(20)).thenReturn(Optional.of(actividad));
        when(nodoRepository.findById(30)).thenReturn(Optional.of(gateway));
        when(arcoRepository.existsByProcesoIdAndFromNodoIdAndToNodoIdAndActivoTrue(10, 20, 30)).thenReturn(false);
        when(arcoRepository.save(any())).thenReturn(arco);

        ArcoResponse resp = service.crearArco(10, req);

        assertThat(resp.getId()).isEqualTo(40);
        verify(auditService).registrar(any(), any(), eq("ARCO"), any(), eq("CREAR"), eq(null), any());
    }

    @Test
    void crearArco_procesoNoEncontrado_throwsNotFound() {
        when(procesoRepository.findByIdAndActivoTrue(99)).thenReturn(Optional.empty());

        var req = new CrearArcoRequest(3, 20, 30, null, null);
        assertThatThrownBy(() -> service.crearArco(99, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void crearArco_usuarioDeOtraEmpresa_throwsForbidden() {
        Empresa otra = new Empresa(); otra.setId(99);
        Usuario ajeno = new Usuario(); ajeno.setId(3); ajeno.setEmpresa(otra);
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(ajeno));

        var req = new CrearArcoRequest(3, 20, 30, null, null);
        assertThatThrownBy(() -> service.crearArco(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void crearArco_fromNodoNoEncontrado_throwsNotFound() {
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(nodoRepository.findById(99)).thenReturn(Optional.empty());

        var req = new CrearArcoRequest(3, 99, 30, null, null);
        assertThatThrownBy(() -> service.crearArco(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void crearArco_nodoOrigenDeOtroProceso_throwsForbidden() {
        Proceso otroProceso = new Proceso(); otroProceso.setId(99);
        Actividad actividadAjena = new Actividad(); actividadAjena.setId(20); actividadAjena.setProceso(otroProceso);
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(nodoRepository.findById(20)).thenReturn(Optional.of(actividadAjena));

        var req = new CrearArcoRequest(3, 20, 30, null, null);
        assertThatThrownBy(() -> service.crearArco(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void crearArco_nodoBaseNoActividadNiGateway_throwsBadRequest() {
        Nodo nodoBase = new Nodo();
        nodoBase.setId(20);
        nodoBase.setProceso(proceso);
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(nodoRepository.findById(20)).thenReturn(Optional.of(nodoBase));

        var req = new CrearArcoRequest(3, 20, 30, null, null);
        assertThatThrownBy(() -> service.crearArco(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void crearArco_mismoNodoOrigenDestino_throwsBadRequest() {
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(nodoRepository.findById(20)).thenReturn(Optional.of(actividad));

        var req = new CrearArcoRequest(3, 20, 20, null, null);
        assertThatThrownBy(() -> service.crearArco(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void crearArco_duplicado_throwsConflict() {
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(nodoRepository.findById(20)).thenReturn(Optional.of(actividad));
        when(nodoRepository.findById(30)).thenReturn(Optional.of(gateway));
        when(arcoRepository.existsByProcesoIdAndFromNodoIdAndToNodoIdAndActivoTrue(10, 20, 30)).thenReturn(true);

        var req = new CrearArcoRequest(3, 20, 30, null, null);
        assertThatThrownBy(() -> service.crearArco(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void crearArco_condicionEnActividadNoGateway_throwsBadRequest() {
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(nodoRepository.findById(20)).thenReturn(Optional.of(actividad));
        when(nodoRepository.findById(30)).thenReturn(Optional.of(gateway));
        when(arcoRepository.existsByProcesoIdAndFromNodoIdAndToNodoIdAndActivoTrue(10, 20, 30)).thenReturn(false);

        var req = new CrearArcoRequest(3, 20, 30, "condicion", null);
        assertThatThrownBy(() -> service.crearArco(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void crearArco_gatewayParaleloConCondicion_throwsBadRequest() {
        Gateway gwParalelo = new Gateway();
        gwParalelo.setId(31);
        gwParalelo.setProceso(proceso);
        gwParalelo.setTipoGateway("paralelo");

        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(nodoRepository.findById(31)).thenReturn(Optional.of(gwParalelo));
        when(nodoRepository.findById(30)).thenReturn(Optional.of(gateway));
        when(arcoRepository.existsByProcesoIdAndFromNodoIdAndToNodoIdAndActivoTrue(10, 31, 30)).thenReturn(false);

        var req = new CrearArcoRequest(3, 31, 30, "condicion", null);
        assertThatThrownBy(() -> service.crearArco(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void crearArco_gatewayExclusivoSinCondicion_exitoso() {
        Gateway gwExclusivo = new Gateway();
        gwExclusivo.setId(31);
        gwExclusivo.setProceso(proceso);
        gwExclusivo.setTipoGateway("exclusivo");

        Arco nuevoArco = new Arco();
        nuevoArco.setId(50);
        nuevoArco.setFromNodo(gwExclusivo);
        nuevoArco.setToNodo(actividad);
        nuevoArco.setProceso(proceso);
        nuevoArco.setActivo(true);

        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(nodoRepository.findById(31)).thenReturn(Optional.of(gwExclusivo));
        when(nodoRepository.findById(20)).thenReturn(Optional.of(actividad));
        when(arcoRepository.existsByProcesoIdAndFromNodoIdAndToNodoIdAndActivoTrue(10, 31, 20)).thenReturn(false);
        when(arcoRepository.save(any())).thenReturn(nuevoArco);

        ArcoResponse resp = service.crearArco(10, new CrearArcoRequest(3, 31, 20, null, null));
        assertThat(resp).isNotNull();
    }

    // ─── editarArco ──────────────────────────────────────────────────────────

    @Test
    void editarArco_exitoso_sinCambioDeNodos() {
        EditarArcoRequest req = new EditarArcoRequest(3, null, null, null, null);
        when(arcoRepository.findByIdAndActivoTrue(40)).thenReturn(Optional.of(arco));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(arcoRepository.existsActivoDuplicadoExcluyendoId(10, 20, 30, 40)).thenReturn(false);
        when(arcoRepository.save(any())).thenReturn(arco);

        ArcoResponse resp = service.editarArco(10, 40, req);

        assertThat(resp).isNotNull();
        verify(auditService).registrar(any(), any(), eq("ARCO"), eq(40), eq("EDITAR"), any(), any());
    }

    @Test
    void editarArco_conCambioDeNodos_exitoso() {
        Gateway nuevoDestino = new Gateway();
        nuevoDestino.setId(35);
        nuevoDestino.setProceso(proceso);
        nuevoDestino.setTipoGateway("exclusivo");

        EditarArcoRequest req = new EditarArcoRequest(3, 20, 35, null, null);
        when(arcoRepository.findByIdAndActivoTrue(40)).thenReturn(Optional.of(arco));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(nodoRepository.findById(20)).thenReturn(Optional.of(actividad));
        when(nodoRepository.findById(35)).thenReturn(Optional.of(nuevoDestino));
        when(arcoRepository.existsActivoDuplicadoExcluyendoId(10, 20, 35, 40)).thenReturn(false);
        when(arcoRepository.save(any())).thenReturn(arco);

        ArcoResponse resp = service.editarArco(10, 40, req);
        assertThat(resp).isNotNull();
    }

    @Test
    void editarArco_noEncontrado_throwsNotFound() {
        when(arcoRepository.findByIdAndActivoTrue(99)).thenReturn(Optional.empty());

        var req = new EditarArcoRequest(3, null, null, null, null);
        assertThatThrownBy(() -> service.editarArco(10, 99, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void editarArco_deOtroProceso_throwsForbidden() {
        Proceso otroProceso = new Proceso(); otroProceso.setId(99); otroProceso.setEmpresa(empresa);
        arco.setProceso(otroProceso);
        when(arcoRepository.findByIdAndActivoTrue(40)).thenReturn(Optional.of(arco));

        var req = new EditarArcoRequest(3, null, null, null, null);
        assertThatThrownBy(() -> service.editarArco(10, 40, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void editarArco_usuarioDeOtraEmpresa_throwsForbidden() {
        Empresa otra = new Empresa(); otra.setId(99);
        Usuario ajeno = new Usuario(); ajeno.setId(3); ajeno.setEmpresa(otra);
        when(arcoRepository.findByIdAndActivoTrue(40)).thenReturn(Optional.of(arco));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(ajeno));

        var req = new EditarArcoRequest(3, null, null, null, null);
        assertThatThrownBy(() -> service.editarArco(10, 40, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    // ─── eliminarArco ────────────────────────────────────────────────────────

    @Test
    void eliminarArco_exitoso_softDelete() {
        EliminarArcoRequest req = new EliminarArcoRequest(3, true);
        when(arcoRepository.findByIdAndActivoTrue(40)).thenReturn(Optional.of(arco));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(arcoRepository.save(any())).thenReturn(arco);

        service.eliminarArco(10, 40, req);

        assertThat(arco.isActivo()).isFalse();
        verify(auditService).registrar(any(), any(), eq("ARCO"), eq(40), eq("ELIMINAR"), any(), any());
    }

    @Test
    void eliminarArco_noEncontrado_throwsNotFound() {
        when(arcoRepository.findByIdAndActivoTrue(99)).thenReturn(Optional.empty());

        var req = new EliminarArcoRequest(3, true);
        assertThatThrownBy(() -> service.eliminarArco(10, 99, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void eliminarArco_deOtroProceso_throwsForbidden() {
        Proceso otroProceso = new Proceso(); otroProceso.setId(99); otroProceso.setEmpresa(empresa);
        arco.setProceso(otroProceso);
        when(arcoRepository.findByIdAndActivoTrue(40)).thenReturn(Optional.of(arco));

        var req = new EliminarArcoRequest(3, true);
        assertThatThrownBy(() -> service.eliminarArco(10, 40, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void eliminarArco_usuarioDeOtraEmpresa_throwsForbidden() {
        Empresa otra = new Empresa(); otra.setId(99);
        Usuario ajeno = new Usuario(); ajeno.setId(3); ajeno.setEmpresa(otra);
        when(arcoRepository.findByIdAndActivoTrue(40)).thenReturn(Optional.of(arco));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(ajeno));

        var req = new EliminarArcoRequest(3, true);
        assertThatThrownBy(() -> service.eliminarArco(10, 40, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    // ─── consultas ───────────────────────────────────────────────────────────

    @Test
    void getLanes_retornaListaMapeada() {
        RolProceso rolProceso = new RolProceso();
        rolProceso.setId(7);
        rolProceso.setNombre("Analista");
        lane.setRolProceso(rolProceso);

        when(laneRepository.findByProcesoIdOrderByOrdenAsc(10)).thenReturn(List.of(lane));

        List<LaneResponse> result = service.getLanes(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Lane 1");
        assertThat(result.get(0).getRolProcesoNombre()).isEqualTo("Analista");
    }

    @Test
    void getLanes_sinRolProceso_retornaRolNulo() {
        lane.setRolProceso(null);
        when(laneRepository.findByProcesoIdOrderByOrdenAsc(10)).thenReturn(List.of(lane));

        List<LaneResponse> result = service.getLanes(10);

        assertThat(result.get(0).getRolProcesoId()).isNull();
    }

    @Test
    void getNodos_retornaListaMapeada() {
        when(nodoRepository.findByProcesoId(10)).thenReturn(List.of(actividad, gateway));

        List<NodoResponse> result = service.getNodos(10);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTipo()).isEqualTo("actividad");
        assertThat(result.get(1).getTipo()).isEqualTo("gateway");
    }

    @Test
    void getArcos_retornaListaMapeada() {
        when(arcoRepository.findByProcesoIdAndActivoTrue(10)).thenReturn(List.of(arco));

        List<ArcoResponse> result = service.getArcos(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFromNodoId()).isEqualTo(20);
        assertThat(result.get(0).getToNodoId()).isEqualTo(30);
    }

    // ─── mappers estáticos ───────────────────────────────────────────────────

    @Test
    void toNodoResponse_actividad_tienetipoActividadYPropsJson() {
        actividad.setPropsJson("{\"color\":\"red\"}");

        NodoResponse resp = DiagramService.toNodoResponse(actividad);

        assertThat(resp.getTipo()).isEqualTo("actividad");
        assertThat(resp.getTipoActividad()).isEqualTo("tarea");
        assertThat(resp.getPropsJson()).isEqualTo("{\"color\":\"red\"}");
        assertThat(resp.getLaneId()).isEqualTo(5);
    }

    @Test
    void toNodoResponse_gateway_tieneTipoGatewayYConfigJson() {
        gateway.setConfigJson("{\"max\":3}");
        gateway.setLane(null);

        NodoResponse resp = DiagramService.toNodoResponse(gateway);

        assertThat(resp.getTipo()).isEqualTo("gateway");
        assertThat(resp.getTipoGateway()).isEqualTo("exclusivo");
        assertThat(resp.getConfigJson()).isEqualTo("{\"max\":3}");
        assertThat(resp.getLaneId()).isNull();
    }

    @Test
    void toNodoResponse_nodoBase_tipoEsNodo() {
        Nodo nodoBase = new Nodo();
        nodoBase.setId(99);
        nodoBase.setLabel("Inicio");
        nodoBase.setProceso(proceso);

        NodoResponse resp = DiagramService.toNodoResponse(nodoBase);

        assertThat(resp.getTipo()).isEqualTo("nodo");
        assertThat(resp.getTipoActividad()).isNull();
        assertThat(resp.getTipoGateway()).isNull();
    }

    @Test
    void toArcoResponse_conCondicion() {
        arco.setCondicionExpr("aprobado == true");
        arco.setPropsJson("{\"color\":\"blue\"}");

        ArcoResponse resp = DiagramService.toArcoResponse(arco);

        assertThat(resp.getId()).isEqualTo(40);
        assertThat(resp.getCondicionExpr()).isEqualTo("aprobado == true");
        assertThat(resp.isActivo()).isTrue();
    }

    @Test
    void toLaneResponse_conYSinRolProceso() {
        LaneResponse sinRol = DiagramService.toLaneResponse(lane);
        assertThat(sinRol.getRolProcesoId()).isNull();

        RolProceso rol = new RolProceso(); rol.setId(7); rol.setNombre("Revisor");
        lane.setRolProceso(rol);
        LaneResponse conRol = DiagramService.toLaneResponse(lane);
        assertThat(conRol.getRolProcesoId()).isEqualTo(7);
        assertThat(conRol.getRolProcesoNombre()).isEqualTo("Revisor");
    }
}
