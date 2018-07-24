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

import py.gov.stp.ws.movil.Model.Device;
import py.gov.stp.ws.movil.Model.EventoDevice;
import py.gov.stp.ws.movil.Model.Respuesta;
import py.gov.stp.ws.movil.database.InsertarBD;
import py.gov.stp.ws.movil.exceptions.ExcepcionTokenValid;
import py.gov.stp.ws.movil.filter.HeadersCheck;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Path("v6/device")
public class DeviceEndpoint {
	
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response receiverDispositivo(@Context HttpHeaders headers,
			                            @FormParam("serialsim") String serialsim,
			                            @FormParam("iso") String iso,
			                            @FormParam("operadora") String operadora,
			                            @FormParam("fecha") String fecha,
			                            @FormParam("imei") String imei,
			                            @FormParam("androidid") String deviceid,
			                            @FormParam("osversion") String osversion,
			                            @FormParam("fabricante") String fabricante,
			                            @FormParam("modelo") String modelo,
			                            @FormParam("usuario") String usuario){
		
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
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
		return Response.ok(json.toString()).build();
	}
	
	@POST
	@Path("eventos")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response receiverEventosCell(@Context HttpHeaders headers,
										@FormParam("usuario") String usuario,
									    @FormParam("descripcion") String descripcion,
									    @FormParam("deviceid") String deviceid,
									    @FormParam("fecha") String fecha){
		
		try {
			HeadersCheck.getInstance().validToken(headers);
		} catch (ExcepcionTokenValid e1) {
			e1.printStackTrace();
			return Response.status(UNAUTHORIZED).build();
		}
		
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
		return Response.ok(json.toString()).build();
	}
}
