package py.gov.stp.ws.movil.view;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import py.gov.stp.ws.movil.Model.Adjuntos;
import py.gov.stp.ws.movil.Model.Captura;
import py.gov.stp.ws.movil.Model.Device;
import py.gov.stp.ws.movil.Model.Encuestados;
import py.gov.stp.ws.movil.Model.Evento;
import py.gov.stp.ws.movil.Model.EventoDevice;
import py.gov.stp.ws.movil.Model.LoginResult;
import py.gov.stp.ws.movil.Model.Proyecto;
import py.gov.stp.ws.movil.Model.Recorrido;
import py.gov.stp.ws.movil.Model.Respuesta;
import py.gov.stp.ws.movil.Model.Ubicacion;
import py.gov.stp.ws.movil.Model.Visita;
import py.gov.stp.ws.movil.Util.FechaUtil;
import py.gov.stp.ws.movil.Util.FileUtil;
import py.gov.stp.ws.movil.database.ConsultaDB;
import py.gov.stp.ws.movil.database.InsertarBD;
import py.gov.stp.ws.movil.database.UpdateBD;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;


//import para consumir webservices

// fin import para consumir webservies


/**
 * @author desarrollo
 * Indexa todos los posibles comandos del API
 */
@Path("v1")
public class index{

	public index() {
		super();
	}
	
	/**
	 * Metodo de autenticacion de Credenciales
	 * @param username
	 * @param password
	 * @return
	 */
	@POST
	@Path("login")
	@Produces(MediaType.TEXT_PLAIN)
	public String autenticar(@FormParam("username") String username,@FormParam("password") String password){
		LoginResult loginResult = null;
		try {
			System.out.println(username+password);
			loginResult = ConsultaDB.autenticarUser(username, password);
		} catch (SQLException e) {
			loginResult = LoginResult.ERROR_SERVER(); 
			e.printStackTrace();
		} 
		JsonElement json = new Gson().toJsonTree(loginResult);
		return json.toString();
	}
	
	@GET
	@Path("asignados/{cedula}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getAsignados(@PathParam("cedula") String cedula){
		ArrayList<Encuestados> encuestados = null;
		try {
			encuestados = ConsultaDB.getEncuestadoAsig(cedula);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(encuestados);
		JsonArray jaArray=json.getAsJsonArray();
		return jaArray.toString();
	}
	
	
//get para consumir webservices
	@GET
	@Path("consumirWebservice")
	@Produces(MediaType.TEXT_PLAIN)
	public String getPrueba(){
		ClientConfig config = new DefaultClientConfig();
		  Client client = Client.create(config);
		  WebResource service = client.resource(UriBuilder.fromUri("http://man.datapy.info").build());
		  // getting XML data
		  System.out.println(service.path("dist").path("datos.json").accept(MediaType.APPLICATION_JSON).get(String.class));
		return service.path("dist").path("datos.json").accept(MediaType.APPLICATION_JSON).get(String.class);
	}
	
//fin get para consumir webservices
	
	
	@GET
	@Path("encuesta/{cedula}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getEncuesta(@PathParam("cedula") String cedula){
		ArrayList<Proyecto>proyectos = null;
		try {
			proyectos = ConsultaDB.getProyectos(cedula,false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(proyectos);
		return json.toString();
	}

	@POST
	@Path("recepcion/visita")
	@Produces(MediaType.TEXT_PLAIN)
	public String receiverVisita(@FormParam("documento") String cedula,
			                     @FormParam("nombre") String nombre,
			                     @FormParam("apellido") String apellido,
			                     @FormParam("tipobeneficiario") int tipobeneficiario,
			                     @FormParam("longitud") String longitud,
			                     @FormParam("latitud") String latitud,
			                     @FormParam("presicion") Double presicion,
			                     @FormParam("horaini") String horaini,
			                     @FormParam("horafin") String horafin,
			                     @FormParam("duracion") String duracion,
			                     @FormParam("observacion") String observacion,
			                     @FormParam("motivo") String motivo,
			                     @FormParam("proyecto") String proyecto,
			                     @FormParam("usuario") String usuario,
			                     @FormParam("distrito") String distrito,
			                     @FormParam("departamento") String departamento,
			                     @FormParam("localidad") String localidad,
			                     @FormParam("capturas") String capturas,
			                     @FormParam("adjuntos") String adjuntos,
			                     @FormParam("resultado") String resultado,
			                     @FormParam("esjefe") int esjefe) {
		
		Visita visita = new Visita();
		visita.setCedula(cedula);
		visita.setNombre(nombre);
		visita.setApellido(apellido);
		visita.setTipobeneficiario(tipobeneficiario);
		visita.setLongitud(longitud);
		visita.setLatitud(latitud);
		visita.setPresicion(presicion);
		visita.setHoraini(horaini);
		visita.setHorafin(horafin);
		visita.setDuracion(duracion);
		visita.setObservacion(observacion);
		visita.setMotivo(motivo);
		visita.setProyecto(proyecto);
		visita.setUsuario(usuario);
		visita.setCapturas(capturas);
		visita.setAdjuntos(adjuntos);
		visita.setResultado(resultado);
		visita.setDepartamento(departamento);
		visita.setDistrito(distrito);
		visita.setLocalidad(Integer.parseInt(localidad));
		visita.setEsjefe(esjefe);
		Respuesta resp=null;
		try {
			resp = InsertarBD.addVisita(visita);
		} catch (SQLException | ParseException e) {
			resp = new Respuesta();
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(resp);
		return json.toString();
	}
	
	@POST
	@Path("recepcion/captura")
	@Produces(MediaType.TEXT_PLAIN)
	public String receiverCapturas(@FormParam("encuestado") String encuestado,
								   @FormParam("archivo") String archivo,
								   @FormParam("nombre") String nombre,
								   @FormParam("fecha") String fecha,
								   @FormParam("numero") int numero){
		
		Captura captura = new Captura();
		captura.setNombre(nombre);
		captura.setArchivo(archivo);
		captura.setFecha(fecha);
		captura.setNumero(numero);
		captura.setEncuestado(encuestado);
		Respuesta resp = InsertarBD.addCapturaFile(captura);
		JsonElement json = new Gson().toJsonTree(resp);
		return json.toString();
	}
	
	@POST
	@Path("recepcion/captura/valid")
	@Produces(MediaType.TEXT_PLAIN)
	public String validImagen(@FormParam("nombre") String nombre,
							  @FormParam("size") long size){
		Captura captura = new Captura();
		captura.setNombre(nombre);
		captura.setSize(size);
		Respuesta respuesta = FileUtil.checkImagenFile(captura);
		JsonElement json = new Gson().toJsonTree(respuesta);
		return json.toString();
	}
	
	@POST
	@Path("recepcion/adjuntos")
	@Produces(MediaType.TEXT_PLAIN)
	public String receiverAdjuntos(@FormParam("archivo") String archivo,
			   					   @FormParam("nombre") String nombre,
			   					   @FormParam("fecha") String fecha,
			   					   @FormParam("encuestado") String encuestado){
		
		Adjuntos adjuntos = new Adjuntos();
		adjuntos.setNombre(nombre);
		adjuntos.setArchivo(archivo);
		adjuntos.setFecha(fecha);
		adjuntos.setEncuestado(encuestado);
		Respuesta resp = InsertarBD.addAdjuntoFile(adjuntos);
		JsonElement json = new Gson().toJsonTree(resp);
		return json.toString();
	}
	
	@POST
	@Path("recepcion/ubicacion")
	@Produces(MediaType.TEXT_PLAIN)
	public String receiverUbicacion(@FormParam("longitud") String longitud,
									@FormParam("latitud") String latitud,
									@FormParam("altitud") String altitud,
									@FormParam("precision") String precision,
									@FormParam("proveedor") String proveedor,
									@FormParam("horaobtenido") String horaobtenido,
									@FormParam("horaguardado") String horaguardado,
									@FormParam("bateria") String bateria,
									@FormParam("tipo") String tipo,
									@FormParam("usuario") String usuario){
		Ubicacion ubicacion = new Ubicacion();
		ubicacion.setLongitud(longitud);
		ubicacion.setLatitud(latitud);
		ubicacion.setAltitud(altitud);
		ubicacion.setPrecision(precision);
		ubicacion.setProveedor(proveedor);
		ubicacion.setHoraobtenido(horaobtenido);
		ubicacion.setHoraguardado(horaguardado);
		ubicacion.setBateria(bateria);
		ubicacion.setTipo(tipo);
		ubicacion.setUsuario(usuario);
		Respuesta resp = null;
		try {
			resp = InsertarBD.addUbicacion(ubicacion);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(resp);
		return json.toString();
	}
	
	@POST
	@Path("device/register")
	@Produces(MediaType.TEXT_PLAIN)
	public String registerDispositivo(@FormParam("serialsim") String serialsim,
		    						  @FormParam("iso") String iso,
		    						  @FormParam("operadora") String operadora,
		    						  @FormParam("fecha") String fecha,
		    						  @FormParam("imei") String imei,
		    						  @FormParam("androidid") String deviceid,
		    						  @FormParam("osversion") String osversion,
		    						  @FormParam("fabricante") String fabricante,
								      @FormParam("modelo") String modelo){
		Device device = new Device();
		device.setSerialsim(serialsim);
		device.setIso(iso);
		device.setOperadora(operadora);
		device.setImei(imei);
		device.setFabricante(fabricante);
		device.setModelo(modelo);
		device.setFecha(fecha);
		device.setAndroidid(deviceid);
		device.setOsversion(osversion);
		
		Respuesta resp = null;
		try {
			resp = InsertarBD.registerDevice(device);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(resp);
		return json.toString();
	}
	
	
	@POST
	@Path("recepcion/device")
	@Produces(MediaType.TEXT_PLAIN)
	public String receiverDispositivo(@FormParam("serialsim") String serialsim,
		    						  @FormParam("iso") String iso,
		    						  @FormParam("operadora") String operadora,
		    						  @FormParam("fecha") String fecha,
		    						  @FormParam("imei") String imei,
		    						  @FormParam("androidid") String deviceid,
		    						  @FormParam("osversion") String osversion,
		    						  @FormParam("fabricante") String fabricante,
								      @FormParam("modelo") String modelo,
								      @FormParam("usuario") String usuario){
		Device device = new Device();
		device.setSerialsim(serialsim);
		device.setIso(iso);
		device.setOperadora(operadora);
		device.setImei(imei);
		device.setFabricante(fabricante);
		device.setModelo(modelo);
		device.setFecha(fecha);
		device.setAndroidid(deviceid);
		device.setOsversion(osversion);
		device.setUsuario(usuario);
		
		Respuesta resp = null;
		try {
			resp = InsertarBD.addDevices(device);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(resp);
		return json.toString();
	}
	
	@POST
	@Path("recepcion/eventos")
	@Produces(MediaType.TEXT_PLAIN)
	public String receiverEventos(@FormParam("usuario") String usuario,
								  @FormParam("longitud") String longitud,
								  @FormParam("latitud") String latitud,
								  @FormParam("presicion") String presicion,
								  @FormParam("descripcion") String descripcion,
								  @FormParam("capturas") String capturas,
								  @FormParam("fecha") String fecha){
		Evento evento = new Evento();
		evento.setUsuario(usuario);
		evento.setLongitud(longitud);
		evento.setLatitud(latitud);
		evento.setPresicion(presicion);
		evento.setDescripcion(descripcion);
		evento.setCapturas(capturas);
		evento.setFecha(fecha);
		
		Respuesta resp = null;
		try {
			resp = InsertarBD.addEvento(evento);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(resp);
		return json.toString();
	}
	
	@POST
	@Path("recepcion/eventos/captura")
	@Produces(MediaType.TEXT_PLAIN)
	public String receiverCapturaEventos(@FormParam("encuestado") String encuestado,
			   						     @FormParam("archivo") String archivo,
			   						     @FormParam("nombre") String nombre,
			   						     @FormParam("fecha") String fecha){
		Captura captura = new Captura();
		captura.setNombre(nombre);
		captura.setArchivo(archivo);
		captura.setFecha(fecha);
		captura.setEncuestado(encuestado);
		Respuesta resp = InsertarBD.addCapturaFileEvt(captura);
		JsonElement json = new Gson().toJsonTree(resp);
		return json.toString();
	}
	
	@POST
	@Path("recepcion/eventos/device")
	@Produces(MediaType.TEXT_PLAIN)
	public String receiverEventosCell(@FormParam("usuario") String usuario,
									  @FormParam("descripcion") String descripcion,
									  @FormParam("deviceid") String deviceid,
									  @FormParam("fecha") String fecha){
		EventoDevice eventoDevice = new EventoDevice();
		eventoDevice.setUsuario(usuario);
		eventoDevice.setDescripcion(descripcion);
		eventoDevice.setFecha(fecha);
		eventoDevice.setDeviceid(deviceid);
		
		Respuesta resp = null;
		try {
			resp = InsertarBD.addDeviceEvt(eventoDevice);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(resp);
		return json.toString();
	}
	
	@POST
	@Path("recepcion/recorrido")
	@Produces(MediaType.TEXT_PLAIN)
	public String receiverRecorrido(@FormParam("usuario") String usuario,
									@FormParam("inicio") String inicio,
									@FormParam("fin") String fin,
									@FormParam("duracion") int duracion){
		Recorrido recorrido = new Recorrido();
		recorrido.setUsuario(usuario);
		recorrido.setInicio(inicio);
		recorrido.setFin(fin);
		recorrido.setDuracion(duracion);
		
		Respuesta resp=null;
		try {
			resp = InsertarBD.addRecorrido(recorrido);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(resp);
		return json.toString();	
	}
	
	@PUT
	@Path("session/salir")
	@Produces(MediaType.TEXT_PLAIN)
	public String cerrarSession(@FormParam("usuario") String usuario){
		Respuesta respuesta = new Respuesta();
		try {
			UpdateBD.cambioEstadoLogin(usuario, false);
			respuesta.setRespuesta(0);
			UpdateBD.fechaCerrarSession(ConsultaDB.getLastIdSessionByUser(usuario));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(respuesta);
		return json.toString();	
	}
	
	@GET
	@Path("prueba")
	@Produces(MediaType.TEXT_PLAIN)
	public String holamundo(){
		return "<html> " + "<title>" + "Hola Mundo" + "</title>"  
	             + "<body><h1>" + "Hola Mundo en HTML : " 
	             + "</body></h1>" + "</html> ";
		
	}
	
	@GET
	@Path("horaactual")
	@Produces(MediaType.TEXT_PLAIN)
	public String horaactual(){
		JsonElement json = new Gson().toJsonTree(FechaUtil.getFechaActual());
		return json.toString();	
		
	}
}
