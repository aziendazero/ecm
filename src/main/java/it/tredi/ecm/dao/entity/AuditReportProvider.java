package it.tredi.ecm.dao.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="audit_report_provider")
public class AuditReportProvider extends BaseEntityDefaultId {

	@ManyToOne
	private Provider provider;
	@Column(name="data_inizio")
	private LocalDateTime dataInizio;
	@Column(name="data_fine")
	private LocalDateTime dataFine;

	@Enumerated(EnumType.STRING)
	private ProviderStatoEnum status;
	//Privata, Pubblica
	private String naturaOrganizzazione;
	@Enumerated(EnumType.STRING)
	private TipoOrganizzatore tipoOrganizzatore;

	private String sedeLegaleProvincia;
	private String sedeLegaleComune;
	private String sedeLegaleIndirizzo;
	private String sedeLegaleCap;
	private String sedeLegaleTelefono;
	private String sedeLegaleAltroTelefono;
	private String sedeLegaleFax;
	private String sedeLegaleEmail;

	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<ProceduraFormativa> procedureFormative = new HashSet<ProceduraFormativa>();

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE } )
	@JoinTable(name = "audit_report_provider_discipline",
				joinColumns = @JoinColumn(name = "audit_report_provider_id"),
				inverseJoinColumns = @JoinColumn(name = "disciplina_id")
	)
	private Set<Disciplina> discipline = new HashSet<Disciplina>();

	public AuditReportProvider() {
	}

	public AuditReportProvider(Provider provider, DatiAccreditamento datiAccreditamento) {
		this.provider = provider;
		this.status = provider.getStatus();
		this.naturaOrganizzazione = provider.getNaturaOrganizzazione();
		this.tipoOrganizzatore = provider.getTipoOrganizzatore();
		if(provider.getSedeLegale() != null) {
			this.sedeLegaleAltroTelefono = provider.getSedeLegale().getAltroTelefono();
			this.sedeLegaleCap = provider.getSedeLegale().getCap();
			this.sedeLegaleComune = provider.getSedeLegale().getComune();
			this.sedeLegaleEmail = provider.getSedeLegale().getEmail();
			this.sedeLegaleFax = provider.getSedeLegale().getFax();
			this.sedeLegaleIndirizzo = provider.getSedeLegale().getIndirizzo();
			this.sedeLegaleProvincia = provider.getSedeLegale().getProvincia();
			this.sedeLegaleTelefono = provider.getSedeLegale().getTelefono();
		}
		if(datiAccreditamento != null) {
			if(datiAccreditamento.getProcedureFormative() != null)
				this.procedureFormative.addAll(datiAccreditamento.getProcedureFormative());
			if(datiAccreditamento.getDiscipline() != null)
				this.discipline.addAll(datiAccreditamento.getDiscipline());
		}
	}
}
