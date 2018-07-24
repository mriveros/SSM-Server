package py.gov.stp.ws.movil.Model;

public class Encuestados {
	
	private long idencuestado;
	private String nombre;
	private String apellido;
	private String documento;
	private String tipo;
	private int tipo_destinatario_id;
	private String proyecto;
	private String longitud;
	private String latitud;
	private double presicion;
	private long distrito;
	private int departamento;
	private int esjefe;
	private int codDpto;
	private int codDist;
	private int codlocalidad;
	
	private int totalVisitas;
	private String descrip_departamento;
	private String descrip_distrito;
	private String descrip_localidad;
	
	public Encuestados() {
		super();
	}

	public long getIdencuestado() {
		return idencuestado;
	}

	public void setIdencuestado(long idencuestado) {
		this.idencuestado = idencuestado;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getProyecto() {
		return proyecto;
	}

	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}

	public String getLongitud() {
		return longitud;
	}

	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}

	public String getLatitud() {
		return latitud;
	}

	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}

	public long getDistrito() {
		return distrito;
	}

	public void setDistrito(long distrito) {
		this.distrito = distrito;
	}

	public double getPresicion() {
		return presicion;
	}

	public void setPresicion(double presicion) {
		this.presicion = presicion;
	}

	public int getDepartamento() {
		return departamento;
	}

	public void setDepartamento(int departamento) {
		this.departamento = departamento;
	}
	
	public int getEsjefe() {
		return esjefe;
	}

	public void setEsjefe(int esjefe) {
		this.esjefe = esjefe;
	}

	public int getCodDpto() {
		return codDpto;
	}

	public void setCodDpto(int codDpto) {
		this.codDpto = codDpto;
	}

	public int getCodDist() {
		return codDist;
	}

	public void setCodDist(int codDist) {
		this.codDist = codDist;
	}

	public int getCodlocalidad() {
		return codlocalidad;
	}

	public void setCodlocalidad(int codlocalidad) {
		this.codlocalidad = codlocalidad;
	}
	
	public int getTotalVisitas() {
		return totalVisitas;
	}

	public void setTotalVisitas(int totalVisitas) {
		this.totalVisitas = totalVisitas;
	}

	public String getDescrip_departamento() {
		return descrip_departamento;
	}

	public void setDescrip_departamento(String descrip_departamento) {
		this.descrip_departamento = descrip_departamento;
	}

	public String getDescrip_distrito() {
		return descrip_distrito;
	}

	public void setDescrip_distrito(String descrip_distrito) {
		this.descrip_distrito = descrip_distrito;
	}

	public String getDescrip_localidad() {
		return descrip_localidad;
	}

	public void setDescrip_localidad(String descrip_localidad) {
		this.descrip_localidad = descrip_localidad;
	}

	public int getTipo_destinatario_id() {
		return tipo_destinatario_id;
	}

	public void setTipo_destinatario_id(int tipo_destinatario_id) {
		this.tipo_destinatario_id = tipo_destinatario_id;
	}

	@Override
	public String toString() {
		return "Encuestados [nombre=" + nombre + ", apellido=" + apellido
				+ ", documento=" + documento + ", tipo=" + tipo + ", proyecto="
				+ proyecto + "]";
	}
	
	public static enum TipoEncuestados{
		PERSONA(0,1,"persona"),
        ENTIDAD(1,2,"entidad"),
        COMUNIDAD(2,3,"comunidad"),
        DISTRITO(3,4,"distrito"),
        ENTIDAD_2(4,5,"entidad_no_institucion");
        
        public final int codigoMovil;
        public final int codigoDB;
        public final String descripcion;
        
        TipoEncuestados(int codigoMovil,int codigoDB,String descripcion) {
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
        
		public static TipoEncuestados findByCodigoMovil(int codigoMovil){
            for(TipoEncuestados tipo : TipoEncuestados.values()){
                if( tipo.codigoMovil == codigoMovil){
                    return tipo;
                }
            }
            return null;
        }
		
		public static TipoEncuestados findByCodigoDB(int codigoDB){
            for(TipoEncuestados tipo : TipoEncuestados.values()){
                if( tipo.codigoDB == codigoDB){
                    return tipo;
                }
            }
            return null;
        }
	}   
}
