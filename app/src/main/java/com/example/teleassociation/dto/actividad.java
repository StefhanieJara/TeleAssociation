package com.example.teleassociation.dto;

public class actividad {

    private String delegado;
    private String descripcion;
    private String nombre;
    private String url_imagen;
    private int activo = 1;

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    public String getDelegado() {
        return delegado;
    }

    public void setDelegado(String delegado) {
        this.delegado = delegado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrl_imagen() {
        return url_imagen;
    }

    public void setUrl_imagen(String url_imagen) {
        this.url_imagen = url_imagen;
    }
}
