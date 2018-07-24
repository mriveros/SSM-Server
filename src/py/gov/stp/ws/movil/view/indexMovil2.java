package py.gov.stp.ws.movil.view;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import py.gov.stp.ws.movil.Model.BeneficiarioSalud;
import py.gov.stp.ws.movil.Model.Captura;
import py.gov.stp.ws.movil.Model.Destinatario;
import py.gov.stp.ws.movil.Model.Proyecto;
import py.gov.stp.ws.movil.Model.Respuesta;
import py.gov.stp.ws.movil.Model.Visita;
import py.gov.stp.ws.movil.database.ConsultaMovil;
import py.gov.stp.ws.movil.database.ConsultaReportes;
import py.gov.stp.ws.movil.database.InsertMovil;
import py.gov.stp.ws.movil.database.UpdateBD;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("v3")
public class indexMovil2 {

	public indexMovil2() {
		super();
	}
	
	@GET
	@Path("asignados/{cedtecnico}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getDestinatarios(@PathParam("cedtecnico") String cedtecnico){
		ArrayList<Destinatario> destinatarios = new ArrayList<Destinatario>();
		try {
			destinatarios = ConsultaMovil.getDestinatariosAsig(cedtecnico);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(destinatarios);
		JsonArray jaArray=json.getAsJsonArray();
		return jaArray.toString();
	}
	
	@GET
	@Path("proyectos/{cedtecnico}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getEncuesta(@PathParam("cedtecnico") String cedtecnico){
		ArrayList<Proyecto>proyectos = null;
		try {
			proyectos = ConsultaMovil.getProyectos(cedtecnico);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(proyectos);
		return json.toString();
	}
	
	@POST
	@Path("recepcion/visita")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String receiverVisita(@FormParam("documento") String cedula,
			                     @FormParam("nombre") String nombre,
			                     @FormParam("apellido") String apellido,
			                     @FormParam("tipobeneficiario") int tipobeneficiario,
			                     @FormParam("esjefe") int esjefe,
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
			                     @FormParam("id_key") String id_key,
			                     @FormParam("original") int original,
			                     @FormParam("resultado") String resultado) {
		
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
		visita.setId_key(id_key);
		visita.setOriginal(original);
		Respuesta resp=null;
		try {
			resp = InsertMovil.addVisita(visita);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(resp);
		return json.toString();
	}
	
	@POST
	@Path("recepcion/correccion")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String receiverCorreccion(@FormParam("relevamiento_origen") String relevamiento_origen,
									 @FormParam("relevamiento_final") String relevamiento_final){
		Respuesta resp=null;
		try {
			resp = UpdateBD.correccion(relevamiento_origen, relevamiento_final);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(resp);
		return json.toString();
	}
	
	@POST
	@Path("recepcion/captura")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String receiverCapturas(@FormDataParam("encuestado") String encuestado,
								   @FormDataParam("archivo") InputStream fileInputStream,
								   @FormDataParam("archivo") FormDataContentDisposition contentDispositionHeader,
								   @FormDataParam("fecha") String fecha,
								   @FormDataParam("numero") int numero){
		Captura captura = new Captura();
		captura.setNombre(contentDispositionHeader.getFileName());
		captura.setFecha(fecha);
		captura.setNumero(numero);
		captura.setEncuestado(encuestado);
		captura.setInputStream(fileInputStream);
		
		Respuesta resp = InsertMovil.addCapturaFile(captura);
		JsonElement json = new Gson().toJsonTree(resp);
		return json.toString();
	}
	
	@POST
	@Path("recepcion/file/captura")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String receiverFileCapturas(@FormDataParam("archivo") InputStream fileInputStream,
								       @FormDataParam("archivo") FormDataContentDisposition contentDispositionHeader,
								       @FormDataParam("hash") String hash,
								       @FormDataParam("fecha") String fecha){
		Captura captura = new Captura();
		captura.setNombre(contentDispositionHeader.getFileName());
		captura.setInputStream(fileInputStream);
		captura.setHash(hash);
		captura.setFecha(fecha);
		
		Respuesta resp = new Respuesta();
		try {
			resp = InsertMovil.recibirImagen(captura);
		} catch (Exception e) {
			resp.setRespuesta(3);
		}
		
		JsonElement json = new Gson().toJsonTree(resp);
		return json.toString();
	}
	
	@POST
	@Path("recepcion/eventos/captura")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String receiverCapturaEventos(@FormDataParam("encuestado") String encuestado,
										 @FormDataParam("archivo") InputStream fileInputStream,
										 @FormDataParam("archivo") FormDataContentDisposition contentDispositionHeader,
										 @FormDataParam("nombre") String nombre,
										 @FormDataParam("fecha") String fecha){
		Captura captura = new Captura();
		captura.setNombre(contentDispositionHeader.getFileName());
		captura.setFecha(fecha);
		captura.setEncuestado(encuestado);
		captura.setInputStream(fileInputStream);
		Respuesta resp = InsertMovil.addCapturaFileEvt(captura);
		JsonElement json = new Gson().toJsonTree(resp);
		return json.toString();
	}
	
	@POST
	@Path("recepcion/captura/invalido")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String estadoCaptura(@FormParam("captura") String captura,
								@FormParam("tipo") int tipo){
		try {
			Respuesta resp = UpdateBD.estadoCaptura(captura,1);
			JsonElement json = new Gson().toJsonTree(resp);
			return json.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@POST
	@Path("recepcion/eventos/captura/invalido")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String estadoCapturaEvt(@FormParam("captura") String captura,
								   @FormParam("tipo") int tipo){
		try {
			Respuesta resp = UpdateBD.estadoCaptura(captura,2);
			JsonElement json = new Gson().toJsonTree(resp);
			return json.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@GET
	@Path("captura/vista/{img}")
	@Produces("images/jpg")
	public Response getImagen(@PathParam("img") String img){
		  File imagen = new File("/usr/share/tomcat/moviles/capturas/"+img);
		  ResponseBuilder response = null;
		  if(imagen.exists()){
			  response = Response.ok((Object) imagen);  ;
		      response.header("Content-Disposition","attachment; filename=\""+img+"\"");   
		  }else{
			  response = Response.status(Status.INTERNAL_SERVER_ERROR);
		  }
		 
	      return response.build();  
	}
	
	@GET
	@Path("respuesta/salud")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getRespuestas(@HeaderParam("token") String token,
			                    @PathParam("idusuario") long idusuario){
		try {
			ArrayList<BeneficiarioSalud> arrBeneficiarioSaluds = ConsultaReportes.getRespuestaSalud();
			JsonElement json = new Gson().toJsonTree(arrBeneficiarioSaluds);
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
