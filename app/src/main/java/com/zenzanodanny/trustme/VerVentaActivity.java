package com.zenzanodanny.trustme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zenzanodanny.trustme.Objetos.FirebaseReferences;
import com.zenzanodanny.trustme.Objetos.Usuario;

public class VerVentaActivity extends AppCompatActivity {
    String KeyTransaction;
    String Titulo;
    String IdVendedor;
    String IdComprador;
    String Estado_transaccion;
    String Descripcion;
    String Estado_conservacion;
    String fotografia;

    private TextView TVViewTitulo;
    private TextView TVDescripcion;
    private TextView TVVENDEDOR;
    private ImageView IVIMG;
    private Button BotonVerCalificacion;

    public Usuario DatosUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_venta);
        //Boton Atras


        //RECUPERO LAS VARIABLES ENVIADAS
        System.out.println("LOGDANNY:VERCOMPRA:0");

        //Obtengo mis datos
        KeyTransaction=getIntent().getExtras().getString("KeyTransaction");
        System.out.println("LOGDANNY:VERCOMPRA:KeyTransaction:"+KeyTransaction);

        Titulo=getIntent().getExtras().getString("Titulo");
        IdVendedor=getIntent().getExtras().getString("IdVendedor");
        IdComprador=getIntent().getExtras().getString("IdComprador");
        Estado_transaccion=getIntent().getExtras().getString("Estado_transaccion");
        Descripcion=getIntent().getExtras().getString("Descripcion");
        Estado_conservacion=getIntent().getExtras().getString("Estado_conservacion");
        fotografia=getIntent().getExtras().getString("fotografia");

        //Instancio mis campos
        TVViewTitulo = (TextView) findViewById(R.id.txtTitulo);
        TVDescripcion = (TextView) findViewById(R.id.txtdescripcion);
        TVVENDEDOR = (TextView) findViewById(R.id.txt_Nombre_Vendedor);
        IVIMG = (ImageView) findViewById(R.id.ImgComp);
        BotonVerCalificacion = (Button) findViewById(R.id.button_raiting_ver);


        //SETEO LOS CAMPOS
        TVViewTitulo.setText(Titulo);
        TVDescripcion.setText(getString(R.string.txt_estado)+": "+Estado_conservacion+"\n"+Descripcion);
        //Seteariamagen
        Glide.with(this)
                .load(fotografia)
                .placeholder(R.drawable.relojdearena)
                .into(IVIMG);
        //SETEO EL NOMBRE DEL USUARIO VENDEDOR
        SetUsuarioID();
        //Desactivar el boton para ver la calificacion si no se califico aun.
        if(Estado_transaccion.compareTo("calificado")==0){
            BotonVerCalificacion.setEnabled(true);
            BotonVerCalificacion.setText("VER CALIFICACION"); //txt_calificar
            BotonVerCalificacion.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }

    }

    public void VerVendedor(View view) {
        System.out.println("LOGDANNY:VERCOMPRAS:VerVendedor");
    }

    public void SetUsuarioID() {
        //Obtengo Los valores de usuario de la Base de Datos.
        FirebaseDatabase midatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = midatabase.getReference(FirebaseReferences.User_Reference);

        Query query_get_unique_user = mDatabase.child(IdVendedor);
        query_get_unique_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot miSnapshot) {
                if (miSnapshot.exists()) {
                    System.out.println("LOGDANNY:VERCOMPRAS:ONDATACHANGE:registrar:EL USUARIO FUE REGUSTRADO Y OBTENGO SUS DATOS");
                    DatosUsuario=miSnapshot.getValue(Usuario.class);
                    System.out.println("LOGDANNY:VERCOMPRAS:ONDATACHANGE:DATOS DE USUARIO DE BD" +DatosUsuario.toString());
                    if(DatosUsuario!=null){
                        String user_msisdn=DatosUsuario.getUser_msisdn();
                        TVVENDEDOR.setText(DatosUsuario.getUser_name()+" "+DatosUsuario.getUser_lastname());
                    }
                } else {
                    //NO PUEDE PASAR
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        ////////////////////////

    }

    public void enviar_a_ver_calificacion(View view) {
        Intent intent = new Intent(VerVentaActivity.this, RatingActivityVer.class);
        intent.putExtra("KeyTransaction",KeyTransaction);
        intent.putExtra("Titulo",Titulo);
        intent.putExtra("IdVendedor",IdVendedor);
        intent.putExtra("IdComprador",IdComprador);
        intent.putExtra("Estado_transaccion",Estado_transaccion);
        intent.putExtra("Descripcion",Descripcion);
        intent.putExtra("Estado_conservacion",Estado_conservacion);
        intent.putExtra("fotografia",fotografia);
        


        startActivity(intent);
        finish();
    }


    public void IrAVentas(View view) {
        Intent intent = new Intent(VerVentaActivity.this, NavActivity.class);
        startActivity(intent);
        finish();

    }

}
