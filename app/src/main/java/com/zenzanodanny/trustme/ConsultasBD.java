package com.zenzanodanny.trustme;

/**
 * Created by Zenzano on 5/11/2017.
 */
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zenzanodanny.trustme.Objetos.FirebaseReferences;
import com.zenzanodanny.trustme.Objetos.Usuario;

public class ConsultasBD {
    public Usuario miUsuario;

    public ConsultasBD() {
    }
    public Usuario getUserBDInfo(String USER_ID_KEY) {
        miUsuario=null;
        miUsuario = new Usuario();
        //REVISO QUE SE HAYA GUARDADO LOS VALORES DE DATOS DE USUARIO
        FirebaseDatabase midatabaseUE = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseUE = midatabaseUE.getReference(FirebaseReferences.User_Reference);
        //obtengo si esta registrado
        System.out.println("LOGDANNY:CELLAUTH:getUserBDInfo:1");
        Query query_get_unique_user = mDatabaseUE.child(USER_ID_KEY);
        System.out.println("LOGDANNY:CELLAUTH:getUserBDInfo:2");
        query_get_unique_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot miSnapshot) {
                if (miSnapshot.exists()) {
                    System.out.println("LOGDANNY:CELLAUTH:getUserBDInfo:EL USUARIO FUE REGUSTRADO Y OBTENGO SUS DATOS");
                    System.out.println("LOGDANNY:CELLAUTH:getUserBDInfo::Snapshot to string"+miSnapshot.toString());
                    miUsuario=miSnapshot.getValue(Usuario.class);
                }else{
                    miUsuario=null;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        System.out.println("LOGDANNY:CELLAUTH:getUserBDInfo:3");
        return miUsuario;
    }
}
