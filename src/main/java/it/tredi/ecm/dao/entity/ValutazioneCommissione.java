package it.tredi.ecm.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ValutazioneCommissione extends BaseEntity{
	@OneToOne
	private Seduta seduta;
	@OneToOne
	private Accreditamento accreditamento;

	private String oggettoDiscussione;

	@Column(name="valutazione_commissione")
	private String valutazioneCommissione;
}
