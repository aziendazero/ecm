package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Where;

import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.RagioneSocialeEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@NamedEntityGraphs({

//	@NamedEntityGraph(name="graph.provider.files",
//			attributeNodes = {@NamedAttributeNode("id"), @NamedAttributeNode("denominazioneLegale"),
//					@NamedAttributeNode(value="files", subgraph="minimalFileInfo")},
//			subgraphs = @NamedSubgraph(name="minimalFileInfo", attributeNodes={
//					@NamedAttributeNode("id"),
//					@NamedAttributeNode("nomeFile"),
//					@NamedAttributeNode("tipo")
//			}))
//	,
	@NamedEntityGraph(name="graph.provider.minimal",
	attributeNodes = {@NamedAttributeNode("id"), @NamedAttributeNode("denominazioneLegale"),
			})

})
public class Provider extends BaseEntity{
	/*	ACCOUNTS DEL PROVIDER	*/
	@OneToMany(mappedBy = "provider", cascade = { CascadeType.REMOVE })
	Set<Account> accounts;

	/*	INFO PROVIDER FORNITE IN FASE DI REGISTRAZIONE	*/
	private String denominazioneLegale;
	@Enumerated(EnumType.STRING)
	private TipoOrganizzatore tipoOrganizzatore;
	private String gruppo;
	private String partitaIva;
	private String codiceFiscale;
	private String emailStruttura;

	public Long getCodiceIdentificativoUnivoco(){
		return this.getId();
	}

	private boolean hasPartitaIVA = false;

	/*	PERSONE REGISTRATE DAL PROVIDER
	 * 	alcune in fase di registrazione, altre in fase di accreditamento */
	@OneToMany(mappedBy="provider")
	@Where(clause = "dirty = 'false'")
	private Set<Persona> persone = new HashSet<Persona>();

	/*	SEDI DEL PROVIDER FORNITE IN FASE DI ACCREDITAMENTO	*/
	@OneToMany(mappedBy="provider")
	@Where(clause = "dirty = 'false'")
	private Set<Sede> sedi = new HashSet<Sede>();

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

//	@ManyToMany(fetch=FetchType.LAZY)
//	@JoinTable(name="provider_files",
//	joinColumns={@JoinColumn(name="provider_id")},
//	inverseJoinColumns={@JoinColumn(name="files_id")}
//			)
//	Set<File> files = new HashSet<File>();
//
//	public void addFile(File file){
//		Iterator<File> it = this.getFiles().iterator();
//		while(it.hasNext()){
//			if(it.next().getTipo() == file.getTipo())
//				it.remove();
//		}
//		this.getFiles().add(file);
//	}

	@Column(name ="can_insert_accreditamento_standard")
	private boolean canInsertAccreditamentoStandard;
	@Column(name ="can_insert_piano_formativo")
	private boolean canInsertPianoFormativo;
	@Column(name ="can_insert_evento")
	private boolean canInsertEvento;
	@Column(name ="can_insert_relazione_annuale")
	private Boolean canInsertRelazioneAnnuale;

//	@OneToMany(mappedBy = "provider")
//	private Set<QuotaAnnuale> pagamenti = new HashSet<QuotaAnnuale>();

	@Column(name ="codice_cogeaps")
	private String codiceCogeaps;

	/** UTILS **/
	public void addPersona(Persona persona){
		this.persone.add(persona);
		persona.setProvider(this);
	}

	public void addSede(Sede sede) {
		this.sedi.add(sede);
		sede.setProvider(this);
	}

	public void setCodiceFiscale(String codiceFiscale){
		this.codiceFiscale = codiceFiscale.toUpperCase();
	}

	public Persona getPersonaByRuolo(Ruolo ruolo){
		for(Persona p : persone)
			if(p.getRuolo() == ruolo)
				return p;
		return null;
	}

	public Set<Persona> getComponentiComitatoScientifico(){
		Set<Persona> componentiComitato = new HashSet<Persona>();
		persone.forEach(p ->{
			if(p.isComponenteComitatoScientifico())
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

	public boolean isGruppoA(){
		if(tipoOrganizzatore != null && tipoOrganizzatore.getGruppo().equalsIgnoreCase("A"))
			return true;
		return false;
	}

	public boolean isGruppoB(){
		if(tipoOrganizzatore != null && tipoOrganizzatore.getGruppo().equalsIgnoreCase("B"))
			return true;
		return false;
	}

	public Sede getSedeLegale(){
		for(Sede s : sedi)
			if(s.isSedeLegale())
				return s;
		return null;
	}

	public Persona getLegaleRappresentante(){
		for(Persona p : persone)
			if(p.isLegaleRappresentante())
				return p;
		return null;
	}
}
