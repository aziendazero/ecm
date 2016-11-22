package it.tredi.ecm.web.bean;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RicercaAccreditamentoWrapper {
	private Long campoIdProvider;
	private String denominazioneLegale;
	private Set<TipoOrganizzatore> tipologieSelezionate;
	private Set<ProceduraFormativa> procedureSelezionate;
	private Set<AccreditamentoTipoEnum> accreditamentoTipoSelezionati;
	private Set<AccreditamentoStatoEnum> accreditamentoStatoSelezionati;
	private Set<String> provnceSelezionate;
	
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataScadenzaAccreditamentoStart;
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataScadenzaAccreditamentoEnd;
	
	//campo non di ricerca ma il solito id che mettiamo in hidden per gestire il form
	private Long providerId;
}
