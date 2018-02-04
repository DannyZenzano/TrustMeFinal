package com.zenzanodanny.trustme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zenzanodanny.trustme.Objetos.FirebaseReferences;
import com.zenzanodanny.trustme.Objetos.rating;

public class RatingActivityVer extends AppCompatActivity {
    String KeyTransaction;
    String Titulo;
    String IdVendedor;
    String IdComprador;
    String Estado_transaccion;
    String Descripcion;
    String Estado_conservacion;
    String fotografia;


    private RatingBar RBarArticulo;
    private RatingBar RBarComunicacion;
    private RatingBar RBarPuntualidad;
    private Button BotonRating;
    private TextView Titulo1;
    private TextView Titulo2;

    rating MiCalificacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_ver);

        //
        RBarArticulo = (RatingBar) findViewById(R.id.ratingBarArticulo);
        RBarComunicacion = (RatingBar) findViewById(R.id.ratingBarComunicacion);
        RBarPuntualidad = (RatingBar) findViewById(R.id.ratingBarPuntualidad);
        BotonRating =(Button) findViewById(R.id.button_Save_Rating);
        Titulo1 = (TextView) findViewById(R.id.txtTituloTransaccion);
        Titulo2 = (TextView) findViewById(R.id.txtTituloTransaccion2);

        //Obtengo mis datos
        KeyTransaction=getIntent().getExtras().getString("KeyTransaction");
        Titulo=getIntent().getExtras().getString("Titulo");
        IdVendedor=getIntent().getExtras().getString("IdVendedor");
        IdComprador=getIntent().getExtras().getString("IdComprador");

        Estado_transaccion=getIntent().getExtras().getString("Estado_transaccion");
        Descripcion=getIntent().getExtras().getString("Descripcion");
        Estado_conservacion=getIntent().getExtras().getString("Estado_conservacion");
        fotografia=getIntent().getExtras().getString("fotografia");

        System.out.println("LOGDANNY:RATINGACTIVITY:IdComprador:"+IdComprador);
        System.out.println("LOGDANNY:RATINGACTIVITY:IdVendedor:"+IdVendedor);
        System.out.println("LOGDANNY:RATINGACTIVITY:Titulo:"+Titulo);
        System.out.println("LOGDANNY:RATINGACTIVITY:KeyTransaction:"+KeyTransaction);

        //SETEO EL TITULO
        Titulo1.setText(Titulo);
        //obtengo los datos de rating de la base de datos

        //Obtengo Los valores de usuario de la Base de Datos.
        FirebaseDatabase midatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = midatabase.getReference(FirebaseReferences.Rating_Reference);

        Query query_get_unique_user = mDatabase.child(KeyTransaction);
        query_get_unique_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot miSnapshot) {
                if (miSnapshot.exists()) {
                    Titulo2.setText(getString(R.string.TransaccionYaCalificada));
                    rating ratingdb = new rating();
                    ratingdb=miSnapshot.getValue(rating.class);
                    System.out.println("LOGDANNY:RatingActivity:ONDATACHANGE:DATOS DE RATING DE BD" +ratingdb.toString());
                    RBarArticulo.setRating(ratingdb.getArticulo());
                    RBarComunicacion.setRating(ratingdb.getComunicacion());
                    RBarPuntualidad.setRating(ratingdb.getPuntualidad());

                } else {
                    //No existe el rating aun.
                    Titulo2.setText(getString(R.string.TransaccionNoCalificada));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        ////////////////////////

    }
    public void GuardarRating(View view) {
        //Obtengo los valores seteados en el rating y los guardo en el objeto rating
        MiCalificacion=new rating(RBarArticulo.getRating(),RBarComunicacion.getRating(),RBarPuntualidad.getRating(),IdComprador,IdVendedor);

        //SUBO EL ARCHIVO A LA BASE DE DATOS
        FirebaseDatabase midatabase=FirebaseDatabase.getInstance();
        DatabaseReference mDatabase=midatabase.getReference(FirebaseReferences.Rating_Reference);
        DatabaseReference currentUserDB=mDatabase.child(KeyTransaction);
        currentUserDB.setValue(MiCalificacion);


        //Actualizo el estado de la Transaccion a "calificado"
        FirebaseDatabase midatabase2=FirebaseDatabase.getInstance();
        DatabaseReference mDatabase2=midatabase2.getReference(FirebaseReferences.Transaction_Reference);
        DatabaseReference currenttransaccioinDB=mDatabase2.child(KeyTransaction).child("tr_estado_transaccion");
        currenttransaccioinDB.setValue("calificado");

        //
        Intent intent = new Intent(RatingActivityVer.this, NavActivity.class);
        startActivity(intent);
        finish();

    }

    public void cancelar(View view) {
        Intent intent = new Intent(RatingActivityVer.this, VerVentaActivity.class);


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
}
