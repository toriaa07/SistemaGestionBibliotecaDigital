package com.bibliotec.admin.service;

import com.bibliotec.admin.model.*;
import com.bibliotec.admin.util.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class ApiService {

    private static final String BASE = "http://localhost:5165/api";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static ApiService instance;
    private final OkHttpClient http = new OkHttpClient();
    private final ObjectMapper mapper;

    private ApiService() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ApiService get() {
        if (instance == null) instance = new ApiService();
        return instance;
    }

    // ── Internals ─────────────────────────────────────
    private Request.Builder auth(String url) {
        String tok = SessionManager.getInstance().getToken();
        Request.Builder b = new Request.Builder().url(url);
        if (tok != null) b.header("Authorization", "Bearer " + tok);
        return b;
    }

    private String exec(Request req) throws IOException {
        try (Response r = http.newCall(req).execute()) {
            String body = r.body() != null ? r.body().string() : "";
            if (!r.isSuccessful()) {
                try {
                    String msg = mapper.readTree(body).path("message").asText("Error " + r.code());
                    throw new IOException(msg);
                } catch (IOException e2) { throw e2; }
                catch (Exception e3) { throw new IOException("Error " + r.code()); }
            }
            return body;
        }
    }

    private <T> T data(String json, TypeReference<T> ref) throws IOException {
        return mapper.convertValue(mapper.readTree(json).path("data"), ref);
    }

    private RequestBody body(Object o) throws IOException {
        return RequestBody.create(mapper.writeValueAsString(o), JSON);
    }

    // ── Auth ──────────────────────────────────────────
    public record LoginResp(String token, String nombre, String correo, String rol) {}

    public LoginResp login(String correo, String pass) throws IOException {
        var node = mapper.createObjectNode().put("correo", correo).put("password", pass);
        var req = new Request.Builder().url(BASE + "/auth/login")
                .post(RequestBody.create(mapper.writeValueAsString(node), JSON)).build();
        String resp = exec(req);
        var d = mapper.readTree(resp).path("data");
        return new LoginResp(d.path("token").asText(), d.path("nombre").asText(),
                             d.path("correo").asText(), d.path("rol").asText());
    }

    // ── Usuarios ──────────────────────────────────────
    public List<UsuarioDto> getUsuarios(String q, String rol, String estado) throws IOException {
        StringBuilder url = new StringBuilder(BASE + "/usuarios?x=1");
        if (q     != null && !q.isBlank())     url.append("&q=").append(q);
        if (rol   != null && !rol.isBlank())   url.append("&rol=").append(rol);
        if (estado!= null && !estado.isBlank())url.append("&estado=").append(estado);
        return data(exec(auth(url.toString()).get().build()), new TypeReference<>() {});
    }

    public UsuarioDto createUsuario(String nombre, String correo, String password, String rol) throws IOException {
        var node = mapper.createObjectNode()
                .put("nombre", nombre).put("correo", correo)
                .put("password", password).put("rol", rol);
        return data(exec(auth(BASE + "/usuarios").post(body(node)).build()), new TypeReference<>() {});
    }

    public UsuarioDto updateUsuario(int id, String nombre, String correo) throws IOException {
        var node = mapper.createObjectNode().put("nombre", nombre).put("correo", correo);
        return data(exec(auth(BASE + "/usuarios/" + id).put(body(node)).build()), new TypeReference<>() {});
    }

    public void updateEstadoUsuario(int id, String estado) throws IOException {
        var node = mapper.createObjectNode().put("estado", estado);
        exec(auth(BASE + "/usuarios/" + id + "/estado").patch(body(node)).build());
    }

    public void deleteUsuario(int id) throws IOException {
        exec(auth(BASE + "/usuarios/" + id).delete().build());
    }

    // ── Libros ────────────────────────────────────────
    public List<LibroDto> getLibros(String q, Boolean activo) throws IOException {
        StringBuilder url = new StringBuilder(BASE + "/libros?x=1");
        if (q != null && !q.isBlank()) url.append("&q=").append(q);
        if (activo != null) url.append("&activo=").append(activo);
        return data(exec(auth(url.toString()).get().build()), new TypeReference<>() {});
    }

    public LibroDto createLibro(String titulo, String autor, String editorial,
                                 Integer anio, String rutaPdf, int ejemplares) throws IOException {
        var node = mapper.createObjectNode()
                .put("titulo", titulo).put("autor", autor)
                .put("editorial", editorial).put("rutaPdf", rutaPdf)
                .put("totalEjemplares", ejemplares);
        if (anio != null) node.put("anio", anio);
        return data(exec(auth(BASE + "/libros").post(body(node)).build()), new TypeReference<>() {});
    }

    public LibroDto updateLibro(int id, String titulo, String autor, String editorial,
                                 Integer anio, String rutaPdf, int ejemplares) throws IOException {
        var node = mapper.createObjectNode()
                .put("titulo", titulo).put("autor", autor)
                .put("editorial", editorial).put("rutaPdf", rutaPdf)
                .put("totalEjemplares", ejemplares);
        if (anio != null) node.put("anio", anio);
        return data(exec(auth(BASE + "/libros/" + id).put(body(node)).build()), new TypeReference<>() {});
    }

    public void updateEstadoLibro(int id, boolean activo) throws IOException {
        exec(auth(BASE + "/libros/" + id + "/estado")
                .patch(RequestBody.create(String.valueOf(activo), JSON)).build());
    }

    public void deleteLibro(int id) throws IOException {
        exec(auth(BASE + "/libros/" + id).delete().build());
    }

    public void asignarCategorias(int libroId, List<Integer> catIds) throws IOException {
        var node = mapper.createObjectNode();
        var arr  = node.putArray("categorias");
        catIds.forEach(arr::add);
        exec(auth(BASE + "/libros/" + libroId + "/categorias").post(body(node)).build());
    }

    // ── Préstamos ─────────────────────────────────────
    public List<PrestamoDto> getPrestamos(String estado, Integer idUsuario) throws IOException {
        StringBuilder url = new StringBuilder(BASE + "/prestamos?x=1");
        if (estado   != null && !estado.isBlank()) url.append("&estado=").append(estado);
        if (idUsuario != null)                     url.append("&idUsuario=").append(idUsuario);
        return data(exec(auth(url.toString()).get().build()), new TypeReference<>() {});
    }

    public void marcarVencidos() throws IOException {
        exec(auth(BASE + "/prestamos/vencidos").patch(RequestBody.create("", JSON)).build());
    }

    public void updateEstadoPrestamo(int id, String estado) throws IOException {
        exec(auth(BASE + "/prestamos/" + id + "/estado")
                .patch(RequestBody.create("\"" + estado + "\"", JSON)).build());
    }

    public void deletePrestamo(int id) throws IOException {
        exec(auth(BASE + "/prestamos/" + id).delete().build());
    }

    // ── Categorías ────────────────────────────────────
    public List<CategoriaDto> getCategorias() throws IOException {
        return data(exec(auth(BASE + "/categorias").get().build()), new TypeReference<>() {});
    }

    public CategoriaDto createCategoria(String nombre) throws IOException {
        var node = mapper.createObjectNode().put("nombre", nombre);
        return data(exec(auth(BASE + "/categorias").post(body(node)).build()), new TypeReference<>() {});
    }

    public CategoriaDto updateCategoria(int id, String nombre) throws IOException {
        var node = mapper.createObjectNode().put("nombre", nombre);
        return data(exec(auth(BASE + "/categorias/" + id).put(body(node)).build()), new TypeReference<>() {});
    }

    public void deleteCategoria(int id) throws IOException {
        exec(auth(BASE + "/categorias/" + id).delete().build());
    }

    // ── Notificaciones ────────────────────────────────
    public NotificacionDto createNotificacion(int idUsuario, String tipo, String mensaje) throws IOException {
        var node = mapper.createObjectNode()
                .put("idUsuario", idUsuario).put("tipo", tipo).put("mensaje", mensaje);
        return data(exec(auth(BASE + "/notificaciones").post(body(node)).build()), new TypeReference<>() {});
    }

    // ── Configuración ─────────────────────────────────
    public ConfiguracionDto getConfiguracion() throws IOException {
        return data(exec(auth(BASE + "/configuraciones").get().build()), new TypeReference<>() {});
    }

    public ConfiguracionDto updateConfiguracion(int dias, int maxPrestamos, boolean notifs) throws IOException {
        var node = mapper.createObjectNode()
                .put("diasPrestamo", dias)
                .put("maxPrestamosActivos", maxPrestamos)
                .put("notificacionesActivas", notifs);
        return data(exec(auth(BASE + "/configuraciones/1").put(body(node)).build()), new TypeReference<>() {});
    }
}
