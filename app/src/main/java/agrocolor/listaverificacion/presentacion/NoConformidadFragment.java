package agrocolor.listaverificacion.presentacion;

import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuInflater;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import agrocolor.listaverificacion.fachadas.FachadaExcel;
import agrocolor.listaverificacion.modelos.Auditoria;
import agrocolor.listaverificacion.modelos.NoConformidad;


public class NoConformidadFragment extends Fragment {
    //creacion de las variables
    private String ARG_NOMBRE_AUDITORIA="nombre_auditoria";
    private String[] arraySpinner;
    private Context context;
    private TableLayout tabla;
    private TableRow fila;
    private LinearLayout rootView;
    private EditText eddescripcion, edreferencia;
    private Spinner sprequisito, sptipo;
    private CheckBox cbseleccion;
    private NoConformidad nc;
    private MainActivity contexto;
    private Auditoria auditoria;
    private FachadaExcel fachadaExcel;



    private Button btadd, btdelete;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //referencia al contendedor
        rootView = (LinearLayout) inflater.inflate(R.layout.fragment_no_conformidad, container, false);

        //referencia a la tabla
        tabla = (TableLayout) rootView.findViewById(R.id.tbnoconformidades);



        //que ocupen todo
        tabla.setColumnStretchable(0, true);
        tabla.setColumnStretchable(1, true);
        tabla.setColumnStretchable(2, true);
        tabla.setColumnStretchable(3, true);

        //recepcion de bundle
        String archivo = getArguments().getString(ARG_NOMBRE_AUDITORIA);
        //le pasamos el contexto
        contexto = (MainActivity)getActivity();
        //fachada
        fachadaExcel=new FachadaExcel(contexto);

        //auditoria
        auditoria = new Auditoria(archivo);


        //Mostramos el contenido del primer punto de control de la lista de conformidades
        try {
            //si nuevalista es true
            fachadaExcel.leerListaNoConformidades(auditoria);
            if(nc != null)
            {   /*
                pcIter = new PCIterator(lv);
                if(savedInstanceState != null)
                {
                    pcIter.setActual(savedInstanceState.getInt(ARG_PC_ACTUAL));
                    mostrarPC(pcIter.actual());
                }
                else
                    mostrarPC(pcIter.siguiente());
                    */
            }

        } catch (IOException e) {
            //Mensaje.mostrar(contexto, getResources().getString(R.string.tit_error), e.getStackTrace().toString(), getResources().getString(R.string.aceptar), null);
        }


        // Inflate the layout for this fragment
        return rootView;
    }

    public NoConformidadFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_noconformidad, menu);

    }

    //funcionalidad del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_add_nc:
                addfila();
                return true;
            case R.id.btn_borrar_nc:
                deletefila();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    /**
     * añadir fila al tablelayout
     */
    private void addfila() {
        //creamos una nueva fila
        fila = new TableRow(getContext());

        //añadimos los componentes que forman la fila
        edreferencia = new EditText(getContext());
        eddescripcion = new EditText(getContext());
        sprequisito = new Spinner(getContext());
        sptipo = new Spinner(getContext());
        cbseleccion = new CheckBox(getContext());

        //adaptador de array
        ArrayAdapter<CharSequence> adp = ArrayAdapter.createFromResource(getContext(), R.array.valores_noconformidad, R.layout.layout_spinner);
        //y cargamos
        sprequisito.setAdapter(adp);

        //añadimos a la tabla
        fila.addView(edreferencia);
        fila.addView(eddescripcion);
        fila.addView(sprequisito);
        fila.addView(sptipo);
        fila.addView(cbseleccion);
        //y la añadimos a la vista

        tabla.addView(fila);

        Toast.makeText(getContext(), "fila añadida", Toast.LENGTH_SHORT).show();
    }

    private void deletefila() {
        //contador de seleccion
        int seleccion = 0;
        //numero de filas ,(la primera es el titulo y no se cuenta
        int datos = (tabla.getChildCount()) - 1;
        //recorremos las filas
        for (int i = 1; i < tabla.getChildCount(); i++) {
            //nos colocamos en la fila
            fila= (TableRow) tabla.getChildAt(i);
            //checkbox
            cbseleccion = (CheckBox) fila.getChildAt(4);
            //comprobamos si el checkbox esta seleccionado
            if (cbseleccion.isChecked()){

                tabla.removeView(fila);

            }

        }

    }

}
