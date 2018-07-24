package py.gov.stp.ws.movil.Model;

public class Usuario {
	private long idusuario;
	private String cedula;
	private String password;
	private String nombre;
	private String apellido;
	private String distrito;
	private String telParticular;
	private String telCorporativo;
	private int institucion;
	private String descripcion_institucion;
	private String email;
	private Nivel nivel;
	private int totalRelevamientos;
	private int totalBeneficiarios;
	
	private Usuario(usuarioBuilder builder) {
		this.idusuario = builder.idusuario;
		this.cedula = builder.cedula;
		this.password = builder.password;
		this.nombre = builder.nombre;
		this.apellido = builder.apellido;
		this.distrito = builder.distrito;
		this.telParticular = builder.telParticular;
		this.telCorporativo = builder.telCorporativo;
		this.institucion = builder.institucion;
		this.descripcion_institucion = builder.descripcion_institucion;
		this.email = builder.email;
		this.nivel = builder.nivel;
		this.totalBeneficiarios = builder.totalBeneficiarios;
		this.totalRelevamientos = builder.totalRelevamientos;
	}
	
	public long getIdusuario() {
		return idusuario;
	}

	public String getCedula() {
		return cedula;
	}

	public String getPassword() {
		return password;
	}

	public Nivel getNivel() {
		return nivel;
	}

	public String getNombre() {
		return nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public String getDistrito() {
		return distrito;
	}

	public String getTelParticular() {
		return telParticular;
	}

	public String getTelCorporativo() {
		return telCorporativo;
	}

	public int getInstitucion() {
		return institucion;
	}

	public String getDescripcion_institucion() {
		return descripcion_institucion;
	}

	public void setDescripcion_institucion(String descripcion_institucion) {
		this.descripcion_institucion = descripcion_institucion;
	}

	public String getEmail() {
		return email;
	}

	public int getTotalRelevamientos() {
		return totalRelevamientos;
	}

	public int getTotalBeneficiarios() {
		return totalBeneficiarios;
	}

	@Override
	public String toString() {
		return "Usuario [idusuario=" + idusuario + ", cedula=" + cedula
				+ ", password=" + password + ", nombre=" + nombre
				+ ", apellido=" + apellido + ", distrito=" + distrito
				+ ", telParticular=" + telParticular + ", telCorporativo="
				+ telCorporativo + ", institucion=" + institucion
				+ ", descripcion_institucion=" + descripcion_institucion
				+ ", email=" + email + ", nivel=" + nivel
				+ ", totalRelevamientos=" + totalRelevamientos
				+ ", totalBeneficiarios=" + totalBeneficiarios + "]";
	}

	public static class usuarioBuilder{
		private long idusuario;
		private String cedula;
		private String password;
		private String nombre;
		private String apellido;
		private String distrito;
		private String telParticular;
		private String telCorporativo;
		private int institucion;
		private String descripcion_institucion;
		private String email;
		private Nivel nivel;
		private int totalRelevamientos;
		private int totalBeneficiarios;
		
		public usuarioBuilder() {}
		
		public usuarioBuilder idusuario(long idusuario){
			this.idusuario = idusuario;
			return this;
		}
		
		public usuarioBuilder cedula(String cedula){
			this.cedula = cedula;
			return this;
		}
		
		public usuarioBuilder password(String password){
			this.password = password;
			return this;
		}
		
		public usuarioBuilder nivel(Nivel nivel){
			this.nivel = nivel;
			return this;
		}
		
		public usuarioBuilder nombre(String nombre){
			this.nombre = nombre;
			return this;
		}
		
		public usuarioBuilder apellido(String apellido){
			this.apellido = apellido;
			return this;
		}
		
		public usuarioBuilder distrito(String distrito){
			this.distrito = distrito;
			return this;
		}
		
		public usuarioBuilder telParticular(String telParticular){
			this.telParticular = telParticular;
			return this;
		}
		
		public usuarioBuilder telCorporativo(String telCorporativo){
			this.telCorporativo = telCorporativo;
			return this;
		}
		
		public usuarioBuilder institucion(int institucion){
			this.institucion = institucion;
			return this;
		}
		
		public usuarioBuilder descripcion_institucion(String descripcion_institucion){
			this.descripcion_institucion = descripcion_institucion;
			return this;
		}
		
		public usuarioBuilder email(String email){
			this.email = email;
			return this;
		}
		
		public usuarioBuilder totalBeneficiarios(int totalBeneficiarios){
			this.totalBeneficiarios = totalBeneficiarios;
			return this;
		}
		
		public usuarioBuilder totalRelevamientos(int totalRelevamientos){
			this.totalRelevamientos = totalRelevamientos;
			return this;
			
		}
		
		public Usuario build(){
            return new Usuario(this);
		}    
	}
}
