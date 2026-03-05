package com.bibliotec.service;

import com.bibliotec.model.LibroDto;
import com.bibliotec.model.LoginResponseDto;
import com.bibliotec.model.NotificacionDto;
import com.bibliotec.model.PrestamoDto;
import com.bibliotec.util.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class ApiService {

    private static final String BASE_URL = "http://localhost:5165/api";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static ApiService instance;
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    private ApiService() {
        client = new OkHttpClient();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ApiService getInstance() {
        if (instance == null) instance = new ApiService();
        return instance;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Request.Builder authBuilder(String url) {
        String token = SessionManager.getInstance().getToken();
        Request.Builder b = new Request.Builder().url(url);
        if (token != null) b.header("Authorization", "Bearer " + token);
        return b;
    }

    private String execute(Request req) throws IOException {
        try (Response resp = client.newCall(req).execute()) {
            String body = resp.body() != null ? resp.body().string() : "";
            if (!resp.isSuccessful()) {
                // Try to parse error message
                try {
                    JsonNode node = mapper.readTree(body);
                    String msg = node.path("message").asText("Error desconocido");
                    throw new IOException(msg);
                } catch (Exception e2) {
                    throw new IOException("Error " + resp.code() + ": " + body);
                }
            }
            return body;
        }
    }

    private <T> T parseData(String json, TypeReference<T> ref) throws IOException {
        JsonNode root = mapper.readTree(json);
        return mapper.convertValue(root.path("data"), ref);
    }

    // ── Auth ─────────────────────────────────────────────────────────────────

    public LoginResponseDto login(String correo, String password) throws IOException {
        String body = mapper.writeValueAsString(
                mapper.createObjectNode()
                        .put("correo", correo)
                        .put("password", password)
        );
        Request req = new Request.Builder()
                .url(BASE_URL + "/auth/login")
                .post(RequestBody.create(body, JSON))
                .build();
        String resp = execute(req);
        return parseData(resp, new TypeReference<LoginResponseDto>() {});
    }

    public void changePassword(String passwordActual, String passwordNuevo) throws IOException {
        String body = mapper.writeValueAsString(
                mapper.createObjectNode()
                        .put("passwordActual", passwordActual)
                        .put("passwordNuevo", passwordNuevo)
        );
        Request req = authBuilder(BASE_URL + "/auth/change-password")
                .post(RequestBody.create(body, JSON))
                .build();
        execute(req);
    }

    // ── Libros ────────────────────────────────────────────────────────────────

    public List<LibroDto> getLibros(String query) throws IOException {
        String url = BASE_URL + "/libros";
        if (query != null && !query.isBlank()) url += "?q=" + query;
        Request req = authBuilder(url).get().build();
        String resp = execute(req);
        return parseData(resp, new TypeReference<List<LibroDto>>() {});
    }

    // ── Préstamos ─────────────────────────────────────────────────────────────

    public List<PrestamoDto> getMisPrestamos() throws IOException {
        Request req = authBuilder(BASE_URL + "/prestamos/mis-prestamos").get().build();
        String resp = execute(req);
        return parseData(resp, new TypeReference<List<PrestamoDto>>() {});
    }

    public PrestamoDto crearPrestamo(int idLibro) throws IOException {
        String body = mapper.writeValueAsString(
                mapper.createObjectNode().put("idLibro", idLibro)
        );
        Request req = authBuilder(BASE_URL + "/prestamos")
                .post(RequestBody.create(body, JSON))
                .build();
        String resp = execute(req);
        return parseData(resp, new TypeReference<PrestamoDto>() {});
    }

    public void devolverPrestamo(int idPrestamo) throws IOException {
        Request req = authBuilder(BASE_URL + "/prestamos/" + idPrestamo + "/devolver")
                .patch(RequestBody.create("", JSON))
                .build();
        execute(req);
    }

    // ── Notificaciones ────────────────────────────────────────────────────────

    public List<NotificacionDto> getMisNotificaciones() throws IOException {
        Request req = authBuilder(BASE_URL + "/notificaciones/mis-notificaciones").get().build();
        String resp = execute(req);
        return parseData(resp, new TypeReference<List<NotificacionDto>>() {});
    }

    public void marcarNotificacionLeida(int id) throws IOException {
        Request req = authBuilder(BASE_URL + "/notificaciones/" + id + "/leer")
                .patch(RequestBody.create("", JSON))
                .build();
        execute(req);
    }

    public void marcarTodasLeidas() throws IOException {
        Request req = authBuilder(BASE_URL + "/notificaciones/leer-todas")
                .patch(RequestBody.create("", JSON))
                .build();
        execute(req);
    }
}
