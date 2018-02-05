package it.tredi.ecm.dao.entity;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class StoricoDataModel {
	
	@JsonView(StoricoDataTableModel.View.class)
	private String fullName = "";
	
	@JsonView(StoricoDataTableModel.View.class)
	private String accreditamentoStatoValutazione = "";
	
	@JsonView(StoricoDataTableModel.View.class)
	private String dataValutazione = "";
	
//	@JsonView(StoricoDataTableModel.View.class)
//	private String selezionaLink = "";
}
