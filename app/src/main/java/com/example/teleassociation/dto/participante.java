package com.example.teleassociation.dto;

public class participante {
    private String asignacion;
    private String codigo;
    private String evento;

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
    public participante(String asignacion, String codigo, String evento) {
        this.asignacion = asignacion;
        this.codigo = codigo;
        this.evento = evento;
    }
}
