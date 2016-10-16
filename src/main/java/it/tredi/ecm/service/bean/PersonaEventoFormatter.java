package it.tredi.ecm.service.bean;

import java.util.Locale;

import org.springframework.expression.ParseException;
import org.springframework.format.Formatter;

import it.tredi.ecm.dao.entity.PersonaEvento;

public class PersonaEventoFormatter implements Formatter<PersonaEvento> {

	@Override
	public String print(PersonaEvento personaEvento, Locale locale) {
		return personaEvento.getId().toString();
	}

	@Override
	public PersonaEvento parse(String id, Locale locale) throws ParseException {
		PersonaEvento personaEvento = new PersonaEvento();
		personaEvento.setId(Long.valueOf(id));
		return personaEvento;
	}
}