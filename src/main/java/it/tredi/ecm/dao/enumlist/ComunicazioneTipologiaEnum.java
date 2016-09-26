package it.tredi.ecm.dao.enumlist;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

@Getter
public enum ComunicazioneTipologiaEnum {
//	Evento
	DATA_INIZIO(1, "Comunicazioni relative a data di inizio", "EVENTI"),
	DATA_INTERMEDIA(2, "Comunicazioni relative a data intermedia", "EVENTI"),
	DATA_FINE(3, "Comunicazioni relative a data fine", "EVENTI"),
	DOCENTE_NON_PREVISTO(4, "Comunicazioni relative a docente non previsto", "EVENTI"),
	PROFESSIONI(5, "Comunicazioni relative a professioni", "EVENTI"),
	NUMERO_PARTECIPANTI(6, "Comunicazioni relative a n. partecipanti", "EVENTI"),
	PROGRAMMA(7, "Comunicazione relative a programma", "EVENTI"),
	PROROGA_TERMINI(8, "Richiesta di proroga termini rapporto", "EVENTI"),
	RAPPORTO_XML(9, "Comunicazioni relative al rapporto XML (es. correzione codici fiscali)", "EVENTI"),
	PROLUNGAMENTO_FAD(10, "Prolungamento FAD", "EVENTI"),
	DATI_VERSAMENTO(11, "Modifiche dati versamento eventi", "EVENTI"),
	ALTRO_EVENTI(12, "Altro", "EVENTI"),

//	Provider
	PROVVISORIO(13, "Accreditamento provvisorio", "PROVIDER"),
	STANDARD(14, "Accreditamento standard", "PROVIDER"),
	INSERIMENTO_PFA(15, "Comunicazioni relative a inserimento PFA", "PROVIDER"),
	VERSAMENTO_CONTRIBUTO_ANNUALE(16, "Modifiche dati versamento contributo annuale", "PROVIDER"),
	APERTURA_CAMPI(17, "Comunicazioni relative ad apertura campi", "PROVIDER"),
	CONTRIBUTO_ANNUALE(18, "Comunicazioni relative a contributo annuale", "PROVIDER"),
	RELAZIONE_ANNUALE(19, "Comunicazioni relative a Relazione Annuale", "PROVIDER"),
	RECUPER_PEC(20, "Recupero PEC", "PROVIDER"),
	AMPLIAMENTO_FAD(21, "Ampliamento tipologia FAD", "PROVIDER"),
	ALTRO_PROVIDER(22, "Altro", "PROVIDER");

	private int id;
	private String nome;
	private String ambito;

	private ComunicazioneTipologiaEnum(int id, String nome, String ambito){
		this.id = id;
		this.nome = nome;
		this.ambito = ambito;
	}

	public static Set<ComunicazioneTipologiaEnum> getAllTipologiaByAmbito(String ambito) {
		Set<ComunicazioneTipologiaEnum> allTipologia = new HashSet<ComunicazioneTipologiaEnum>();
		for (ComunicazioneTipologiaEnum c : ComunicazioneTipologiaEnum.values()) {
			if (c.getAmbito().equals(ambito)) {
				allTipologia.add(c);
			}
		}
		return allTipologia;
	}
}
