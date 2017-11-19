package com.zenzanodanny.trustme;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zenzanodanny.trustme.Objetos.FirebaseReferences;
import com.zenzanodanny.trustme.Objetos.Usuario;
import com.zenzanodanny.trustme.Objetos.transacciones;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewTransactionActivity extends AppCompatActivity {

    private SessionManager mysession;
    private StorageReference MyStorage;

    private ImageButton btn_Imageupload;
    private EditText etxt_titulo;
    private EditText etxt_descipcion;
    private EditText etxt_nro_cel_destino;
    private Switch miswitchnuevo;
    private TextView TxtViewAnnounce;
    private Button BotonEnviar;


    private transacciones MyTransaction;
    private String URLFOTOGRAFIA;
    private String userID;
    private int prefijoLocal;
    private String country;
    private String IdTransaction;
    private static final int GALLERY_INTENT=1;

    private ProgressDialog myProgressDialog;

    //Variables para permisos
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;
    private static final int SEND_SMS_PERMISSION_CONSTANT = 200;
    private static final int REQUEST_PERMISSION_SETTING = 201;

    public boolean permisos_concedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);
        URLFOTOGRAFIA="";

        System.out.println("LOGDANNY:INGRESOMETODO"+" INGRESO A NewTransactionActivity");
        //IMPRIMO MIS VARIABLES DE SESSION
        TelephoneUtils mytelephoneutilstest=new TelephoneUtils(NewTransactionActivity.this);
        mytelephoneutilstest.imprimir_Shared_Preferences();

        btn_Imageupload=(ImageButton) findViewById(R.id.UploadImg);
        etxt_titulo=(EditText) findViewById(R.id.etxttitulo);
        etxt_descipcion=(EditText) findViewById(R.id.etxtdescripcion);
        etxt_nro_cel_destino=(EditText) findViewById(R.id.etxceldestino);
        miswitchnuevo=(Switch) findViewById(R.id.SwitchNuevo);
        TxtViewAnnounce=(TextView) findViewById(R.id.announce_text);
        BotonEnviar=(Button) findViewById(R.id.button_start_verification);

        /////OBTENGO MIS VAR DE SESSSION
        mysession= new SessionManager(getApplicationContext());
        userID=mysession.getUserId();
        prefijoLocal=mysession.getKEY_MSISDN_prefix();
        System.out.println("LOGDANNY:prefijoLocal:"+prefijoLocal);
        country=mysession.getKEY_COUNTRY();
        System.out.println("LOGDANNY:country:"+country);


        // CREO EL ID DE LA TRANSACCION
        Long milis=System.currentTimeMillis();
        IdTransaction=userID+"-"+milis;

        //BOTON FOTOGRAFIA
        MyStorage = FirebaseStorage.getInstance().getReference();
        myProgressDialog = new ProgressDialog(this);

        btn_Imageupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent=new Intent(Intent.ACTION_PICK);
                myintent.setType("image/*");
                startActivityForResult(myintent,GALLERY_INTENT);
            }
        });

        //





        TelephoneUtils myTelephoneUtils = new TelephoneUtils(NewTransactionActivity.this);
        int CODIGO_TELEF_PAIS=myTelephoneUtils.getCountryTelephoneCode(country);
        System.out.println("LOGDANNY:prefijoLocal:CODIGO_TELEF_PAIS MeDIANTE COUNTRYCODE"+CODIGO_TELEF_PAIS);

    }

    public void enviar_transaccion(View view) {
        //Verifico Los Campos
        if(!verificar_campos()){
            //los campos no estan llenos
            return;
        }

        //Formatar el telefono del comprador
        String Cel_formateado=formatear_user_id(etxt_nro_cel_destino.getText().toString());
        System.out.println("LOGDANNY:respuest formatear_user_id:"+Cel_formateado);

        //CREO EL OBJETO TRANSACCION
        String Estado_Tr="pendiente";
        Boolean NuevoActivado=miswitchnuevo.isChecked();
        String estadoconservacion="nuevo";
        if(NuevoActivado){
            System.out.println("LOG DANNY:Es NUEVO");
            estadoconservacion="nuevo";
        }else{
            System.out.println("LOG DANNY:NOOOOO Es NUEVO");
            estadoconservacion="usado";
        }
        //Fecha de registro
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String fechaderegistro=hourdateFormat.format(date);


        MyTransaction=new transacciones(userID,Cel_formateado,Estado_Tr,etxt_titulo.getText().toString(),etxt_descipcion.getText().toString(),estadoconservacion,URLFOTOGRAFIA,"2017/01/01 12:00:00", "Direccion de entrega","Coordenadas de entrega",fechaderegistro);
        //SUBO EL ARCHIVO A LA BASE DE DATOS

        FirebaseDatabase midatabase=FirebaseDatabase.getInstance();
        DatabaseReference mDatabase=midatabase.getReference(FirebaseReferences.Transaction_Reference);
        DatabaseReference currentUserDB=mDatabase.child(IdTransaction);
        //Actualizo los datos user_name y user_lastname
        currentUserDB.setValue(MyTransaction);


        //Verifico si el usuario comprador existe y si no existe le envio una invitacion via SMS
        /////////////////////////////////////VERIFICO SI YA ESTA REGISTRADO/////////////////////
        //Inicializo la Autentificacion de firebase
        FirebaseApp.initializeApp(NewTransactionActivity.this);
        //Obtengo Los valores de usuario de la Base de Datos.
        FirebaseDatabase midatabaseUserinfo = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseUI = midatabaseUserinfo.getReference(FirebaseReferences.User_Reference);
        Query query_get_unique_user = mDatabaseUI.child(Cel_formateado);
        query_get_unique_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot miSnapshot) {
                Usuario UsuarioParaRegistrar;
                if (miSnapshot.exists()) {
                    Usuario MiUsuarioAntiguo=new Usuario();
                    MiUsuarioAntiguo=miSnapshot.getValue(Usuario.class);


                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(NewTransactionActivity.this);

                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder2.setMessage(R.string.dialog_message3)
                            .setTitle(R.string.dialog_title3);
                    builder2.setNeutralButton(R.string.entendi, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            ////Paso Al Activity
                            System.out.println("LOGDANNY:RegisterActivity:Pasamos a NAVACTIVITY");
                            Intent intent = new Intent(NewTransactionActivity.this, NavActivity.class);
                            intent.putExtra("Id_transaction",IdTransaction);
                            startActivity(intent);
                            finish();

                        }
                    });
                    // 4. Get the AlertDialog from create()
                    AlertDialog dialog2 = builder2.create();
                    dialog2.show();




                    ////Paso Al Activity
                    System.out.println("LOGDANNY:RegisterActivity:Pasamos a NAVACTIVITY");
                    Intent intent = new Intent(NewTransactionActivity.this, NavActivity.class);
                    intent.putExtra("Id_transaction",IdTransaction);
                    startActivity(intent);
                    finish();
                }else{
                    //ES UN USUARIO QUE NO EXISTE
                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewTransactionActivity.this);

                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage(R.string.dialog_message1)
                            .setTitle(R.string.dialog_title1);

                    //3 Add the Buttons
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button, enviar el SMS

                            //REVISO QUE TENGA PERMISO PARA LEER LOS DATOS DEL TELEFONO
                            permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);
                            validarPermisos();
                            if(permisos_concedidos) {
                                String phone =etxt_nro_cel_destino.getText().toString();
                                String text = "ACABO DE DARTE ACCESO A CALIFICARME POR LA COMPRA DE :\""+etxt_titulo.getText()+"\" Solo debes instalarte la app TRUSTME";
                                SmsManager sms = SmsManager.getDefault();
                                sms.sendTextMessage(phone, null, text , null, null);

                                ////Paso Al Activity
                                System.out.println("LOGDANNY:RegisterActivity:Pasamos a NAVACTIVITY");
                                Intent intent = new Intent(NewTransactionActivity.this, NavActivity.class);
                                intent.putExtra("Id_transaction",IdTransaction);
                                startActivity(intent);
                                finish();
                            }else{
                                //NO NOS DIO PERMISO PARA ENVIAR UN SMS
                                System.out.println("LOGDANNY:CELLAUTH:permiso no concedido");
                                // No desea enviar SMS
                                //ES UN USUARIO NUEVO ACTUALIZO CON DATOS NUEVOS
                                // 1. Instantiate an AlertDialog.Builder with its constructor
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(NewTransactionActivity.this);

                                // 2. Chain together various setter methods to set the dialog characteristics
                                builder2.setMessage(R.string.dialog_message2)
                                        .setTitle(R.string.dialog_title2);
                                builder2.setNeutralButton(R.string.entendi, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User clicked OK button
                                        ////Paso Al Activity
                                        System.out.println("LOGDANNY:RegisterActivity:Pasamos a NAVACTIVITY");
                                        Intent intent = new Intent(NewTransactionActivity.this, NavActivity.class);
                                        intent.putExtra("Id_transaction",IdTransaction);
                                        startActivity(intent);
                                        finish();

                                    }
                                });
                                // 4. Get the AlertDialog from create()
                                AlertDialog dialog2 = builder2.create();
                                dialog2.show();


                            }



                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // No desea enviar SMS

                            // 1. Instantiate an AlertDialog.Builder with its constructor
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(NewTransactionActivity.this);

                            // 2. Chain together various setter methods to set the dialog characteristics
                            builder2.setMessage(R.string.dialog_message2)
                                    .setTitle(R.string.dialog_title2);
                            builder2.setNeutralButton(R.string.entendi, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                    ////Paso Al Activity
                                    System.out.println("LOGDANNY:RegisterActivity:Pasamos a NAVACTIVITY");
                                    Intent intent = new Intent(NewTransactionActivity.this, NavActivity.class);
                                    intent.putExtra("Id_transaction",IdTransaction);
                                    startActivity(intent);
                                    finish();

                                }
                            });
                            // 4. Get the AlertDialog from create()
                            AlertDialog dialog2 = builder2.create();
                            dialog2.show();

                        }
                    });

                    // 4. Get the AlertDialog from create()
                    AlertDialog dialog = builder.create();
                    dialog.show();



                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {            }

        });







    }

    public void validarPermisos() {

        if (ActivityCompat.checkSelfPermission(NewTransactionActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(NewTransactionActivity.this, Manifest.permission.SEND_SMS)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(NewTransactionActivity.this);
                builder.setTitle("PARA PODER NOTIFICARLE NECESITAMOS DE TU PERMISO.");
                builder.setMessage("PARA PODER NOTIFICAR A TU CLIENTE NECESITAMOS DE TU PERMISO");
                builder.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(NewTransactionActivity.this, new String[]{android.Manifest.permission.SEND_SMS},SEND_SMS_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(android.Manifest.permission.SEND_SMS,false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(NewTransactionActivity.this);
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
                ActivityCompat.requestPermissions(NewTransactionActivity.this, new String[]{android.Manifest.permission.SEND_SMS},SEND_SMS_PERMISSION_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(android.Manifest.permission.SEND_SMS,true);
            editor.commit();


        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }
    }

    public boolean verificar_campos() {

        if (TextUtils.isEmpty(etxt_titulo.getText().toString())) {
            etxt_titulo.setError(getString(R.string.txt_campo_obligatorio));
            return false;
        }else if (TextUtils.isEmpty(etxt_descipcion.getText().toString())) {
            etxt_descipcion.setError(getString(R.string.txt_campo_obligatorio));
            return false;
        }else if (TextUtils.isEmpty(etxt_nro_cel_destino.getText().toString())) {
            etxt_nro_cel_destino.setError(getString(R.string.txt_campo_obligatorio));
            return false;
        }else if(!NroCelularBienEscrito(etxt_nro_cel_destino.getText().toString())){
            etxt_nro_cel_destino.setError(getString(R.string.txt_mal_formato_celular));
            return false;
        }

        return true;

    }

    public boolean usuario_registrado(){
        //Verifico si el comprador esta registrado, devuelvo TRUE SI ESTA REGISTRADO
        return true;
    }
    public String formatear_user_id(String msisdn_sin_formato){
        System.out.println("LOGDANNY:msisdn_sin_formato:"+msisdn_sin_formato);
        String msisdn_formateado=msisdn_sin_formato;
        //SI es internacional, lo devuelvo directamente
        if(msisdn_sin_formato.startsWith("+")){
            return msisdn_sin_formato.substring(1,msisdn_sin_formato.length());
        }else{

            return prefijoLocal+msisdn_sin_formato;
        }
    }
    public boolean NroCelularBienEscrito(String _msisdn){
        for(int i=0; i<_msisdn.length(); i++){
            String Digito=_msisdn.substring(i,i+1);
            if(i==0){
                if((Digito.compareTo("+")!=0)&&(Digito.compareTo("0")!=0)&&(Digito.compareTo("1")!=0)&&(Digito.compareTo("2")!=0)&&(Digito.compareTo("3")!=0)&&(Digito.compareTo("4")!=0)&&(Digito.compareTo("5")!=0)&&(Digito.compareTo("6")!=0)&&(Digito.compareTo("7")!=0)&&(Digito.compareTo("8")!=0)&&(Digito.compareTo("9")!=0)){
                    return false;
                }
            }else{
                if((Digito.compareTo("0")!=0)&&(Digito.compareTo("1")!=0)&&(Digito.compareTo("2")!=0)&&(Digito.compareTo("3")!=0)&&(Digito.compareTo("4")!=0)&&(Digito.compareTo("5")!=0)&&(Digito.compareTo("6")!=0)&&(Digito.compareTo("7")!=0)&&(Digito.compareTo("8")!=0)&&(Digito.compareTo("9")!=0)){
                    return false;
                }
            }



        }

        return true;
    }

    //Sube la Foto al Storage y luego la descarga al telefono para mostrarla.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT && resultCode==RESULT_OK){
            myProgressDialog.setTitle(getString(R.string.txt_Dialog_title_imgprof));
            myProgressDialog.setMessage(getString(R.string.txt_Dialog_desc_imgprof));
            myProgressDialog.setCancelable(false);//Para que si hacemos clic afuera no se salga
            myProgressDialog.show();

            Uri myUri=data.getData();
            //NombreDelArchivo
            //String NomFoto= myUri.getLastPathSegment();
            StorageReference filepath=MyStorage.child("TransactionPicture").child(IdTransaction);
            filepath.putFile(myUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //
                    /*
                    myProgressDialog.dismiss();
                    //Descargamos la foto para mostrarla
                    myProgressDialog.setTitle(getString(R.string.txt_Dialog_title_downimg));
                    myProgressDialog.setMessage(getString(R.string.txt_Dialog_desc_downimg));
                    myProgressDialog.setCancelable(false);//Para que si hacemos clic afuera no se salga
                    myProgressDialog.show();
                    */
                    Uri descargafoto=taskSnapshot.getDownloadUrl();
                    //Guardo la URL DE LA FOTOGRAFIA
                    URLFOTOGRAFIA=descargafoto.toString();


                    System.out.println("LOGDANNY:URI DE ARCHIVO MULTIMEDIA:"+URLFOTOGRAFIA);
                    /*
                    Glide.with(RegisterActivity.this)
                            .load(descargafoto)
                            .transform(new CircleTransform(RegisterActivity.this))
                            .fitCenter()
                            .centerCrop()
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ImageProfile);
                    */

                    //FUNCIONA SOLO

                    Glide.with(NewTransactionActivity.this)
                            .load(descargafoto)
                            .asBitmap()
                            .fitCenter()
                            .centerCrop()
                            .placeholder(R.drawable.relojdearena)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new BitmapImageViewTarget(btn_Imageupload) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(NewTransactionActivity.this.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    btn_Imageupload.setImageDrawable(circularBitmapDrawable);
                                }
                            });

                    myProgressDialog.dismiss();

                }
            });
        }
    }


    private void proceedAfterPermission() {
        //We've got the permission, now we can proceed further
        //Toast.makeText(getBaseContext(), "Tenemos el permiso", Toast.LENGTH_LONG).show();
        permisos_concedidos=true;
    }

}
