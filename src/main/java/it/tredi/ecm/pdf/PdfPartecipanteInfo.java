package it.tredi.ecm.pdf;

import java.util.HashSet;
import java.util.Set;

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
}
