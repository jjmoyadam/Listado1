package agrocolor.listaverificacion.modelos;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class NoConformidad {

	private Auditoria auditoria;
	private String descripcion;
	private int numero;
	private String nombrearchivo;

	//añado variables
	private char requisito;
	private String Tipo;
	private int fila;


	
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	//metodos get and set
	public char getRequisito() {
		return requisito;
	}

	public void setRequisito(char requisito) {
		this.requisito = requisito;
	}

	public String getTipo() {
		return Tipo;
	}

	public void setTipo(String tipo) {
		Tipo = tipo;
	}

	public Auditoria getAuditoria() {
		return auditoria;
	}

	public void setAuditoria(Auditoria auditoria) {
		this.auditoria = auditoria;
	}

	public String getNombrearchivo() {
		return nombrearchivo;
	}

	public void setNombrearchivo(String nombrearchivo) {
		this.nombrearchivo = nombrearchivo;
	}

	public int getFila() {
		return fila;
	}

	public void setFila(int fila) {
		this.fila = fila;
	}
}
