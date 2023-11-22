package com.example.teleassociation.dto;

public class usuarioSesion {
    private String condicion;
    private String contrasenha;
    private String correo;
    private static String nombre;
    private String rol;
    private String validado;
    private static String id;
    private String comentario;

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getContrasenha() {
        return contrasenha;
    }

    public void setContrasenha(String contrasenha) {
        this.contrasenha = contrasenha;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public static String getNombre() {
        return nombre;
    }

    public static void setNombre(String nombre) {
        usuarioSesion.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getValidado() {
        return validado;
    }

    public void setValidado(String validado) {
        this.validado = validado;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        usuarioSesion.id = id;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
