package agrocolor.listaverificacion.presentacion;

import java.util.ArrayList;

import agrocolor.listaverificacion.fachadas.FachadaExcel;
import agrocolor.listaverificacion.modelos.Auditoria;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private static final int POSICION_NUEVA_LISTA = 0;

    /*
     DECLARACIONES
     */
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private String nombre;
    private Auditoria auditoria;


    //tipo enumerado de creacion de 2 tipos de objetos ListadoLV y PuntoControl
    private enum Modo {
        ListadoLV,
        PuntoControl,
        Portada,
        NoConformidades,
        Firma,
        Volver;
    }


    //declaracion de objeto enumerado modo
    private Modo modo;

    private CharSequence activityTitle;
    private CharSequence itemTitle;
    private String[] tagTitles;
    //declaracion objeto fachada excel
    private FachadaExcel fachadaExcel;

    //creacion del arraylist para los item del menu
    ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Creacion del objeto ListadoLV ce la clase enum
        modo = Modo.ListadoLV;
        //creacion de objeto fachada excel que le pasa por parametro el contexto de la activity
        fachadaExcel = new FachadaExcel(this);
        //creacion del directorio
        fachadaExcel.crearDirectorio();
        //tomamos el valor del titulo de la actividad
        itemTitle = activityTitle = getTitle();
        //tagTitles
        tagTitles = getResources().getStringArray(R.array.Tags);
        //referencias a las ListView y DrawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        // Setear una sombra sobre el contenido principal cuando el drawer se despliegue
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        //carga menu creacion auditoria
        cargarMenuAuditorias();

        //Crear elementos de la lista
        //ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();
        /*
        items.add(new DrawerItem(tagTitles[0], R.drawable.ic_html));
        items.add(new DrawerItem(tagTitles[1], R.drawable.ic_css));
        items.add(new DrawerItem(tagTitles[2], R.drawable.ic_javascript));
        items.add(new DrawerItem(tagTitles[3], R.drawable.ic_angular));
        items.add(new DrawerItem(tagTitles[4], R.drawable.ic_python));
        items.add(new DrawerItem(tagTitles[5], R.drawable.ic_ruby));
        */

        // Habilitar el icono de la app por si hay algún estilo que lo deshabilitó
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Crear ActionBarDrawerToggle para la apertura y cierre (la barra lateral para la lista de verificaciones)
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            @Override
            /**
             * cuando cerrados la barra ponemos el titulo de la app
             */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(itemTitle);

                /*Usa este método si vas a modificar la action bar
                con cada fragmento
                 */
                //invalidateOptionsMenu();
            }

            @Override
            /**
             * cuando abrimos la app ponemos titulo de la actividad seleccionada
             */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(activityTitle);

                /*Usa este método si vas a modificar la action bar
                con cada fragmento
                 */
                //invalidateOptionsMenu();
            }
        };
        //Seteamos la escucha con el drawerToggle
        drawerLayout.setDrawerListener(drawerToggle);

        //si el salvar estado es null
        if (savedInstanceState == null) {
            //reemplaza el layout con un nuevo fragment
            mostrarListadoLV();
            drawerList.setItemChecked(0, true);
        }

    }

    /**
     * Metodo que carga los items en una arraylist los iconos de nuevo y listado para iniciarlo
     *
     * @return Arraylist
     */
    private ArrayList<DrawerItem> itemsInicio() {
        ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();
        items.add(new DrawerItem(getResources().getString(R.string.nueva_auditoria), R.drawable.ic_nuevo));
        items.add(new DrawerItem(getResources().getString(R.string.tit_listas_auditorias), R.drawable.ic_listado));
        return items;
    }

    /**
     * Metodo para la carga de iconos en el menu de navegacion del documento
     *
     * @return
     */
    private ArrayList<DrawerItem> itemsDocumento() {

        ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();
        items.add(new DrawerItem(getResources().getString(R.string.tit_navegacion_portada), R.drawable.ic_portada));
        items.add(new DrawerItem(getResources().getString(R.string.tit_navegacion_verificacion), R.drawable.ic_verificaciones));
        items.add(new DrawerItem(getResources().getString(R.string.tit_navegacion_noconformidades), R.drawable.ic_noconformidades));
        items.add(new DrawerItem(getResources().getString(R.string.tit_firma), R.drawable.ic_firma));
        items.add(new DrawerItem(getResources().getString(R.string.tit_volverlista), R.drawable.ic_volver));
        return items;
    }

    /**
     * Metodo para la carga del menu de navegacion en el documento
     */
    public void cargarMenuDocumento() {
        //cargamos los items en el menu
        items = itemsDocumento();

        // Relacionar el adaptador y la escucha de la lista del drawer
        drawerList.setAdapter(new DrawerListAdapter(this, items));
        drawerList.setOnItemClickListener(new DrawerItemClickDocumentoListener());

    }


    public void cargarMenuAuditorias() {
        //carga de los iconos
        items = itemsInicio();
        // Relacionar el adaptador y la escucha de la lista del drawer
        drawerList.setAdapter(new DrawerListAdapter(this, items));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

    }

    ;

    /**
     * Invocamos al  menu superior de la aplicacion
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        if (modo == Modo.ListadoLV) {
            inflater.inflate(R.menu.menu_main, menu);
        } else {
            if (modo == Modo.Portada) {
                inflater.inflate(R.menu.menu_portada, menu);
            } else {
                if (modo == Modo.PuntoControl) {
                    inflater.inflate(R.menu.menu_pc, menu);
                } else if (modo == Modo.NoConformidades) {
                    inflater.inflate(R.menu.menu_noconformidad, menu);
                    } else {
                        if (modo == Modo.Firma) {
                        inflater.inflate(R.menu.menu_firma, menu);
                        } else {
                            if (modo == Modo.Volver) {
                                inflater.inflate(R.menu.menu_main, menu);
                            } else {
                                inflater.inflate(R.menu.menu_main, menu);
                        }
                    }
                }
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item))
            // Toma los eventos de selección del toggle aquí
            return true;
        return super.onOptionsItemSelected(item);
    }

    /* La escucha del ListView en el Drawer del menu de la aplicacion para  */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //selectItem(position);
            switch (position) {
                case 0:
                    mostrarIntroducirNombreArchivo();
                    break;
                case 1:
                    mostrarListadoLV();
                    break;
                default:
                    break;
            }

        }

    }

    /* La escucha del ListView en el Drawer de menu de navegacion en el documento*/
    private class DrawerItemClickDocumentoListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            switch (position) {
                case 0:
                    editarPortada(auditoria, false,nombre);
                    Toast.makeText(getApplicationContext(), "Ir a Portada", Toast.LENGTH_SHORT).show();
                    modo = Modo.Portada;
                    break;

                case 1:
                    //carga de la auditoria creada
                    editarLV(auditoria,nombre);
                    Toast.makeText(getApplicationContext(), "Ir a Lista de Verificacion", Toast.LENGTH_SHORT).show();
                    modo = Modo.ListadoLV;
                    break;

                case 2:
                    Toast.makeText(getApplicationContext(), "Ir a no Conformidades", Toast.LENGTH_SHORT).show();
                    modo = Modo.NoConformidades;
                    editarNoConformidades(auditoria,nombre);

                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "Ir a Firma de Auditoria", Toast.LENGTH_SHORT).show();
                    editarFirma(auditoria,nombre);
                    modo = Modo.Firma;
                    break;
                case 4:
                    cargarMenuAuditorias();
                    Toast.makeText(getApplicationContext(), "Volver al Menu", Toast.LENGTH_SHORT).show();
                    mostrarListadoLV();
                    modo = Modo.Volver;
                    break;
                default:
                    break;
            }

        }

    }

    /**
     * mostrar fragments portada de la Auditoria
     */
    private void mostrarPortadaAuditoria() {
        // Reemplazar el contenido del layout principal por un fragmento que muestra la portada de la auditoria
        //ArticleFragment fragment = new ArticleFragment();
        PortadaFragment fragment = new PortadaFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        //cambiamos titulo por listas de verificacion
        setTitle(R.string.tit_portadaauditoria);
        //y cerramos drawerList
        drawerLayout.closeDrawer(drawerList);

    }

    /**
     * mostrar fragment  listas de auditorias
     */
    private void mostrarListadoLV() {
        // Reemplazar el contenido del layout principal por un fragmento que muestra las auditorias creadas
        //ArticleFragment fragment = new ArticleFragment();
        ListadoArchivosFragment fragment = new ListadoArchivosFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        //cambiamos a menu
        cargarMenuAuditorias();

        //cambiamos titulo por listas de verificacion
        setTitle(R.string.tit_listas_auditorias);
        //y cerramos drawerList
        drawerLayout.closeDrawer(drawerList);


    }

    /**
     * mostrar fragment lista de verificaciones
     */
    private void mostrarVerificaciones() {

        // Reemplazar el contenido del layout principal por un fragmento que muestra las auditorias creadas
        //ArticleFragment fragment = new ArticleFragment();
        PuntoControlFragment fragment = new PuntoControlFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        //cambiamos titulo por listas de verificacion
        setTitle(R.string.tit_navegacion_noconformidades);
        //y cerramos drawerList
        drawerLayout.closeDrawer(drawerList);
    }


    public void editarLV(Auditoria auditoria,String nombre) {

        String nombreArchivo=auditoria.getNombreArchivo();

        PuntoControlFragment fragment = new PuntoControlFragment();
        //pasamos los argumentos al fragment el nombre del archivo y si es nueva o no
        Bundle args = new Bundle();
        args.putString(PuntoControlFragment.ARG_NOMBRE_AUDITORIA, nombreArchivo);
        //args.putBoolean(PuntoControlFragment.ARG_NUEVA_AUDITORIA, nuevo);
        //lo asignamos con el set
        fragment.setArguments(args);
        //mediante el fragmentManager reemplazamos el content_frame por el fragment creado con los datos
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        //selecion por defecto del elemento
        drawerList.setItemChecked(POSICION_NUEVA_LISTA, true);
        //cambio de titulo
        setTitle(getResources().getString(R.string.tit_navegacion_verificacion)+" : "+nombre);
        //cerramos el drawerLayout
        drawerLayout.closeDrawer(drawerList);


    }


    public void editarPortada(Auditoria auditoria, boolean nuevo,String nombre) {

        String nombreArchivo=auditoria.getNombreArchivo();

        PortadaFragment fragmentportada = new PortadaFragment();

        //pasamos los argumentos al fragment el nombre del archivo y si es nueva o no
        Bundle args = new Bundle();
        args.putString(PortadaFragment.ARG_NOMBRE_AUDITORIA, nombreArchivo);
        args.putBoolean(PortadaFragment.ARG_NUEVA_AUDITORIA, nuevo);
        //lo asignamos con el set
        fragmentportada.setArguments(args);
        //mediante el fragmentManager reemplazamos el content_frame por el fragment creado con los datos
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragmentportada).commit();
        //selecion por defecto del elemento
        drawerList.setItemChecked(POSICION_NUEVA_LISTA, true);
        //asignamos la auditoria creada para pasarla a los otros metodos editar
        if(!nuevo) {
            this.auditoria = auditoria;
            this.nombre=nombre;
        }
        //cambio de titulo
        setTitle(getResources().getString(R.string.tit_portadaauditoria)+" : "+nombre);
        //cerramos el drawerLayout
        drawerLayout.closeDrawer(drawerList);


    }

    /**cargar fragment de la firma para la auditoria
     *
     */
    public void editarFirma(Auditoria auditoria,String nombre) {

        String nombreArchivo=auditoria.getNombreArchivo();

        FirmaFragment fragmentfirma = new FirmaFragment();

        //pasamos los argumentos al fragment el nombre del archivo y si es nueva o no
        Bundle args = new Bundle();
        args.putString(FirmaFragment.ARG_NOMBRE_AUDITORIA, nombreArchivo);
        //lo asignamos con el set
        fragmentfirma.setArguments(args);
        //mediante el fragmentManager reemplazamos el content_frame por el fragment creado con los datos
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragmentfirma).commit();
        //selecion por defecto del elemento
        drawerList.setItemChecked(POSICION_NUEVA_LISTA, true);
        setTitle(getResources().getString(R.string.tit_portadaauditoria)+" : "+nombre);
        //cerramos el drawerLayout
        drawerLayout.closeDrawer(drawerList);

    }

    /**Cargar fragment de las conformidades de la auditoria
     * debemos de hacer que gire
     */
    private void editarNoConformidades(Auditoria auditoria,String nombre) {

        String nombreArchivo=auditoria.getNombreArchivo();

        NoConformidadFragment fragmentnoconformidades = new NoConformidadFragment();

        //pasamos los argumentos al fragment el nombre del archivo y si es nueva o no
        Bundle args = new Bundle();
        args.putString(PortadaFragment.ARG_NOMBRE_AUDITORIA, nombreArchivo);
        //lo asignamos con el set
        fragmentnoconformidades.setArguments(args);
        //mediante el fragmentManager reemplazamos el content_frame por el fragment creado con los datos

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragmentnoconformidades).commit();

        //cambiamos titulo por listas de verificacion
        setTitle(R.string.tit_navegacion_noconformidades+" : "+nombre);
        //y cerramos drawerList
        drawerLayout.closeDrawer(drawerList);

        //giramos la vista
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //recargamos
        cargarMenuDocumento();

    }


    /**
     * mostrar Dialogo para introducir nombre de archivo para crear una nueva auditoria
     */
    public void mostrarIntroducirNombreArchivo() {
        //alerta de dialogo para guardar el nombre de archivo
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.layout_nuevo_archivo, null);
        //declarada final para evitar cambios
        final EditText et = (EditText) v.findViewById(R.id.et_nombre_archivo);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        ad
                .setTitle(R.string.tit_nueva_lista)
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
        //creacion del dialogo a partir del constructor y lo mostramos
        final AlertDialog dialog = ad.create();
        dialog.show();
        //si pulsamos en acepta
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //trim() quita los espacios en blanco del editext
                         nombre = et.getText().toString().trim();
                        //si la validacion del nombre es incorrecta
                        if (!FachadaExcel.nombreValido(nombre))
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.msg_error_nombre_archivo_no_valido), Toast.LENGTH_SHORT).show();
                            //si el archivo ya existe
                        else if (new FachadaExcel(getBaseContext()).existe(nombre))
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.msg_archivo_existe), Toast.LENGTH_SHORT).show();
                        else {
                            //nombre que contiene la ruta total
                            auditoria=new Auditoria(nombre);
                            //si ok llamamos al metodo de editar lista de verificaciones  para añadir un nuevo archivo
                            editarPortada(auditoria, true, nombre);
                            //editarLV(nombre, true);
                            //pasamso a modo punto de control o
                            modo = Modo.Portada;
                            //cerramos dialogo
                            dialog.dismiss();
                        }
                    }
                });
    }


    /* Método auxiliar para setear el titulo de la action bar */
    @Override
    public void setTitle(CharSequence title) {
        itemTitle = title;
        getSupportActionBar().setTitle(itemTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sincronizar el estado del drawer
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Cambiar las configuraciones del drawer si hubo modificaciones
        drawerToggle.onConfigurationChanged(newConfig);
    }


}