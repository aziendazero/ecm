package it.tredi.ecm.dao.enumlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum ProviderStatoEnum {
	INSERITO (1, "Inserito - Domanda in stato di bozza"),
	VALIDATO (2, "Domanda inviata"),
	ACCREDITATO_PROVVISORIAMENTE (3, "Accreditamento provvisorio accettato"),
	DINIEGO (4, "Accreditamento diniegato"),
	ACCREDITATO_STANDARD (5, "Accreditamento standard accettato"),
	SOSPESO (6, "Accreditamento temporaneamente sospeso"),
	CANCELLATO (7, "Accreditamento cancellato"),
	CESSATO (8, "Cessato");

	private int id;
	private String nome;

	private ProviderStatoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}

	public static List<ProviderStatoEnum> getAllStatiByFlagAttivi(boolean attivi) {
		// prende tutti gli stati a seconda del flag
		ArrayList<ProviderStatoEnum> stati = null;
		if(attivi) {
			stati = new ArrayList<ProviderStatoEnum>(Arrays.asList(INSERITO, VALIDATO, SOSPESO, ACCREDITATO_PROVVISORIAMENTE, ACCREDITATO_STANDARD));
		}
		else {
			stati = new ArrayList<ProviderStatoEnum>(Arrays.asList(DINIEGO, CANCELLATO, CESSATO));
		}
		return stati;
	}
}