package it.tredi.ecm.audit.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;
import org.javers.core.metamodel.annotation.ValueObject;

import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.RagioneSocialeEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import lombok.Getter;
import lombok.Setter;

@TypeName("ProviderAudit")
@Entity
@Getter
@Setter
public class ProviderAudit {
	/*	ACCOUNTS DEL PROVIDER	*/
	@Id
	private Long id;

	/*	INFO PROVIDER FORNITE IN FASE DI REGISTRAZIONE	*/
	private String denominazioneLegale;
	private TipoOrganizzatore tipoOrganizzatore;
	private String gruppo;
	private String partitaIva;
	private String codiceFiscale;
	private String emailStruttura;
	private boolean hasPartitaIVA = false;

	private RagioneSocialeEnum ragioneSociale;
	private String naturaOrganizzazione;
	private boolean noProfit = false;
	//private ProviderStatoEnum status;

	private PersonaAudit legaleRappresentante;
	private PersonaAudit delegatoLegaleRappresentante;
	private PersonaAudit responsabileSegreteria;
	private PersonaAudit coordinatoreComitatoScientifico;

//	private Set<PersonaAudit> componentiComitatoScientifico = new HashSet<PersonaAudit>();
//	private List<PersonaAudit> componentiComitatoScientificoList = new ArrayList<PersonaAudit>();

	private Map<Long, PersonaAudit> componentiComitatoScientificoMap = new HashMap<Long, PersonaAudit>();

	private SedeAudit sedeLegale;
	private Map<Long, SedeAudit> sediOperative = new HashMap<Long, SedeAudit>();

//	private boolean canInsertAccreditamentoStandard;
//	private LocalDate dataScadenzaInsertAccreditamentoStandard;
//
//	private boolean canInsertAccreditamentoProvvisorio;
//	private LocalDate dataRinnovoInsertAccreditamentoProvvisorio;
//
//	private boolean canInsertPianoFormativo;
//	private LocalDate dataScadenzaInsertPianoFormativo;
//
//	private boolean canInsertEvento;
//
//	private boolean canInsertRelazioneAnnuale = true;
//	private LocalDate dataScadenzaInsertRelazioneAnnuale;
	private ProviderAttivitaAudit providerAttivitaAudit;

	private String codiceCogeaps;

	public ProviderAudit(Provider provider) {
		this.id = provider.getId();
		this.denominazioneLegale = provider.getDenominazioneLegale();
		this.tipoOrganizzatore = provider.getTipoOrganizzatore();
		this.gruppo = provider.getGruppo();
		this.partitaIva = provider.getPartitaIva();
		this.codiceFiscale = provider.getCodiceFiscale();
		this.emailStruttura = provider.getEmailStruttura();
		this.hasPartitaIVA = provider.isHasPartitaIVA();

		this.ragioneSociale = provider.getRagioneSociale();
		this.naturaOrganizzazione = provider.getNaturaOrganizzazione();
		this.noProfit = provider.isNoProfit();

//		this.status = provider.getStatus();

//		this.canInsertAccreditamentoStandard = provider.isCanInsertAccreditamentoStandard();
//		this.dataScadenzaInsertAccreditamentoStandard = provider.getDataScadenzaInsertAccreditamentoStandard();
//
//		this.canInsertAccreditamentoProvvisorio = provider.isCanInsertAccreditamentoProvvisorio();
//		this.dataRinnovoInsertAccreditamentoProvvisorio = provider.getDataRinnovoInsertAccreditamentoProvvisorio();
//
//		this.canInsertPianoFormativo = provider.isCanInsertPianoFormativo();
//		this.dataScadenzaInsertPianoFormativo = provider.getDataScadenzaInsertPianoFormativo();
//
//		this.canInsertEvento = provider.isCanInsertEvento();
//
//		this.canInsertRelazioneAnnuale = provider.isCanInsertRelazioneAnnuale();
//		this.dataScadenzaInsertRelazioneAnnuale = provider.getDataScadenzaInsertRelazioneAnnuale();

		this.providerAttivitaAudit = new ProviderAttivitaAudit(provider);

		this.codiceCogeaps = provider.getCodiceCogeaps();

		if(provider.getLegaleRappresentante() == null)
			this.legaleRappresentante = null;
		else
			this.legaleRappresentante = new PersonaAudit(provider.getLegaleRappresentante());
		if(provider.getDelegatoLegaleRappresentante() == null)
			this.delegatoLegaleRappresentante = null;
		else
			this.delegatoLegaleRappresentante = new PersonaAudit(provider.getDelegatoLegaleRappresentante());
		if(provider.getPersonaByRuolo(Ruolo.RESPONSABILE_SEGRETERIA) == null)
			this.responsabileSegreteria = null;
		else
			this.responsabileSegreteria = new PersonaAudit(provider.getPersonaByRuolo(Ruolo.RESPONSABILE_SEGRETERIA));

		for(Persona p : provider.getPersone()) {
			if(p.isDirty())
				continue;
			if (p.isCoordinatoreComitatoScientifico()) {
				this.coordinatoreComitatoScientifico = new PersonaAudit(p);
			} else if(p.isComponenteComitatoScientifico()) {
				PersonaAudit pa = new PersonaAudit(p);
//				this.componentiComitatoScientifico.add(pa);
//				if(this.componentiComitatoScientifico.contains(pa)) {
//					System.out.println("uguale");
//				}
//				this.componentiComitatoScientificoList.add(new PersonaAudit(p));
				this.componentiComitatoScientificoMap.put(pa.getId(), pa);
			}
		}

		if(provider.getSedeLegale() == null)
			this.sedeLegale = null;
		else
			this.sedeLegale = new SedeAudit(provider.getSedeLegale());
		for(Sede s : provider.getSedi()) {
			if(s.isDirty())
				continue;
			if(s.isSedeOperativa()) {
				SedeAudit sa =  new SedeAudit(s);
				this.sediOperative.put(sa.getId(), sa);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ProviderAudit entitapiatta = (ProviderAudit) o;
		return Objects.equals(id, entitapiatta.id);
	}

	@Override
	public int hashCode() {
		return id.intValue();
	}
}
