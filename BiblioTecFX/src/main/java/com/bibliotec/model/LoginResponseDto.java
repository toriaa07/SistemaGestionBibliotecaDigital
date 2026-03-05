package com.bibliotec.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponseDto {
    public String token;
    public String nombre;
    public String correo;
    public String rol;
}
