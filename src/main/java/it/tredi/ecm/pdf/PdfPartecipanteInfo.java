package it.tredi.ecm.pdf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.Disciplina;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PdfPartecipanteInfo {
	private String nome = null;
	private String cognome = null;
	private String codiceFiscale = null;
	private String reclutato = null;
	private String sponsor = null;
	private String tipologiaPartecipante = null;
	private String numeroCrediti = null;
	private String dataCreditiAcquisiti = null;
	private Set<String> professioni = new HashSet<String>();

	private Map<String,Set<String>> professioni_discipline = new HashMap<String,Set<String>>();
}
