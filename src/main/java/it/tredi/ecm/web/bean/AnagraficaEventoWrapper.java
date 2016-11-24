package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.AnagraficaEvento;
import it.tredi.ecm.dao.entity.AnagraficaFullEvento;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnagraficaEventoWrapper {
	private Long providerId;

	private AnagraficaEvento anagraficaEvento;

	private boolean full = false;
}
