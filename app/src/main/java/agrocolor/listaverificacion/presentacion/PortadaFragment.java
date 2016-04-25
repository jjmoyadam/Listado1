package agrocolor.listaverificacion.presentacion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import agrocolor.listaverificacion.fachadas.FachadaExcel;
import agrocolor.listaverificacion.modelos.Auditoria;
import agrocolor.listaverificacion.modelos.ListaVerificacion;


public class PortadaFragment extends Fragment {
    //variables para almacenar los datos del bundle
    public static final String ARG_NOMBRE_AUDITORIA = "nombre_auditoria";
    public static final String ARG_NUEVA_AUDITORIA = "nueva_auditoria";


    private TextView tvfecha,tvvisita,tvoperador;
    private EditText edfecha,edvisita,edoperador;
    private MainActivity contexto;
    private Auditoria auditoria;
    private TableLayout tabla;
    private boolean nuevaaduditoria;
    private FachadaExcel fachadaexcel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //referencia al contendedor
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_portada, container, false);

        //le pasamos el contexto
        contexto = (MainActivity)getActivity();
        //recogida de datos de nombre de archivo y si es nuevo o no de un bundle
        String archivo = getArguments().getString(ARG_NOMBRE_AUDITORIA);
        nuevaaduditoria = getArguments().getBoolean(ARG_NUEVA_AUDITORIA);
        //referencia a los objetos
        tvfecha=(TextView)rootView.findViewById(R.id.tvFecha);
        tvvisita=(TextView)rootView.findViewById(R.id.tvCodVisita);
        tvoperador=(TextView)rootView.findViewById(R.id.tvCodCooperador);


        //fachada excel
        fachadaexcel=new FachadaExcel(contexto);

        ListaVerificacion lv;

        edfecha=(EditText)rootView.findViewById(R.id.edFecha);
        edvisita =(EditText)rootView.findViewById(R.id.edCodigoVisita);
        edoperador=(EditText)rootView.findViewById(R.id.edCodigoCooperador);
        //carga del menu lateral
        contexto.cargarMenuDocumento();


        //comprobacion de si es un archivo nuevo o no y cargamos los datos de la portada
        try {
            //si nuevalista es true
            if (nuevaaduditoria) {

                Toast.makeText(getContext(),"Nueva auditoria",Toast.LENGTH_SHORT).show();
                fachadaexcel.nuevaauditoria(archivo);

            } else {
                Toast.makeText(getContext(), "la auditoria existe", Toast.LENGTH_SHORT).show();
                fachadaexcel.leerPortada(archivo);

            }
        } catch (IOException e) {
            //Mensaje.mostrar(contexto, getResources().getString(R.string.tit_error), e.getStackTrace().toString(), getResources().getString(R.string.aceptar), null);
        }

        return rootView;
    }


    public PortadaFragment() {
        setHasOptionsMenu(true);
    }

    //creacion del menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_portada, menu);

    }
    //funcionalidad del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_guardarPortada:
                guardarDatosPortada(auditoria);
                resetFormulario();
                return true;
            case R.id.btn_resetPortada:
                resetFormulario();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void resetFormulario(){
        //reseteo de datos
        edfecha.setText("");
        edvisita.setText("");
        edoperador.setText("");
        //mensaje
        Toast.makeText(getContext(),"Reset Portada",Toast.LENGTH_SHORT).show();

    }

    private void guardarDatosPortada(Auditoria auditoria){
        //reseteo de datos
        String fecha=edfecha.getText().toString();
        String visita= edvisita.getText().toString();
        String operador=edoperador.getText().toString();
        //lo guardamos con el metodo de la fachada
        //fachadaexcel.escribirPortada(auditoria,fecha,visita,operador);
        //mensaje
        Toast.makeText(getContext(),"Datos Guardados",Toast.LENGTH_SHORT).show();


    }



}
