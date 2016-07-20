package it.tredi.ecm.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Pagamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.repository.PagamentoRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class PagamentoServiceImpl implements PagamentoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PagamentoServiceImpl.class);
	
	@Autowired private PagamentoRepository pagamentoRepository;
	
	@Override
	public boolean providerIsPagamentoEffettuato(Long providerId, Integer annoPagamento) {
		LOGGER.debug(Utils.getLogMessage("Controllo se il Provider " + providerId + " ha effetturato il pagamento per anno " + annoPagamento));
		Pagamento pagamento = pagamentoRepository.findOneByProviderIdAndAnnoPagamento(providerId, annoPagamento);
		return (pagamento != null) ? true : false;
	}
	
	@Override
	public Set<Provider> getAllProviderNotPagamentoEffettuato(Integer annoPagamento) {
		LOGGER.debug(Utils.getLogMessage("Recupero lista Provider che non hanno effettuato il pagamento per anno " + annoPagamento));
		
		Set<Provider> list = pagamentoRepository.findAllProviderNotPagamentoEffettuato(annoPagamento);
		if(list != null)
			LOGGER.debug(Utils.getLogMessage("Trovati " + list.size() + " Provider"));
		else
			LOGGER.debug(Utils.getLogMessage("Nessun Provider trovato"));
		
		return list;
	}
}
