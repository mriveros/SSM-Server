package py.gov.stp.ws.movil.Model;

public class Nivel {
	
	private int codigo_nivel;
	private String descripcion;
	
	private Nivel(nivelBuilder builder){
		this.codigo_nivel = builder.codigo_nivel;
		this.descripcion = builder.descripcion;
	}
	
	public int getCodigo_nivel() {
		return codigo_nivel;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public static class nivelBuilder{
		private int codigo_nivel;
		private String descripcion;
		
		public nivelBuilder() {
			super();
		}
		
		public nivelBuilder codigo_nivel(int codigo_nivel){
			this.codigo_nivel = codigo_nivel;
			return this;
		}
		
		public nivelBuilder descripcion(String descripcion){
			this.descripcion = descripcion;
			return this;
		}
		
		public Nivel build(){
			return new Nivel(this);
		}
	}
}
