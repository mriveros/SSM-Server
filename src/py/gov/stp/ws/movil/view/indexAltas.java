package py.gov.stp.ws.movil.view;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import py.gov.stp.ws.movil.Model.Captura;
import py.gov.stp.ws.movil.Model.Formulario;
import py.gov.stp.ws.movil.Model.ReclamoCallCenter;
import py.gov.stp.ws.movil.Model.Respuesta;
import py.gov.stp.ws.movil.database.ConsultaMovil;
import py.gov.stp.ws.movil.database.ConsultaReportes;
import py.gov.stp.ws.movil.database.InsertarBD;

@Path("v4")
public class indexAltas {
	
	@GET
	@Path("prueba")
	@Produces(MediaType.TEXT_PLAIN)
	public String holamundo(){
		return "<html> " + "<title>" + "Hola Mundo" + "</title>"  
	             + "<body><h1>" + "Hola Mundo en HTML : " 
	             + "</body></h1>" + "</html> ";
		
	}
	
	@POST
	@Path("callcenter")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String altaCallcenter(@FormParam("usuario") String usuario,
								 @FormParam("cedula") String cedula,
								 @FormParam("idproyecto") String idproyecto,
								 @FormParam("nombre") String nombre,
								 @FormParam("nrocarnet") String nrocarnet,
								 @FormParam("telefono") String telefono,
								 @FormParam("localidad") String localidad,
								 @FormParam("centrosalud") String centrosalud,
								 @FormParam("reclamo") String reclamo){
		
		ReclamoCallCenter reclamoCallCenter = new ReclamoCallCenter();
		reclamoCallCenter.setCedula(cedula);
		reclamoCallCenter.setNombre(nombre);
		reclamoCallCenter.setNrocarnet(Integer.parseInt(nrocarnet));
		reclamoCallCenter.setTelefono(telefono);
		reclamoCallCenter.setLocalidad(localidad);
		reclamoCallCenter.setCentroSalud(centrosalud);
		reclamoCallCenter.setReclamo(reclamo);
	
		Respuesta respuesta = new Respuesta();
		
		try {
			InsertarBD.registraReclamo(reclamoCallCenter,usuario,Integer.parseInt(idproyecto));
			respuesta.setRespuesta(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(respuesta);
		return json.toString();
	}
	
	@GET
	@Path("reclamo/callcenter")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getReclamoCallCenter(){
		ArrayList<ReclamoCallCenter> arr_reclamo_callcenter = new ArrayList<ReclamoCallCenter>();
		try {
			arr_reclamo_callcenter = ConsultaReportes.getReclamoCallCenter();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(arr_reclamo_callcenter);
		return json.toString();
	}
	
	@GET
	@Path("imagenes")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getImagenes(){
		ArrayList<Captura> arr_capturas = new ArrayList<Captura>();
		try {
			arr_capturas = ConsultaMovil.getImagenes();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(arr_capturas);
		return json.toString();
	}
	
	@GET
	@Path("formulario/{idformulario}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String getFormulario(@PathParam("idformulario") int idformulario){
		Formulario formulario = new Formulario();
		try {
			formulario = ConsultaMovil.getFormulario(idformulario,true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JsonElement json = new Gson().toJsonTree(formulario);
		return json.toString();
	}
}
