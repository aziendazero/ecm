package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;


import it.tredi.ecm.dao.enumlist.ValutazioneTipoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Valutazione extends BaseEntity{
	@Column(name="data_valutazione")
	private LocalDate dataValutazione;
	@OneToOne
	private Account account;
	@OneToOne
	private Accreditamento accreditamento;
	@Column(name="tipo_valutazione")
	@Enumerated(EnumType.STRING)
	private ValutazioneTipoEnum tipoValutazione;
	@OneToMany (cascade = CascadeType.ALL)
	private Set<FieldValutazioneAccreditamento> valutazioni = new HashSet<FieldValutazioneAccreditamento>();
	private String valutazioneComplessiva;

	//TODO mettere unic coppia id account / id accreditamento
}
