package it.tredi.ecm.web.bean;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImpostazioniProviderWrapper {
	private Boolean canInsertPianoFormativo;
	private Boolean canInsertEventi;
	private Boolean canInsertDomandaStandard;
	private Boolean canInsertRelazioneAnnuale;

	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataScadenzaInsertPianoFormativo;
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataScadenzaInsertDomandaStandard;
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataScadenzaInsertRelazioneAnnuale;

	private ProviderStatoEnum stato;

	private boolean submitError = false;
}
