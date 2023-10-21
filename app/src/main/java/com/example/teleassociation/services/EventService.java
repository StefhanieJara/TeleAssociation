package com.example.teleassociation.services;
import com.example.teleassociation.dto.evento;
import com.example.teleassociation.dto.eventoDTO;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface EventService {
    @GET("/listaEventos")
    Call<eventoDTO> getEventList();
}
