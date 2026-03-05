package com.bibliotec.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UsuarioDto {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public int idUsuario;
    public String nombre;
    public String correo;
    public String rol;
    public String estado;
    public LocalDateTime fechaRegistro;

    public int    getIdUsuario()    { return idUsuario; }
    public String getNombre()       { return nombre != null ? nombre : ""; }
    public String getCorreo()       { return correo != null ? correo : ""; }
    public String getRol()          { return rol != null ? rol : ""; }
    public String getEstado()       { return estado != null ? estado : ""; }
    public String getFechaRegistroStr() {
        return fechaRegistro != null ? fechaRegistro.format(FMT) : "";
    }
}
