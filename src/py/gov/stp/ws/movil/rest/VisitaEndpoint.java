package py.gov.stp.ws.movil.rest;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import py.gov.stp.ws.movil.Model.Adjuntos;
import py.gov.stp.ws.movil.Model.Captura;
import py.gov.stp.ws.movil.Model.Respuesta;
import py.gov.stp.ws.movil.Model.Visita;
import py.gov.stp.ws.movil.Util.FileUtil;
import py.gov.stp.ws.movil.database.InsertMovil;
import py.gov.stp.ws.movil.database.InsertarBD;
import py.gov.stp.ws.movil.database.UpdateBD;
import py.gov.stp.ws.movil.exceptions.ExcepcionTokenValid;
import py.gov.stp.ws.movil.filter.HeadersCheck;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("v6/visita")
public class VisitaEndpoint {
	
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response receiverVisita(@Context HttpHeaders headers,
								 @FormParam("documento") String cedula,
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
		
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
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
		return Response.ok(json.toString()).build();
	}
	
	@POST
	@Path("captura")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response receiverCapturas(@Context HttpHeaders headers,
								   @FormDataParam("encuestado") String encuestado,
								   @FormDataParam("archivo") InputStream fileInputStream,
								   @FormDataParam("archivo") FormDataContentDisposition contentDispositionHeader,
								   @FormDataParam("fecha") String fecha,
								   @FormDataParam("numero") int numero){
		
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
		Captura captura = new Captura();
		captura.setNombre(contentDispositionHeader.getFileName());
		captura.setFecha(fecha);
		captura.setNumero(numero);
		captura.setEncuestado(encuestado);
		captura.setInputStream(fileInputStream);
		
		Respuesta resp = InsertMovil.addCapturaFile(captura);
		JsonElement json = new Gson().toJsonTree(resp);
		return Response.ok(json.toString()).build();
	}
	
	
	@POST
	@Path("adjuntos")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response receiverAdjuntos(@Context HttpHeaders headers,
									 @FormParam("archivo") String archivo,
			   					     @FormParam("nombre") String nombre,
			   					     @FormParam("fecha") String fecha,
			   					     @FormParam("encuestado") String encuestado){
		
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
		Adjuntos adjuntos = new Adjuntos();
		adjuntos.setNombre(nombre);
		adjuntos.setArchivo(archivo);
		adjuntos.setFecha(fecha);
		adjuntos.setEncuestado(encuestado);
		Respuesta resp = InsertarBD.addAdjuntoFile(adjuntos);
		JsonElement json = new Gson().toJsonTree(resp);
		return Response.ok(json.toString()).build();
	}
	
	@POST
	@Path("captura/valid")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response validImagen(@Context HttpHeaders headers,
							  @FormParam("nombre") String nombre,
							  @FormParam("size") long size){
		
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
		Captura captura = new Captura();
		captura.setNombre(nombre);
		captura.setSize(size);
		Respuesta respuesta = FileUtil.checkImagenFile(captura);
		JsonElement json = new Gson().toJsonTree(respuesta);
		return Response.ok(json.toString()).build();
	}
	
	@GET
	@Path("captura/vista/{img}")
	@Produces("images/jpg")
	public Response getImagen(@Context HttpHeaders headers,
							  @PathParam("img") String img){
		  
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
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
	
	@POST
	@Path("correccion")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response receiverCorreccion(@Context HttpHeaders headers,
									   @FormParam("relevamiento_origen") String relevamiento_origen,
									   @FormParam("relevamiento_final") String relevamiento_final){
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
		Respuesta resp = null;
		try {
			resp = UpdateBD.correccion(relevamiento_origen, relevamiento_final);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(resp);
		return Response.ok(json.toString()).build();
	}
	
	@POST
	@Path("captura/invalido")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response estadoCaptura(@Context HttpHeaders headers,
								  @FormParam("captura") String captura,
								  @FormParam("tipo") int tipo){
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
		Respuesta resp = null;
		try {
			resp = UpdateBD.estadoCaptura(captura,1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(resp);
		return Response.ok(json.toString()).build();
	}
	
	@POST
	@Path("captura/file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response receiverFileCapturas(@Context HttpHeaders headers,
									     @FormDataParam("archivo") InputStream fileInputStream,
								         @FormDataParam("archivo") FormDataContentDisposition contentDispositionHeader,
								         @FormDataParam("hash") String hash,
								         @FormDataParam("fecha") String fecha){
		
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
		
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
		return Response.ok(json.toString()).build();
	}
}
