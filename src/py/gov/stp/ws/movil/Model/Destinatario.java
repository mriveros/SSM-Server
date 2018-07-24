package py.gov.stp.ws.movil.Model;

public class Destinatario {
	
	private final long iddestinatario;
	private final String documento;
	private final String nombre;
	private final String apellido;
	private final int tipo_destinatario;
	private final int esjefe;
	private final int proyecto;
	private final String longitud;
	private final String latitud;
	private final double presicion;
	private final int codigo_departamento;
	private final int codigo_distrito;
	private final int codigo_localidad;
	private final String departamento;
	private final String distrito;
	private final String localidad;
	
	private Destinatario(DestinatarioBuilder builder) {
		this.iddestinatario = builder.iddestinatario;
		this.documento = builder.documento;
		this.nombre = builder.nombre;
		this.apellido = builder.apellido;
		this.tipo_destinatario = builder.tipo_destinatario;
		this.esjefe = builder.esjefe;
		this.proyecto = builder.proyecto;
		this.longitud = builder.longitud;
		this.latitud = builder.latitud;
		this.presicion = builder.presicion;
		this.codigo_departamento = builder.codigo_departamento;
		this.codigo_distrito = builder.codigo_distrito;
		this.codigo_localidad = builder.codigo_localidad;
		this.departamento = builder.departamento;
		this.distrito = builder.distrito;
		this.localidad = builder.localidad;
	}
	
	public long getIddestinatario() {
		return iddestinatario;
	}

	public String getDocumento() {
		return documento;
	}

	public String getNombre() {
		return nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public int getTipo_destinatario() {
		return tipo_destinatario;
	}

	public int getEsjefe() {
		return esjefe;
	}

	public int getProyecto() {
		return proyecto;
	}

	public String getLongitud() {
		return longitud;
	}

	public String getLatitud() {
		return latitud;
	}

	public double getPresicion() {
		return presicion;
	}

	public int getCodigo_departamento() {
		return codigo_departamento;
	}

	public int getCodigo_distrito() {
		return codigo_distrito;
	}

	public int getCodigo_localidad() {
		return codigo_localidad;
	}

	public String getDepartamento() {
		return departamento;
	}

	public String getDistrito() {
		return distrito;
	}

	public String getLocalidad() {
		return localidad;
	}

	public static class DestinatarioBuilder{
		private long iddestinatario;
		private String documento;
		private String nombre;
		private String apellido;
		private int tipo_destinatario;
		private int esjefe;
		private int proyecto;
		private String longitud;
		private String latitud;
		private double presicion;
		private int codigo_departamento;
		private int codigo_distrito;
		private int codigo_localidad;
		private String departamento;
		private String distrito;
		private String localidad;
		
		public DestinatarioBuilder() {}
		
		public DestinatarioBuilder iddestinatario(long iddestinatario){
			this.iddestinatario = iddestinatario;
			return this;
		}
		
		public DestinatarioBuilder documento(String documento){
			this.documento = documento;
			return this;
		}
		
		public DestinatarioBuilder nombre(String nombre){
			this.nombre = nombre;
			return this;
		}
		
		public DestinatarioBuilder apellido(String apellido){
			this.apellido = apellido;
			return this;
		}
		
		public DestinatarioBuilder tipo_destinatario(int tipo_destinatario){
			this.tipo_destinatario = tipo_destinatario;
			return this;
		}
		
		public DestinatarioBuilder proyecto(int proyecto){
			this.proyecto = proyecto;
			return this;
		}
		
		public DestinatarioBuilder esjefe(int esjefe){
			this.esjefe = esjefe;
			return this;
		}
		
		public DestinatarioBuilder longitud(String longitud){
			this.longitud = longitud;
			return this;
		}
		
		public DestinatarioBuilder latitud(String latitud){
			this.latitud = latitud;
			return this;
		}
		
		public DestinatarioBuilder presicion(Double presicion){
			this.presicion = presicion;
			return this;
		}
		
		public DestinatarioBuilder codigo_departamento(int codigo_departamento){
			this.codigo_departamento = codigo_departamento;
			return this;
		}
		
		public DestinatarioBuilder codigo_distrito(int codigo_distrito){
			this.codigo_distrito = codigo_distrito;
			return this;
		}
		
		public DestinatarioBuilder codigo_localidad(int codigo_localidad){
			this.codigo_localidad = codigo_localidad;
			return this;
		}
		
		public DestinatarioBuilder departamento(String departamento){
			this.departamento = departamento;
			return this;
		}
		
		public DestinatarioBuilder distrito(String distrito){
			this.distrito = distrito;
			return this;
		}
		
		public DestinatarioBuilder localidad(String localidad){
			this.localidad = localidad;
			return this;
		}
		
		public Destinatario build(){
            return new Destinatario(this);
		}
	}
	
	public static enum TipoDestintario{
		PERSONA(0,1,"persona"),
        ENTIDAD(1,2,"entidad"),
        COMUNIDAD(2,3,"comunidad");
        
        public final int codigoMovil;
        public final int codigoDB;
        public final String descripcion;
        
        TipoDestintario(int codigoMovil,int codigoDB,String descripcion) {
            this.codigoMovil = codigoMovil;
            this.codigoDB = codigoDB;
            this.descripcion = descripcion;
        }

		public int getCodigoMovil() {
			return codigoMovil;
		}

		public int getCodigoDB() {
			return codigoDB;
		}

		public String getDescripcion() {
			return descripcion;
		}
        
		public static TipoDestintario findByCodigoMovil(int codigoMovil){
            for(TipoDestintario tipo : TipoDestintario.values()){
                if( tipo.codigoMovil == codigoMovil){
                    return tipo;
                }
            }
            return null;
        }
		
		public static TipoDestintario findByCodigoDB(int codigoDB){
            for(TipoDestintario tipo : TipoDestintario.values()){
                if( tipo.codigoDB == codigoDB){
                    return tipo;
                }
            }
            return null;
        }
	}
}