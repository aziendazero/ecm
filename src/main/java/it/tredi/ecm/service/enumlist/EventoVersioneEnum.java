package it.tredi.ecm.service.enumlist;

import lombok.Getter;

@Getter
public enum EventoVersioneEnum {
	UNO_PRIMA_2018 (1),
	DUE_DAL_2018 (2)
	;

	private int numeroVersione;

	private EventoVersioneEnum(int numeroVersione){
		this.numeroVersione = numeroVersione;
	}

	public static EventoVersioneEnum getByNumeroVersione(int numeroVersione) {
		for(EventoVersioneEnum a : EventoVersioneEnum.values()){
			if(a.getNumeroVersione() == numeroVersione)
				return a;
		}
		return null;
	}
}
