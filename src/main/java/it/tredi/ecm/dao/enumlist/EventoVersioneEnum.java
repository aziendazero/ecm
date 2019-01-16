package it.tredi.ecm.dao.enumlist;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

// EVENTO_VERSIONE
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

	public static Set<EventoVersioneEnum> getSetByNumeroVersioni(List<Integer> numeriVersione) {
		Set<EventoVersioneEnum> toRet = null;
		if(numeriVersione != null && !numeriVersione.isEmpty()) {
			toRet = new HashSet<EventoVersioneEnum>();
			EventoVersioneEnum versEnum;
			for(Integer numVers : numeriVersione){
				if(numVers != null) {
					versEnum = getByNumeroVersione(numVers);
					if(versEnum != null)
						toRet.add(versEnum);
				}
			}
		}
		return toRet;
	}
}
