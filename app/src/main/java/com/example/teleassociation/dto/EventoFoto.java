package com.example.teleassociation.dto;

public class EventoFoto {
    private String id;
    private String url;

    public EventoFoto(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
