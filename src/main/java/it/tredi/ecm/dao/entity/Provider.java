package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import org.apache.tomcat.jni.Local;
import org.hibernate.annotations.Where;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.TypeName;

import com.fasterxml.jackson.annotation.JsonView;

import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.RagioneSocialeEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import lombok.Getter;
import lombok.Setter;

@TypeName("Provider")
@Entity
@Getter
@Setter
@NamedEntityGraphs({

		// @NamedEntityGraph(name="graph.provider.files",
		// attributeNodes = {@NamedAttributeNode("id"),
		// @NamedAttributeNode("denominazioneLegale"),
		// @NamedAttributeNode(value="files", subgraph="minimalFileInfo")},
		// subgraphs = @NamedSubgraph(name="minimalFileInfo", attributeNodes={
		// @NamedAttributeNode("id"),
		// @NamedAttributeNode("nomeFile"),
		// @NamedAttributeNode("tipo")
		// }))
		// ,
		@NamedEntityGraph(name = "graph.provider.minimal", attributeNodes = { @NamedAttributeNode("id"),
				@NamedAttributeNode("denominazioneLegale"), })

})
public class Provider extends BaseEntity {

	@SequenceGenerator(name = "provider_sequence", sequenceName = "provider_sequence", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "provider_sequence")
	protected Long id;

	public Long getId() {
		return id;
	}

	/* ACCOUNTS DEL PROVIDER */
	@DiffIgnore
	@OneToMany(mappedBy = "provider", cascade = { CascadeType.REMOVE })
	Set<Account> accounts;

	/* INFO PROVIDER FORNITE IN FASE DI REGISTRAZIONE */
	@JsonView(EventoListDataTableModel.View.class)
	private String denominazioneLegale;
	@Enumerated(EnumType.STRING)
	private TipoOrganizzatore tipoOrganizzatore;
	private String gruppo;
	private String partitaIva;
	private String codiceFiscale;
	private String emailStruttura;

	//for saving logo of provider
	@OneToOne
	private File providerFile;

	// boolean che serve ai provider di tipo A per decidere se possono pagare con
	// myPay
	// (null si guarda la tipologia del provider, default: B sì, A no)
	private Boolean myPay;

	public Long getCodiceIdentificativoUnivoco() {
		return this.getId();
	}

	private boolean hasPartitaIVA = false;

	/*
	 * PERSONE REGISTRATE DAL PROVIDER alcune in fase di registrazione, altre in
	 * fase di accreditamento
	 */
	@OneToMany(mappedBy = "provider")
	@Where(clause = "dirty = 'false'")
	private Set<Persona> persone = new HashSet<Persona>();

	/* SEDI DEL PROVIDER FORNITE IN FASE DI ACCREDITAMENTO */
	@OneToMany(mappedBy = "provider")
	@Where(clause = "dirty = 'false'")
	private Set<Sede> sedi = new HashSet<Sede>();

	/* INFO PROVIDER FORNITE IN FASE DI ACCREDITAMENTO */
	@Enumerated(EnumType.STRING)
	private RagioneSocialeEnum ragioneSociale;
	private String naturaOrganizzazione;
	@Column(name = "no_profit")
	private boolean noProfit = false;

	/* IL GRUPPO VIENE DESIGNATO IN FUNZIONE DEL TIPO DI ORGANIZZATORE */
	public void setTipoOrganizzatore(TipoOrganizzatore tipoOrganizzatore) {
		if (tipoOrganizzatore != null) {
			this.tipoOrganizzatore = tipoOrganizzatore;
			this.gruppo = tipoOrganizzatore.getGruppo();
		} else {
			this.tipoOrganizzatore = null;
			this.gruppo = "";
		}
	}

	@Enumerated(EnumType.STRING)
	private ProviderStatoEnum status;

	// @ManyToMany(fetch=FetchType.LAZY)
	// @JoinTable(name="provider_files",
	// joinColumns={@JoinColumn(name="provider_id")},
	// inverseJoinColumns={@JoinColumn(name="files_id")}
	// )
	// Set<File> files = new HashSet<File>();
	//
	// public void addFile(File file){
	// Iterator<File> it = this.getFiles().iterator();
	// while(it.hasNext()){
	// if(it.next().getTipo() == file.getTipo())
	// it.remove();
	// }
	// this.getFiles().add(file);
	// }

	@Column(name = "can_insert_accreditamento_standard")
	private boolean canInsertAccreditamentoStandard;
	@Column(name = "data_insert_accreditamento_standard") // termine per invio domanda standard dopo attivazione da
															// parte della segreteria (90 gg dall'abilitazione)
	private LocalDate dataScadenzaInsertAccreditamentoStandard;

	@Column(name = "can_insert_accreditamento_provvisorio")
	private boolean canInsertAccreditamentoProvvisorio;
	@Column(name = "data_rinnovo_inster_accreditamento_provvisorio") // data dalla quale è possibile ripresentare una
																		// nuova domanda provvisoria (almeno 6mesi da
																		// diniego standard)
	private LocalDate dataRinnovoInsertAccreditamentoProvvisorio;

	@Column(name = "can_insert_piano_formativo")
	private boolean canInsertPianoFormativo;
	@Column(name = "data_insert_piano_formativo")
	private LocalDate dataScadenzaInsertPianoFormativo;

	@Column(name = "can_insert_evento")
	private boolean canInsertEvento;

	@Column(name = "can_insert_relazione_annuale")
	private boolean canInsertRelazioneAnnuale = true;
	@Column(name = "data_insert_relazione_annuale")
	private LocalDate dataScadenzaInsertRelazioneAnnuale;

	@Column(name = "codice_cogeaps")
	private String codiceCogeaps;

	@Column(name = "inviato_accreditamento_standard")
	private Boolean inviatoAccreditamentoStandard;

	/** UTILS **/
	public void addPersona(Persona persona) {
		this.persone.add(persona);
		persona.setProvider(this);
	}

	public void addSede(Sede sede) {
		this.sedi.add(sede);
		sede.setProvider(this);
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale.toUpperCase();
	}

	public Persona getPersonaByRuolo(Ruolo ruolo) {
		for (Persona p : persone)
			if (p.getRuolo() == ruolo)
				return p;
		return null;
	}

	public Set<Persona> getComponentiComitatoScientifico() {
		Set<Persona> componentiComitato = new HashSet<Persona>();
		persone.forEach(p -> {
			if (p.isComponenteComitatoScientifico())
				componentiComitato.add(p);
		});
		return componentiComitato;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Provider entitapiatta = (Provider) o;
		return Objects.equals(id, entitapiatta.id);
	}

	public boolean isGruppoA() {
		if (tipoOrganizzatore != null && tipoOrganizzatore.getGruppo().equalsIgnoreCase("A"))
			return true;
		return false;
	}

	public boolean isGruppoB() {
		if (tipoOrganizzatore != null && tipoOrganizzatore.getGruppo().equalsIgnoreCase("B"))
			return true;
		return false;
	}

	public Sede getSedeLegale() {
		for (Sede s : sedi)
			if (s.isSedeLegale())
				return s;
		return null;
	}

	public Persona getCoordinatoreComitatoScientifico() {
		for (Persona p : persone)
			if (p.isCoordinatoreComitatoScientifico())
				return p;
		return null;
	}

	public Persona getLegaleRappresentante() {
		for (Persona p : persone)
			if (p.isLegaleRappresentante())
				return p;
		return null;
	}

	public Persona getDelegatoLegaleRappresentante() {
		for (Persona p : persone)
			if (p.isDelegatoLegaleRappresentante())
				return p;
		return null;
	}

	public boolean canInsertAccreditamentoStandard() {
		// se flag disattivato - non permetto l'inserimento
		if (!canInsertAccreditamentoStandard)
			return false;

		// se flag attivato, controllo la data
		if (dataScadenzaInsertAccreditamentoStandard != null
				&& LocalDate.now().isBefore(dataScadenzaInsertAccreditamentoStandard))
			return true;

		return false;
	}

	public boolean canInsertAccreditamentoProvvisorio() {
		// se flag disattivato - non permetto l'inserimento
		if (!canInsertAccreditamentoProvvisorio)
			return false;

		// se flag attivato, controllo la data
		if (dataRinnovoInsertAccreditamentoProvvisorio != null
				&& LocalDate.now().isAfter(dataRinnovoInsertAccreditamentoProvvisorio))
			return true;

		return false;
	}

	public boolean canInsertPianoFormativo() {
		// se flag disattivato - non permetto l'inserimento
		if (!canInsertPianoFormativo)
			return false;

		// se flag attivato, controllo la data
		if (dataScadenzaInsertPianoFormativo != null && ( LocalDate.now().isBefore(dataScadenzaInsertPianoFormativo) || LocalDate.now().isEqual(dataScadenzaInsertPianoFormativo)) )
			return true;

		return false;
	}

	public boolean canInsertRelazioneAnnuale() {
		// se flag disattivato - non permetto l'inserimento
		if (!canInsertRelazioneAnnuale)
			return false;

		// se flag attivato, controllo la data
		if (dataScadenzaInsertRelazioneAnnuale != null
				&& (!LocalDate.now().isAfter(dataScadenzaInsertRelazioneAnnuale)))
			return true;

		return false;
	}

	public boolean canInsertEvento() {
		// se flag disattivato - non permetto l'inserimento
		if (!canInsertEvento)
			return false;

		// se flag attivato, controllo la stato del provider
		if (status != ProviderStatoEnum.CANCELLATO && status != ProviderStatoEnum.SOSPESO && status != ProviderStatoEnum.CESSATO)
			return true;

		return false;
	}

	public boolean isBloccato() {
		if (status == ProviderStatoEnum.CANCELLATO || status == ProviderStatoEnum.SOSPESO || status == ProviderStatoEnum.CESSATO)
			return true;
		return false;
	}

	public boolean isAttivo() {
		if (status == ProviderStatoEnum.ACCREDITATO_STANDARD|| status == ProviderStatoEnum.ACCREDITATO_PROVVISORIAMENTE)
			return true;
		return false;
	}

}
