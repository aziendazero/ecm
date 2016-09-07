package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Seduta extends BaseEntity{
	@JsonView(JsonViewModel.Seduta.class)
	@JsonProperty("start")
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	@Column(name="data")
	private LocalDate data;
	@JsonView(JsonViewModel.Seduta.class)
	@JsonProperty("title")
	@DateTimeFormat (pattern = "HH:mm")
	@Column(name="ora")
	private LocalTime ora;
	@JsonIgnore
	@OneToMany(mappedBy="seduta")
	private Set<ValutazioneCommissione> valutazioniCommissione = new HashSet<ValutazioneCommissione>();
	private boolean locked;

	public Seduta(){}
	public Seduta(LocalDate data) {
		this.data = data;
	}
}
