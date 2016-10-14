package it.tredi.ecm.dao.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.RendicontazioneInviata;

public interface RendicontazioneInviataRepository extends CrudRepository<RendicontazioneInviata, Long> {
	//Optional<Token> findOneByToken(String token);
}