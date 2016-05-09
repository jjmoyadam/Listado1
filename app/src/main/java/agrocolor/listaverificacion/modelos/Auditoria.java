package agrocolor.listaverificacion.modelos;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ainhoa on 31/03/2016.
 */
public class Auditoria {
    //variables
    private int fecha;
    private int codvista;
    private int codopeador;
    private int numvisita;
    private String nombreArchivo;
    //objeto de Arraylist no conformidad que guarda los datos de las no conformidades
    private ArrayList <NoConformidad> noconformidades;
    //objeto Workbook para manejar los datos
    private HSSFWorkbook workbook;

    //constructor
    public Auditoria(String nombreArchivo) {

        this.nombreArchivo = nombreArchivo;

    }
    //metodos get y set
    public int getCodvista() {
        return codvista;
    }

    public void setCodvista(int codvista) {
        this.codvista = codvista;
    }

    public int getFecha() {
        return fecha;
    }

    public void setFecha(int fecha) {
        this.fecha = fecha;
    }

    public ArrayList<NoConformidad> getNoconformidades() {
        return noconformidades;
    }

    public void setNoconformidades(ArrayList<NoConformidad> noconformidades) {
        this.noconformidades = noconformidades;
    }

    public int getCodopeador() {
        return codopeador;
    }

    public void setCodopeador(int codopeador) {
        this.codopeador = codopeador;
    }

    public int getNumvisita() {
        return numvisita;
    }

    public void setNumvisita(int numvisita) {
        this.numvisita = numvisita;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public HSSFWorkbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(HSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public NoConformidad getNoConformidad (int pos){

        return noconformidades.get(pos);
    }



}
