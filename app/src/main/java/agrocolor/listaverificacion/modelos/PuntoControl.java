package agrocolor.listaverificacion.modelos;

import java.util.ArrayList;

public class PuntoControl {
	
	private String descripcion;
	private String clasificacion;
	private String valor;
	private String observacion;
	private String[] listaPosiblesObservaciones;
	private String posiblesObservaciones;

	//variables objeto
	private NoConformidad noConformidad;
	private String codigo;
	private GrupoPuntoControl grupo;
	private ListaVerificacion listaVerificacion;
	private int fila;

	//variables de verdadero falso y no sabe
	public static final String V = "V";
	public static final String F = "F";
	public static final String NA = "NA";	

	//variables de peso de cada una de las preguntas
	public static final String A = "A";
	public static final String APLUS = "A+";
	public static final String B = "B";
	public static final String C = "C";
	public static final String BA = "B/A";
			
	


	public PuntoControl()
	{
	
	}

	//metodos de retorno para las variables
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getClasificacion() {
		return clasificacion;
	}
	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	/**
	 * devuelve lista de posibles verificaciones
	 * @return lista
	 */
	public ArrayList<String> getListaPosiblesObservaciones() {
		//split : metodo que convierte una cadeda de caracteres separados ; en un array de String
		String[] strings = posiblesObservaciones.split(";");
		//inicializacion arraylist que cargaremos con los datos del array String
		ArrayList<String> lista = new ArrayList<String>();
		for(int i=0; i<strings.length; i++)
			lista.add(strings[i]);
		return lista;
	}
	
	public NoConformidad getNoConformidad() {
		return noConformidad;
	}
	public void setNoConformidad(NoConformidad noConformidad) {
		this.noConformidad = noConformidad;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public GrupoPuntoControl getGrupo() {
		return grupo;
	}
	public void setGrupo(GrupoPuntoControl grupo) {
		this.grupo = grupo;
	}
	
	public int getFila() {
		return fila;
	}

	public void setFila(int fila) {
		this.fila = fila;
	}

	/**
	 * metodo de posibles observaciones, si null devuelve ""
	 * @return
	 */
	public String getPosiblesObservaciones() {
		if(posiblesObservaciones == null)
			return "";
		return posiblesObservaciones;
	}

	/**
	 *
	 * @param posiblesObservaciones
	 */
	public void setPosiblesObservaciones(String posiblesObservaciones) {
		if(posiblesObservaciones == null)
			posiblesObservaciones = "";
		else
			this.posiblesObservaciones = posiblesObservaciones;
	}
	
	public ListaVerificacion getListaVerificacion() {
		return listaVerificacion;
	}

	public void setListaVerificacion(ListaVerificacion listaVerificacion) {
		this.listaVerificacion = listaVerificacion;
	}

	//comprobacion de que el formulario se ha completado
	public boolean completado()
	{
		if(clasificacion.trim().length() == 0 && observacion.trim().length() == 0)
			return false;
		if(clasificacion.trim().length() > 0 && valor.trim().length() == 0)
			return false;
		if(clasificacion.trim().length() > 0 && valor.equalsIgnoreCase(NA) && observacion.trim().length() == 0)
			return false;
		if(clasificacion.trim().length() > 0 && valor.equalsIgnoreCase(F) && observacion.trim().length() == 0)
			return false;
		if(clasificacion.trim().length() > 0 && clasificacion.equalsIgnoreCase(A) && observacion.trim().length() == 0)			
			return false;
		if(clasificacion.trim().length() > 0 && clasificacion.equalsIgnoreCase(APLUS) && observacion.trim().length() == 0)			
			return false;
		if(clasificacion.trim().length() > 0 && clasificacion.equalsIgnoreCase(B) && observacion.trim().length() == 0)
			return false;
		if(clasificacion.trim().length() > 0 && clasificacion.equalsIgnoreCase(BA) && observacion.trim().length() == 0)
			return false;
		
		return true;
			
	}
	
	
	@Override
	public String toString()
	{
		return codigo+" "+descripcion;
	}
	

	
}
