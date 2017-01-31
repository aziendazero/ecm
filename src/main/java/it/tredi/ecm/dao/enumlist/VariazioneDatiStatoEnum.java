package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum VariazioneDatiStatoEnum {
	RICHIESTA_INTEGRAZIONE (9, "Abilitazione dei campi da parte della Segreteria"),
	RICHIESTA_INTEGRAZIONE_IN_PROTOCOLLAZIONE (18, "Richiesta integrazione in protocollazione"),//il thread in background controlla se il documento viene protocollato e quando questo avviene esegue il task successivo
	INTEGRAZIONE (10, "Modifica dei campi da parte del Provider"),
	VALUTAZIONE_SEGRETERIA (13, "Valutazione della modifica da parte della Segreteria"),
	VALUTAZIONE_CRECM (3, "Valutazione della modifica da parte del Referee"),
	ASSEGNAMENTO (4, "Assegnamento nuovi referee"),//domanda restituita alla segreteria che deve assegnare un nuovo gruppo CRECM (perchè 2/3 del primo gruppo non hanno valutato)
	INS_ODG (5, "Inserimento nell'Ordine del giorno"),//domanda deve essere inserita in ODG per valutazione della Commissione ECM
	VALUTAZIONE_COMMISSIONE (6, "Valutazione della modifica da parte della Commissione ECM");

	private int id;
	private String nome;

	private VariazioneDatiStatoEnum(int id, String nome) {
		this.id = id;
		this.nome = nome;
	}
}
