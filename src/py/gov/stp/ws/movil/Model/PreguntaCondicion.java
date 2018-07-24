package py.gov.stp.ws.movil.Model;

public class PreguntaCondicion {
	
	private long idpreguntacondicionada;
	private int condicion;
	private String valor;
	
	
	public PreguntaCondicion() {
		super();
	}

	public long getIdpreguntacondicionada() {
		return idpreguntacondicionada;
	}

	public void setIdpreguntacondicionada(long idpreguntacondicionada) {
		this.idpreguntacondicionada = idpreguntacondicionada;
	}

	public int getCondicion() {
		return condicion;
	}

	public void setCondicion(int condicion) {
		this.condicion = condicion;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public static enum Condiciones{
		IGUAL(1),
		MAYOR(2),
		MENOR(3),
		MAYOR_IGUAL(4),
		MENOR_IGUAL(5);
		
		private final int codigo;
	        
		Condiciones(int codigo) {
			this.codigo = codigo;
		}
	}
}
