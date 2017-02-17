package it.tredi.ecm.dao.enumlist;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

@Getter
public enum ProviderStatoEnum {
	INSERITO (1, "Inserito - Domanda in stato di bozza"),
	VALIDATO (2, "Domanda inviata"),
	ACCREDITATO_PROVVISORIAMENTE (3, "Accreditamento provvisorio accettato"),
	DINIEGO (4, "Accreditamento rifiutato"),
	ACCREDITATO_STANDARD (5, "Accreditamento standard accettato"),
	SOSPESO (6, "Accreditamento temporaneamente sospeso"),
	CANCELLATO (7, "Accreditamento cancellato");

	private int id;
	private String nome;

	private ProviderStatoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}

	public static Set<ProviderStatoEnum> getAllStatiByFlagAttivi(boolean attivi) {
		// prende tutti gli stati a seconda del flag
		Set<ProviderStatoEnum> stati = null;
		if(attivi) {
			stati = new HashSet<ProviderStatoEnum>(Arrays.asList(INSERITO, VALIDATO, SOSPESO, ACCREDITATO_PROVVISORIAMENTE, ACCREDITATO_STANDARD));
		}
		else {
			stati = new HashSet<ProviderStatoEnum>(Arrays.asList(DINIEGO, CANCELLATO));
		}
		return stati;
	}
}