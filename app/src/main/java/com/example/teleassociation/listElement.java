package com.example.teleassociation;

public class listElement {
    public String evento;
    public String hora;

    public listElement(String evento, String hora) {
        this.evento = evento;
        this.hora = hora;
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
