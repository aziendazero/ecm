package it.tredi.ecm.service;

import java.util.Set;

public interface PersonaEventoService {

	Set<Long> getAllEventoIdByNomeAndCognomeDocente(String nome, String cognome);

}
