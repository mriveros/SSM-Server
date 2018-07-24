package py.gov.stp.ws.movil.Model;

import java.util.ArrayList;

public class Proyecto {
	private String proyectoid;
	private String proyecto;
	private String institucion;
	private String institucionid;
	private String cedtecnico;
	private String nomtecnico;
	private int tipo;
	private ArrayList<Motivos>motivos;
	private int totalUsuarios;
	private int totalRelevamientos;
	private int totalBeneficiarios;
	private ConfigProyect configProyect;
	
	public Proyecto() {
		super();
	}

	public String getProyectoid() {
		return proyectoid;
	}


	public void setProyectoid(String proyectoid) {
		this.proyectoid = proyectoid;
	}


	public String getProyecto() {
		return proyecto;
	}


	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}


	public String getInstitucion() {
		return institucion;
	}


	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}


	public String getInstitucionid() {
		return institucionid;
	}


	public void setInstitucionid(String institucionid) {
		this.institucionid = institucionid;
	}


	public ArrayList<Motivos> getMotivos() {
		return motivos;
	}

	public void setMotivos(ArrayList<Motivos> motivos) {
		this.motivos = motivos;
	}

	public String getCedtecnico() {
		return cedtecnico;
	}

	public void setCedtecnico(String cedtecnico) {
		this.cedtecnico = cedtecnico;
	}

	public String getNomtecnico() {
		return nomtecnico;
	}

	public void setNomtecnico(String nomtecnico) {
		this.nomtecnico = nomtecnico;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public int getTotalUsuarios() {
		return totalUsuarios;
	}

	public void setTotalUsuarios(int totalUsuarios) {
		this.totalUsuarios = totalUsuarios;
	}

	public int getTotalRelevamientos() {
		return totalRelevamientos;
	}

	public void setTotalRelevamientos(int totalRelevamientos) {
		this.totalRelevamientos = totalRelevamientos;
	}

	public int getTotalBeneficiarios() {
		return totalBeneficiarios;
	}

	public void setTotalBeneficiarios(int totalBeneficiarios) {
		this.totalBeneficiarios = totalBeneficiarios;
	}

	public ConfigProyect getConfigProyect() {
		return configProyect;
	}

	public void setConfigProyect(ConfigProyect configProyect) {
		this.configProyect = configProyect;
	}
}
