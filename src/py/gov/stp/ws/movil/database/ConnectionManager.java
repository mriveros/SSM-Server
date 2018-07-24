package py.gov.stp.ws.movil.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	
	public ConnectionManager() {}
	
	public Connection getConexion(){
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		Connection connection = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://URI/dbname?useUnicode=true&characterEncoding=UTF-8","","");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}
}
