package it.tredi.ecm.web.bean;

import java.util.HashSet;
import java.util.Set;

import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoWrapper {
	private Evento evento;
	private Long providerId;
	private Set<Disciplina> discipline = new HashSet<Disciplina>();
	private Set<ProceduraFormativa> procedureFormative = new HashSet<ProceduraFormativa>();
	
	public EventoWrapper(){
	}
	
	public Set<Professione> getProfessioniSelezionate(){
		Set<Professione> professioniSelezionate = new HashSet<Professione>();
		for(Disciplina d : discipline)
			professioniSelezionate.add(d.getProfessione());
		return professioniSelezionate;
	}
}
