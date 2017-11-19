package com.zenzanodanny.trustme;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.zenzanodanny.trustme.Objetos.transacciones;
import com.zenzanodanny.trustme.R;

import java.util.ArrayList;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.zenzanodanny.trustme.Objetos.transacciones;

import java.util.ArrayList;

/**
 * Created by Zenzano on 9/11/2017.
 */

public class AdapterPurchase extends RecyclerView.Adapter<com.zenzanodanny.trustme.AdapterPurchase.ViewHolderTransactions>
        implements View.OnClickListener{

    ArrayList<transacciones> ListaTransacciones;
    private View.OnClickListener listener;  //Este es el ESCUCHADOR
    private Context mContext;
    private Fragment mFragment;

    public AdapterPurchase(ArrayList<transacciones> listaTransacciones,Fragment mifragment) {
        ListaTransacciones = listaTransacciones;
        mFragment = mifragment;
    }

    @Override
    public com.zenzanodanny.trustme.AdapterPurchase.ViewHolderTransactions onCreateViewHolder(ViewGroup parent, int viewType) {
        //Aqui enlazo con item_list_personajes.xml, solo cambio la referencia al archivo itemlist
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.purchases,parent,false);
        //El return devuelve el mismo tipodenombre del HOLDER

        view.setOnClickListener(this); //pongo escuchar al Listener

        return new com.zenzanodanny.trustme.AdapterPurchase.ViewHolderTransactions(view);
    }

    @Override
    public void onBindViewHolder(com.zenzanodanny.trustme.AdapterPurchase.ViewHolderTransactions holder, int position) {
        holder.asignarDatos(ListaTransacciones.get(position));
    }

    @Override
    public int getItemCount() {

        return ListaTransacciones.size();
    }

    //CREO ESTE METODO PARA PODER LLAMAR EL LISTENER DESDE AFUERA
    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }

    public class ViewHolderTransactions extends RecyclerView.ViewHolder {
        ImageView RVidImagen;
        TextView RVidItem;
        TextView RVidcelular;
        TextView RVidInfo;
        TextView RVidFecha;
        TextView RVidhora;
        TextView RVidEstado;

        View Vista;


        public ViewHolderTransactions(View itemView) {
            super(itemView);
            Vista=itemView;
            RVidImagen=(ImageView) itemView.findViewById(R.id.idImagenP);
            RVidItem=(TextView) itemView.findViewById(R.id.idItemP);
            RVidcelular=(TextView) itemView.findViewById(R.id.idcelularP);
            RVidInfo=(TextView) itemView.findViewById(R.id.idInfoP);
            RVidFecha=(TextView) itemView.findViewById(R.id.idFechaP);
            RVidhora=(TextView) itemView.findViewById(R.id.idhoraP);
            RVidEstado=(TextView) itemView.findViewById(R.id.idEstadoP);
        }

        public void asignarDatos(transacciones mitransaccion) {
            if(mitransaccion!=null) {
                System.out.println("LOGDANNY:VenderFragmento:" + mitransaccion.toString());
                RVidItem.setText(mitransaccion.getTr_titulo());
                RVidcelular.setText(mitransaccion.getTr_id_vendedor()); //Cambio al id vendedor por que es mi lista de objetos que yo compre
                RVidInfo.setText(mitransaccion.getTr_descripcion());
                RVidFecha.setText(mitransaccion.getTr_fecha_registro().substring(0, 10));
                RVidhora.setText(mitransaccion.getTr_fecha_registro().substring(11, 19));
                RVidEstado.setText(mitransaccion.getTr_estado_transaccion());
                if((mitransaccion.getTr_fotografia().compareTo("")!=0)&&(mitransaccion.getTr_fotografia().compareTo("FOTOGRAFIA")!=0)){


                    //Cargar imagen
                    Glide.with(mFragment)
                            .load(mitransaccion.getTr_fotografia())
                            .asBitmap()
                            .fitCenter()
                            .centerCrop()
                            .placeholder(R.drawable.relojdearena)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new BitmapImageViewTarget(RVidImagen) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(mFragment.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    RVidImagen.setImageDrawable(circularBitmapDrawable);
                                }
                            });

                }
            }

        }
    }
}
