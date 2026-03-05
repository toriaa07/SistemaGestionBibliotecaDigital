package com.bibliotec.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PrestamoDto {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public int idPrestamo;
    public int idUsuario;
    public String nombreUsuario;
    public int idLibro;
    public String tituloLibro;
    public String autorLibro;
    public LocalDateTime fechaPrestamo;
    public LocalDateTime fechaVencimiento;
    public LocalDateTime fechaDevolucion;
    public String estado;

    public int getIdPrestamo() { return idPrestamo; }
    public int getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public int getIdLibro() { return idLibro; }
    public String getTituloLibro() { return tituloLibro; }
    public String getAutorLibro() { return autorLibro; }
    public LocalDateTime getFechaPrestamo() { return fechaPrestamo; }
    public LocalDateTime getFechaVencimiento() { return fechaVencimiento; }
    public LocalDateTime getFechaDevolucion() { return fechaDevolucion; }
    public String getEstado() { return estado; }

    public String getFechaPrestamoStr() {
        return fechaPrestamo != null ? fechaPrestamo.format(FMT) : "";
    }
    public String getFechaVencimientoStr() {
        return fechaVencimiento != null ? fechaVencimiento.format(FMT) : "";
    }
    public String getFechaDevolucionStr() {
        return fechaDevolucion != null ? fechaDevolucion.format(FMT) : "Pendiente";
    }
}
