package it.tredi.ecm.dao.entity;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class DatiEconomici {
	@Embedded
	private FatturatoTriennio fatturatoComlpessivo;
	@Embedded
	private FatturatoTriennio fatturatoFormazione;
	private int numeroDipendentiFormazioneIndeterminato;
	private int numeroDipendentiFormazioneAltro;
}
