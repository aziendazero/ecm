package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum FaseDiLavoroFSCEnum {
	//per tipologia: Training individualizzato
	AMBIENTAMENTO(1,"Ambientamento"),
	LAVORO_AFFIANCATO_TUTOR(2,"Lavoro affiancato dal tutor"),
	LAVORO_AUTONOMO_COLLABORAZIONE_TUTOR(3,"Lavoro autonomo in collaborazione con consulenza del tutor"),
	VALUTAZIONE_FINALE(4,"Valutazione finale"),
	
	//per tipologia: Gruppi di migioramento
	//il campo e libero non un enum
	
	//per tipologia: Progetti di miglioramento
	ANALISI_DEL_PROBLEMA(5,"Analisi del problema"),
	INDIVIDUAZIONE_DELLE_SOLUZIONI(6,"Individuazione delle soluzioni"),
	CONFRONTO_E_CONDIVISIONE(7,"Confronto e condivisione con gli operatori coinvolti sulle soluzioni ipotizzate"),
	IMPLEMENTAZIONE_CAMBIAMENTO_E_MONITORAGGIO(8,"Implementazione del cambiamento e suo monitoraggio"),
	VALUTAZIONE_IMPATTO_CAMBIAMENTO(9,"Valutazione dell’impatto del cambiamento"),
	
	//per tipologia: Attivita' di ricerca
	ESPLICITAZIONE_IPOTESI_LAVORO(10,"Esplicitazione dell’ipotesi di lavoro – indicazione della metodologia della ricerca"),
	FASE_RACCOLTA_DATI(11,"Fase della raccolta dati"),
	ANALISI_DATI(12,"Analisi dei dati"),
	PRESENTAZIONE_RISULTATI(13,"Presentazione dei risultati della ricerca"),
	
	//per tipologia: Audit clinico e/o assistenziale
	DEFINIZIONE_CRITERI_VALUTAZIONI_PRATICITA_CLINICA(14,"Definizione di criteri e standard concordati e misurabili e valutazioni della pratica clinica in termini di processo"),
	ELABORAZIONE_PROPOSTE(15,"Elaborazione di proposte di miglioramento"),
	APPLICAZIONI_GESTIONALI_ORGANIZZATIVE(16,"Applicazioni gestionali/organizzative delle proposte di miglioramento"),
	VERIFICA_PRATICA_CORRENTE(17,"Verifica della buona pratica corrente rispetto a standard concordati"),
	VALUTAZIONE_IMPATTO(18,"Valutazione dell’impatto del cambiamento");
	
	private int id;
	private String nome;

	private FaseDiLavoroFSCEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
	
}
