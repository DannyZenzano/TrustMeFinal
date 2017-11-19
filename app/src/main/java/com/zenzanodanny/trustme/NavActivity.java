package com.zenzanodanny.trustme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class NavActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager myfragmentmanager =getSupportFragmentManager();
            FragmentTransaction transccion = myfragmentmanager.beginTransaction();


            switch (item.getItemId()) {
                case R.id.navigation_buscar:
                    transccion.replace(R.id.content,new BuscarFragment()).commit();
                    return true;
                case R.id.navigation_home:
                    transccion.replace(R.id.content,new HomeFragment()).commit();
                    return true;
                case R.id.navigation_vender:
                    transccion.replace(R.id.content,new VenderFragment()).commit();
                    return true;
                case R.id.navigation_comprar:
                    transccion.replace(R.id.content,new ComprarFragment()).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        System.out.println("LOGDANNY:INGRESOMETODO"+" INGRESO A NavActivity");
        //IMPRIMO MIS VARIABLES DE SESSION
        TelephoneUtils mytelephoneutilstest=new TelephoneUtils(NavActivity.this);
        mytelephoneutilstest.imprimir_Shared_Preferences();



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager myfragmentmanager =getSupportFragmentManager();
        FragmentTransaction transccion = myfragmentmanager.beginTransaction();
        transccion.replace(R.id.content,new HomeFragment()).commit();






    }

    public void creartransaccion(View view) {
        Intent intent = new Intent(NavActivity.this, NewTransactionActivity.class);
        startActivity(intent);
        finish();
    }
}
