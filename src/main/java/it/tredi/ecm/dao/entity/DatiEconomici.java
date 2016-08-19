package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class DatiEconomici {
	private int fatturatoComplessivoAnnoUno = 0;
	private Double fatturatoComplessivoValoreUno;
	private int fatturatoComplessivoAnnoDue= 0;
	private Double fatturatoComplessivoValoreDue;
	private int fatturatoComplessivoAnnoTre= 0;
	private Double fatturatoComplessivoValoreTre;

	private int fatturatoFormazioneAnnoUno= 0;
	private Double fatturatoFormazioneValoreUno;
	private int fatturatoFormazioneAnnoDue= 0;
	private Double fatturatoFormazioneValoreDue;
	private int fatturatoFormazioneAnnoTre= 0;
	private Double fatturatoFormazioneValoreTre;

	public boolean hasFatturatoComplessivo(){
		if(fatturatoComplessivoValoreUno != null &&
			fatturatoComplessivoValoreDue != null &&
			fatturatoComplessivoValoreTre != null)
			return false;
		return true;
	}

	public boolean hasFatturatoFormazione() {
		if(fatturatoFormazioneValoreUno != null &&
			fatturatoFormazioneValoreDue != null &&
			fatturatoFormazioneValoreTre != null)
			return false;
		return true;
	}

	public DatiEconomici() {
		init(LocalDate.now().getYear());
	}

	public void init(int currentYear){
		fatturatoComplessivoAnnoUno = currentYear;
		fatturatoComplessivoAnnoDue = currentYear - 1;
		fatturatoComplessivoAnnoTre = currentYear - 2;

		fatturatoFormazioneAnnoUno = currentYear;
		fatturatoFormazioneAnnoDue = currentYear - 1;
		fatturatoFormazioneAnnoTre = currentYear - 2;
	}
}
