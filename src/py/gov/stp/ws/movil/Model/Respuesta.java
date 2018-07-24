package py.gov.stp.ws.movil.Model;

public class Respuesta {
	
	private int respuesta = 1;
	private String hash;

	public Respuesta() {
		super();
	}

	public int getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(int respuesta) {
		this.respuesta = respuesta;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@Override
	public String toString() {
		return "Respuesta [respuesta=" + respuesta + ", hash=" + hash + "]";
	}
}
