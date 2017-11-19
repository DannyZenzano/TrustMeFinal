package com.zenzanodanny.trustme;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zenzanodanny.trustme.Objetos.FirebaseReferences;
import com.zenzanodanny.trustme.Objetos.Usuario;
import com.zenzanodanny.trustme.Objetos.rating;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BuscarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BuscarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuscarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    int micodigopais;
    String celularBuscado;
    Usuario UsuarioBuscado;

    public int nro_transacciones=0;
    public float sumatoriaA = 0;
    public float sumatoriaC = 0;
    public float sumatoriaP = 0;
    public float promedioA = 0;
    public float promedioC = 0;
    public float promedioP = 0;



    private EditText CELUserBuscado;
    private TextView TituloUsuarioEncontrado;
    private TextView TituloUsuarioEncontrado2;
    private ImageButton botonbuscar;

    private RatingBar UserRBarArticulo;
    private RatingBar UserRBarComunicacion;
    private RatingBar UserRBarPuntualidad;

    View Mivista;

    public BuscarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BuscarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BuscarFragment newInstance(String param1, String param2) {
        BuscarFragment fragment = new BuscarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Mivista= inflater.inflate(R.layout.fragment_buscar, container, false);

        SessionManager mysession1;
        mysession1= new SessionManager(getContext());
        micodigopais=mysession1.getKEY_MSISDN_prefix();


        TituloUsuarioEncontrado=(TextView) Mivista.findViewById(R.id.txtNombreEncontrado);
        TituloUsuarioEncontrado2=(TextView) Mivista.findViewById(R.id.txtNombreEncontrado2);
        CELUserBuscado=(EditText) Mivista.findViewById(R.id.etxtBuscador);
        botonbuscar=(ImageButton)Mivista.findViewById(R.id.imageSearch);

        UserRBarArticulo = (RatingBar) Mivista.findViewById(R.id.UserRatingBarArticulo);
        UserRBarComunicacion = (RatingBar) Mivista.findViewById(R.id.UserRatingBarComunicacion);
        UserRBarPuntualidad = (RatingBar) Mivista.findViewById(R.id.UserRatingBarPuntualidad);


        TituloUsuarioEncontrado.setText("HOLA");

        botonbuscar.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
                //Lineas para ocultar el teclado virtual (Hide keyboard) PARA usarlo en un FRAGMENTO AUMENTO getActivity()
                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(botonbuscar.getWindowToken(), 0);


                TituloUsuarioEncontrado.setText(getString(R.string.Buscando)+"...");
                BuscarUser();
            }
        });



        return Mivista;

    }
    public void ActualizarCalificacion() {
        //Obtengo mis datos de rating y calculo mis promedios
        nro_transacciones=0;
        sumatoriaA = 0;
        sumatoriaC = 0;
        sumatoriaP = 0;
        promedioA=0;
        promedioC=0;
        promedioP=0;


        FirebaseDatabase midatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = midatabase.getReference(FirebaseReferences.Rating_Reference);
        System.out.println("LOGDANNY:BUSCAR FRAGMENTO:IDVENDEDOR:"+celularBuscado);
        Query query_get_unique_user = mDatabase.orderByChild("idVendedor").equalTo(celularBuscado);
        query_get_unique_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot miSnapshot) {
                if (miSnapshot.exists()) {
                    System.out.println("LOGDANNY:BUSCAR FRAGMENTO:onDataChange:Snapshoot VALOR:"+miSnapshot.getValue(rating.class).toString());
                    for (DataSnapshot snapshothijo:miSnapshot.getChildren() ) {
                        String TR_KEY=snapshothijo.getKey();
                        rating nuevorating=snapshothijo.getValue(rating.class);
                        nro_transacciones++;
                        sumatoriaA=sumatoriaA+nuevorating.getArticulo();
                        sumatoriaC=sumatoriaC+nuevorating.getComunicacion();
                        sumatoriaP=sumatoriaP+nuevorating.getPuntualidad();
                    }
                    promedioA=sumatoriaA/nro_transacciones;
                    promedioC=sumatoriaC/nro_transacciones;
                    promedioP=sumatoriaP/nro_transacciones;


                    UserRBarArticulo.setRating(promedioA);
                    UserRBarComunicacion.setRating(promedioC);
                    UserRBarPuntualidad.setRating(promedioP);

                } else {
                    //No hay transacciones
                    TituloUsuarioEncontrado2.setText(getString(R.string.NoCalificaciones));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        ////////////////////////

    }
    public void BuscarUser() {
        //Busco el usuario de la base de datos
        celularBuscado=CELUserBuscado.getText().toString();
        UsuarioBuscado=null;
        //codigo pais micodigopais

        //Busco elusuario en la Base de datos.
        //Obtengo Los valores de usuario de la Base de Datos.
        FirebaseDatabase midatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = midatabase.getReference(FirebaseReferences.User_Reference);
        System.out.println("LOGDANNY:BUSCARFRAGMENT:BUSCANDO A:"+celularBuscado);
        Query query_get_unique_user = mDatabase.child(celularBuscado);
        query_get_unique_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot miSnapshot) {
                if (miSnapshot.exists()) {
                    UsuarioBuscado=miSnapshot.getValue(Usuario.class);
                    System.out.println("LOGDANNY:BUSCARFRAGMENT:ONDATACHANGE:EL USUARIO FUE ENCONTRADO:Datos:"+UsuarioBuscado.toString());
                    TituloUsuarioEncontrado.setText(UsuarioBuscado.getUser_name()+" "+UsuarioBuscado.getUser_lastname());
                    ActualizarCalificacion();

                } else {
                    System.out.println("LOGDANNY:BUSCARFRAGMENT:NO SE ENCONTRO A:"+celularBuscado);
                    //El usuario no coincide, le aumento el codigo del pais local.
                    celularBuscado=micodigopais+celularBuscado;
                    //-------------------//////////////////////////////////////////
                    //Obtengo Los valores de usuario de la Base de Datos.
                    FirebaseDatabase midatabase2 = FirebaseDatabase.getInstance();
                    DatabaseReference mDatabase2 = midatabase2.getReference(FirebaseReferences.User_Reference);
                    System.out.println("LOGDANNY:BUSCARFRAGMENT:BUSCANDO A:"+celularBuscado);
                    Query query_get_unique_user2 = mDatabase2.child(celularBuscado);
                    query_get_unique_user2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot miSnapshot) {
                            if (miSnapshot.exists()) {
                                UsuarioBuscado=miSnapshot.getValue(Usuario.class);
                                System.out.println("LOGDANNY:BUSCARFRAGMENT:ONDATACHANGE:EL USUARIO CON CODIGO DE PAIS FUE ENCONTRADO:Datos:"+UsuarioBuscado.toString());
                                TituloUsuarioEncontrado.setText(UsuarioBuscado.getUser_name()+" "+UsuarioBuscado.getUser_lastname());
                                ActualizarCalificacion();
                            }else{
                                System.out.println("LOGDANNY:BUSCARFRAGMENT:NO SE ENCONTRO A:"+celularBuscado);
                                TituloUsuarioEncontrado.setText(getString(R.string.usuarioNoEncontrado));
                                UserRBarArticulo.setRating(0);
                                UserRBarComunicacion.setRating(0);
                                UserRBarPuntualidad.setRating(0);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });


                    ////////////////////////
                    //------------/////////////////////////////////////////////////-----------------



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        ////////////////////////


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            Toast.makeText(context, "Buscar Fragment Attached", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
