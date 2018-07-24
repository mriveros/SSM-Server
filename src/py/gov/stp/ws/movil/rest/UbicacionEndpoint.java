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
import py.gov.stp.ws.movil.Model.Respuesta;
import py.gov.stp.ws.movil.Model.Ubicacion;
import py.gov.stp.ws.movil.database.InsertarBD;
import py.gov.stp.ws.movil.exceptions.ExcepcionTokenValid;
import py.gov.stp.ws.movil.filter.HeadersCheck;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Path("v6/ubicacion")
public class UbicacionEndpoint {
	
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response receiverUbicacion(@Context HttpHeaders headers,
									@FormParam("longitud") String longitud,
									@FormParam("latitud") String latitud,
									@FormParam("altitud") String altitud,
									@FormParam("precision") String precision,
									@FormParam("proveedor") String proveedor,
									@FormParam("horaobtenido") String horaobtenido,
									@FormParam("horaguardado") String horaguardado,
									@FormParam("bateria") String bateria,
									@FormParam("tipo") String tipo,
									@FormParam("usuario") String usuario){
		
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
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
		return Response.ok(json.toString()).build();
	}
}
