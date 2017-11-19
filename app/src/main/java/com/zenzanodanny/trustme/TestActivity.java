package com.zenzanodanny.trustme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zenzanodanny.trustme.Objetos.FirebaseReferences;
import com.zenzanodanny.trustme.Objetos.transacciones;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class TestActivity extends AppCompatActivity {
    private Button btnEnviarCodigo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);



    }
    public void requestCode(View view) {



    }

    public void iraotrolado(View view) {
        //Vuelvo a pagina de registro o logueo
        Intent intent = new Intent(TestActivity.this, VerCompraActivity.class);
        intent.putExtra("parametro", "parametro");
        startActivity(intent);
        finish();

    }
}
