package com.example.teleassociation.services;
import com.example.teleassociation.dto.evento;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface EventService {
    @GET("/listaEventos")
    Call<List<evento>> getEventList();
}
