package py.gov.stp.ws.movil.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import py.gov.stp.ws.movil.Model.Adjuntos;
import py.gov.stp.ws.movil.Model.Captura;
import py.gov.stp.ws.movil.Model.Registro;
import py.gov.stp.ws.movil.Model.Respuesta;
import py.gov.stp.ws.movil.Util.FechaUtil;

public class UpdateBD {
	
	private static ConnectionManager connectionManager;
	
	private static void init(){
		if(connectionManager == null){
			connectionManager = new ConnectionManager();
		}
	}
	
	public static boolean cambioEstadoLogin(String usuario,boolean logueado) throws SQLException{
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		try {
			ps = conect.prepareStatement("UPDATE usuarios SET logueado=? WHERE cedula=?");
			ps.setBoolean(1,logueado);
			ps.setString(2,usuario);
			ps.executeUpdate();
		    ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		return true;
	}
	
	public static boolean recepcionCaptura(Captura captura) throws SQLException, ParseException{
		init();
		Connection conect = connectionManager.getConexion();
		
		PreparedStatement ps = conect.prepareStatement("UPDATE capturas SET recepcion=true,fecha_captura=?,fecha_recepcion_fichero=NOW() WHERE imagen=? AND encuestado=?");
		ps.setTimestamp(1, FechaUtil.getTimeStamp(captura.getFecha()));
		ps.setString(2, captura.getNombre());
		ps.setString(3, captura.getEncuestado());
		ps.executeUpdate();
	    ps.close();
	    conect.close();
		return true;
	}
	
	public static boolean recepcionCaptura(String imagen,String fechacaptura) throws SQLException, ParseException{
		init();
		Connection conect = connectionManager.getConexion();
		
		PreparedStatement ps = conect.prepareStatement("UPDATE capturas SET recepcion=true,fecha_captura=?,fecha_recepcion_fichero=NOW() WHERE imagen=? ");
		ps.setTimestamp(1, FechaUtil.getTimeStamp(fechacaptura));
		ps.setString(2, imagen);
		ps.executeUpdate();
	    ps.close();
	    conect.close();
		return true;
	}
	
	public static boolean recepcionCaptura(String fechacaptura,long idcaptura,Connection connection){
		String UPDATE = "UPDATE capturas SET recepcion=true,fecha_captura=?,fecha_recepcion_fichero=NOW() WHERE id=?"; 
		try {
			PreparedStatement ps = connection.prepareStatement(UPDATE);
			ps.setTimestamp(1, FechaUtil.getTimeStamp(fechacaptura));
			ps.setLong(2, idcaptura);
			ps.executeUpdate();
			if (ps != null) {ps.close();}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean recepcionCapturaEvt(Captura captura) throws SQLException, ParseException{
		init();
		Connection conect = connectionManager.getConexion();
		
		PreparedStatement ps = conect.prepareStatement("UPDATE capturas_eventos SET recepcion=true,fecha=?,fecha_recepcion_fichero=NOW() WHERE imagen=? ");
		ps.setTimestamp(1, FechaUtil.getTimeStamp(captura.getFecha()));
		ps.setString(2, captura.getNombre());
		ps.executeUpdate();
	    ps.close();
	    conect.close();
		return true;
	}
	
	public static boolean recepcionAdjunto(Adjuntos adjuntos) throws SQLException, ParseException{
		init();
		Connection conect = connectionManager.getConexion();
		
		PreparedStatement ps = conect.prepareStatement("UPDATE adjuntos SET recepcion=true,fecha_recepcion_fichero=NOW() WHERE archivo=? AND encuestado=?;");
		ps.setString(1, adjuntos.getNombre().substring(0,(adjuntos.getNombre().length()-4)));
		ps.setString(2, adjuntos.getEncuestado());
		ps.executeUpdate();
	    ps.close();
	    conect.close();
		return true;
	}
	
	public static boolean recepcionAdjunto(long idadjunto,Connection connection) throws SQLException, ParseException{
		String UPDATE = "UPDATE adjuntos SET recepcion=true,fecha_recepcion_fichero=NOW() WHERE id=?;";
		PreparedStatement ps = connection.prepareStatement(UPDATE);
		ps.setLong(1, idadjunto);
		ps.executeUpdate();
	    ps.close();
		return true;
	}
	
	public static void fechaCerrarSession(long idregistro) throws SQLException{
		init();
		Connection conect = connectionManager.getConexion();
		
		PreparedStatement ps = null;
		try {
			ps = conect.prepareStatement("UPDATE sessiones_movil SET cerro_session=NOW() WHERE id=?;");
			ps.setLong(1,idregistro);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
	}
	
	@SuppressWarnings("resource")
	public static Respuesta correccion(String rel_origen,String rel_final) throws SQLException{
		String UPDATE = "UPDATE relevamientos SET corregido_origen=? WHERE id_key=?;";
		String UPDATE_ACTIVO = "UPDATE relevamientos SET activo=? WHERE id_key=?";
		init();
		Connection conection = connectionManager.getConexion();
		PreparedStatement ps = null;
		try {
			ps = conection.prepareStatement(UPDATE);
			ps.setString(1, rel_origen);
			ps.setString(2, rel_final);
			ps.executeUpdate();
			ps.clearParameters();
			
			ps = conection.prepareStatement(UPDATE_ACTIVO);
			ps.setInt(1, 0);
			ps.setString(2, rel_origen);
			ps.executeUpdate();
			ps.clearParameters();
			
			ps = conection.prepareStatement(UPDATE_ACTIVO);
			ps.setInt(1, 1);
			ps.setString(2, rel_final);
			ps.executeUpdate();
			ps.clearParameters();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conection != null) {conection.close();}
		}
		Respuesta respuesta = new Respuesta();
		respuesta.setRespuesta(0);
		return respuesta;
	}
	
	public static Respuesta estadoCaptura(String captura,int tipo) throws SQLException{
		Respuesta respuesta = new Respuesta();
		respuesta.setRespuesta(0);
		
		String UPDATE_1 = "UPDATE capturas SET estado=2 WHERE imagen=?";
		String UPDATE_2 = "UPDATE capturas_eventos SET estado=2 WHERE imagen=?";
		
		init();
		Connection conection = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		try {
			if(tipo == 1){
				ps = conection.prepareStatement(UPDATE_1);
			}else{
				ps = conection.prepareStatement(UPDATE_2);
			}
			ps.setString(1, captura);
			ps.executeUpdate();
		} catch (SQLException e) {
			respuesta.setRespuesta(1);
		}finally{
			if (ps != null) {ps.close();}
			if (conection != null) {conection.close();}
		}
		
		return respuesta;
	}
	
	public static boolean registarFirebaseToken(Registro registro) throws SQLException{
		init();
		Connection conection = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		try {
			ps = conection.prepareStatement("UPDATE usuarios SET fire_base_token=? WHERE cedula=?");
			ps.setString(1, registro.getFirebasetoken());
			ps.setString(2, registro.getCedusuario());
			ps.executeUpdate();
		    ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conection != null) {conection.close();}

		}
		return true;
	}
}
