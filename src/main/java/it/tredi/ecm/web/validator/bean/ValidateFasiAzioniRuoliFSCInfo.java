package it.tredi.ecm.web.validator.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateFasiAzioniRuoliFSCInfo {
	private boolean atLeastOnePartecipante = false;
	private boolean atLeastOneTutor = false;
	private boolean invalidResponsabileScientifico = false;
//	private boolean invalidResponsabileScientificoA = false;
//	private boolean invalidResponsabileScientificoB = false;
//	private boolean invalidResponsabileScientificoC = false;
	private boolean invalidCoordinatore = false;
//	private boolean invalidCoordinatoreA = false;
//	private boolean invalidCoordinatoreB = false;
//	private boolean invalidCoordinatoreC = false;
	private boolean invalidEsperto = false;
//	private boolean invalidEspertoA = false;
//	private boolean invalidEspertoB = false;
//	private boolean invalidEspertoC = false;
}
