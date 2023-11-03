package com.example.teleassociation.entitys;

public class listActividadesEventos {
    public String evento;
    public String hora;
    public String apoyo;
    public String titulo;
    public String fecha;

    public listActividadesEventos(String fecha, String hora, String apoyo, String titulo, String evento) {
        this.evento = evento;
        this.hora = hora;
        this.fecha = fecha;
        this.apoyo = apoyo;
        this.titulo = titulo;
    }

    public String getApoyo() {
        return apoyo;
    }

    public void setApoyo(String apoyo) {
        this.apoyo = apoyo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }


    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}


