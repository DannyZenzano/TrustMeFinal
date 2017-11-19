package com.zenzanodanny.trustme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
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

import static com.zenzanodanny.trustme.R.id.imageView;

public class RegisterActivity extends AppCompatActivity {

    private SessionManager mysession;

    private EditText etxtNombre;
    private EditText etxtApellido;
    private TextView TxtViewAnnounce;
    private Button BotonRegistrar;
    private ImageButton ImageProfile;


    private ProgressDialog myProgressDialog;

    private FirebaseAuth mAuth;
    public String userID;
    public Usuario DatosUsuario;
    private StorageReference MyStorage;

    private static final int GALLERY_INTENT=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("LOGDANNY:INGRESOMETODO"+" INGRESO A REGISTERACT");
        //IMPRIMO MIS VARIABLES DE SESSION
        TelephoneUtils mytelephoneutilstest=new TelephoneUtils(RegisterActivity.this);
        mytelephoneutilstest.imprimir_Shared_Preferences();


        setContentView(R.layout.activity_register);

        TxtViewAnnounce = (TextView) findViewById(R.id.announce_text);
        etxtNombre = (EditText) findViewById(R.id.etxtnombre);
        etxtApellido = (EditText) findViewById(R.id.etxtapellido);
        BotonRegistrar = (Button) findViewById(R.id.button_start_verification);
        ImageProfile = (ImageButton) findViewById(R.id.ImageProfile);

        mysession= new SessionManager(getApplicationContext());

        myProgressDialog = new ProgressDialog(this);

        MyStorage = FirebaseStorage.getInstance().getReference();

        ImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent=new Intent(Intent.ACTION_PICK);
                myintent.setType("image/*");
                startActivityForResult(myintent,GALLERY_INTENT);
            }
        });


        //Inicializo la Autentificacion de firebase
        //FirebaseApp.initializeApp(RegisterActivity.this);
        //mAuth = FirebaseAuth.getInstance();
        //userID=mAuth.getCurrentUser().getUid();
        //DEBO CAMBIAR A USERID DE MSISDN

        userID=mysession.getUserId();

        //Obtengo Los valores de usuario de la Base de Datos.
        FirebaseDatabase midatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = midatabase.getReference(FirebaseReferences.User_Reference);

        Query query_get_unique_user = mDatabase.child(userID);
        query_get_unique_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot miSnapshot) {
                if (miSnapshot.exists()) {
                    System.out.println("LOGDANNY:RegisterActivity:ONDATACHANGE:registrar:EL USUARIO FUE REGUSTRADO Y OBTENGO SUS DATOS");
                    DatosUsuario=miSnapshot.getValue(Usuario.class);
                    System.out.println("LOGDANNY:RegisterActivity:ONDATACHANGE:DATOS DE USUARIO DE BD" +DatosUsuario.toString());
                    if(DatosUsuario!=null){
                        String user_msisdn=DatosUsuario.getUser_msisdn();
                        if(user_msisdn.compareTo("")!=0) {
                            System.out.println("LOGDANNY:RegisterActivity:ONDATACHANGE:DATOS DESCARGADOS");
                            //Actualizao el texto del textview announce_text
                            TxtViewAnnounce.setText(user_msisdn);
                            TxtViewAnnounce.setText(getString(R.string.txt_continua_registro2)+user_msisdn);
                            //Activo El BOTON DE REGISTRO
                            BotonRegistrar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            BotonRegistrar.setEnabled(true);
                        }else{
                            //El usuario esta registrado pero no tiene MSISDN en la BD
                            //ESTO NO DEBERIA PASAR NUNCA
                            //PANIC
                            System.out.println("LOGDANNY:RegisterActivity:ONDATACHANGE:PANIC:El usuario esta registrado pero no tiene MSISDN en la BD");
                            //Cambiamos el nivel de registroa 0

                            //Desautenticamos

                            //Enviamos al Activity Inicial
                        }
                    }
                } else {
                    //El usuario no ESTA REGISTRADO. Volvemos a un principio
                    mysession.saveRegisterLevel(0);
                    //Deslogueo
                    if (mAuth.getCurrentUser() != null) {
                        mAuth.signOut();
                    }
                    //Vuelvo a pagina de registro o logueo
                    Intent intent = new Intent(RegisterActivity.this, CellAuthActivity.class);
                    startActivity(intent);
                    finish();

                    //Esto no deberia pasar nunca.
                    //PANIC
                    System.out.println("LOGDANNY:RegisterActivity:ONDATACHANGE:PANIC:El usuario no ESTA REGISTRADO. Volvemos a un principio");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        ////////////////////////



    }

    //Registra al usuario
    public void registrar_usuario(View view) {
        //Verifico  QUE LOS CAMPOS NOMBRE Y APELLIDO ESTEN LLENOS
        String MiNombre =etxtNombre.getText().toString();
        String MiApellido =etxtApellido.getText().toString();
        if (TextUtils.isEmpty(MiNombre)) {
            System.out.println("LOGDANNY:1");
            String MensajeError=getString(R.string.txt_campo_obligatorio);
            etxtNombre.setError("MensajeError");
            return;
        }else{
            System.out.println("LOGDANNY:0");
            if (TextUtils.isEmpty(MiApellido)) {
                System.out.println("LOGDANNY:2");
                String MensajeError=getString(R.string.txt_campo_obligatorio);
                etxtApellido.setError("MensajeError");
                return;
            }
        }
        System.out.println("LOGDANNY:3");
        //Subo los datos a la Base de datos.
        FirebaseDatabase midatabase=FirebaseDatabase.getInstance();
        DatabaseReference mDatabase=midatabase.getReference(FirebaseReferences.User_Reference);
        DatabaseReference currentUserDB=mDatabase.child(userID);
        //Actualizo los datos user_name y user_lastname
        currentUserDB.child("user_name").setValue(MiNombre);
        currentUserDB.child("user_lastname").setValue(MiApellido);
        mysession.saveRegisterLevel(2);
        mysession.saveUserName(MiNombre);
        mysession.saveUserLastName(MiApellido);



        //
        System.out.println("LOGDANNY:INGRESOMETODO"+"REGISTERACT:VERIFICO DESPUES DE MODIFICAR EL USERID");
        //IMPRIMO MIS VARIABLES DE SESSION
        TelephoneUtils mytelephoneutilstest=new TelephoneUtils(RegisterActivity.this);
        mytelephoneutilstest.imprimir_Shared_Preferences();


        //Paso Al Activity
        System.out.println("LOGDANNY:RegisterActivity:Pasamos a NAVACTIVITY");
        Toast.makeText(RegisterActivity.this,getString(R.string.txt_toast_logueado), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(RegisterActivity.this, NavActivity.class);
        startActivity(intent);
        finish();



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
            StorageReference filepath=MyStorage.child("ProfilePictures").child(userID);
            filepath.putFile(myUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //
                    mysession.savePROFILE_PHOTO_EXIST(true);
                    /*
                    myProgressDialog.dismiss();
                    //Descargamos la foto para mostrarla
                    myProgressDialog.setTitle(getString(R.string.txt_Dialog_title_downimg));
                    myProgressDialog.setMessage(getString(R.string.txt_Dialog_desc_downimg));
                    myProgressDialog.setCancelable(false);//Para que si hacemos clic afuera no se salga
                    myProgressDialog.show();
                    */
                    Uri descargafoto=taskSnapshot.getDownloadUrl();
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

                    Glide.with(RegisterActivity.this)
                            .load(descargafoto)
                            .asBitmap()
                            .fitCenter()
                            .centerCrop()
                            .placeholder(R.drawable.relojdearena)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new BitmapImageViewTarget(ImageProfile) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                     RoundedBitmapDrawableFactory.create(RegisterActivity.this.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    ImageProfile.setImageDrawable(circularBitmapDrawable);
                                }
                            });

                    myProgressDialog.dismiss();

                }
            });
        }
    }


}
