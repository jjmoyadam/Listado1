package agrocolor.listaverificacion.presentacion;

import java.io.File;
import java.io.IOException;

import agrocolor.listaverificacion.fachadas.FachadaExcel;
import agrocolor.listaverificacion.modelos.Auditoria;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * clase que muesta el listado de archivos en el menu principal
 */
public class ListadoArchivosFragment extends Fragment {

    private ListView listView;
	private ArchivosAdapter archivosAdapter;
	private Auditoria auditoria;
	private FachadaExcel fachadaExcel;
	private MainActivity contexto;
	

    public ListadoArchivosFragment() {
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	contexto = (MainActivity)getActivity();
        View rootView = inflater.inflate(R.layout.listado_archivos, container, false);
        //int i = getArguments().getInt(ARG_ARTICLES_NUMBER);
        //String article = getResources().getStringArray(R.array.Tags)[i];
		//fachada excel
		fachadaExcel=new FachadaExcel(contexto);
        listView = (ListView) rootView.findViewById(R.id.lv_archivos);
		archivosAdapter = new ArchivosAdapter(contexto);
        listView.setAdapter(archivosAdapter);
        listView.setOnItemClickListener(onItemClick);
        listView.setOnItemLongClickListener(onItemLongClickListener_listView);
        return rootView;


    }

	/**
	 * metodo del menu para la aplicacion
	 * @param menu
	 * @param inflater
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {	
		menu.clear();
    		
	}

	/**
	 * recibe el elemento del item y devuelve true
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		return true;
	}

	/**
	 * mostrar nombre del archivo donde recibe el nombre del archivo de origen del archivo
	 * @param nombreOrigen
	 */
	public void mostrarIntroducirNombreArchivo(final String nombreOrigen) {
		AlertDialog.Builder ad = new AlertDialog.Builder(contexto);
		// Get the layout inflater
		LayoutInflater inflater = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.layout_nuevo_archivo, null); 
		final EditText et = (EditText)v.findViewById(R.id.et_nombre_archivo);
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		ad
		.setTitle(R.string.tit_copiar)
		.setView(v)
		// Add action buttons
		.setPositiveButton(R.string.aceptar, null)
		.setNegativeButton(R.string.cancelar,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							// LoginDialogFragment.this.getDialog().cancel();
						}
					});
		
		final AlertDialog dialog = ad.create();
		dialog.show();

		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String nombreDestino = et.getText().toString().trim();
						//si no es valido
						if(!FachadaExcel.nombreValido(nombreDestino))
							Toast.makeText(contexto, getResources().getString(R.string.msg_error_nombre_archivo_no_valido), Toast.LENGTH_SHORT).show();
						//si existe el archivo
						else if(new FachadaExcel(contexto).existe(nombreDestino))
							Toast.makeText(contexto, getResources().getString(R.string.msg_archivo_existe), Toast.LENGTH_SHORT).show();
						else
						{
							//si todo ok copia el archivo
							try {
								FachadaExcel f = new FachadaExcel(contexto);
								f.copiar(nombreOrigen, nombreDestino);
								archivosAdapter.actualizar();
							} catch (IOException e) {
								//Mensaje.mostrar(getContext(), getResources().getString(R.string.tit_error), e.getStackTrace().toString(), 
								//		contexto.getResources().getString(R.string.aceptar), null);
							}
							dialog.dismiss();
						}
					}
				});
	}
	/**
	 * carga la auditoria cuando pulsamos el elemento de la lista de auditorias (lista de archivos)
	 */
	private OnItemClickListener onItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int arg2,
				long arg3) {
			//nombre del archivo
			String nombre=((TextView)v.findViewById(R.id.tv_nombre_archivo)).getText().toString();
			//pasamos al main el nombre del archivo
			contexto.setNombre(rutaarchivo(nombre));
			//editar lista de verificaciones
			//contexto.editarLV(((TextView)v.findViewById(R.id.tv_nombre_archivo)).getText().toString(), false);
			contexto.editarPortada(rutaarchivo(nombre), false);
			//tostada de on click
			Toast.makeText(contexto,"Carga de Portada",Toast.LENGTH_SHORT).show();
			//carga del menu lateral
			contexto.cargarMenuDocumento();

		}
	    };
    
    private OnItemLongClickListener onItemLongClickListener_listView = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View v,
				int arg2, long arg3) {
			final String nombre = ((TextView)v.findViewById(R.id.tv_nombre_archivo)).getText().toString();
			mostrarIntroducirNombreArchivo(nombre);
			return true;
		}
	};
	private  String rutaarchivo (String nombre){

		//nombre de directorio
		String dir=nombre.substring(0,nombre.lastIndexOf("."));
		//creamos la ruta
		String ruta=fachadaExcel.getRuta().toString()+ File.separator+dir+File.separator+nombre;

		return ruta;
	}


    

    
}
