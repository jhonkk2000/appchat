package com.jhonkkman.app2;

public class SessionMensajes {

    private String mensaje, user;

    public SessionMensajes(String mensaje, String user) {
        this.mensaje = mensaje;
        this.user = user;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
