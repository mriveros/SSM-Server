package py.gov.stp.ws.movil.Model;

import java.util.ArrayList;

public class Evento {
	
	private long id_evento;
	private String usuario;
	private String fecha;
	private String fecha_inserccion;
	private String longitud;
	private String latitud;
	private String presicion;
	private String descripcion;
	private String capturas;
	private ArrayList<String>arr_capturas;
	
	public Evento() {
		super();
	}
	
	public long getId_evento() {
		return id_evento;
	}


	public void setId_evento(long id_evento) {
		this.id_evento = id_evento;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getLongitud() {
		return longitud;
	}

	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}

	public String getLatitud() {
		return latitud;
	}

	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}

	public Double getPresicion() {
		return Double.parseDouble(presicion);
	}

	public void setPresicion(String presicion) {
		this.presicion = presicion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCapturas() {
		return capturas;
	}

	public void setCapturas(String capturas) {
		this.capturas = capturas;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	

	public String getFecha_inserccion() {
		return fecha_inserccion;
	}

	public void setFecha_inserccion(String fecha_inserccion) {
		this.fecha_inserccion = fecha_inserccion;
	}

	public ArrayList<String> getArr_capturas() {
		return arr_capturas;
	}

	public void setArr_capturas(ArrayList<String> arr_capturas) {
		this.arr_capturas = arr_capturas;
	}
}
