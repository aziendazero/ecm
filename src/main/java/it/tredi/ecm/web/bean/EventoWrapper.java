package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.File;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoWrapper extends Wrapper {
	private Evento evento;
	private Long providerId;
	private Long accreditamentoId;
	private Long pianoFormativoId;

	private String eventoFrom;

	private File reportPartecipanti;

	public EventoWrapper(){
	}
}
