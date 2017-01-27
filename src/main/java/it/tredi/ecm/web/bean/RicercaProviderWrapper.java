package it.tredi.ecm.web.bean;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RicercaProviderWrapper {
	/* Info relative al provider*/
	private Long campoIdProvider;
	private String denominazioneLegale;
	private Set<TipoOrganizzatore> TipoOrganizzatoreSelezionati;

	/* Info relative alla sede legale provider */
	private Set<String> provinciaSelezionate;

	/*Info relative allo stato del provider */
	private Set<ProviderStatoEnum> statoProvider;

	/* Info relative all'accreditamento del provider */
	private Set<ProceduraFormativa> proceduraFormativaSelezionate;
	private Set<AccreditamentoTipoEnum> accreditamentoTipoSelezionati;
	private Set<AccreditamentoStatoEnum> accreditamentoStatoSelezionati;
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataFineAccreditamentoStart;
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataFineAccreditamentoEnd;

	/* Info relative al pagamento del provider */
	private Boolean pagato;
}
