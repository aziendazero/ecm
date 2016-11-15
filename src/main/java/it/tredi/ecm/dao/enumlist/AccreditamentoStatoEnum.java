package it.tredi.ecm.dao.enumlist;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

@Getter
public enum AccreditamentoStatoEnum {
	BOZZA (1, "Bozza", ""), //domanda non ufficiale
	VALUTAZIONE_SEGRETERIA_ASSEGNAMENTO (2, "Valutazione da parte della segreteria", "valutazioneAssegnamento"),//domanda inviata alla segreteria che deve valutarla e assegnare il gruppo CRECM
	VALUTAZIONE_CRECM (3, "Valutazione Referee", "valutazioneReferee"),//[SOLO PROVVISORIA] - domanda assegnata al gruppo CRECM, che deve valutarla
	ASSEGNAMENTO (4, "Assegnamento nuovi referee", "assegnamento"),//[SOLO PROVVISORIA] - //domanda restituita alla segreteria che deve assegnare un nuovo gruppo CRECM (perchè 2/3 del primo gruppo non hanno valutato)
	INS_ODG (5, "Inserimento nell'Ordine del giorno", "odg"),//domanda deve essere inserita in ODG per valutazione della Commissione ECM
	VALUTAZIONE_COMMISSIONE (6, "Valutazione Commissione ECM", "valutazioneCommissione"),//domanda in discussione da parte della Commissione ECM, al termine la segreteria riporta l'esito
	DINIEGO (7, "Respinto", "diniego"),//al secondo giro...la domanda può essere definitivamente respinta
	ACCREDITATO (8, "Accreditato", "accreditato"),//domanda approvata...90gg di tempo per pagare
	RICHIESTA_INTEGRAZIONE (9, "Richiesta integrazione", "richiestaIntegrazione"),//segreteria deve selezionare gli id dei campi da modificare
	INTEGRAZIONE (10, "Integrazione", "integrazione"),//provider deve modificare i campi da richiesta integrazione
	RICHIESTA_PREAVVISO_RIGETTO (11, "Richiesta Preavviso di Rigetto", "richiestaPreavvisoRigetto"),//segreteria deve selezionare gli id dei campi da modificare
	PREAVVISO_RIGETTO (12, "Preavviso di Rigetto", "preavvisoRigetto"),//provider deve modificare i campi da richiesta preavviso di rigetto
	VALUTAZIONE_SEGRETERIA (13, "Valutazione Segreteria", "valutazione"),//domanda rimandata in valutazione alla segretria in seguito alle integrazioni effettuate (assegnamento CRECM mantenuto in automatico)

	/*
	 *  Domanda assegnata ad un team di valutazione per valutare l'accreditamento STANDARD
	 *  Task eseguito dalla segreteria (sostituisce 3 al primo giro nella domanda standard)
	 *  La segreteria compila la maschera dlel verbale e la salva. Il task termina quando la segreteria esplicitamente VALIDA il verbale. (salvataggio in bozza nel frattempo)
	 * */
	VALUTAZIONE_SUL_CAMPO (14, "Valutazione sul campo", "valutazioneCampo"),//[SOLO STANDARD]
	VALUTAZIONE_TEAM_LEADER(15, "Valutazione del Team Leader", "valutazioneLeader"),//[SOLO STANDARD] - domanda asseganata al Team Leader che deve compilare la griglia (sostituisce 3 dal secondo giro nella domanda standard)
	
	CANCELLATO(16, "Cancellato", ""),//domanda cancellata - prevista dal vecchio sistema...
	SOSPESO(17, "Sospeso", "");//domanda sospesa - prevista dal vecchio sistema...


	private int id;
	private String nome;
	private String gruppo;

	private AccreditamentoStatoEnum(int id, String nome, String gruppo){
		this.id = id;
		this.nome = nome;
		this.gruppo = gruppo;
	}

	public static Set<AccreditamentoStatoEnum> getAllStatoByGruppo(String gruppo) {
		Set<AccreditamentoStatoEnum> allStati = new HashSet<AccreditamentoStatoEnum>();
		for (AccreditamentoStatoEnum a : AccreditamentoStatoEnum.values()) {
			if (a.getGruppo().equals(gruppo)) {
				allStati.add(a);
			}
		}
		return allStati;
	}
}