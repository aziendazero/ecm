package it.tredi.ecm.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;

import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;
import lombok.Data;

@Data
public class EventoListDataModel {

	@JsonView(EventoListDataTableModel.View.class)
	private String links = "";
	
	@JsonView(EventoListDataTableModel.View.class)
	private String codiceIdent = "";
	
	@JsonView(EventoListDataTableModel.View.class)
	private String denominazioneLeg = "";
	
	@JsonView(EventoListDataTableModel.View.class)
	private int edizione;
	
	@JsonView(EventoListDataTableModel.View.class)
	private String tipo = "";
	
	@JsonView(EventoListDataTableModel.View.class)
	private String titolo = "";
	
	@JsonView(EventoListDataTableModel.View.class)
	private String sede = "";
	
	@JsonView(EventoListDataTableModel.View.class)
	private String dataInizio = "";
	
	@JsonView(EventoListDataTableModel.View.class)
	private String dataFine = "";
	
	@JsonView(EventoListDataTableModel.View.class)
	private String stato = "";
	
	@JsonView(EventoListDataTableModel.View.class)
	private String numPart;

	@JsonView(EventoListDataTableModel.View.class)
	private String durata;
	
	@JsonView(EventoListDataTableModel.View.class)
	private String dataScadenzaRediconto = "";
	
	@JsonView(EventoListDataTableModel.View.class)
	private String creditiConfermati;
	
	@JsonView(EventoListDataTableModel.View.class)
	private Integer versione;
	
	@JsonView(EventoListDataTableModel.View.class)
	private String crediti;
	
}
