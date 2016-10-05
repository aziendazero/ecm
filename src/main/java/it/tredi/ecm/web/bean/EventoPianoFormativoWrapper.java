package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.EventoPianoFormativo;
import it.tredi.ecm.dao.entity.File;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoPianoFormativoWrapper extends Wrapper {
	private EventoPianoFormativo evento;
	private Long providerId;
	private Long accreditamentoId;
	private Long pianoFormativoId;

	private String eventoFrom;

	private File reportPartecipanti;

	public EventoPianoFormativoWrapper(){
	}
}
