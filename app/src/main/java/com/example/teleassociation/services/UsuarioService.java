package com.example.teleassociation.services;

import com.example.teleassociation.dto.eventoDTO;
import com.example.teleassociation.dto.usuarioDTO;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UsuarioService {

    @GET("/listaUsuarios")
    Call<usuarioDTO> getUsuarios();
}
