package it.tredi.ecm.dao.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import it.tredi.ecm.dao.enumlist.ComunicazioneAmbitoEnum;
import it.tredi.ecm.dao.enumlist.ComunicazioneTipologiaEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="comunicazione")
@Getter
@Setter
//@NamedEntityGraphs({
//
//	@NamedEntityGraph(name="graph.comunicazione.forRicerca",
//			attributeNodes = {@NamedAttributeNode("id"),
//					@NamedAttributeNode(value="destinatari", subgraph="accountFull")},
//			subgraphs = {@NamedSubgraph(name="accountFull", attributeNodes={
//					@NamedAttributeNode("id"),
//					@NamedAttributeNode("nome"),
//					@NamedAttributeNode("cognome"),
//					@NamedAttributeNode("email")
//			})
//			}
//	)
//
//})
public class Comunicazione extends BaseEntityDefaultId{

	public Comunicazione(){}
	public Comunicazione(Account mittente) {
		this.mittente = mittente;
	}

	@OneToOne
	private Account mittente;
	@ManyToMany(cascade= CascadeType.REMOVE, fetch=FetchType.EAGER)
	@JoinTable(name="comunicazione_destinatari",
				joinColumns={@JoinColumn(name="comunicazione_id")},
				inverseJoinColumns={@JoinColumn(name="account_id")}
	)
	private Set<Account> destinatari;
	private Boolean inviatoAllaSegreteria;
	@Column(name="data_creazione")
	private LocalDateTime dataCreazione;
	@Column(name="data_ultima_modifica")
	private LocalDateTime dataUltimaModifica;
	@Enumerated(EnumType.STRING)
	private ComunicazioneAmbitoEnum ambito;
	@Enumerated(EnumType.STRING)
	private ComunicazioneTipologiaEnum tipologia;
	private String oggetto;
	@Column(columnDefinition="text")
	private String messaggio;
	private boolean chiusa = false;
	@OneToMany(mappedBy="comunicazione")
	private Set<ComunicazioneResponse> risposte = new HashSet<ComunicazioneResponse>();

	private String codiceEventoLink;

	//set di id che devono ancora leggere la comunicazione
	@Column(name="utenti_che_devono_leggere")
    @ElementCollection(targetClass=Long.class)
	private Set<Long> utentiCheDevonoLeggere = new HashSet<Long>();

	//allegato
	@OneToOne
	private File allegatoComunicazione;

	@ManyToOne
	private Account fakeAccountComunicazioni;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Comunicazione entitapiatta = (Comunicazione) o;
		return Objects.equals(id, entitapiatta.id);
	}
}
