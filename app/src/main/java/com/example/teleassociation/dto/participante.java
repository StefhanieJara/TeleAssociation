package com.example.teleassociation.dto;

public class participante {
    private String asignacion;
    private String codigo;
    private String evento;
    private String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(String asignacion) {
        this.asignacion = asignacion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }
    public participante(String asignacion, String codigo, String evento, String nombre) {
        this.asignacion = asignacion;
        this.codigo = codigo;
        this.evento = evento;
        this.nombre = nombre;
    }
}
