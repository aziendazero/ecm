package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum MetodologiaDidatticaRESEnum {

	_1(1,"Lezione",TipoMetodologiaEnum.FRONTALE),
	_2(2,"Lezione",TipoMetodologiaEnum.FRONTALE),
	_3(3,"Lezione",TipoMetodologiaEnum.FRONTALE),
	_4(4,"Lezione",TipoMetodologiaEnum.FRONTALE),
	_5(5,"Lezione",TipoMetodologiaEnum.FRONTALE),
	_6(6,"Lezione",TipoMetodologiaEnum.FRONTALE),
	_7(7,"Lezione",TipoMetodologiaEnum.INTERATTIVA),
	_8(8,"Lezione",TipoMetodologiaEnum.INTERATTIVA),
	_9(9,"Lezione",TipoMetodologiaEnum.INTERATTIVA),
	_10(10,"Lezione",TipoMetodologiaEnum.INTERATTIVA),
	_11(11,"Lezione",TipoMetodologiaEnum.INTERATTIVA);
	
	private int id;
	private String nome;
	private TipoMetodologiaEnum metodologia;

	private MetodologiaDidatticaRESEnum(int id, String nome, TipoMetodologiaEnum metodologia){
		this.id = id;
		this.nome = nome;
		this.metodologia = metodologia;
	}
}
