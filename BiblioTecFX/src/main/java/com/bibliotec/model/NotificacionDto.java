package com.bibliotec.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificacionDto {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public int idNotificacion;
    public int idUsuario;
    public String tipo;
    public String mensaje;
    public LocalDateTime fechaEnvio;
    public boolean leida;

    public int getIdNotificacion() { return idNotificacion; }
    public String getTipo() { return tipo; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public boolean isLeida() { return leida; }

    public String getFechaEnvioStr() {
        return fechaEnvio != null ? fechaEnvio.format(FMT) : "";
    }

    public String getEstadoLeida() {
        return leida ? "Leída" : "Nueva";
    }
}
