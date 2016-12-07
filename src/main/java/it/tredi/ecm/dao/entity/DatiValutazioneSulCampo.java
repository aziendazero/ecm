package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class DatiValutazioneSulCampo {

	@Column(columnDefinition="text")
	private String osservazioniTeamValutazione;

	@Column(columnDefinition="text")
	private String osservazioniDelProvider;

	@OneToMany(cascade = CascadeType.ALL)
	private Set<FieldValutazioneAccreditamento> valutazioniSulCampo = new HashSet<FieldValutazioneAccreditamento>();
}
