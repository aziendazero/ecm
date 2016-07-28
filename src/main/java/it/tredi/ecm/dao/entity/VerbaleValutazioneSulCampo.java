package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.OneToOne;

public class VerbaleValutazioneSulCampo {
	private LocalDate giorno;
	private LocalTime ora;
	@OneToOne
	private Accreditamento accreditamento;
	@OneToOne
	private Valutazione valutazione;
	
}
