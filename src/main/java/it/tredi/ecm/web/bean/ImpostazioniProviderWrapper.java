package it.tredi.ecm.web.bean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.MotivazioneDecadenzaEnum;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImpostazioniProviderWrapper {
	private Boolean canInsertPianoFormativo;
	private Boolean canInsertEventi;
	private Boolean canInsertDomandaStandard;
	private Boolean canInsertDomandaProvvisoria;
	private Boolean canInsertRelazioneAnnuale;
	private Boolean canMyPay;

	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataScadenzaInsertPianoFormativo;
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataScadenzaInsertDomandaStandard;
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataRinnovoInsertDomandaProvvisoria;
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataScadenzaInsertRelazioneAnnuale;

	private ProviderStatoEnum stato;

	private MotivazioneDecadenzaEnum motivazioneDecadenza;

	private File allegatoDecadenza;

	private boolean submitError = false;
	private boolean motivazioneError = false;

	private Long providerId;
	private Integer numeroDecreto;
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataDecreto;
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataComunicazioneDecadenza;

}
