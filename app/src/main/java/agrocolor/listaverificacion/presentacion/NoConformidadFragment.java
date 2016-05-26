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
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import agrocolor.listaverificacion.fachadas.FachadaExcel;
import agrocolor.listaverificacion.modelos.Auditoria;
import agrocolor.listaverificacion.modelos.NoConformidad;


public class NoConformidadFragment extends Fragment {
    //creacion de las variables
    private String ARG_NOMBRE_AUDITORIA = "nombre_auditoria";
    private String[] arraySpinner;
    private Context context;
    private TableLayout tabla;
    private TableRow fila;
    private LinearLayout rootView;
    private EditText eddescripcion, edreferencia,edrequisito;
    private Spinner sptipo;
    private CheckBox cbseleccion;
    private ArrayList<NoConformidad> nc;
    private ArrayList<String> spinnernc;
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
        contexto = (MainActivity) getActivity();
        //fachada
        fachadaExcel = new FachadaExcel(contexto);

        //auditoria
        auditoria = new Auditoria(archivo);

        //creacion del spinner con los datos
        //array para tipo no conformidades
        try {
            spinnernc=crearArrayNoconformidades(auditoria.getNombreArchivo());

        } catch (IOException e) {
            e.printStackTrace();
        }


        //Mostramos el contenido de la lista de noconformidades
        try {

            nc = fachadaExcel.leerListaNoConformidades(auditoria);

            if (nc != null) {

                mostrarNoConformidades(nc);

            } else {


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
            case R.id.btn_guardar_nc:
                escribirNoconformidades();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private ArrayList<String> crearArrayNoconformidades(String archivo) throws IOException {


        File inputWorkbook = new File(archivo);
        //entrada de flujo de archivo
        FileInputStream input_document = new FileInputStream(inputWorkbook);
        //creacion del libro xls
        HSSFWorkbook book = new HSSFWorkbook(input_document);
        //posicionamiento en la hoja configuracion
        HSSFSheet sconfig= book.getSheet("Lista");

        spinnernc=new ArrayList<>();

        //recorremos los datos para añadirlo a un arraylist
        for(int i=1;i<sconfig.getLastRowNum();i++){

            Row rowConfig = sconfig.getRow(i);
            Cell cel = rowConfig.getCell(0);
            if(cel!=null)
                spinnernc.add(cel.getStringCellValue());

        }

       return spinnernc;
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
        edrequisito = new EditText(getContext());
        sptipo = new Spinner(getContext());
        cbseleccion = new CheckBox(getContext());

        //adaptador de array
        ArrayAdapter<CharSequence> adp2 = ArrayAdapter.createFromResource(getContext(), R.array.valores_noconformidad, R.layout.layout_spinner);
        //y cargamos
        sptipo.setAdapter(adp2);

        //añadimos a la tabla
        fila.addView(edreferencia);
        fila.addView(eddescripcion);
        fila.addView(edrequisito);
        fila.addView(sptipo);
        fila.addView(cbseleccion);
        //y la añadimos a la vista

        tabla.addView(fila);

        Toast.makeText(getContext(), "fila añadida", Toast.LENGTH_SHORT).show();


    }
    //leer hoja de conformidades
    private TableRow leerfilanoconformidades (NoConformidad nc) {
        //creamos una nueva fila
        fila = new TableRow(getContext());

        //añadimos los componentes que forman la fila
        edreferencia = new EditText(getContext());
        eddescripcion = new EditText(getContext());
        sptipo = new Spinner(getContext());
        edrequisito = new EditText(getContext());
        cbseleccion = new CheckBox(getContext());


        //adaptador de array para requisito
        ArrayAdapter<CharSequence> adp = ArrayAdapter.createFromResource(getContext(), R.array.valores_noconformidad, R.layout.layout_spinner);
        //y cargamos
        sptipo.setAdapter(adp);

        //escribimos el dato en el elemento
        edreferencia.setText(String.valueOf(nc.getNumero()));
        eddescripcion.setText(nc.getDescripcion());

        //para el spinner
        String tipo = nc.getTipo();
        switch (tipo) {
            case "OC":
                sptipo.setSelection(0);
                break;
            case "ID":
                sptipo.setSelection(1);
                break;
            case "IR":
                sptipo.setSelection(2);
                break;
            case "IF":
                sptipo.setSelection(3);
                break;
        }

        edrequisito.setText(nc.getRequisito());


        //añadimos a la tabla
        fila.addView(edreferencia);
        fila.addView(eddescripcion);
        fila.addView(edrequisito);
        fila.addView(sptipo);
        fila.addView(cbseleccion);
        //y la añadimos a la vista

        tabla.addView(fila);

        return fila;
    }
    //borrar una fila
    private void deletefila() {
        //contador de seleccion
        int seleccion = 0;
        //numero de filas ,(la primera es el titulo y no se cuenta
        int datos = (tabla.getChildCount()) - 1;
        //recorremos las filas
        for (int i = 1; i < tabla.getChildCount(); i++) {
            //nos colocamos en la fila
            fila = (TableRow) tabla.getChildAt(i);
            //checkbox
            cbseleccion = (CheckBox) fila.getChildAt(4);
            //comprobamos si el checkbox esta seleccionado
            if (cbseleccion.isChecked()) {

                tabla.removeView(fila);

            }

        }

    }

    //crear filas segun datos leidos
    private void crearFilas(ArrayList<NoConformidad> noConformidades) {
        //extension del arraylist
        int size = noConformidades.size();


    }
    //mostramos las noconformidades en la tabla
    private void mostrarNoConformidades(ArrayList<NoConformidad> noConformidades) {


        for (int i = 0; i < noConformidades.size(); i++) {
            //añadimosfila
            leerfilanoconformidades (noConformidades.get(i));
            //y escribimos los datos de la hoja excel en ña tabla

        }


    }

    //escribimos datos de la tabla en la hoja de excel
    private void escribirNoconformidades (){

        //tamaño de la tabla para guardar datos
        int size=tabla.getChildCount()-1;

        //recorremos cada fila
        for (int i = 1; i < size; i++) {


            //fachadaExcel.escribirNoConformidades(nc,fila);


        }







    }



}
