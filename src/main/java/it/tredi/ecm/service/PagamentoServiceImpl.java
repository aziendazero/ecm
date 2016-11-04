package it.tredi.ecm.service;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Pagamento;
import it.tredi.ecm.dao.repository.PagamentoRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class PagamentoServiceImpl implements PagamentoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PagamentoServiceImpl.class);
	
	@Autowired private PagamentoRepository pagamentoRepository;
	
	@Override
	public Pagamento getPagamentoById(Long pagamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero Pagamento " + pagamentoId));
		return pagamentoRepository.findOne(pagamentoId);
	}
	
	@Override
	public Set<Pagamento> getAllPagamenti(){
		LOGGER.debug(Utils.getLogMessage("Recupero lista di tutti i pagamenti"));
		return pagamentoRepository.findAll();
	}
	
	@Override
	public Pagamento getPagamentoByQuotaAnnualeId(Long quotaAnnualeId) {
		LOGGER.debug(Utils.getLogMessage("Recupero Pagamento per quota annuale: " + quotaAnnualeId));
		return pagamentoRepository.findOneByQuotaAnnualeId(quotaAnnualeId);
	}
	
	@Override
	public Pagamento getPagamentoByEvento(Evento evento) {
		LOGGER.debug(Utils.getLogMessage("Recupero Pagamento evento " + evento.getId()));
		return pagamentoRepository.getPagamentoByEvento(evento);
	}
	
	@Override
	public Set<Pagamento> getPagamentiEventiDaVerificare() {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di Pagamenti Eventi in sospeso"));
		return pagamentoRepository.getPagamentiEventiDaVerificare();
	}
	
	@Override
	@Transactional
	public void save(Pagamento p) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio Pagamento"));
		pagamentoRepository.save(p);
	}
	
	@Override
	@Transactional
	public void deleteAll(Iterable<Pagamento> pagamenti) {
		LOGGER.debug(Utils.getLogMessage("Eliminazione tutti Pagamenti"));
		pagamentoRepository.delete(pagamenti);
	}
	
	
}
