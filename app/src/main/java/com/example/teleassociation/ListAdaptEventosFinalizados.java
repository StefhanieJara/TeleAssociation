package com.example.teleassociation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdaptEventosFinalizados extends RecyclerView.Adapter<ListAdaptEventosFinalizados.ViewHolder> {
    private List<listElementFin> mData;
    private LayoutInflater mInflater;
    private Context context;
    public ListAdaptEventosFinalizados(List<listElementFin> itemList, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData=itemList;
    }
    @Override
    public int getItemCount(){return mData.size();}
    @Override
    public ListAdaptEventosFinalizados.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.list_eventos_finalizados,null);
        return new ListAdaptEventosFinalizados.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdaptEventosFinalizados.ViewHolder holder, final int position){
        holder.binData(mData.get(position));
    }
    public void setItems (List<listElementFin> items){mData=items;}
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView evento,tituloActividad,iconImage;
        ViewHolder(View itemView){
            super(itemView);
            tituloActividad=itemView.findViewById(R.id.actividades);
            evento = itemView.findViewById(R.id.miActividad);
            iconImage=itemView.findViewById(R.id.verEvento);
        }
        void binData(final listElementFin item){
            evento.setText(item.getEvento());
        }
    }
}
