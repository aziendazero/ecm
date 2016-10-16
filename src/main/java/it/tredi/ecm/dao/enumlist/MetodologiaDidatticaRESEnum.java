package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum MetodologiaDidatticaRESEnum {

	_1(1,"Lezione Frontale/Relazione (metodologia frontale)",TipoMetodologiaEnum.FRONTALE),
	_2(2,"Lezione Frontale/Relazione con dibattito (metodologia frontale)",TipoMetodologiaEnum.FRONTALE),
	_3(3,"Tavola rotonda con disussione tra esperti (metodologia frontale)",TipoMetodologiaEnum.FRONTALE),
	_4(4,"Lezione Frontale con l'uso di videoconferenza (metodologia frontale)",TipoMetodologiaEnum.FRONTALE),
	_5(5,"Dimostrazione senza esecuzione diretta da parte dei Partecipanti (metodologia frontale)",TipoMetodologiaEnum.FRONTALE),
	_6(6,"Esecuzione diretta da parte di tutti i partecipanti di attività pratiche nell'uso di strumenti, di tecniche e di metodologie (metodologia frontale)",TipoMetodologiaEnum.FRONTALE),
	_7(7,"Presentazione e discussione di problemi o di casi didattici in grande gruppo (metodologia interattiva)",TipoMetodologiaEnum.INTERATTIVA),
	_8(8,"Role playing (metodologia interattiva)",TipoMetodologiaEnum.INTERATTIVA),
	_9(9,"Lavoro a piccoli gruppi e/o individuale con presentazione delle conclusioni (metodologia interattiva)",TipoMetodologiaEnum.INTERATTIVA),
	_10(10,"Giochi didattici (metodologia interattiva)",TipoMetodologiaEnum.INTERATTIVA),
	_11(11,"Tecniche di formazione esperienziale con debriefing (metodologia interattiva)",TipoMetodologiaEnum.INTERATTIVA);
	
	private int id;
	private String nome;
	private TipoMetodologiaEnum metodologia;

	private MetodologiaDidatticaRESEnum(int id, String nome, TipoMetodologiaEnum metodologia){
		this.id = id;
		this.nome = nome;
		this.metodologia = metodologia;
	}
}
