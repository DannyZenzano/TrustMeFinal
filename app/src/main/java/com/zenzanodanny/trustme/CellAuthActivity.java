package com.zenzanodanny.trustme;
//
import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
//import com.melardev.tutorialsfirebase.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zenzanodanny.trustme.Objetos.FirebaseReferences;
import com.zenzanodanny.trustme.Objetos.Usuario;
import com.zenzanodanny.trustme.R;

import java.util.concurrent.TimeUnit;

import static android.widget.TextView.BufferType.EDITABLE;

//public class ActivityPhoneAuth extends AppCompatActivity {
public class CellAuthActivity extends AppCompatActivity {

    private SessionManager mysession;

    private View layoutPrincipal;
    private EditText etxtPhone;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText etxtPhoneCode;
    private String mVerificationId;
    private ProgressDialog mProgress;
    private Button btnEnviarCodigo;

    public int NivelDeRegistroDeBD;
    public boolean registrarenbd=false;
    public String bd_msisdn;

    public boolean User_exists;
    public String userID;
    public String tipo_de_usuario;
    public Usuario DatosUsuario;
    public boolean ERRORFATAL;

    public String CODIGO_PAIS;
    public int CODIGO_TELEF_PAIS;

    //Variables para permisos
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;
    private static final int READ_PHONE_STATE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;

    public boolean permisos_concedidos;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_auth);

        //IMPRIMO MIS VARIABLES DE SESSION
        System.out.println("LOGDANNY:INGRESOMETODO"+"onCreate");
        TelephoneUtils mytelephoneutilstest=new TelephoneUtils(CellAuthActivity.this);
        mytelephoneutilstest.imprimir_Shared_Preferences();


        //VIRIFICO QUE SE HAYA GUARDADO EL PREFIJO
        mysession= new SessionManager(getApplicationContext());
        int prefijoLocal=mysession.getKEY_MSISDN_prefix();
        System.out.println("LOGDANNY:CELLAUTH:ingresando:prefijoLocal:"+prefijoLocal);
        System.out.println("LOGDANNY:CELLAUTH:ingresando:prefijoLocal country:"+mysession.getKEY_COUNTRY());








        // INICIALIZAR
        permisos_concedidos=false;
        registrarenbd=false;
        User_exists=false;
        ERRORFATAL=false;

        layoutPrincipal=(LinearLayout) findViewById(R.id.main_layout);
        etxtPhone = (EditText) findViewById(R.id.etxtPhone);
        etxtPhoneCode = (EditText) findViewById(R.id.etxtPhoneCode);
        btnEnviarCodigo = (Button) findViewById(R.id.button_verify_phone);
        //btnEnviarCodigo.setBackgroundColor(getResources().getColor(R.color.grey_100));
        //btnEnviarCodigo.setEnabled(false);




        //Inicializo la Autentificacion de firebase
        FirebaseApp.initializeApp(CellAuthActivity.this);
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);



        //OBTENGO EL ESTADO DEL NIVEL DE REGISTRO DE LAS VARIIABLES DE SESION
        System.out.println("LOGDANNY:ONCREATE milog:1");
//        mysession= new SessionManager(getApplicationContext());
        System.out.println("LOGDANNY:ONCREATE milog:2");
        NivelDeRegistroDeBD=mysession.getRegisterLevel();
        System.out.println("LOGDANNY:ONCREATE milog:3"+NivelDeRegistroDeBD);





        //SI EL NIVEL DE REGISTRO ESTA EN 0, ENTONCES DESAUTENTIFICO AL USUARIO
        if(NivelDeRegistroDeBD==0){
            //Des Autentifico y envio al registro
            if (mAuth.getCurrentUser() != null) {
                mAuth.signOut();
            }
            //REVISO QUE TENGA PERMISO PARA LEER LOS DATOS DEL TELEFONO
            permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);
            this.validarPermisos();
            if(permisos_concedidos) {
                TelephonyManager tmanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                //MSISDN_DEL_TELEFONO = tmanager.getLine1Number();
                CODIGO_PAIS= tmanager.getSimCountryIso().toUpperCase();
                TelephoneUtils myTelephoneUtils = new TelephoneUtils(CellAuthActivity.this);
                CODIGO_TELEF_PAIS=myTelephoneUtils.getCountryTelephoneCode(CODIGO_PAIS);
                System.out.println("LOGDANNY:CELLAUTH:getSimCountryIso:"+CODIGO_PAIS+": CODIGO NUMERICO"+CODIGO_TELEF_PAIS);
            }else{
                System.out.println("LOGDANNY:CELLAUTH:permiso no concedido");
            }
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                System.out.println("LOGDANNY:INGRESOMETODO"+"onAuthStateChanged");
                if (firebaseAuth.getCurrentUser() != null) {
                    System.out.println("LOGDANNY:CELLAUTH:SE AUTENTIFICO AL USUARIO");
                    if(registrarenbd){
                        //Obtengo el id de usuarios de la autenticacion realizada por firebase
                        userID=mAuth.getCurrentUser().getUid();
                        //OBTENGO EL NUEVO USERID MSISDN INTERNACIONAL
                        //REGISTRAR EL USUARIO SI NO EXISTE EN BD
                        userID=CODIGO_TELEF_PAIS+bd_msisdn;
                        registrarSinoExist();
                    }else{
                        System.out.println("LOGDANNY:CELLAUTH:OnAuthStateChanged:No Se Registra al usuario");
                        //CARGO LOS DATOS DE LA BD
                        //Escondo EL Layout principal para que no se vea.
                        layoutPrincipal.setVisibility(View.GONE);
                        userID=mAuth.getCurrentUser().getUid();
                        //OBTENGO EL NUEVO USERID MSISDN INTERNACIONAL
                        registrarSinoExist();
                    }


                }
            }
        };

        System.out.println("LOGDANNY:CELLAUTH:4");

    }

    public void requestCode(View view) {
        if(NivelDeRegistroDeBD==0){
            //REVISO QUE TENGA PERMISO PARA LEER LOS DATOS DEL TELEFONO
            permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);
            this.validarPermisos();
            if(permisos_concedidos) {
                TelephonyManager tmanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                //MSISDN_DEL_TELEFONO = tmanager.getLine1Number();
                CODIGO_PAIS= tmanager.getSimCountryIso().toUpperCase();
                TelephoneUtils myTelephoneUtils = new TelephoneUtils(CellAuthActivity.this);
                CODIGO_TELEF_PAIS=myTelephoneUtils.getCountryTelephoneCode(CODIGO_PAIS);
                System.out.println("LOGDANNY:CELLAUTH:getSimCountryIso:"+CODIGO_PAIS+": CODIGO NUMERICO"+CODIGO_TELEF_PAIS);
            }else{
                Toast.makeText(this,"Debe Otorgar los permisos para continuar", Toast.LENGTH_LONG).show();
                return;
            }
        }

        System.out.println("LOGDANNY:INGRESOMETODO"+"requestCode");
        btnEnviarCodigo.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        btnEnviarCodigo.setEnabled(true);
        etxtPhoneCode.setEnabled(true);
        System.out.println("LOGDANNY:CELLAUTH:requestCode:1");

        final String phoneNumber = etxtPhone.getText().toString();

        System.out.println("LOGDANNY:CELLAUTH:requestCode:"+phoneNumber);
        if (TextUtils.isEmpty(phoneNumber))
            return;

        mProgress.setMessage("Solicitando Codigo");
        mProgress.show();
        System.out.println("LOGDANNY:CELLAUTH:requestCode:2");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, 60, TimeUnit.SECONDS, CellAuthActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        System.out.println("LOGDANNY:CELLAUTH:USUARIO REGISTRADO NO ES NECESARIO ENVIAR CODIGO DE VERIFICACION");
                        //Called if it is not needed to enter verification code
                        signInWithCredential(phoneAuthCredential);
                        //Seteo los valores para que al autentificar se llame al metodo registrarSiNoExiste
                        //Asi obtendremos los datos de usuario.
                        registrarenbd=true;
                        bd_msisdn=phoneNumber;
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        System.out.println("LOGDANNY:CELLAUTH:onVerificationFailed");
                        //incorrect phone number, verification code, emulator, etc.
                        Toast.makeText(CellAuthActivity.this,getString(R.string.txt_toast_auth_error)+" " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        System.out.println("LOGDANNY:CELLAUTH:requestCode:5");
                        //now the code has been sent, save the verificationId we may need it
                        super.onCodeSent(verificationId, forceResendingToken);

                        mVerificationId = verificationId;
                        System.out.println("LOGDANNY:CELLAUTH:requestCode:mVerificationId:"+mVerificationId);
                        registrarenbd=true;
                        bd_msisdn=phoneNumber;
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String verificationId) {
                        System.out.println("LOGDANNY:CELLAUTH:requestCode:6");
                        //called after timeout if onVerificationCompleted has not been called
                        super.onCodeAutoRetrievalTimeOut(verificationId);
                        Toast.makeText(CellAuthActivity.this,getString(R.string.txt_toast_time_out_code), Toast.LENGTH_LONG).show();

                    }
                }
        );
        mProgress.dismiss();
    }
    public void registrarSinoExist() {
        System.out.println("LOGDANNY:INGRESOMETODO"+"registrarSinoExist");
        //Verifico el nivel de registro
        int NivelDeRegistro=mysession.getRegisterLevel();
        if(NivelDeRegistro==2){
            //YA ESTA REGISTRADO
            System.out.println("LOGDANNY:REGISTRARSINOEXISTE:YAESTAREGISTRADO");
            Toast.makeText(CellAuthActivity.this,getString(R.string.txt_toast_logueado), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CellAuthActivity.this, NavActivity.class);
            startActivity(intent);
            finish();
        }else if(NivelDeRegistro==1){
            System.out.println("LOGDANNY:REGISTRARSINOEXISTE:FALTA 2da PARTE DE REGISTRO");
            //Se registro su Numero, pero no su nombre
            //NO TERMINO EL REGISTO
            Toast.makeText(CellAuthActivity.this,getString(R.string.txt_toast_registro), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CellAuthActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();

        }else{
            //Debe ser 0, es decir que no se registro nunca
            //Registrar usuario
            if(registrarenbd) {
                //Si se autentifico
                registrar();
            }else{
                //No Se autentifico y debe iniciarse el proceso de autentificacion
                if (mAuth.getCurrentUser() != null) {
                    System.out.println("LOGDANNY:CELLAUTH:registrarSinoExist:DESAUTENTICO");
                    mAuth.signOut();
                }
                System.out.println("LOGDANNY:CELLAUTH:registrarSinoExist:NO REGISTRADO EN BD");
                layoutPrincipal.setVisibility(View.VISIBLE);
            }
        }

    }



    public void registrar() {
        System.out.println("LOGDANNY:INGRESOMETODO"+"registrar");


        /////////////////////////////////////VERIFICO SI YA ESTA REGISTRADO/////////////////////
        //Inicializo la Autentificacion de firebase
        FirebaseApp.initializeApp(CellAuthActivity.this);


        //Obtengo Los valores de usuario de la Base de Datos.
        FirebaseDatabase midatabaseUserinfo = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseUI = midatabaseUserinfo.getReference(FirebaseReferences.User_Reference);

        Query query_get_unique_user = mDatabaseUI.child(userID);
        query_get_unique_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot miSnapshot) {
                Usuario UsuarioParaRegistrar;
                if (miSnapshot.exists()) {

                    System.out.println("LOGDANNY:RegisterActivity:ONDATACHANGE:registrar:EL USUARIO FUE REGUSTRADO Y OBTENGO SUS DATOS");
                    Usuario MiUsuarioAntiguo=new Usuario();
                    MiUsuarioAntiguo=miSnapshot.getValue(Usuario.class);
                    //ES UN USUARIO YA REGISTRADO USO LOS DATOS ANTIGUOS
                    UsuarioParaRegistrar=MiUsuarioAntiguo;
                    System.out.println("LOGDANNY:RegisterActivity:ONDATACHANGE:DATOS DE USUARIO DE BD" +MiUsuarioAntiguo.toString());
                    if(MiUsuarioAntiguo!=null){
                        String user_msisdn=MiUsuarioAntiguo.getUser_msisdn();
                        String User_name=MiUsuarioAntiguo.getUser_msisdn();
                        String user_lastname=MiUsuarioAntiguo.getUser_lastname();
                        if((user_msisdn.compareTo("")!=0)) {
                            if((user_msisdn.compareTo("")!=0)&&(user_msisdn.compareTo("")!=0)){
                                //Usuario ya registrado.
                            }else{
                                //usuario nuevo
                            }
                        }
                    }
                }else{
                    //ES UN USUARIO NUEVO ACTUALIZO CON DATOS NUEVOS

                    UsuarioParaRegistrar=new Usuario(bd_msisdn,CODIGO_PAIS);
                }

                ///---
                if (userID != null){
                    FirebaseDatabase midatabase=FirebaseDatabase.getInstance();
                    DatabaseReference mDatabase=midatabase.getReference(FirebaseReferences.User_Reference);


                    DatabaseReference currentUserDB=mDatabase.child(userID);
                    System.out.println("LOGDANNY:CELLAUTH:registrar:REGISTRANDO USUARIO:"+UsuarioParaRegistrar.toString());
                    currentUserDB.setValue(UsuarioParaRegistrar);
                    //currentUserDB.child("user_msisdn").setValue(miusuario.getUser_msisdn());
                    System.out.println("LOGDANNY:CELLAUTH:registrar:Verificamos los datos desde BD");
                    //REVISO QUE SE HAYA GUARDADO LOS VALORES DE DATOS DE USUARIO
                    FirebaseDatabase midatabaseUE = FirebaseDatabase.getInstance();
                    DatabaseReference mDatabaseUE = midatabaseUE.getReference(FirebaseReferences.User_Reference);
                    //obtengo si esta registrado
                    Query query_get_unique_user = mDatabaseUE.child(userID);
                    query_get_unique_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot miSnapshot) {
                            if (miSnapshot.exists()) {
                                System.out.println("LOGDANNY:CELLAUTH:registrar:EL USUARIO FUE REGUSTRADO Y OBTENGO SUS DATOS");
                                System.out.println("LOGDANNY:CELLAUTH:registrar::Snapshot to string"+miSnapshot.toString());
                                DatosUsuario=miSnapshot.getValue(Usuario.class);
                                System.out.println("LOGDANNY:CELLAUTH:registrar:DATOS DE USUARIO DE BD" +DatosUsuario.toString());
                                if(DatosUsuario!=null){
                                    mysession.saveRegisterLevel(1);
                                    mysession.saveUserId(userID);
                                    mysession.saveKEY_COUNTRY(CODIGO_PAIS);
                                    mysession.saveKEY_MSISDN(Integer.parseInt(bd_msisdn));
                                    mysession.saveKEY_MSISDN_prefix(CODIGO_TELEF_PAIS);

                                    //VIRIFICO QUE SE HAYA GUARDADO EL PREFIJO
                                    System.out.println("LOGDANNY:CELLAUTH:VERIFICO LAS VARIABLES QUE ACABO DE GUARDAR");
                                    TelephoneUtils mytelephoneutilstest=new TelephoneUtils(CellAuthActivity.this);
                                    mytelephoneutilstest.imprimir_Shared_Preferences();

                                    if(DatosUsuario.getUser_name().compareTo("")==0) {
                                        //NO TERMINO EL REGISTO
                                        Toast.makeText(CellAuthActivity.this,getString(R.string.txt_toast_registro), Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(CellAuthActivity.this, RegisterActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        //YA TERMINO EL REGISTO
                                        Toast.makeText(CellAuthActivity.this,getString(R.string.txt_toast_logueado), Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(CellAuthActivity.this, NavActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            } else {
                                //El usuario no se registro ERROR
                                ERRORFATAL=true;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });


                    //
                }


                ////---

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });





        ////////////////////////////////////VERIFICO SI YA ESTA REGISTRADO/////////////////////

    }

    private void signInWithCredential(PhoneAuthCredential phoneAuthCredential) {
        System.out.println("LOGDANNY:INGRESOMETODO"+"signInWithCredential");
        System.out.println("LOGDANNY:CELLAUTH:signInWithCredential");
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CellAuthActivity.this,getString(R.string.txt_toast_aut_correctamente), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CellAuthActivity.this,getString(R.string.txt_toast_aut_error) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signIn(View view) {
        System.out.println("LOGDANNY:INGRESOMETODO"+"signIn");
        System.out.println("LOGDANNY:CELLAUTH:signIn");

        String code = etxtPhoneCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            return;
        }
        if(mVerificationId==null){
            System.out.println("LOGDANNY:CELLAUTH:signIn:mVerificationID null");
            etxtPhoneCode.setText("",TextView.BufferType.EDITABLE);
            return;
        }else {
            signInWithCredential(PhoneAuthProvider.getCredential(mVerificationId, code));
        }

    }

    public void validarPermisos() {

        if (ActivityCompat.checkSelfPermission(CellAuthActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CellAuthActivity.this, android.Manifest.permission.READ_PHONE_STATE)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(CellAuthActivity.this);
                builder.setTitle("Esta Aplicacion Necesita algunos permisos.");
                builder.setMessage("Para poder continuar deberas habilitar algunos permisos");
                builder.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(CellAuthActivity.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(android.Manifest.permission.READ_PHONE_STATE,false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(CellAuthActivity.this);
                builder.setTitle("Esta Aplicacion Necesita algunos permisos.Dentro de Ajustes>Permisos Activa los permisos solicitados");
                builder.setMessage("Para poder continuar deberas habilitar algunos permisos");
                builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Ir a la configuración de permisos.", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(CellAuthActivity.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_PERMISSION_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(android.Manifest.permission.READ_PHONE_STATE,true);
            editor.commit();


        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }
    }

    private void proceedAfterPermission() {
        //We've got the permission, now we can proceed further
        //Toast.makeText(getBaseContext(), "Tenemos el permiso", Toast.LENGTH_LONG).show();
        permisos_concedidos=true;
    }


}