package com.example.teleassociation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.teleassociation.R;
import com.example.teleassociation.dto.EventoFoto;

import java.util.List;

public class FotosEventoAdapter extends RecyclerView.Adapter<FotosEventoAdapter.ViewHolder> {

    private List<EventoFoto> fotos;

    public FotosEventoAdapter(List<EventoFoto> fotos) {
        this.fotos = fotos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foto_evento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventoFoto foto = fotos.get(position);
        Glide.with(holder.itemView.getContext())
                .load(foto.getUrl())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return fotos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewFoto);
        }
    }
}
