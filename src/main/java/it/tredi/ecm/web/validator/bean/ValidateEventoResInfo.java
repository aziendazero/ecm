package it.tredi.ecm.web.validator.bean;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateEventoResInfo {
	private Set<String> risultatiAttesiUtilizzati;
	private boolean alertResDocentiPartecipanti = false;
	private boolean alertResDocentiNonPresenti = false;
}
