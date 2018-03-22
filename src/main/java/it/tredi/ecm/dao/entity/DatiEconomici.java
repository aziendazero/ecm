package it.tredi.ecm.dao.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Embeddable;

import org.javers.core.metamodel.annotation.TypeName;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@TypeName("DatiEconomici")
@Getter
@Setter
public class DatiEconomici {
	private int fatturatoComplessivoAnnoUno = 0;
	@NumberFormat(pattern = "0.00")
	private BigDecimal fatturatoComplessivoValoreUno;
	private int fatturatoComplessivoAnnoDue= 0;
	@NumberFormat(pattern = "0.00")
	private BigDecimal fatturatoComplessivoValoreDue;
	private int fatturatoComplessivoAnnoTre= 0;
	@NumberFormat(pattern = "0.00")
	private BigDecimal fatturatoComplessivoValoreTre;

	private int fatturatoFormazioneAnnoUno= 0;
	@NumberFormat(pattern = "0.00")
	private BigDecimal fatturatoFormazioneValoreUno;
	private int fatturatoFormazioneAnnoDue= 0;
	@NumberFormat(pattern = "0.00")
	private BigDecimal fatturatoFormazioneValoreDue;
	private int fatturatoFormazioneAnnoTre= 0;
	@NumberFormat(pattern = "0.00")
	private BigDecimal fatturatoFormazioneValoreTre;

	public boolean hasFatturatoComplessivo(){
		if(fatturatoComplessivoValoreUno == null &&
			fatturatoComplessivoValoreDue == null &&
			fatturatoComplessivoValoreTre == null)
			return false;
		return true;
	}

	public boolean hasFatturatoFormazione() {
		if(fatturatoFormazioneValoreUno == null &&
			fatturatoFormazioneValoreDue == null &&
			fatturatoFormazioneValoreTre == null)
			return false;
		return true;
	}

	public DatiEconomici() {
		init(LocalDate.now().getYear());
	}

	public void init(int currentYear){
		fatturatoComplessivoAnnoUno = currentYear - 1;
		fatturatoComplessivoAnnoDue = currentYear - 2;
		fatturatoComplessivoAnnoTre = currentYear - 3;

		fatturatoFormazioneAnnoUno = currentYear - 1;
		fatturatoFormazioneAnnoDue = currentYear - 2;
		fatturatoFormazioneAnnoTre = currentYear - 3;
	}
}
