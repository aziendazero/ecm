package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Pagamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.repository.PagamentoRepository;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.utils.Utils;

@Service
public class PagamentoServiceImpl implements PagamentoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PagamentoServiceImpl.class);
	
	@Autowired private PagamentoRepository pagamentoRepository;
	@Autowired private ProviderService providerService;
	@Autowired private EcmProperties ecmProperties;
	
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
	
	@Override
	public Pagamento getPagamentoByProviderIdAndAnnoRiferimento(Long providerId, Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage("Recupero Pagamento quota Provider " + providerId + " per anno " + annoRiferimento));
		Pagamento p = pagamentoRepository.findOneByProviderIdAndAnnoPagamento(providerId, annoRiferimento);
		if(p == null)
			LOGGER.debug(Utils.getLogMessage("Pagamento non trovato"));
		return p;
	}
	
	@Override
	public Set<Pagamento> getPagamentiProviderDaVerificare() {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di Pagamenti quota di Provider in sospeso"));
		return pagamentoRepository.getPagamentiProviderDaVerificare();
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
	
	@Override
	public Set<Pagamento> getAllPagamenti(){
		LOGGER.debug(Utils.getLogMessage("Recupero lista di tutti i pagamenti"));
		return pagamentoRepository.findAll();
	}
	
	@Override
	public Set<Pagamento> getAllPagamentiByProviderId(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di tutti i pagamenti per il provider " + providerId));
		return pagamentoRepository.findAllByProviderId(providerId);
	}
	
	@Override
	public Pagamento preparePagamentoProviderPerQuotaAnnua(Long providerId, Integer annoRiferimento, boolean primoAnno) {
		LOGGER.debug(Utils.getLogMessage("Creazione pagamento per quota iscrizione anno " + annoRiferimento + " per provider " + providerId));
		
		Double importoBase = 258.22;
		
		Provider provider = providerService.getProvider(providerId);
		Pagamento pagamento = new Pagamento();
		pagamento.setProvider(provider);
		pagamento.setAnnoPagamento(annoRiferimento);
		
		// i provider sono Ragioni Sociali, valorizzo i dati obbligatori.
		pagamento.setAnagrafica(provider.getDenominazioneLegale());
		pagamento.setPartitaIva(provider.getPartitaIva());
		pagamento.setEmail(provider.getEmailStruttura());
		pagamento.setTipoVersamento(EngineeringServiceImpl.TIPO_VERSAMENTO_ALL);
		pagamento.setCausale(EngineeringServiceImpl.CAUSALE_PAGAMENTO_QUOTA_PROVIDER + annoRiferimento);
		
		if(primoAnno){
			pagamento.setImporto(importoBase);
			pagamento.setDataScadenzaPagamento(LocalDate.now().plusDays(90));
		}else{
			if(provider.getTipoOrganizzatore().getGruppo().equalsIgnoreCase("A")){
				//TODO calcolare il numero di eventi realizzati l'anno precedente all'anno di riferimento per il pagamento
				int numeroEventi = 0;
				if(numeroEventi <= 30){
					pagamento.setImporto(3000.00);
				}else if(numeroEventi > 30 && numeroEventi <= 60){
					pagamento.setImporto(5500.00);
				}else if(numeroEventi > 60 && numeroEventi <= 90){
					pagamento.setImporto(8000.00);
				}else if(numeroEventi > 90 && numeroEventi <= 120){
					pagamento.setImporto(10500.00);
				}else if(numeroEventi > 120){
					pagamento.setImporto(13000.00);
				}
				//entro il 31 luglio
				pagamento.setDataScadenzaPagamento(LocalDate.of(annoRiferimento, 7, 31));
			}else if(provider.getTipoOrganizzatore().getGruppo().equalsIgnoreCase("B")){
				pagamento.setImporto(importoBase);
				//entro il 31 marzo
				pagamento.setDataScadenzaPagamento(LocalDate.of(annoRiferimento, 3, 31));
			}
		}
		
		save(pagamento);
		return pagamento;
	}
}
