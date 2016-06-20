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
	private Double fatturatoComplessivoValoreUno = 0.0;
	private int fatturatoComplessivoAnnoDue= 0;
	private Double fatturatoComplessivoValoreDue= 0.0;
	private int fatturatoComplessivoAnnoTre= 0;
	private Double fatturatoComplessivoValoreTre= 0.0;
	
	private int fatturatoFormazioneAnnoUno= 0;
	private Double fatturatoFormazioneValoreUno= 0.0;
	private int fatturatoFormazioneAnnoDue= 0;
	private Double fatturatoFormazioneValoreDue= 0.0;
	private int fatturatoFormazioneAnnoTre= 0;
	private Double fatturatoFormazioneValoreTre= 0.0;
	
	public boolean isEmpty(){
		if(fatturatoComplessivoValoreUno == 0.0 || 
			fatturatoComplessivoValoreDue == 0.0 ||
			fatturatoComplessivoValoreTre == 0.0 ||
			fatturatoFormazioneValoreUno == 0.0 ||
			fatturatoFormazioneValoreDue == 0.0 ||
			fatturatoFormazioneValoreTre == 0.0)
			return true;
		return false;
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
