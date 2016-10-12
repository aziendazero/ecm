package it.tredi.ecm.dao.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="comunicazione_response")
@Getter
@Setter
public class ComunicazioneResponse extends BaseEntity{

	public ComunicazioneResponse() {}
	public ComunicazioneResponse(Account mittente, Comunicazione comunicazione) {
		this.mittente = mittente;
		this.comunicazione = comunicazione;
	}

	@ManyToOne @JoinColumn(name = "comunicazione_id")
	private Comunicazione comunicazione;
	@OneToOne
	private Account mittente;
	@Column(columnDefinition="text")
	private String messaggio;
	@Column(name="data_risposta")
	private LocalDateTime dataRisposta;

	@OneToOne
	private File allegatoRisposta;
}