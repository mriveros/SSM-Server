package py.gov.stp.ws.movil.Model;

public class Registro {
	
	private String cedusuario;
	private String firebasetoken;
	private int codigoUsuario;
	
	
	public Registro(){}
	
	public Registro(String cedusuario, String firebasetoken) {
		super();
		this.cedusuario = cedusuario;
		this.firebasetoken = firebasetoken;
	}

	public String getCedusuario() {
		return cedusuario;
	}

	public String getFirebasetoken() {
		return firebasetoken;
	}

	public int getCodigoUsuario() {
		return codigoUsuario;
	}

	public void setCodigoUsuario(int codigoUsuario) {
		this.codigoUsuario = codigoUsuario;
	}

	public void setCedusuario(String cedusuario) {
		this.cedusuario = cedusuario;
	}

	public void setFirebasetoken(String firebasetoken) {
		this.firebasetoken = firebasetoken;
	}
}
