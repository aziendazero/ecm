package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerbaleValutazioneSulCampo {
	private LocalDate giorno;
	private LocalTime ora;
	@OneToOne
	private Accreditamento accreditamento;
	@OneToOne
	private Valutazione valutazione;
	//TODO come settare i campi aggiuntivi??? (pianoformativo, idoneità della sede, scheda qualità percepita...)
	@OneToOne
	private File verbaleFirmato;
}
