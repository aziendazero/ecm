package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum VerificaApprendimentoInnerFADEnum {
	
	SVOLTO_IN_PRESENZA(1,"Svolto in presenza"),
	ESEGUITO_ON_LINE(2,"Eseguito on line"),
	TRASMESSO_VIA_PEC(3,"trasmesso via PEC"),
	TRASMESSO_VIA_POSTA(4,"Trasmesso via posta");
	
	private int id;
	private String nome;

	private VerificaApprendimentoInnerFADEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
