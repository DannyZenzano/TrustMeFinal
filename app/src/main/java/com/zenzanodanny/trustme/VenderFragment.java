package com.zenzanodanny.trustme;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zenzanodanny.trustme.Objetos.FirebaseReferences;
import com.zenzanodanny.trustme.Objetos.Usuario;
import com.zenzanodanny.trustme.Objetos.transacciones;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VenderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VenderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VenderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ArrayList<transacciones> ListaTransacciones;
    ArrayList<String> ListaKeyTransacciones;
    RecyclerView recyclerTransactions;
    String miuid;

    public VenderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VenderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VenderFragment newInstance(String param1, String param2) {
        VenderFragment fragment = new VenderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("LOGDANNY:VenderFragmento:OnCREATE");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("LOGDANNY:VenderFragmento:OnCREATEVIEW");
        View vista=inflater.inflate(R.layout.fragment_vender, container, false);
        ListaTransacciones=new ArrayList<transacciones>();
        ListaKeyTransacciones=new ArrayList<String>();
        recyclerTransactions= (RecyclerView) vista.findViewById(R.id.recyclerId);
        recyclerTransactions.setLayoutManager(new LinearLayoutManager(getContext()));

        SessionManager mysession1;
        mysession1= new SessionManager(getContext());
        miuid=mysession1.getUserId();

        //LlenarListaTransacciones();
        final AdapterTransactions adapter = new AdapterTransactions(ListaTransacciones,this);

        //PARA EL ONCLICK
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Seleccion:"+ListaTransacciones.get(recyclerTransactions.getChildAdapterPosition(v)).getTr_titulo(),Toast.LENGTH_LONG);
                int posicionclick=recyclerTransactions.getChildAdapterPosition(v);
                System.out.println("LOGDANNY:VenderFragmento:ONCLIC:"+ListaTransacciones.get(posicionclick).getTr_titulo());
                System.out.println("LOGDANNY:VenderFragmento:ONCLICKEY:"+ListaKeyTransacciones.get(posicionclick));
                //
                //IR A VER VENTAS, LE ENVIO EL ID DE LA VENTA Y EL OBJETO TRANSACCION
                //Vuelvo a pagina de registro o logueo
                Intent intent = new Intent(getActivity(), VerVentaActivity.class);
                intent.putExtra("parametro", "string");
                intent.putExtra("KeyTransaction",ListaKeyTransacciones.get(posicionclick));
                intent.putExtra("Titulo",ListaTransacciones.get(posicionclick).getTr_titulo());
                intent.putExtra("IdVendedor",ListaTransacciones.get(posicionclick).getTr_id_vendedor());
                intent.putExtra("IdComprador",ListaTransacciones.get(posicionclick).getTr_id_comprador());
                intent.putExtra("Estado_transaccion",ListaTransacciones.get(posicionclick).getTr_estado_transaccion());
                intent.putExtra("Descripcion",ListaTransacciones.get(posicionclick).getTr_descripcion());
                intent.putExtra("Estado_conservacion",ListaTransacciones.get(posicionclick).getTr_estado_conservacion());
                intent.putExtra("fotografia",ListaTransacciones.get(posicionclick).getTr_fotografia());
                getActivity().startActivity(intent);
                getActivity().finish();

            }
        });
        //FIN DE PARA EL ONCLICK

        recyclerTransactions.setAdapter(adapter);


        //Obtengo Los valores de usuario de la Base de Datos. y MODIFICO LOS VALORES
        System.out.println("LOGDANNY:VenderFragmento:OnCREATEVIEW:CREANDO BASE DE DATOS");


        FirebaseDatabase midatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = midatabase.getReference(FirebaseReferences.Transaction_Reference);

        Query query_get_unique_user = mDatabase.orderByChild("tr_id_vendedor").equalTo(miuid);
        query_get_unique_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot miSnapshot) {
                System.out.println("LOGDANNY:VenderFragmento:OnDataChange");
                ListaTransacciones.removeAll(ListaTransacciones);
                ListaKeyTransacciones.removeAll(ListaKeyTransacciones);
                if (miSnapshot.exists()) {
                    System.out.println("LOGDANNY:VenderFragmento:OnCREATEVIEW:onDataChange:Snapshoot existe");
                    System.out.println("LOGDANNY:VenderFragmento:OnCREATEVIEW:onDataChange:Snapshoot VALOR:"+miSnapshot.getValue(transacciones.class).toString());
                    for (DataSnapshot snapshothijo:miSnapshot.getChildren() ) {
                        String TR_KEY=snapshothijo.getKey();
                        transacciones nuevatransaccion=snapshothijo.getValue(transacciones.class);
                        ListaTransacciones.add(nuevatransaccion);
                        ListaKeyTransacciones.add(TR_KEY);
                    }
                    adapter.notifyDataSetChanged();
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


        return vista;
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
            Toast.makeText(context, "VENDER Fragment Attached", Toast.LENGTH_SHORT).show();
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

    public void creartransaccion() {
        Intent intent = new Intent(getActivity(), NewTransactionActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
