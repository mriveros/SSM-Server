package py.gov.stp.ws.movil.Model;

import java.util.ArrayList;

public class EventosView {
	
	private Usuario usuario;
	private ArrayList<Evento>eventos;
	
	public EventosView(Usuario usuario, ArrayList<Evento> eventos) {
		super();
		this.usuario = usuario;
		this.eventos = eventos;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public ArrayList<Evento> getEventos() {
		return eventos;
	}

	@Override
	public String toString() {
		return "EventosView [usuario=" + usuario + ", eventos=" + eventos + "]";
	}
}
