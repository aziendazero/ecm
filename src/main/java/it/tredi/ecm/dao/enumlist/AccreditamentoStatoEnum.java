package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum AccreditamentoStatoEnum {
	BOZZA (1, "Bozza"), //domanda non ufficiale
	VALUTAZIONE_SEGRETERIA_ASSEGNAMENTO (2, "Valutazione da parte della segreteria"),//domanda inviata alla segreteria che deve valutarla e assegnare il gruppo CRECM
	VALUTAZIONE_CRECM (3, "Valutazione CRECM"),//domanda assegnata al gruppo CRECM, che deve valutarla
	ASSEGNAMENTO (4, "Assegnamento nuovo gruppo CRECM"),//domanda restituita alla segreteria che deve assegnare un nuovo gruppo CRECM (perchè 2/3 del primo gruppo non hanno valutato)
	INS_ODG (5, "Inserimento nell'Ordine del giorno"),//domanda deve essere inserita in ODG per valutazione della Commissione ECM
	VALUTAZIONE_COMMISSIONE (6, "Valutazione Commissione ECM"),//domanda in discussione da parte della Commissione ECM, al termine la segreteria riporta l'esito
	DINIEGO (7, "Respinto"),//al secondo giro...la domanda può essere definitivamente respinta
	ACCREDITATO (8, "Accreditato"),//domanda approvata...90gg di tempo per pagare
	RICHIESTA_INTEGRAZIONE (9, "Richiesta integrazione"),//segreteria deve selezionare gli id dei campi da modificare
	INTEGRAZIONE (10, "Integrazione"),//provider deve modificare i campi da richiesta integrazione
	VALUTAZIONE_SEGRETERIA (11, "Valutazione Segreteria");//domanda rimandata in valutazione alla segretria in seguito alle integrazioni effettuate (assegnamento CRECM mantenuto in automatico)
	
	private int id;
	private String nome;
	
	private AccreditamentoStatoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}