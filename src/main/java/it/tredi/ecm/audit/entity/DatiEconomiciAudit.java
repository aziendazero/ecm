package it.tredi.ecm.audit.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

import javax.persistence.Embeddable;

import org.javers.core.metamodel.annotation.TypeName;
import org.javers.core.metamodel.annotation.ValueObject;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;
import org.springframework.format.number.NumberStyleFormatter;

import it.tredi.ecm.dao.entity.DatiEconomici;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@TypeName("DatiEconomiciAudit")
@Getter
@Setter
public class DatiEconomiciAudit {
	private String fatturatoComplessivoAnnoUno = "";
	private String fatturatoComplessivoAnnoDue = "";
	private String fatturatoComplessivoAnnoTre = "";

	private String fatturatoFormazioneAnnoUno = "";
	private String fatturatoFormazioneAnnoDue = "";
	private String fatturatoFormazioneAnnoTre = "";


	public DatiEconomiciAudit(DatiEconomici datiEconomici) {
		NumberStyleFormatter intFormatter = new NumberStyleFormatter("0");
		NumberStyleFormatter valutaFormatter = new NumberStyleFormatter("0.00");

		//addCellSubTable(intFormatter.print(riepRu.getNumeroPartecipanti(), Locale.getDefault()), subTable);
		//addCellSubTable(floatFormatter.print(riepRu.getTempoDedicato(), Locale.getDefault()), subTable);

		this.fatturatoComplessivoAnnoUno = intFormatter.print(datiEconomici.getFatturatoComplessivoAnnoUno(), Locale.getDefault()) + " - " + (datiEconomici.getFatturatoComplessivoValoreUno() == null ? "" : valutaFormatter.print(datiEconomici.getFatturatoComplessivoValoreUno(), Locale.getDefault()) );
		this.fatturatoComplessivoAnnoDue = intFormatter.print(datiEconomici.getFatturatoComplessivoAnnoDue(), Locale.getDefault()) + " - " + (datiEconomici.getFatturatoComplessivoValoreDue() == null ? "" : valutaFormatter.print(datiEconomici.getFatturatoComplessivoValoreDue(), Locale.getDefault()) );
		this.fatturatoComplessivoAnnoTre = intFormatter.print(datiEconomici.getFatturatoComplessivoAnnoTre(), Locale.getDefault()) + " - " + (datiEconomici.getFatturatoComplessivoValoreTre() == null ? "" : valutaFormatter.print(datiEconomici.getFatturatoComplessivoValoreTre(), Locale.getDefault()) );

		this.fatturatoFormazioneAnnoUno = intFormatter.print(datiEconomici.getFatturatoFormazioneAnnoUno(), Locale.getDefault()) + " - " + (datiEconomici.getFatturatoFormazioneValoreUno() == null ? "" : valutaFormatter.print(datiEconomici.getFatturatoFormazioneValoreUno(), Locale.getDefault()) );
		this.fatturatoFormazioneAnnoDue = intFormatter.print(datiEconomici.getFatturatoFormazioneAnnoDue(), Locale.getDefault()) + " - " + (datiEconomici.getFatturatoFormazioneValoreDue() == null ? "" : valutaFormatter.print(datiEconomici.getFatturatoFormazioneValoreDue(), Locale.getDefault()) );
		this.fatturatoFormazioneAnnoTre = intFormatter.print(datiEconomici.getFatturatoFormazioneAnnoTre(), Locale.getDefault()) + " - " + (datiEconomici.getFatturatoFormazioneValoreTre() == null ? "" : valutaFormatter.print(datiEconomici.getFatturatoFormazioneValoreTre(), Locale.getDefault()) );
	}
}
