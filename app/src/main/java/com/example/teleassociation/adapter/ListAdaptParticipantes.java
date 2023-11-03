package com.example.teleassociation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.teleassociation.ListaParticipantes;
import com.example.teleassociation.R;

import java.util.List;

public class ListAdaptParticipantes extends RecyclerView.Adapter<ListAdaptParticipantes.ViewHolder>{
    private List<ListaParticipantes> mData;
    private LayoutInflater mInflater;
    private Context context;
    public ListAdaptParticipantes(List<ListaParticipantes> itemList, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData=itemList;
    }
    @Override
    public int getItemCount(){return mData.size();}
    @Override
    public ListAdaptParticipantes.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.list_participantes,null);
        return new ListAdaptParticipantes.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdaptParticipantes.ViewHolder holder, final int position){
        holder.binData(mData.get(position));
    }
    public void setItems (List<ListaParticipantes> items){mData=items;}
public class ViewHolder extends RecyclerView.ViewHolder{
    TextView actividad,rol,asignacion,tituloActividad,tituloRol,tituloAsignacion,iconImage;
    ViewHolder(View itemView){
        super(itemView);
        tituloActividad=itemView.findViewById(R.id.actividades);
        tituloRol=itemView.findViewById(R.id.rol);
        tituloAsignacion=itemView.findViewById(R.id.asignacion);
        actividad = itemView.findViewById(R.id.miActividad);
        rol = itemView.findViewById(R.id.rolParticipante);
        asignacion = itemView.findViewById(R.id.asignacionParticipante);
        iconImage=itemView.findViewById(R.id.imageView16); //oa
    }
    void binData(final ListaParticipantes item){
        actividad.setText(item.getActividad());
        rol.setText(item.getRol());
        asignacion.setText(item.getAsignacion());
    }
}
}
