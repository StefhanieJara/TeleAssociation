package com.example.teleassociation.dto;

public class eventoListarUsuario {

    private String nombre;
    private String fecha;
    private String hora;
    private String apoyos;

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

    public eventoListarUsuario(String nombre, String fecha, String hora, String apoyos) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.apoyos = apoyos;
    }
}
