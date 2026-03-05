package com.bibliotec.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LibroDto {
    public int libroId;
    public String titulo;
    public String autor;
    public String editorial;
    public Integer anio;
    public int totalEjemplares;
    public int disponibles;
    public boolean activo;
    public List<String> categorias;

    public int getLibroId() { return libroId; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getEditorial() { return editorial != null ? editorial : ""; }
    public Integer getAnio() { return anio; }
    public int getTotalEjemplares() { return totalEjemplares; }
    public int getDisponibles() { return disponibles; }
    public boolean isActivo() { return activo; }
    public List<String> getCategorias() { return categorias; }

    public String getCategoriasStr() {
        if (categorias == null || categorias.isEmpty()) return "Sin categoría";
        return String.join(", ", categorias);
    }

    public String getEstadoDisponibilidad() {
        if (!activo) return "Inactivo";
        if (disponibles <= 0) return "Sin disponibilidad";
        return disponibles + " disponible(s)";
    }
}
