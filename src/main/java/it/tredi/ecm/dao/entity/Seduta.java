package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.OneToMany;

public class Seduta {
	@Column(name="data")
	private LocalDate data;
	@Column(name="ora")
	private LocalTime ora;
	@OneToMany(mappedBy="seduta")
	private Set<ValutazioneCommissione> valutazioniCommissione = new HashSet<ValutazioneCommissione>();
	
}
