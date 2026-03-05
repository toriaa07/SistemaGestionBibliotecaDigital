package com.bibliotec.admin.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfiguracionDto {
    public int diasPrestamo;
    public int maxPrestamosActivos;
    public boolean notificacionesActivas;

    public int     getDiasPrestamo()          { return diasPrestamo; }
    public int     getMaxPrestamosActivos()   { return maxPrestamosActivos; }
    public boolean isNotificacionesActivas()  { return notificacionesActivas; }
}
