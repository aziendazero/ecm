package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoWrapper {
	private Evento evento;
	private ProceduraFormativa proceduraFormativa;
	private Long providerId;

	//parte rendicontazione
	private File reportPartecipanti;

}
