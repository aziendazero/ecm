package it.tredi.ecm.dao.entity;

import javax.persistence.Column;
import javax.persistence.OneToOne;

public class ValutazioneCommissione {
	@OneToOne 
	private Seduta seduta;
	@OneToOne
	private Accreditamento accreditamento;
	@Column(name="valutazione_commissione")
	private String valutazioneCommissione;
}
