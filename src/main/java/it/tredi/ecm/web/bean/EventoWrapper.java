package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.Evento;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoWrapper extends Wrapper2 {
	private Evento evento;
	private Long providerId;
	private Long accreditamentoId;
	private Long pianoFormativoId;
	
	public EventoWrapper(){
	}
}
