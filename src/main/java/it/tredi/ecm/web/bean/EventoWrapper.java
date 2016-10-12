package it.tredi.ecm.web.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoWrapper {
	private Evento evento;
	private ProceduraFormativa proceduraFormativa;
	private Long providerId;

	//parte rendicontazione
	private File reportPartecipanti;

	//parte ripetibili
	private List<String> dateIntermedieTemp = new ArrayList<String>();

	//liste edit
	private Set<Obiettivo> obiettiviNazionali;
	private Set<Obiettivo> obiettiviRegionali;
	private Set<Disciplina> disciplinaList;
	private Set<Professione> professioneList;



}
