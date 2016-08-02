package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.RagioneSocialeEnum;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@NamedEntityGraphs({

	@NamedEntityGraph(name="graph.provider.files",
			attributeNodes = {@NamedAttributeNode("id"), @NamedAttributeNode("denominazioneLegale"),
					@NamedAttributeNode(value="files", subgraph="minimalFileInfo")},
			subgraphs = @NamedSubgraph(name="minimalFileInfo", attributeNodes={
					@NamedAttributeNode("id"),
					@NamedAttributeNode("nomeFile"),
					@NamedAttributeNode("tipo")
			}))
	,
	@NamedEntityGraph(name="graph.provider.minimal",
	attributeNodes = {@NamedAttributeNode("id"), @NamedAttributeNode("denominazioneLegale"),
			})

})
public class Provider extends BaseEntity{
	/*	ACCOUNT LEGATO AL PROFILO PROVIDER	*/
	@OneToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	private Account account;

	/*	INFO PROVIDER FORNITE IN FASE DI REGISTRAZIONE	*/
	private String denominazioneLegale;
	@Enumerated(EnumType.STRING)
	private TipoOrganizzatore tipoOrganizzatore;
	private String gruppo;
	private String partitaIva;
	private String codiceFiscale;

	private boolean hasPartitaIVA = false;

	/*	PERSONE REGISTRATE DAL PROVIDER
	 * 	alcune in fase di registrazione, altre in fase di accreditamento */
	@OneToMany(mappedBy="provider")
	private Set<Persona> persone = new HashSet<Persona>();

	/*	SEDI DEL PROVIDER FORNITE IN FASE DI ACCREDITAMENTO	*/
	@OneToOne
	private Sede sedeLegale;
	@OneToOne
	private Sede sedeOperativa;

	/*	INFO PROVIDER FORNITE IN FASE DI ACCREDITAMENTO	*/
	@Enumerated(EnumType.STRING)
	private RagioneSocialeEnum ragioneSociale;
	private String naturaOrganizzazione;
	@Column(name ="no_profit")
	private boolean noProfit = false;

	/*	IL GRUPPO VIENE DESIGNATO IN FUNZIONE DEL TIPO DI ORGANIZZATORE	*/
	public void setTipoOrganizzatore(TipoOrganizzatore tipoOrganizzatore){
		if(tipoOrganizzatore != null){
			this.tipoOrganizzatore = tipoOrganizzatore;
			this.gruppo = tipoOrganizzatore.getGruppo();
		}else{
			this.tipoOrganizzatore = null;
			this.gruppo = "";
		}
	}

	@Enumerated(EnumType.STRING)
	private ProviderStatoEnum status;

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="provider_files",
	joinColumns={@JoinColumn(name="provider_id")},
	inverseJoinColumns={@JoinColumn(name="files_id")}
			)
	Set<File> files = new HashSet<File>();

	public void addFile(File file){
		this.getFiles().add(file);
	}

	@Column(name ="can_insert_accreditamento_standard")
	private boolean canInsertAccreditamentoStandard;
	@Column(name ="can_insert_piano_formativo")
	private boolean canInsertPianoFormativo;
	@Column(name ="can_insert_evento")
	private boolean canInsertEvento;

	@OneToMany(mappedBy = "provider")
	private Set<Pagamento> pagamenti = new HashSet<Pagamento>();

	/** UTILS **/
	public void addPersona(Persona persona){
		this.persone.add(persona);
		persona.setProvider(this);
	}

	public boolean isSedeCoincide(){
		if(sedeLegale != null && sedeOperativa != null){
			return sedeLegale.getId().equals(sedeOperativa.getId());
		}else{
			return false;
		}
	}

	public void setCodiceFiscale(String codiceFiscale){
		this.codiceFiscale = codiceFiscale.toUpperCase();
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
}
