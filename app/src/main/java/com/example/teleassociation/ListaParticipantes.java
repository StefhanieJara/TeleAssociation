package com.example.teleassociation;

public class ListaParticipantes {
    public String actividad;
    public String rol;
    public String asignacion;

    public ListaParticipantes(String actividad, String rol, String asignacion) {
        this.actividad = actividad;
        this.rol = rol;
        this.asignacion = asignacion;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(String asignacion) {
        this.asignacion = asignacion;
    }
}
