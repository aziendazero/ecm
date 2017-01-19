package it.tredi.ecm.service;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.repository.PersonaEventoRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class PersonaEventoServiceImpl implements PersonaEventoService {
	private static Logger LOGGER = LoggerFactory.getLogger(PersonaEventoServiceImpl.class);

	@Autowired private PersonaEventoRepository personaEventoRepository;

	@Override
	public Set<Long> getAllEventoIdByNomeAndCognomeDocente(String nome, String cognome) {
		LOGGER.debug(Utils.getLogMessage("Recupero tutti gli id degli eventi che hanno come docente la PersonaEvento con nome: " + nome + " e cognome: " + cognome));
		Set<BigInteger> result = personaEventoRepository.findAllEventoIdByNomeAndCognome(nome.toUpperCase(), cognome.toUpperCase());
		Set<Long> ids = new HashSet<Long>();
		for (BigInteger id : result) {
			ids.add(id.longValue());
		}
		return ids;
	}

}
