package it.tredi.ecm.web.bean;

import java.util.Set;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Seduta;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SedutaWrapper {
	private Seduta seduta;
	private boolean canEdit;
	private String motivazioneDaInserire;
	private Long idAccreditamentoDaInserire;
	private Set<Accreditamento> domandeSelezionabili;
	private Set<Seduta> seduteSelezionabili;
	private Seduta sedutaTarget;
}
