package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.Anagrafica;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnagraficaWrapper {
	private Anagrafica anagrafica;
	private Long providerId;
}
