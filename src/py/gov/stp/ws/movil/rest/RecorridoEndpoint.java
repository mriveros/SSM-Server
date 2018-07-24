package py.gov.stp.ws.movil.rest;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.sql.SQLException;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.gov.stp.ws.movil.Model.Recorrido;
import py.gov.stp.ws.movil.Model.Respuesta;
import py.gov.stp.ws.movil.database.InsertarBD;
import py.gov.stp.ws.movil.exceptions.ExcepcionTokenValid;
import py.gov.stp.ws.movil.filter.HeadersCheck;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Path("v6/recorridos")
public class RecorridoEndpoint {
	
	@POST
	@Path("/")
	@Produces(MediaType.TEXT_PLAIN)
	public Response receiverRecorrido(@Context HttpHeaders headers,
									  @FormParam("usuario") String usuario,
									  @FormParam("inicio") String inicio,
									  @FormParam("fin") String fin,
									  @FormParam("duracion") int duracion){
		
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
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
		return Response.ok(json.toString()).build();
	}
}
