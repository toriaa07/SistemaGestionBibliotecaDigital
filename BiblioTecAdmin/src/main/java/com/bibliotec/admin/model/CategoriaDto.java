package com.bibliotec.admin.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoriaDto {
    public int idCategoria;
    public String nombre;

    public int    getIdCategoria() { return idCategoria; }
    public String getNombre()      { return nombre != null ? nombre : ""; }

    @Override public String toString() { return nombre != null ? nombre : ""; }
}
