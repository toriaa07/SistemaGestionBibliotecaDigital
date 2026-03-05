package com.bibliotec.admin.util;

public class SessionManager {
    private static SessionManager instance;
    private String token, nombre, correo, rol;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void setSession(String token, String nombre, String correo, String rol) {
        this.token = token; this.nombre = nombre;
        this.correo = correo; this.rol = rol;
    }
    public void clearSession() { token = null; nombre = null; correo = null; rol = null; }

    public String getToken()  { return token; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getRol()    { return rol; }
    public boolean isAdmin()  { return "ADMIN".equals(rol); }
    public boolean isLoggedIn() { return token != null && !token.isEmpty(); }
}
