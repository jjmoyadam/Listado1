package agrocolor.listaverificacion.fachadas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import agrocolor.listaverificacion.modelos.Auditoria;
import agrocolor.listaverificacion.modelos.GrupoPuntoControl;
import agrocolor.listaverificacion.modelos.ListaVerificacion;
import agrocolor.listaverificacion.modelos.NoConformidad;
import agrocolor.listaverificacion.modelos.PuntoControl;
import agrocolor.listaverificacion.presentacion.R;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Environment;
import android.widget.Toast;

public class FachadaExcel {

    //variable que le pasa el contexto
    private Context contexto;
    //extension del archivo
    public static final String EXTENSION_EXCEL = ".xls";


    //metodo para obtener la ruta
    public String getRuta() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + contexto.getResources().getString(R.string.nombre_carpeta);
    }

    //metodo para pasar el contexto
    public FachadaExcel(Context contexto) {
        this.contexto = contexto;
    }

    //creacion del directorio para guardar los datos
    public void crearDirectorio() {
        File folder = new File(getRuta());
        if (!folder.exists())
            folder.mkdirs();
    }

    //modificacion:
    //creacion del archivo y directorio
    private void crearArchivo(String nombre) throws IOException {
        File f = new File(getRuta() + File.separator + nombre);
        f.createNewFile();
    }

    //creacion del archivo y directorio
    private File creardirauditoria(String nombre) throws IOException {
        File dir = new File(getRuta() + File.separator + nombre);

        dir.mkdir();
        return dir;
    }



    //metodo para la lista de verificacion que recibe el archivo
    public ListaVerificacion leerListaConformidades(Auditoria auditoria) throws IOException {

        File inputWorkbook = new File(auditoria.getNombreArchivo());
        //entrada de flujo de archivo
        FileInputStream input_document = new FileInputStream(inputWorkbook);
        //creacion del libro xls
        HSSFWorkbook book = new HSSFWorkbook(input_document);
        //posicionamieto en la datos y configuracion en las celdas
        HSSFSheet sLista = book.getSheet("Lista");
        HSSFSheet sConfig = book.getSheet("Config");
        //grupo control puesto a null
        GrupoPuntoControl ultimoGrupo = null;
        PuntoControl pcControl = null;
        //llamada al constructor que crea la lista de verificaciones
        ListaVerificacion lv = new ListaVerificacion(auditoria.getNombreArchivo());
        //si el libro existe
        if (inputWorkbook.exists()) {
            //recorremos todas las filas
            for (int i = 1; i <= sLista.getLastRowNum(); i++) {
                //tomamos los datos de la fila actual para cargar las preguntas del formulario
                Row rowDatos = sLista.getRow(i);
                Row rowConfig = sConfig.getRow(i);
                //tomamos el dato de la columna 0 que es numerico
                Cell cel = rowDatos.getCell(contexto.getResources().getInteger(R.integer.COL_CODIGO));
                String s;
                //tomamos el valor de la celda
                cel.setCellType(Cell.CELL_TYPE_STRING);
                s = cel.getStringCellValue();
                //si contiene punto es un grupo
                if (!s.contains(".")) //es un grupo
                    ultimoGrupo = crearGrupo(rowDatos);
                    //es un punto de control
                else { //Es punto de control
                    pcControl = crearPC(rowDatos, rowConfig, lv, ultimoGrupo);
                    lv.getPuntosControl().add(pcControl);
                }

            }
            //adaptamos el libro
            lv.setWorkbook(book);
            return lv;

        }
        //si no creamos el libro retornams null
        return null;

    }

    /**
     * metodo de lista de no conformidades devuelve arraylist de tipo ob no conformidad
     *
     * @param archivo
     * @return
     * @throws IOException
     */
    public ArrayList<NoConformidad> leerListaNoConformidades(String archivo) throws IOException {
        //recojo el archivo
        File inputWorkbook = new File(getRuta() + File.separator + archivo);
        //entrada de flujo de archivo
        FileInputStream input_document = new FileInputStream(inputWorkbook);
        //creacion del libro xls
        HSSFWorkbook book = new HSSFWorkbook(input_document);
        //posicionamieto en la hoja de datos  y configuracion en las celdas
        HSSFSheet sNoConformidades = book.getSheet("NoConformidad");

        //creacion de objeto no conformidad
        ArrayList<NoConformidad> noConformidades;
        NoConformidad nc = null;
        //inicializamos el array
        noConformidades = new ArrayList<NoConformidad>();

        if (inputWorkbook.exists()) {
            //recorremos todas las filas
            for (int i = 1; i <= sNoConformidades.getLastRowNum(); i++) {
                //tomamos los datos
                Row rowNoConformidades = sNoConformidades.getRow(i);

                //y guardamos los valores en un objeto no conformidad
                nc = CrearNoconformidades(rowNoConformidades);

                //agregamos al arraylist
                noConformidades.add(nc);

            }

            return noConformidades;

        }
        //si no creamos el libro retornams null
        return null;

    }

    /**
     * metodo que lee los datos de la portada
     *
     * @param auditoria
     * @return
     * @throws IOException
     */
    public Auditoria leerPortada(Auditoria auditoria) throws IOException {

        Toast.makeText(contexto, "carga de datos portada", Toast.LENGTH_SHORT).show();
		//recojo el archivo
        File inputWorkbook = new File(auditoria.getNombreArchivo());
		//entrada de flujo de archivo
		FileInputStream input_document = new FileInputStream(inputWorkbook);
		//creacion del libro xls
		HSSFWorkbook book = new HSSFWorkbook(input_document);
		//posicionapmieto en la hoja de datos  y configuracion en las celdas
		HSSFSheet sPortada = book.getSheet("Portada");
        //llamada al constructor que crea la auditoria
       // auditoria = new Auditoria(archivo);

        //si el libro existe
        if (inputWorkbook.exists()) {
            //celdas de portada
            Cell celdafecha=sPortada.getRow(0).getCell(1);
            Cell celdavisita=sPortada.getRow(1).getCell(1);
            Cell celdaoperador=sPortada.getRow(2).getCell(1);
            Cell celdanumvisita=sPortada.getRow(3).getCell(1);
            //los mostramos en los editext
            if(celdafecha!=null)
            auditoria.setFecha((int) celdafecha.getNumericCellValue());
            if(celdavisita!=null)
            auditoria.setCodvista((int)celdavisita.getNumericCellValue());
            if(celdaoperador!=null)
            auditoria.setCodopeador((int)celdaoperador.getNumericCellValue());
            if(celdanumvisita!=null)
            auditoria.setNumvisita((int)celdanumvisita.getNumericCellValue());
        }
        return auditoria;
    }

    //pasa el valor de la celda a String
    private String toString(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue().trim();
            case Cell.CELL_TYPE_NUMERIC:
                return "" + cell.getNumericCellValue();
            default:
                return null;
        }
    }

    //pasamos el nombre del archivo para ponerle la extension excel y devuelve dicho nombre
    private static String nombreExcel(String nombre) {
        if (!nombre.toLowerCase().endsWith(EXTENSION_EXCEL))
            nombre += EXTENSION_EXCEL;

        return nombre;
    }

    //escritura en fichero excel de los datos de los formularios
    public void escribirListaVerificacion(ListaVerificacion lv) throws NotFoundException, IOException {
        //toma el nombre del archivo de las listview
        String nombreArchivo = nombreExcel(lv.getNombreArchivo());
        //creacion del archivo
        File inputWorkbook = new File(getRuta() + File.separator + nombreExcel(nombreArchivo));
        //salida del flujo de datos hacua el libro
        FileOutputStream fos = new FileOutputStream(inputWorkbook);
        //si el archivo se ha creado
        if (inputWorkbook.exists()) {
            //lectura de los datos de la hoja
            HSSFSheet sheet = lv.getWorkbook().getSheetAt(0);
            PuntoControl pc;
            //vamos tomando los valores
            for (int i = 0; i < lv.getPuntosControl().size(); i++) {
                //llamamos al metodo escribirListaVerificacion
                pc = lv.getPuntosControl().get(i);
                escribirPC(pc, sheet.getRow(pc.getFila()));
            }
            //escribirCabecera(lv);
            lv.getWorkbook().write(fos);
        }


    }

    //escritura en fichero excel de los datos del formulario de no conformidad
    public void escribirListaNoConformidades(Auditoria auditoria) throws NotFoundException, IOException {
        //toma el nombre del archivo
        String nombreArchivo = nombreExcel(auditoria.getNombreArchivo());
        //creacion del archivo
        File inputWorkbook = new File(getRuta() + File.separator + nombreExcel(nombreArchivo));
        //salida del flujo de datos hacua el libro
        FileOutputStream fos = new FileOutputStream(inputWorkbook);
        //si el archivo se ha creado
        if (inputWorkbook.exists()) {
            //lectura de los datos de la hoja
            HSSFSheet sheet = auditoria.getWorkbook().getSheet("NoConformidad");
            //declaracion de obj noconformidad
            NoConformidad nc = null;

            //vamos tomando los valores
            for (int i = 1; i < auditoria.getNoconformidades().size(); i++) {
                //recuperamos cada uno de los valores de
                nc = auditoria.getNoConformidad(i);

                //llamamos al metodo escribirListaVerificacion
                escribirNoConformidades(nc, sheet.getRow(nc.getFila()));
            }
            auditoria.getWorkbook().write(fos);
        }


    }
    //escritura en fichero excel la portada
    public void escribirPortada(Auditoria auditoria,String fecha,String visita,String operador) throws NotFoundException, IOException {

        //toma el nombre del archivo
        String nombreArchivo = nombreExcel(auditoria.getNombreArchivo());
        //creacion del archivo
        File inputWorkbook = new File(nombreArchivo);
        //salida del flujo de datos hacia el libro, true para evitar que machaque datos
        FileOutputStream fos = new FileOutputStream(inputWorkbook,true);
        //si el archivo se ha creado
        if (inputWorkbook.exists()) {

            HSSFSheet sPortada = auditoria.getWorkbook().getSheet("Portada");
            //tomamos los datos
            Cell celdafecha=sPortada.getRow(0).getCell(1);
            Cell celdavisita=sPortada.getRow(1).getCell(1);
            Cell celdaoperador=sPortada.getRow(2).getCell(1);
            Cell celdanumvisita=sPortada.getRow(3).getCell(1);

            //y escribimos
            celdafecha.setCellValue(fecha);
            celdavisita.setCellValue(visita);
            celdaoperador.setCellValue(operador);
            celdanumvisita.setCellValue(visita);

            //y escribimos
            auditoria.getWorkbook().write(fos);
            }


        }


    /**
     * metodo para creacion del grupo de control
     *
     * @param r fila
     * @return grupo de control
     */
    private GrupoPuntoControl crearGrupo(Row r) {
        GrupoPuntoControl gr = new GrupoPuntoControl();
        Cell cel = r.getCell(contexto.getResources().getInteger(R.integer.COL_CODIGO));
        gr.setCodigo(toString(cel));
        cel = r.getCell(contexto.getResources().getInteger(R.integer.COL_DESCRIPCION));
        gr.setDescripcion(cel.getStringCellValue().trim());
        return gr;

    }


    /**
     * Metodo para escribirListaVerificacion en la celda de las respuestas y observaciones
     *
     * @param pcPuntoControl recibe objeto de punto de control
     * @param r              fila del excel
     */
    private void escribirPC(PuntoControl pcPuntoControl, Row r) {
        r.getCell(contexto.getResources().getInteger(R.integer.COL_RESPUESTA)).setCellValue(pcPuntoControl.getValor());
        r.getCell(contexto.getResources().getInteger(R.integer.COL_OBSERVACIONES)).setCellValue(pcPuntoControl.getObservacion());
    }

    /**
     * Metodo para escribir en la celdas de las no Conformidades
     *
     * @param r
     */
    private void escribirNoConformidades(NoConformidad nc, Row r) {
        r.getCell(contexto.getResources().getInteger(R.integer.COL_REFERENCIA_NC)).setCellValue(nc.getNumero());
        r.getCell(contexto.getResources().getInteger(R.integer.COL_DESCRIPCION_NC)).setCellValue(nc.getDescripcion());
        r.getCell(contexto.getResources().getInteger(R.integer.COL_REQUISITOS_NC)).setCellValue(nc.getTipo());
    }

    ;

    private void escribirPortada(Row r, String fecha, String visita, String operador) {
        r.getCell(contexto.getResources().getInteger(R.integer.COL_FECHA_PORTADA)).setCellValue(fecha);
        r.getCell(contexto.getResources().getInteger(R.integer.COL_VISITA_PORTADA)).setCellValue(visita);
        r.getCell(contexto.getResources().getInteger(R.integer.COL_OPERADOR_PORTADA)).setCellValue(operador);
    }

    /**
     * Metodo para la escritura de la cabecera
     *
     * @param lv recibe la listview
     */
    /*
    private void escribirCabecera(ListaVerificacion lv) {
        //tomamosla hoja 0
        Sheet sheet = lv.getWorkbook().getSheetAt(0);
        //la cabecera
        Header header = sheet.getHeader();
        //y la escribimos en el centro de la hoja de cabecera
        header.setCenter(escribirCabecera(header.getCenter(), lv.numOperador, lv.numAuditoria));

    }
    */
    /**
     * metodo que escribe en la cabecera
     *
     * @param cabecera nombre de la cabecera
     * @param op       numero de operario
     * @param aud      numero de auditoria
     * @return cabecera
     */
    private String escribirCabecera(String cabecera, int op, int aud) {
        int posIni = 0;
        int longitud = 0;
        int posBarra = cabecera.lastIndexOf("/");
        cabecera = cabecera.substring(0, posBarra + 1) + op + "-" + aud;
        return cabecera;

    }

    private PuntoControl crearPC(Row rDatos, Row rConfig, ListaVerificacion lv, GrupoPuntoControl grupo) {

        PuntoControl pc = new PuntoControl();
        //tomamos el valor de la columna de cada fila pasada por parametro y evaluamos
        //si los datos no null
        Cell cel = rDatos.getCell(contexto.getResources().getInteger(R.integer.COL_CLASIFICACION));
        if (cel != null) pc.setCodigo(toString(cel));
        cel = rDatos.getCell(contexto.getResources().getInteger(R.integer.COL_CLASIFICACION));
        if (cel != null) pc.setClasificacion(cel.getStringCellValue().trim());
        cel = rDatos.getCell(contexto.getResources().getInteger(R.integer.COL_DESCRIPCION));
        if (cel != null) pc.setDescripcion(cel.getStringCellValue().trim());
        cel = rDatos.getCell(contexto.getResources().getInteger(R.integer.COL_OBSERVACIONES));
        if (cel != null) pc.setObservacion(cel.getStringCellValue().trim());
        cel = rDatos.getCell(contexto.getResources().getInteger(R.integer.COL_RESPUESTA));
        if (cel != null) pc.setValor(cel.getStringCellValue());
        cel = rConfig.getCell(contexto.getResources().getInteger(R.integer.COL_OPCIONES));
        if (cel != null) pc.setPosiblesObservaciones(cel.getStringCellValue());
        //guardamos el grupo en el punto de control
        pc.setGrupo(grupo);
        //el numero de la fila
        pc.setFila(rDatos.getRowNum());
        //y la lista de verificacion
        pc.setListaVerificacion(lv);
        //devuelve la lista de verificacion
        return pc;
    }

    private NoConformidad CrearNoconformidades(Row rDatosNoConf) {
        //creacion del objeto
        NoConformidad nc = new NoConformidad();
        //tomamos el valor de la columna de cada fila pasada por parametro y evaluamos
        //si los datos no null
        Cell cel = rDatosNoConf.getCell(contexto.getResources().getInteger(R.integer.COL_REFERENCIA_NC));
        if (cel != null) nc.setNumero(Integer.parseInt(toString(cel)));
        cel = rDatosNoConf.getCell(contexto.getResources().getInteger(R.integer.COL_DESCRIPCION_NC));
        if (cel != null) nc.setDescripcion(cel.getStringCellValue().trim());
        cel = rDatosNoConf.getCell(contexto.getResources().getInteger(R.integer.COL_REQUISITOS_NC));
        if (cel != null) nc.setDescripcion(cel.getStringCellValue().trim());
        cel = rDatosNoConf.getCell(contexto.getResources().getInteger(R.integer.COL_TIPO_NC));
        if (cel != null) nc.setDescripcion(cel.getStringCellValue().trim());
        //devuelve la lista de verificacion
        return nc;
    }

    /**
     * obtenemos la extension con este metodo
     *
     * @param filename
     * @return
     */
    public static String getExtension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    /**
     * lee los nombres del archivo xls y lo pasa a un arraylist
     *
     * @return lista
     */
    public ArrayList<String> leerListasDeRepositorio() {
        File dir = new File(getRuta());
        ArrayList<String> lista = new ArrayList<String>();
        File[] directorio = dir.listFiles();

        if (dir.exists()) {

            for (int x = 0; x < directorio.length; x++) {

                //si es un directorio
                if (directorio[x].isDirectory()) {
                    File[] ficheros = directorio[x].listFiles();
                    //listado de archivos del subdirectorio
                    for (int j = 0; j < ficheros.length; j++) {
                        //obtenemos la extension
                        String extension = getExtension(ficheros[j].getName());
                        if (extension.equals("xls")) {
                            lista.add(ficheros[j].getName());
                        }
                    }
                }
            }

        }
        return lista;
    }


    /**
     * metodo para la creacion de una nueva  auditoria, cargando los datos desde la plantilla excel lv_eco
     *
     * @param  auditoria
     * @return metodo leerListaConformidades de listade verificacion
     * @throws NotFoundException
     * @throws IOException
     */
    public Auditoria nuevaauditoria(Auditoria auditoria) throws NotFoundException, IOException {

        String nombreArchivo=auditoria.getNombreArchivo();

        if (!existe(nombreArchivo)) {
            //crea la carpeta para la auditoria
            File dir = creardirauditoria(nombreArchivo);
            //asinga nombre de archivo
            nombreArchivo = nombreExcel(nombreArchivo);
            String ruta = dir.getPath();
            //AssetManager assetManager = contexto.getResources().getAssets();
            InputStream is = contexto.getResources().openRawResource(R.raw.lv_eco);
            byte[] buffer = new byte[1024];
            crearArchivo(nombreArchivo);
            File out = new File(ruta, nombreArchivo);
            FileOutputStream fos = new FileOutputStream(out);
            int read = 0;
            while ((read = is.read(buffer, 0, 1024)) >= 0)
                fos.write(buffer, 0, read);

            fos.flush();
            fos.close();
            is.close();
            Toast.makeText(contexto, "archivo de la auditoria creada", Toast.LENGTH_LONG).show();
            //llama al metodo leerListaConformidades para cargar los datos de la nueva lista en la interfaz
            return leerPortada(auditoria);
        }
        return null;
    }


    /**
     * MODIFICAR
     * metodo para la creacion de un nuevo formulario, cargando los datos desde la plantilla excel lv_eco
     *
     * @param auditoria archivo a guardar
     * @return metodo leerListaConformidades de listade verificacion
     * @throws NotFoundException
     * @throws IOException
     */
    public ListaVerificacion nuevaLista(Auditoria auditoria) throws NotFoundException, IOException {

        String nombreArchivo = nombreExcel(auditoria.getNombreArchivo());

        if (!existe(nombreArchivo)) {
            //creamos el directorio
            File dir = creardirauditoria(nombreArchivo);
            //AssetManager assetManager = contexto.getResources().getAssets();
            InputStream is = contexto.getResources().openRawResource(R.raw.lv_eco);
            byte[] buffer = new byte[1024];

            crearArchivo(dir.getPath() + nombreArchivo);
            File out = new File(getRuta(), nombreArchivo);
            FileOutputStream fos = new FileOutputStream(out);
            int read = 0;

            while ((read = is.read(buffer, 0, 1024)) >= 0)
                fos.write(buffer, 0, read);

            fos.flush();
            fos.close();
            is.close();
            Toast.makeText(contexto, "archivo creado", Toast.LENGTH_LONG).show();
            //llama al metodo leerListaConformidades para cargar los datos de la nueva lista en la interfaz
            return leerListaConformidades(auditoria);
        }
        return null;
    }

    /**
     * comprobacion de si existe el archivo
     *
     * @param nombreArchivo
     * @return boolean true o false del archivo
     */
    public boolean existe(String nombreArchivo) {
        nombreArchivo = nombreExcel(nombreArchivo);
        File out = new File(getRuta(), nombreArchivo);
        return out.exists();

    }

    /**
     * comprobacion de que el nombre sea valido
     *
     * @param nombre nombre del archivo
     * @return boolean
     */
    public static boolean nombreValido(String nombre) {
        if (nombre.trim().length() == 0)
            return false;

        Pattern pattern = Pattern.compile("^[A-Z0-9 a-z]*$");
        Matcher matcher = pattern.matcher(nombre);
        return matcher.find();
    }

    /**
     * Metodo para copiar el archivo
     *
     * @param src origen
     * @param dst destino
     * @throws IOException
     */
    public void copiar(String src, String dst) throws IOException {

        InputStream in = new FileInputStream(getRuta() + File.separator + nombreExcel(src));
        OutputStream out = new FileOutputStream(getRuta() + File.separator + nombreExcel(dst));

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
            out.write(buf, 0, len);
        in.close();
        out.close();
    }


}
