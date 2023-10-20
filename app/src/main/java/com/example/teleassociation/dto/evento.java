package com.example.teleassociation.dto;

public class evento {

    private String evento;
    private String descripcion;
    private String fecha;
    private String hora;
    private String estado;
    private String actividad;
    private int apoyos;

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public int getApoyos() {
        return apoyos;
    }

    public void setApoyos(int apoyos) {
        this.apoyos = apoyos;
    }
}
