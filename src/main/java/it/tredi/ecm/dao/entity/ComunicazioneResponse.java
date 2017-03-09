package it.tredi.ecm.dao.entity;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="comunicazione_response")
@Getter
@Setter
public class ComunicazioneResponse extends BaseEntity
//implements Comparator<ComunicazioneResponse>
{

	public ComunicazioneResponse() {}
	public ComunicazioneResponse(Account mittente, Comunicazione comunicazione) {
		this.mittente = mittente;
		this.comunicazione = comunicazione;
	}

	@ManyToOne @JoinColumn(name = "comunicazione_id")
	private Comunicazione comunicazione;
	@OneToOne
	private Account mittente;
	//aggiornamento: se il mittente della risposta è un PROVIDER || REFEREE || COMMISSIONE || OSSERVATORE la lista dei destinatari è nulla e il flag inviatoAllaSegreteria è true
	//				 se il mittente della risposta è SEGRETERIA, la lista dei destinatari, recuperiamo la comunicazione padre:
	//						1) se il mittente è il PROVIDER (perchè REFEREE || COMMISSIONE || OSSERVATORE non possono aprire comunicazioni), nella lista dei destinatari metto il mittente ottenuto
	//						2) se il mittente è SEGRETERIA, nella lista dei destinatari metto la lista dei destinatari del padre
	@ManyToMany(cascade= CascadeType.REMOVE, fetch=FetchType.EAGER)
	@JoinTable(name="comunicazione_response_destinatari",
			joinColumns={@JoinColumn(name="comunicazione_response_id")},
			inverseJoinColumns={@JoinColumn(name="account_id")}
	)
	private Set<Account> destinatari;
	private Boolean inviatoAllaSegreteria;
	@Column(columnDefinition="text")
	private String messaggio;
	@Column(name="data_risposta")
	private LocalDateTime dataRisposta;

	@OneToOne
	private File allegatoRisposta;

	@ManyToOne
	private Account fakeAccountComunicazioni;


//	@Override
//	public int compare(ComunicazioneResponse response1, ComunicazioneResponse response2) {
//		if(response1.getDataRisposta().isAfter(response2.getDataRisposta()))
//			return 1;
//		else if(response1.getDataRisposta().isBefore(response2.getDataRisposta()))
//			return -1;
//		else
//			return 0;
//	}

}