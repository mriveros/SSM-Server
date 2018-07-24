package py.gov.stp.ws.movil.rest;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.io.InputStream;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.gov.stp.ws.movil.Model.Captura;
import py.gov.stp.ws.movil.Model.Evento;
import py.gov.stp.ws.movil.Model.Respuesta;
import py.gov.stp.ws.movil.database.InsertMovil;
import py.gov.stp.ws.movil.database.InsertarBD;
import py.gov.stp.ws.movil.database.UpdateBD;
import py.gov.stp.ws.movil.exceptions.ExcepcionTokenValid;
import py.gov.stp.ws.movil.filter.HeadersCheck;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("v6/eventos")
public class EventosEndpoint {
	
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response receiverEventos(@Context HttpHeaders headers,
								    @FormParam("usuario") String usuario,
								    @FormParam("longitud") String longitud,
								    @FormParam("latitud") String latitud,
								    @FormParam("presicion") String presicion,
								    @FormParam("descripcion") String descripcion,
								    @FormParam("capturas") String capturas,
								    @FormParam("fecha") String fecha){
		
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
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
		return Response.ok(json.toString()).build();
	}
	
	@POST
	@Path("captura")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response receiverCapturaEventos(@Context HttpHeaders headers,
										 @FormDataParam("encuestado") String encuestado,
										 @FormDataParam("archivo") InputStream fileInputStream,
										 @FormDataParam("archivo") FormDataContentDisposition contentDispositionHeader,
										 @FormDataParam("nombre") String nombre,
										 @FormDataParam("fecha") String fecha){
		
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
		Captura captura = new Captura();
		captura.setNombre(contentDispositionHeader.getFileName());
		captura.setFecha(fecha);
		captura.setEncuestado(encuestado);
		captura.setInputStream(fileInputStream);
		Respuesta resp = InsertMovil.addCapturaFileEvt(captura);
		JsonElement json = new Gson().toJsonTree(resp);
		return Response.ok(json.toString()).build();
	}
	
	@POST
	@Path("captura/invalido")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response estadoCapturaEvt(@Context HttpHeaders headers,
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
			resp = UpdateBD.estadoCaptura(captura,2);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		JsonElement json = new Gson().toJsonTree(resp);
		return Response.ok(json.toString()).build();
	}
}
