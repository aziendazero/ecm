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
	CAMPO_LIBERO(5,""),
	
	//per tipologia: Progetti di miglioramento
	ANALISI_DEL_PROBLEMA(6,"[A] - Analisi del problema"),
	INDIVIDUAZIONE_DELLE_SOLUZIONI(7,"[B] - Individuazione delle soluzioni"),
	CONFRONTO_E_CONDIVISIONE(8,"[C] - Confronto e condivisione con gli operatori coinvolti sulle soluzioni ipotizzate"),
	IMPLEMENTAZIONE_CAMBIAMENTO_E_MONITORAGGIO(9,"[D] - Implementazione del cambiamento e suo monitoraggio"),
	VALUTAZIONE_IMPATTO_CAMBIAMENTO(10,"[E] - Valutazione dell’impatto del cambiamento"),
	
	//per tipologia: Attivita' di ricerca
	ESPLICITAZIONE_IPOTESI_LAVORO(11,"Esplicitazione dell’ipotesi di lavoro – indicazione della metodologia della ricerca"),
	FASE_RACCOLTA_DATI(12,"Fase della raccolta dati"),
	ANALISI_DATI(13,"Analisi dei dati"),
	PRESENTAZIONE_RISULTATI(14,"Presentazione dei risultati della ricerca"),
	
	//per tipologia: Audit clinico e/o assistenziale
	DEFINIZIONE_CRITERI_VALUTAZIONI_PRATICITA_CLINICA(15,"Definizione di criteri e standard concordati e misurabili e valutazioni della pratica clinica in termini di processo"),
	ELABORAZIONE_PROPOSTE(16,"Elaborazione di proposte di miglioramento"),
	APPLICAZIONI_GESTIONALI_ORGANIZZATIVE(17,"Applicazioni gestionali/organizzative delle proposte di miglioramento"),
	VERIFICA_PRATICA_CORRENTE(18,"Verifica della buona pratica corrente rispetto a standard concordati"),
	VALUTAZIONE_IMPATTO(19,"Valutazione dell’impatto del cambiamento");
	
	private int id;
	private String nome;

	private FaseDiLavoroFSCEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
	
}
