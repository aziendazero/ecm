package it.tredi.ecm.dao.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import it.tredi.ecm.dao.entity.Token;

public interface TokenRepository extends CrudRepository<Token, Long> {
	Optional<Token> findOneByToken(String token);
}
