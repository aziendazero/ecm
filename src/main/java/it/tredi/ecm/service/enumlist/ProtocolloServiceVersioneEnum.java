package it.tredi.ecm.service.enumlist;

import lombok.Getter;

@Getter
public enum ProtocolloServiceVersioneEnum {

	RV(1),
	WEBRAINBOW(2);
	
	private int numeroVersione;

	private ProtocolloServiceVersioneEnum(int numeroVersione){
		this.numeroVersione = numeroVersione;
	}

	public static ProtocolloServiceVersioneEnum getByNumeroVersione(int numeroVersione) {
		for(ProtocolloServiceVersioneEnum a : ProtocolloServiceVersioneEnum.values()){
			if(a.getNumeroVersione() == numeroVersione)
				return a;
		}
		return null;
	}
}
