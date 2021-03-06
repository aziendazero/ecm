package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
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
@NamedEntityGraphs({

	@NamedEntityGraph(name="graph.seduta.dabloccare",
			attributeNodes = {@NamedAttributeNode("id"), @NamedAttributeNode("data"),
					@NamedAttributeNode("ora"), @NamedAttributeNode("locked"),
					@NamedAttributeNode("numeroVerbale"), @NamedAttributeNode("eseguitoTaskInsOdgAccreditamenti"),
					@NamedAttributeNode("dataoraSeduta"),
					@NamedAttributeNode(value="valutazioniCommissione", subgraph="valutazioniCommissione")},
			subgraphs = {@NamedSubgraph(name="valutazioniCommissione", attributeNodes={
					@NamedAttributeNode("id"),
					@NamedAttributeNode("valutazioneCommissione"),
					@NamedAttributeNode("stato"),
					@NamedAttributeNode("oggettoDiscussione"),
					@NamedAttributeNode(value="accreditamento", subgraph="accreditamento")
					}),
					@NamedSubgraph(name="accreditamento", attributeNodes={
							@NamedAttributeNode("id"),
							@NamedAttributeNode("stato"),
							@NamedAttributeNode("statoVariazioneDati")
							})
					}
	)
//	,
//	@NamedEntityGraph(name="graph.seduta.dabloccare",
//	attributeNodes = {@NamedAttributeNode("id"), @NamedAttributeNode("denominazioneLegale"),
//			})

})
public class Seduta extends BaseEntityDefaultId{
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
	@JsonIgnore
	private String numeroVerbale;
	//Indica che il task "Inserimento ODG" è stato eseguito su tutti gli accreditamenti
	@Column(name="eseguito_task_insodg_accreditamenti", columnDefinition="boolean default false")
	private boolean eseguitoTaskInsOdgAccreditamenti = false;

	@Column(name = "dataora_seduta")
	private LocalDateTime dataoraSeduta;

	public Seduta(){}
	public Seduta(LocalDate data) {
		this.setData(data);
	}

	public void setData(LocalDate data) {
		this.data = data;
		setDataOraByDataAndOra();
	}

	public void setOra(LocalTime ora) {
		this.ora = ora;
		setDataOraByDataAndOra();
	}

	private void setDataOraByDataAndOra() {
		if(this.data == null)
			this.dataoraSeduta = null;
		else {
			if(this.ora == null) {
				this.dataoraSeduta = LocalDateTime.of(this.data, LocalTime.MIN);
			} else {
				this.dataoraSeduta = LocalDateTime.of(this.data, this.ora);
			}
		}
	}
}
