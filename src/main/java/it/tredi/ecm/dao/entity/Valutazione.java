package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
	@OneToOne
	private Account account;
	@OneToOne
	private Accreditamento accreditamento;
	@OneToMany
	private Set<FieldValutazione> valutazioni = new HashSet<FieldValutazione>();
	@Enumerated(EnumType.STRING)
	@Column(name="tipo_valutazione")
	private ValutazioneTipoEnum tipoValutazione;
	private String valutazioneComplessiva;
	@Column(name="data_valutazione")
	private LocalDate dataValutazione;
}
