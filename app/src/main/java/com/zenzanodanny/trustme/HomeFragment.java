package com.zenzanodanny.trustme;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.zenzanodanny.trustme.Objetos.transacciones;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    String miuid;
    public int nro_transacciones=0;
    public float sumatoriaA = 0;
    public float sumatoriaC = 0;
    public float sumatoriaP = 0;
    public float promedioA = 0;
    public float promedioC = 0;
    public float promedioP = 0;

    private RatingBar MiRBarArticulo;
    private RatingBar MiRBarComunicacion;
    private RatingBar MiRBarPuntualidad;
    private TextView MiTituloRep;
    private TextView NroNotif;
    private Button BotonIrACalificar;


    int CantidadPendiente;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View Mivista= inflater.inflate(R.layout.fragment_home, container, false);

        MiRBarArticulo = (RatingBar) Mivista.findViewById(R.id.MiRatingBarArticulo);
        MiRBarComunicacion = (RatingBar) Mivista.findViewById(R.id.MiRatingBarComunicacion);
        MiRBarPuntualidad = (RatingBar) Mivista.findViewById(R.id.MiRatingBarPuntualidad);
        MiTituloRep = (TextView) Mivista.findViewById(R.id.txtTituloMiRep);
        BotonIrACalificar=(Button) Mivista.findViewById(R.id.IrACalificar);
        NroNotif=(TextView) Mivista.findViewById(R.id.NroNotif);

        SessionManager mysession1;
        mysession1= new SessionManager(getContext());
        miuid=mysession1.getUserId();

        BotonIrACalificar.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
                FragmentManager myfragmentmanager =getActivity().getSupportFragmentManager();
                FragmentTransaction transccion = myfragmentmanager.beginTransaction();
                transccion.replace(R.id.content,new ComprarFragment()).commit();


            }
        });


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

        Query query_get_unique_user = mDatabase.orderByChild("idVendedor").equalTo(miuid);
        query_get_unique_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot miSnapshot) {
                if (miSnapshot.exists()) {
                    System.out.println("LOGDANNY:HOME FRAGMENTO:onDataChange:Snapshoot VALOR:"+miSnapshot.getValue(rating.class).toString());
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

                    MiTituloRep.setText(R.string.MiReputacion);
                    MiRBarArticulo.setRating(promedioA);
                    MiRBarComunicacion.setRating(promedioC);
                    MiRBarPuntualidad.setRating(promedioP);

                } else {
                    //No hay transacciones
                    MiTituloRep.setText(getString(R.string.NoCalificaciones));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        ////////////////////////

        ////////////////OBTENGO LA CANTIDAD DE TRANSACCIONES PENDIENTES DE CALIFICACION///////////////////////////////
        CantidadPendiente=0;
        FirebaseDatabase midatabase2 = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase2 = midatabase2.getReference(FirebaseReferences.Transaction_Reference);
        System.out.println("LOGDANNY:HomeFragmento:OnCREATEVIEW:onDataChange:miuid"+miuid);
        Query query_get_unique_user2 = mDatabase2.orderByChild("tr_id_comprador").equalTo(miuid);
        query_get_unique_user2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot miSnapshot) {
                System.out.println("LOGDANNY:HOMEFragmento:OnDataChange");




                if (miSnapshot.exists()) {

                    System.out.println("LOGDANNY:HomeFragmento:OnCREATEVIEW:onDataChange:Snapshoot VALOR:"+miSnapshot.getValue(transacciones.class).toString());
                    for (DataSnapshot snapshothijo:miSnapshot.getChildren() ) {
                        transacciones nuevatransaccion=snapshothijo.getValue(transacciones.class);
                        System.out.println("LOGDANNY:HomeFragmento:OnCREATEVIEW:onDataChange:Snapshoot nuevatransaccion:"+nuevatransaccion.toString());
                        String miestado=nuevatransaccion.getTr_estado_transaccion();
                        System.out.println("LOGDANNY:HomeFragmento:miestado"+miestado);
                        if(miestado.compareTo("pendiente")==0){
                            CantidadPendiente=CantidadPendiente+1;
                            System.out.println("LOGDANNY:HomeFragmento:ES PENDIENTE:CantidadPendiente:"+CantidadPendiente);
                        }else{
                            System.out.println("LOGDANNY:HomeFragmento:NO ES PENDIENTE");
                        }
                    }
                    System.out.println("LOGDANNY:HOMEFragmento:CantidadPendiente"+CantidadPendiente);
                    NroNotif.setText(CantidadPendiente+"");
                    if(CantidadPendiente>0){
                        BotonIrACalificar.setText(getString(R.string.txtBtnIrCalif));
                        BotonIrACalificar.setEnabled(true);
                    }
                } else {
                    //El usuario no ESTA REGISTRADO. Volvemos a un principio
                    System.out.println("LOGDANNY:VenderFragmento:OnCREATEVIEW:onDataChange:NO HSY REGISTROS");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        ////////////////////////









        return Mivista;
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
            Toast.makeText(context, "HOME Fragment Attached", Toast.LENGTH_SHORT).show();
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
