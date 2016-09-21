package it.tredi.ecm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Token;
import it.tredi.ecm.dao.repository.TokenRepository;

@Service
public class TokenServiceImpl implements TokenService {
	private static Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

	@Autowired
	private TokenRepository tokenRepository;

	@Override
	public boolean checkTokenAndDelete(String token) {
		LOGGER.info("checkTokenAndDelete token: " + token);
		Token tokenEntity = tokenRepository.findOneByToken(token).orElse(null);
		if(tokenEntity == null) {
			String msg = "Impossibile trovare il token passato token: " + token;
			LOGGER.error(msg);
			return false;
		}
		//cancello il token e modifico lo stato
		tokenRepository.delete(tokenEntity);
		return true;
	}

}
