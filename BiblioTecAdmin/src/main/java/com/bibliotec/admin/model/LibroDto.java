package com.bibliotec.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LibroDto {
    public int libroId;
    public String titulo;
    public String autor;
    public String editorial;
    public Integer anio;
    public String rutaPdf;
    public int totalEjemplares;
    public int disponibles;
    public boolean activo;
    public List<String> categorias;

    public int    getLibroId()         { return libroId; }
    public String getTitulo()          { return titulo != null ? titulo : ""; }
    public String getAutor()           { return autor != null ? autor : ""; }
    public String getEditorial()       { return editorial != null ? editorial : ""; }
    public Integer getAnio()           { return anio; }
    public String getRutaPdf()         { return rutaPdf != null ? rutaPdf : ""; }
    public int    getTotalEjemplares() { return totalEjemplares; }
    public int    getDisponibles()     { return disponibles; }
    public boolean isActivo()          { return activo; }
    public String getActivoStr()       { return activo ? "Activo" : "Inactivo"; }
    public List<String> getCategorias() {
        return categorias;
    }
    public String getCategoriasStr() {
        if (categorias == null || categorias.isEmpty()) return "—";
        return String.join(", ", categorias);
    }
}
