package com.bibliotec.admin.model;
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

    public int    getIdNotificacion() { return idNotificacion; }
    public int    getIdUsuario()      { return idUsuario; }
    public String getTipo()           { return tipo != null ? tipo : ""; }
    public String getMensaje()        { return mensaje != null ? mensaje : ""; }
    public boolean isLeida()          { return leida; }
    public String getLeidaStr()       { return leida ? "Leída" : "Nueva"; }
    public String getFechaEnvioStr()  { return fechaEnvio != null ? fechaEnvio.format(FMT) : ""; }
}
