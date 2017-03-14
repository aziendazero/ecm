package it.tredi.ecm.dao.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.tredi.ecm.dao.enumlist.MetodoDiLavoroEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiFSCEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AzioneRuoliEventoFSC extends BaseEntityDefaultId {
	@Column(columnDefinition = "text")
	private String azione;

	@Column(name="obiettivo_formativo")
	@Enumerated(EnumType.STRING)
	private ObiettiviFormativiFSCEnum obiettivoFormativo;

	@Column(name="risultati_attesi", columnDefinition = "text")
	private String risultatiAttesi;

	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<MetodoDiLavoroEnum> metodiDiLavoro = new HashSet<MetodoDiLavoroEnum>();

//	@ElementCollection
//	private Set<RuoloFSCEnum> ruoli = new HashSet<RuoloFSCEnum>();

	@ElementCollection
	//private Set<RuoloOreFSC> ruoli = new HashSet<RuoloOreFSC>();
	private List<RuoloOreFSC> ruoli = new ArrayList<RuoloOreFSC>();

	private BigDecimal tempoDedicato;
}
