package it.tredi.ecm.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;

import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
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

	@Enumerated(EnumType.STRING)
	private AccreditamentoStatoEnum stato;
}
