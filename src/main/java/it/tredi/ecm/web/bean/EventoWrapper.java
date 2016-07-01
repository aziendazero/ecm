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
public class EventoWrapper extends Wrapper {
	private Evento evento;
	private Long providerId;
	private Long accreditamentoId;
	
	public EventoWrapper(){
	}
}
