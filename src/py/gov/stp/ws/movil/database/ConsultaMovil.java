package py.gov.stp.ws.movil.database;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import py.gov.stp.ws.movil.Model.Captura;
import py.gov.stp.ws.movil.Model.ConfigProyect;
import py.gov.stp.ws.movil.Model.Destinatario;
import py.gov.stp.ws.movil.Model.Formulario;
import py.gov.stp.ws.movil.Model.Proyecto;
import py.gov.stp.ws.movil.Model.Seccion;

public class ConsultaMovil {
	
	private static ConnectionManager connectionManager;
	
	private static void init(){
		if(connectionManager == null){
			connectionManager = new ConnectionManager();
		}
	}
	
	public static ArrayList<Destinatario> getDestinatariosAsig(String cedtecnico) throws SQLException{
		ArrayList<Destinatario>destinatarios = new ArrayList<>();
		
		String CONSULTA_ASIGNADOS = "SELECT en.id as idencuestado,en.nombre,en.apellido,en.cedula as documento,"
										  +"en.tipos_destinatarios_id,en.esjefe,asign.proyecto_id as proyecto,"
										  +"en.longitud,en.latitud,en.presicion,"
										  +"en.codigo_departamento,en.codigo_distrito,en.codigo_localidad "
				                  + "FROM encuestados en,encuestados_asignados_usuario asign," +
				                  	     "usuarios us "
				                  + "WHERE en.id=asign.encuestados_id AND us.id=asign.usuarios_id AND "
				                        + "usuario=? ";
		
		init();
		Connection conection = connectionManager.getConexion();
		PreparedStatement ps = null;
		try {
			ps = conection.prepareStatement(CONSULTA_ASIGNADOS);
			ps.setString(1,cedtecnico);
			ResultSet rs=ps.executeQuery();
			Destinatario destinatario;
			while (rs.next()) {
				destinatario = new Destinatario.DestinatarioBuilder()
											   .iddestinatario(rs.getLong("idencuestado"))
											   .nombre(new String(rs.getString("nombre").getBytes("UTF-8"), "UTF-8"))
											   .apellido(new String(rs.getString("apellido").getBytes("UTF-8")))
											   .documento(rs.getString("documento"))
											   .tipo_destinatario(rs.getInt("tipos_destinatarios_id"))
											   .esjefe(rs.getInt("esjefe"))
											   .proyecto(rs.getInt("proyecto"))
											   .longitud(rs.getString("longitud"))
											   .latitud(rs.getString("latitud"))
											   .presicion(rs.getDouble("presicion"))
											   .codigo_departamento(rs.getInt("codigo_departamento"))
											   .codigo_distrito(rs.getInt("codigo_distrito"))
											   .codigo_localidad(rs.getInt("codigo_localidad"))
											   .build();
				destinatarios.add(destinatario);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conection != null) {conection.close();}
		}
		
		return destinatarios;
	}
	
	public static ArrayList<Proyecto> getProyectos(String usuario) throws SQLException{
		ArrayList<Proyecto>proyectos = new ArrayList<Proyecto>();
		
		String CONSULTA_PROYECTOS = "SELECT pr.id as proyectoid,pr.descripcion as proyecto,inst.id as idinstitucion,"
										 + "inst.descripcion as institucion,pr.tipos_proyecto_id as tipoproyecto,"
										 + "pr.cantidad_min_imagenes,pr.permitir_alta_destinatarios,entidad_relevar "
								  + "FROM proyectos pr,proyectos_asignado_usuario asing,"
								       + "usuarios us,instituciones inst "
								  + "WHERE pr.id=asing.proyectos_id AND "
								        + "asing.usuarios_id=us.id AND "
								        + "pr.instituciones_id=inst.id AND "
								        + "usuario=? ";
		
		init();
		Connection conection = connectionManager.getConexion();
		PreparedStatement ps = null;
		try {
			ps = conection.prepareStatement(CONSULTA_PROYECTOS);
			ps.setString(1,usuario);
			ResultSet rs=ps.executeQuery();
			
			Proyecto proyecto;
			ConfigProyect configProyect;
			while (rs.next()) {
				proyecto = new Proyecto();
				proyecto.setProyectoid(rs.getString("proyectoid"));
				proyecto.setProyecto(rs.getString("proyecto"));
				proyecto.setInstitucionid(rs.getString("idinstitucion"));
				proyecto.setInstitucion(rs.getString("institucion"));
				proyecto.setMotivos(ConsultaDB.getMotivos(rs.getInt("proyectoid"),false));
				proyecto.setTipo(rs.getInt("tipoproyecto"));
				if(proyecto.getTipo() == 3){
					configProyect = new ConfigProyect(rs.getInt("cantidad_min_imagenes"), 
	  						  						  rs.getInt("permitir_alta_destinatarios"),
	  						  						  rs.getString("entidad_relevar"));
				}else{
					configProyect = new ConfigProyect(rs.getInt("cantidad_min_imagenes"), 
							  						  rs.getInt("permitir_alta_destinatarios"));
				}
				proyecto.setConfigProyect(configProyect);
				
				proyectos.add(proyecto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conection != null) {conection.close();}
		}
		return proyectos;
	}
	
	public static long getIdDestinatario(String documento,Connection conection) throws SQLException{
		String CONSULTA = "SELECT id FROM encuestados WHERE cedula=?";
		PreparedStatement ps = null;
		long result = 0;
		try {
			ps = conection.prepareStatement(CONSULTA);
			ps.setString(1,documento);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getLong("id");
			}
		} catch (SQLException e) {	
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
		}	
		return result;
	}
	
	public static int getTipoProyecto(long cod_motivo,Connection connection) throws SQLException{
		String CONSULTA = "SELECT p.tipos_proyecto_id "
				        + "FROM proyectos p,motivos m "
				        + "WHERE p.id=m.proyectos_id AND "
				              + "m.id=? ";
		PreparedStatement ps = null;
		int result = 0;
		try {
			ps = connection.prepareStatement(CONSULTA);
			ps.setLong(1, cod_motivo);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getInt("tipos_proyecto_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
		}	
		return result;
	}
	
	public static ArrayList<Captura> getImagenes() throws SQLException{
		ArrayList<Captura> arr_captura = new ArrayList<Captura>();
		String CONSULTA = "SELECT imagen,fecha_captura FROM capturas WHERE recepcion=true AND "
				                + "EXTRACT(YEAR FROM fecha_recepcion_registro)>2016 ";
		
		init();
		Connection conection = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		try {
			ps = conection.prepareStatement(CONSULTA);
			ResultSet rs = ps.executeQuery();
			Captura captura;
			while (rs.next()) {
				captura = new Captura();
				captura.setArchivo(rs.getString("imagen"));
				captura.setFecha(rs.getString("fecha_captura"));
				File file = new File("/usr/share/tomcat/moviles/capturas/"+rs.getString("imagen"));
				captura.setExiste(file.exists());
				captura.setSize(file.length());
				if(!captura.isExiste() || captura.getSize()==0){
					arr_captura.add(captura);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conection != null) {conection.close();}
		}
		return arr_captura;
	}
	
	@SuppressWarnings("resource")
	public static boolean existImagenBD(String imagen) throws SQLException{
		boolean result = false;
		String CONSULTA = "SELECT id FROM capturas WHERE imagen=?";
		String CONSULTA_1 = "SELECT id FROM capturas_eventos WHERE imagen=?";
		
		init();
		Connection conection = connectionManager.getConexion();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conection.prepareStatement(CONSULTA);
			ps.setString(1, imagen);
			rs = ps.executeQuery();
			if(rs.next()){
				result = true;
			}else{
				ps.clearParameters();
				ps = conection.prepareStatement(CONSULTA_1);
				ps.setString(1, imagen);
				rs = ps.executeQuery();
				if(rs.next()){
					result = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conection != null) {conection.close();}
		}
		return result;
	}
	
	public static Formulario getFormulario(int idformulario,boolean sw) throws SQLException{
		Formulario formulario = new Formulario();
		String CONSULTA = "SELECT id,descripcion FROM formularios WHERE id=?";
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		try {
			ps = conect.prepareStatement(CONSULTA);
			ps.setInt(1,idformulario);
			ResultSet rs=ps.executeQuery();
			
			if(rs.next()){
				formulario.setCodigo_form(rs.getInt("id"));
				formulario.setDescrip_form(rs.getString("descripcion"));
				if(sw){
					formulario.setSecciones(getSecciones(idformulario,sw));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		return formulario;
	}
	
	public static ArrayList<Seccion>getSecciones(int idformulario,boolean sw) throws SQLException{
		ArrayList<Seccion> secciones = new ArrayList<Seccion>();
		
		String SELECT = "SELECT id,descripcion,tipo,condicional,totalizable,multimedia ";
		String FROM = "FROM secciones ";
		String WHERE = "WHERE formularios_id=? AND estado=TRUE ORDER BY indice ASC ";
		
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		try {
			ps = conect.prepareStatement(SELECT+FROM+WHERE);
			ps.setInt(1,idformulario);
			ResultSet rs=ps.executeQuery();
			
			Seccion seccion;
			while (rs.next()) {
				seccion = new Seccion();
				seccion.setCodSeccion(rs.getString("id"));
				seccion.setSeccion(rs.getString("descripcion"));
				if(sw){
					seccion.setPreguntas(ConsultaDB.getPreguntas(rs.getInt("id")));
				}
				seccion.setTipo(rs.getInt("tipo"));
				seccion.setCondicionada(rs.getInt("condicional"));
				seccion.setTotalizable(rs.getInt("totalizable"));
				seccion.setSiguiente_seccion(ConsultaDB.getSeccionCondicion(rs.getInt("id")));
				if(rs.getInt("tipo")==4){
					seccion.setMultimedia(rs.getString("multimedia"));
				}
				secciones.add(seccion);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
		return secciones;
	}	
}
