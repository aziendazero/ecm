package it.tredi.ecm.web.bean;

import java.util.HashSet;
import java.util.Set;

import it.tredi.ecm.dao.entity.Evento;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PianoFormativoWrapper{
	private Long accreditamentoId;
	private Long providerId;
	private String pianoFormativo;
	
	private Set<Evento> listaEventi = new HashSet<Evento>();
	
	public boolean isEditabile(){
		return true;
	}
}
