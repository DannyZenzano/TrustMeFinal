package com.zenzanodanny.trustme;
//public class AccountActivity extends AppCompatActivity {

import android.app.ProgressDialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

public class AccountActivity extends AppCompatActivity {

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private Button btnLogOut;

    private int CAMERA_REQUEST_CODE = 0;
    private ProgressDialog progressDialog;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private ImageView imageProfile;
    private TextView textName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        textName = (TextView) findViewById(R.id.txtName);
        imageProfile = (ImageView) findViewById(R.id.imageView);

        btnLogOut = (Button) findViewById(R.id.btn_singout);
        System.out.println("LOGDANNY:AccountActivity:1");

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("LOGDANNY:AccountActivity:2");
                if (mAuth.getCurrentUser() != null) {
                    mAuth.signOut();
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();


    }










}