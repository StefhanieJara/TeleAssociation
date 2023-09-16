package com.example.teleassociation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapEvent extends RecyclerView.Adapter<ListAdapEvent.ViewHolder> {
    private List<listElement> mData;
    private LayoutInflater mInflater;
    private Context context;
    public ListAdapEvent(List<listElement> itemList, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData=itemList;
    }
    @Override
    public int getItemCount(){return mData.size();}
    @Override
    public ListAdapEvent.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.list_element,null);
        return new ListAdapEvent.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdapEvent.ViewHolder holder, final int position){
        holder.binData(mData.get(position));
    }
    public void setItems (List<listElement> items){mData=items;}
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView evento,hora,tituloActividad,tituloFecha,iconImage;
        ViewHolder(View itemView){
            super(itemView);
            tituloActividad=itemView.findViewById(R.id.actividades);
            tituloFecha=itemView.findViewById(R.id.fechaHora);
            evento = itemView.findViewById(R.id.miActividad);
            hora = itemView.findViewById(R.id.fechaHoraRegistrada);
            iconImage=itemView.findViewById(R.id.verEvento);
        }
        void binData(final listElement item){
            evento.setText(item.getEvento());
            hora.setText(item.getHora());
        }
    }
}
