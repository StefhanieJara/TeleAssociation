package com.example.teleassociation.dto;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class notificacion implements Serializable {
    private String id;
    private String titulo;
    private String detalle;
    private Timestamp fecha;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public CharSequence getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    // Corregir el nombre del constructor a "notificacion"
    public notificacion(String titulo, Timestamp fecha, String detalle, String id) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.detalle = detalle;
        this.id = id;
    }
    // Constructor sin argumentos requerido para Firestore
    public notificacion() {
        // Constructor vacío requerido para la deserialización de Firestore
    }
}
