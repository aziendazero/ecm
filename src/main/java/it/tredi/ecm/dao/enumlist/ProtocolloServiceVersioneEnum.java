package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum ProtocolloServiceVersioneEnum {

	RV(1, "rv"),
	WEBRAINBOW(2, "webrainbow");
	
	private int numeroVersione;
	private String nome;

	private ProtocolloServiceVersioneEnum(int numeroVersione, String nome){
		this.numeroVersione = numeroVersione;
		this.nome = nome;
	}

	public static ProtocolloServiceVersioneEnum getByNumeroVersione(int numeroVersione) {
		for(ProtocolloServiceVersioneEnum a : ProtocolloServiceVersioneEnum.values()){
			if(a.getNumeroVersione() == numeroVersione)
				return a;
		}
		return null;
	}
}
