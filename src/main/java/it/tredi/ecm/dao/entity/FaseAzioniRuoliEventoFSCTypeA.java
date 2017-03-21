package it.tredi.ecm.dao.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.javers.core.metamodel.annotation.DiffIgnore;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.tredi.ecm.dao.enumlist.FaseDiLavoroFSCEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class FaseAzioniRuoliEventoFSCTypeA extends BaseEntity {
	@Enumerated(EnumType.STRING)
	private FaseDiLavoroFSCEnum faseDiLavoro;

	@OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="fase_id")
	private List<AzioneRuoliEventoFSC> azioniRuoli = new ArrayList<AzioneRuoliEventoFSC>();

	@DiffIgnore
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "evento_id")
	private EventoFSC evento;

	public FaseAzioniRuoliEventoFSCTypeA(){};

	public FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum fase){
		this.faseDiLavoro = fase;
	}
}
