package com.example.teleassociation.services;

import com.example.teleassociation.dto.actividadDTO;
import com.example.teleassociation.dto.eventoDTO;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ActividadService {

    @GET("/listaActividades")
    Call<actividadDTO> getActividadLista();
}
