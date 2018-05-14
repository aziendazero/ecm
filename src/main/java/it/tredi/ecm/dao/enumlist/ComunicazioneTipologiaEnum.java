package it.tredi.ecm.dao.enumlist;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.*;

import lombok.Getter;

@Getter
public enum ComunicazioneTipologiaEnum {
//	Evento
	//DATA_INIZIO(1, "Comunicazioni relative a data di inizio", "EVENTI"),
	DATA_INIZIO(1, "Anticipo della data inizio evento", "EVENTI"),
	DATA_INTERMEDIA_FINE(2, "Modifica data/e intermedia/e e/o finale", "EVENTI"),
	SEDE_SVOLGIMENTO(3, "Modifica sede svolgimento evento", "EVENTI"),
	//PROFESSIONI(5, "Comunicazioni relative a professioni", "EVENTI"),
	PROFESSIONI(4, "Modifica professioni/discipline", "EVENTI"),
	RESP_SCIENTIFICO(5, "Modifica responsabile scientifico", "EVENTI"),
	//NUMERO_PARTECIPANTI(6, "Comunicazioni relative a n. partecipanti", "EVENTI"),
	NUMERO_PARTECIPANTI(6, "Modifica numero partecipanti", "EVENTI"),
	
	MOD_CV(7, "Modifica curriculum vitae", "EVENTI"),
	MOD_DOCENTI(8, "Modifica docenti o altri ruoli", "EVENTI"),
	MOD_ATTIVITA(9, "Modifica attività evento - sezione 2 (es.: titolo, orario, metodologia didattica, ...)", "EVENTI"),
	PROVA_VERIFICA(10, "Modifica tipologia prova di verifica", "EVENTI"),
	SPONSOR(11, "Modifica inerente sponsor", "EVENTI"),
	MOD_PARTNER(12, "Modifica inerente partner o altro finanziamento", "EVENTI"),
	XML_GIA(13, "Riapertura dei termini per l'invio di un tracciato xml di un evento gia' rendicontato", "EVENTI"),
	XML_MAI(14, "Riapertura dei termini per l'invio di un tracciato xml di un evento mai rendicontato", "EVENTI"),
	PAGAMENTO(15, "Richiesta proroga pagamento evento", "EVENTI"),	
	ALTRO_EVENTI(16, "Altro [eventi]", "EVENTI"),
	
	// removed
	PROGRAMMA(107, "Comunicazione relative a programma", "EVENTI"),
	PROROGA_TERMINI(108, "Richiesta di proroga termini rapporto", "EVENTI"),
	RAPPORTO_XML(109, "Comunicazioni relative al rapporto XML (es. correzione codici fiscali)", "EVENTI"),
	PROLUNGAMENTO_FAD(110, "Prolungamento FAD", "EVENTI"),
	DATI_VERSAMENTO(111, "Modifiche dati versamento eventi", "EVENTI"),
	DATA_INTERMEDIA(102, "Comunicazioni relative a data intermedia", "EVENTI"),
	DATA_FINE(103, "Comunicazioni relative a data fine", "EVENTI"),
	DOCENTE_NON_PREVISTO(104, "Comunicazioni relative a docente non previsto", "EVENTI"),
	

//	Provider : modifiche per ERM015136
	PROVVISORIO(20, "Accreditamento provvisorio", "PROVIDER"),
	STANDARD(21, "Accreditamento standard", "PROVIDER"),
	//INSERIMENTO_PFA(15, "Comunicazioni relative a inserimento PFA", "PROVIDER"),
	INSERIMENTO_PFA(22, "Piano Formativo Annuale", "PROVIDER"),
	//VERSAMENTO_CONTRIBUTO_ANNUALE(16, "Modifiche dati versamento contributo annuale", "PROVIDER"),
	VERSAMENTO_CONTRIBUTO_ANNUALE(23, "Pagamento contributo annuale", "PROVIDER"),
	//APERTURA_CAMPI(17, "Comunicazioni relative ad apertura campi", "PROVIDER"),
	APERTURA_CAMPI(24, "Variazione dati provider (es. legale rappresentante, organigramma, comitato scientifico, ecc)", "PROVIDER"),	
	//RELAZIONE_ANNUALE(19, "Comunicazioni relative a Relazione Annuale", "PROVIDER"),
	RELAZIONE_ANNUALE(25, "Relazione annuale", "PROVIDER"),
	AMPLIAMENTO_RES_FSC_FAD(26, "Ampliamento tipologia formativa (RES, FSC, FAD)", "PROVIDER"),
	AMPLIAMENTO_PROF_DIC(27, "Ampliamento professioni/discipline", "PROVIDER"),
	ALTRO_PROVIDER(28, "Altro [provider]", "PROVIDER"),
	
	// REMOVED
	CONTRIBUTO_ANNUALE(118, "Comunicazioni relative a contributo annuale", "PROVIDER"),
	RECUPER_PEC(120, "Recupero PEC", "PROVIDER"),
	AMPLIAMENTO_FAD(121, "Ampliamento tipologia FAD", "PROVIDER");
	
	

	private int id;
	private String nome;
	private String ambito;

	private ComunicazioneTipologiaEnum(int id, String nome, String ambito){
		this.id = id;
		this.nome = nome;
		this.ambito = ambito;
	}

	// otiene la lista di valori che non siano deprecati.
	public static ComunicazioneTipologiaEnum[] getFilteredValues(){
		return Stream.of(ComunicazioneTipologiaEnum.values()).filter(c->c.getId() < 100).toArray(size->new ComunicazioneTipologiaEnum[size]);
	}
	
	public static Set<ComunicazioneTipologiaEnum> getAllTipologiaByAmbito(String ambito) {
		return Stream.of(ComunicazioneTipologiaEnum.values()).filter(c->c.getAmbito().equals(ambito)).collect(Collectors.toSet());
		
		/*
		Set<ComunicazioneTipologiaEnum> allTipologia = new HashSet<ComunicazioneTipologiaEnum>();
		for (ComunicazioneTipologiaEnum c : ComunicazioneTipologiaEnum.values()) {
			if (c.getAmbito().equals(ambito)) {
				allTipologia.add(c);
			}
		}
		return allTipologia;
		*/
	}
}
