package py.gov.stp.ws.movil.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteBD {
	
	private static ConnectionManager connectionManager;
	
	private static void init(){
		if(connectionManager == null){
			connectionManager = new ConnectionManager();
		}
	}
	
	public static void deleteRelevamiento(long idrelevamiento) throws SQLException{
		String QUERY = "DELETE FROM adjuntos WHERE relevamientos_id=?;";
		
		init();
		Connection conect = connectionManager.getConexion();
		PreparedStatement ps = null;
		
		try {
			ps = conect.prepareStatement(QUERY);
			ps.setLong(1, idrelevamiento);
			ps.executeUpdate();
			ps.clearParameters();
			
			QUERY = "DELETE FROM capturas WHERE relevamientos_id=?;";
			ps = conect.prepareStatement(QUERY);
			ps.setLong(1, idrelevamiento);
			ps.executeUpdate();
			ps.clearParameters();
			
			QUERY = "DELETE FROM lista_personas WHERE relevamientos_id=?;";
			ps = conect.prepareStatement(QUERY);
			ps.setLong(1, idrelevamiento);
			ps.executeUpdate();
			ps.clearParameters();
			
			QUERY = "DELETE FROM detalles_relevamientos WHERE relevamientos_id=?;";
			ps = conect.prepareStatement(QUERY);
			ps.setLong(1, idrelevamiento);
			ps.executeUpdate();
			ps.clearParameters();
			
			QUERY = "DELETE FROM relevamientos WHERE id=?;";
			ps = conect.prepareStatement(QUERY);
			ps.setLong(1, idrelevamiento);
			ps.executeUpdate();
			ps.clearParameters();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (ps != null) {ps.close();}
			if (conect != null) {conect.close();}
		}
	}
}
