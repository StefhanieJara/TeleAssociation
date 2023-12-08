package com.example.teleassociation.dto;

import java.io.Serializable;

public class Mensaje implements Serializable {
    private String usuario;
    private String mensaje;
    private String userId;  // ID del usuario que envió el mensaje
    private String userProfileImageUrl; // URL de la imagen de perfil del usuario

    // Constructor que incluye el ID del usuario y la URL de la imagen de perfil
    public Mensaje(String usuario, String userId, String mensaje) {
        this.usuario = usuario;
        this.mensaje = mensaje;
        this.userId = userId;
    }

    // Resto de getters y setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    // Otros métodos, si es necesario

    // Constructor y otros métodos, si es necesario

    public Mensaje() {
    }

    // Constructor básico
    public Mensaje(String usuario, String mensaje) {
        this.usuario = usuario;
        this.mensaje = mensaje;
    }
}
