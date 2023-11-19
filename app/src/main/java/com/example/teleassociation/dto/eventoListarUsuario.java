package com.example.teleassociation.dto;

public class eventoListarUsuario {

    private String id;

    private String nombre;
    private String fecha;
    private String hora;
    private String apoyos;
    private String nombre_actividad;
    private String url_imagen;


    public String getNombre_actividad() {
        return nombre_actividad;
    }

    public void setNombre_actividad(String nombre_actividad) {
        this.nombre_actividad = nombre_actividad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApoyos() {
        return apoyos;
    }

    public void setApoyos(String apoyos) {
        this.apoyos = apoyos;
    }

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
    public String getUrl_imagen() {
        return url_imagen;
    }

    public void setUrl_imagen(String url_imagen) {
        this.url_imagen = url_imagen;
    }

    public eventoListarUsuario(String nombre, String fecha, String hora, String apoyos, String nombre_actividad, String url_imagen) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.apoyos = apoyos;
        this.nombre_actividad = nombre_actividad;
        this.url_imagen = url_imagen;

    }
}
