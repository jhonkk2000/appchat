package com.jhonkkman.app2;

public class User {

    private String nombre,sexo;
    private Boolean estado;

    public User(String nombre, String sexo,Boolean estado){
        this.nombre=nombre;
        this.sexo=sexo;
        this.estado=estado;
    }

    public User(){

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
