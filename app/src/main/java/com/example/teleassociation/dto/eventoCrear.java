package com.example.teleassociation.dto;

import com.google.firebase.Timestamp;

import java.util.Date;

public class eventoCrear {
    private String apoyos;
    private String descripcion;
    private String estado;
    private Timestamp fecha;
    private String nombre;
    private String nombre_actividad;
    private String nombre_lugar;

    public String getApoyos() {
        return apoyos;
    }

    public void setApoyos(String apoyos) {
        this.apoyos = apoyos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre_actividad() {
        return nombre_actividad;
    }

    public void setNombre_actividad(String nombre_actividad) {
        this.nombre_actividad = nombre_actividad;
    }

    public String getNombre_lugar() {
        return nombre_lugar;
    }

    public void setNombre_lugar(String nombre_lugar) {
        this.nombre_lugar = nombre_lugar;
    }
}
