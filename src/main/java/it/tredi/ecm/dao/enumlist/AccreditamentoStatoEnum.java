package it.tredi.ecm.dao.enumlist;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum AccreditamentoStatoEnum {
	BOZZA (1, "Bozza", "", EsecutoreStatoEnum.PROVIDER), //domanda non ufficiale
	VALUTAZIONE_SEGRETERIA_ASSEGNAMENTO (2, "Valutazione da parte della segreteria", "valutazioneAssegnamento", EsecutoreStatoEnum.SEGRETERIA),//domanda inviata alla segreteria che deve valutarla e assegnare il gruppo CRECM
	VALUTAZIONE_CRECM (3, "Valutazione Referee", "valutazioneReferee", EsecutoreStatoEnum.SEGRETERIA),//[SOLO PROVVISORIA] - domanda assegnata al gruppo CRECM, che deve valutarla
	ASSEGNAMENTO (4, "Assegnamento nuovi referee", "assegnamento", EsecutoreStatoEnum.SEGRETERIA),//[SOLO PROVVISORIA] - //domanda restituita alla segreteria che deve assegnare un nuovo gruppo CRECM (perchè 2/3 del primo gruppo non hanno valutato)
	INS_ODG (5, "Inserimento nell'Ordine del giorno", "odg", EsecutoreStatoEnum.SEGRETERIA),//domanda deve essere inserita in ODG per valutazione della Commissione ECM
	VALUTAZIONE_COMMISSIONE (6, "Valutazione Commissione ECM", "valutazioneCommissione", EsecutoreStatoEnum.SEGRETERIA),//domanda in discussione da parte della Commissione ECM, al termine la segreteria riporta l'esito
	DINIEGO (7, "Diniego", "diniego", EsecutoreStatoEnum.SEGRETERIA),//al secondo giro...la domanda può essere definitivamente respinta
	ACCREDITATO (8, "Accreditato", "accreditato", EsecutoreStatoEnum.SEGRETERIA),//domanda approvata...90gg di tempo per pagare
	RICHIESTA_INTEGRAZIONE (9, "Richiesta integrazione", "richiestaIntegrazione", EsecutoreStatoEnum.SEGRETERIA),//segreteria deve selezionare gli id dei campi da modificare
	INTEGRAZIONE (10, "Integrazione", "integrazione", EsecutoreStatoEnum.PROVIDER),//provider deve modificare i campi da richiesta integrazione
	RICHIESTA_PREAVVISO_RIGETTO (11, "Richiesta Preavviso di Rigetto", "richiestaIntegrazione", EsecutoreStatoEnum.SEGRETERIA),//segreteria deve selezionare gli id dei campi da modificare
	PREAVVISO_RIGETTO (12, "Preavviso di Rigetto", "preavvisoRigetto", EsecutoreStatoEnum.PROVIDER),//provider deve modificare i campi da richiesta preavviso di rigetto
	VALUTAZIONE_SEGRETERIA (13, "Valutazione Segreteria", "valutazione", EsecutoreStatoEnum.SEGRETERIA),//domanda rimandata in valutazione alla segretria in seguito alle integrazioni effettuate (assegnamento CRECM mantenuto in automatico)

	/*
	 *  Domanda assegnata ad un team di valutazione per valutare l'accreditamento STANDARD
	 *  Task eseguito dalla segreteria (sostituisce 3 al primo giro nella domanda standard)
	 *  La segreteria compila la maschera del verbale e la salva. Il task termina quando la segreteria esplicitamente VALIDA il verbale. (salvataggio in bozza nel frattempo)
	 * */
	VALUTAZIONE_SUL_CAMPO (14, "Valutazione sul campo", "valutazioneCampo", EsecutoreStatoEnum.SEGRETERIA),//[SOLO STANDARD]
	VALUTAZIONE_TEAM_LEADER(15, "Valutazione del Team Leader", "valutazioneReferee", EsecutoreStatoEnum.SEGRETERIA),//[SOLO STANDARD] - domanda asseganata al Team Leader che deve compilare la griglia (sostituisce 3 dal secondo giro nella domanda standard)

	CANCELLATO(16, "Cancellato", "", EsecutoreStatoEnum.SEGRETERIA),//domanda cancellata - prevista dal vecchio sistema...
	SOSPESO(17, "Sospeso", "", EsecutoreStatoEnum.SEGRETERIA),//domanda sospesa - prevista dal vecchio sistema...


	RICHIESTA_INTEGRAZIONE_IN_PROTOCOLLAZIONE (18, "Richiesta integrazione in protocollazione", "inProtocollazione", EsecutoreStatoEnum.SEGRETERIA),//il thread in background controlla se il documento viene protocollato e quando questo avviene esegue il task successivo
	RICHIESTA_PREAVVISO_RIGETTO_IN_PROTOCOLLAZIONE (19, "Richiesta Preavviso di Rigetto in protocollazione", "inProtocollazione", EsecutoreStatoEnum.SEGRETERIA),//il thread in background controlla se il documento viene protocollato e quando questo avviene esegue il task successivo
	DINIEGO_IN_PROTOCOLLAZIONE (20, "Diniego in protocollazione", "inProtocollazione", EsecutoreStatoEnum.SEGRETERIA),//il thread in background controlla se il documento viene protocollato e quando questo avviene esegue il task successivo
	ACCREDITATO_IN_PROTOCOLLAZIONE (21, "Accreditato in protocollazione", "inProtocollazione", EsecutoreStatoEnum.SEGRETERIA),//il thread in background controlla se il documento viene protocollato e quando questo avviene esegue il task successivo

	CONCLUSO (22, "Concluso", "", EsecutoreStatoEnum.SEGRETERIA),//Stato in cui va alla fine della "Variazione Dati"

	RICHIESTA_INTEGRAZIONE_IN_FIRMA (23, "Richiesta integrazione in attesa di firma del documento", "inFirma", EsecutoreStatoEnum.SEGRETERIA),
	RICHIESTA_PREAVVISO_RIGETTO_IN_FIRMA (24, "Richiesta Preavviso di Rigetto in attesa di firma del documento", "inFirma", EsecutoreStatoEnum.SEGRETERIA),
	DINIEGO_IN_FIRMA (25, "Diniego in attesa di firma del documento", "inFirma", EsecutoreStatoEnum.SEGRETERIA),
	ACCREDITATO_IN_FIRMA (26, "Accreditato in attesa di firma del documento", "inFirma", EsecutoreStatoEnum.SEGRETERIA);

	private int id;
	private String nome;
	private String gruppo;
	private EsecutoreStatoEnum esecutoreStato;

	private AccreditamentoStatoEnum(int id, String nome, String gruppo, EsecutoreStatoEnum esecutoreStato){
		this.id = id;
		this.nome = nome;
		this.gruppo = gruppo;
		this.esecutoreStato = esecutoreStato;
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