package it.tredi.ecm.service;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.enumlist.IdentificativoPersonaRuoloEvento;
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

	@Override
	public IdentificativoPersonaRuoloEvento prossimoIdentificativoPersonaRuoloEventoNonUtilizzato(List<PersonaEvento> personeEvento) {
		IdentificativoPersonaRuoloEvento toRet = null;
		boolean nonUtilizzato = true;
		if(personeEvento != null && !personeEvento.isEmpty()) {
			for(IdentificativoPersonaRuoloEvento ident : IdentificativoPersonaRuoloEvento.getOrderedValues()) {
				nonUtilizzato = true;
				for(PersonaEvento pers : personeEvento) {
					if(ident == pers.getIdentificativoPersonaRuoloEvento())
						nonUtilizzato = false;
				}
				if(nonUtilizzato) {
					toRet = ident;
					break;
				}
			}
		} else {
			if(IdentificativoPersonaRuoloEvento.getOrderedValues() != null && !IdentificativoPersonaRuoloEvento.getOrderedValues().isEmpty())
				toRet = IdentificativoPersonaRuoloEvento.getOrderedValues().get(0);
		}
		return toRet;
	}

	@Override
	public void setIdentificativoPersonaRuoloEvento(List<PersonaEvento> personeEvento) {
		for(PersonaEvento persEv : personeEvento) {
			if(persEv.getIdentificativoPersonaRuoloEvento() == null) {
				persEv.setIdentificativoPersonaRuoloEvento(prossimoIdentificativoPersonaRuoloEventoNonUtilizzato(personeEvento));
			}
		}
	}
}
