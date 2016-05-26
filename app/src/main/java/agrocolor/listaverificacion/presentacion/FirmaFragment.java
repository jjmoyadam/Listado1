package agrocolor.listaverificacion.presentacion;

import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

import agrocolor.listaverificacion.fachadas.FachadaExcel;
import agrocolor.listaverificacion.modelos.Auditoria;

public class FirmaFragment extends Fragment {

    //variables
    public static final String ARG_NOMBRE_AUDITORIA = "nombre_auditoria";
    public static final Auditoria ARG_AUDITORIA = null;
    private FirmaVista firmarvista;
    private FachadaExcel fachadaExcel;
    private Auditoria auditoria;
    private MainActivity contexto;
    private Bitmap firmabitmap;

    //private canvas
    private Canvas drawCanvas;

    //color del puntero
    private int paintColor = 0xFF660000;

    //carga del layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //referencia al contendedor
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_firma, container, false);

        //referencia al drawerLayout para cargarlo
        firmarvista = (FirmaVista) rootView.findViewById(R.id.firmavista);

        //recepcion de bundle
        String archivo = getArguments().getString(ARG_NOMBRE_AUDITORIA);
        //le pasamos el contexto
        contexto = (MainActivity) getActivity();
        //fachada
        fachadaExcel = new FachadaExcel(contexto);
        //auditoria
        auditoria=new Auditoria(archivo);
        //devuelve vista
        return  rootView;
    }

    //carga del menu
    public FirmaFragment() {
        setHasOptionsMenu(true);
    }

    //creacion del menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_firma, menu);
    }
    //funcionalidad del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_borrarFirma:
                firmarvista.startNew();
                Toast.makeText(contexto,"Borrar Firma",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.btn_GuardarFirma:
                try {
                    fachadaExcel.guardarfirma(auditoria);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(contexto,"Guardar Firma",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //evento de escritura




}
