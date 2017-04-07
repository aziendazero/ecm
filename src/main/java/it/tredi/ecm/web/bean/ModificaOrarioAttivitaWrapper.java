package it.tredi.ecm.web.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModificaOrarioAttivitaWrapper {
	Long programmaId;
	List<Long> listaRowId;
	String ora;
}
