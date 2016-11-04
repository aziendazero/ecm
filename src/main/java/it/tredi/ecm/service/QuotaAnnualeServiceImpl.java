package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Pagamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.QuotaAnnuale;
import it.tredi.ecm.dao.repository.QuotaAnnualeRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class QuotaAnnualeServiceImpl implements QuotaAnnualeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuotaAnnualeServiceImpl.class);
	
	@Autowired private QuotaAnnualeRepository quotaAnnualeRepository; 
	@Autowired private ProviderService providerService;
	@Autowired private EngineeringServiceImpl engineeringService;
	@Autowired private PagamentoService pagamentoService;
	
	@Override
	public QuotaAnnuale createPagamentoProviderPerQuotaAnnuale(Long providerId, Integer annoRiferimento, boolean primoAnno) {
		LOGGER.debug(Utils.getLogMessage("Creazione pagamento per quota iscrizione anno " + annoRiferimento + " per provider " + providerId));
		
		Double importoBase = 258.22;
		
		Provider provider = providerService.getProvider(providerId);
		QuotaAnnuale quotaAnnuale = new QuotaAnnuale();
		quotaAnnuale.setProvider(provider);
		quotaAnnuale.setAnnoRiferimento(annoRiferimento);
		
		Pagamento pagamento = new Pagamento();
		pagamento.setQuotaAnnuale(quotaAnnuale);
		
		// i provider sono Ragioni Sociali, valorizzo i dati obbligatori.
		pagamento.setAnagrafica(provider.getDenominazioneLegale());
		pagamento.setPartitaIva(provider.getPartitaIva());
		pagamento.setEmail(provider.getEmailStruttura());
		pagamento.setTipoVersamento(EngineeringServiceImpl.TIPO_VERSAMENTO_ALL);
		pagamento.setCausale(EngineeringServiceImpl.CAUSALE_PAGAMENTO_QUOTA_PROVIDER + annoRiferimento);
		
		if(primoAnno){
			if(provider.getTipoOrganizzatore().getGruppo().equalsIgnoreCase("A")){
				//Provider tipo A -> primo anno non pagano
				pagamento.setImporto(0.00);
				pagamento.setDataScadenzaPagamento(LocalDate.now());
				pagamento.setDataPagamento(LocalDate.now());
				pagamento.setCodiceEsito(EngineeringServiceImpl.PAGAMENTO_ESEGUITO);
				quotaAnnuale.setPagato(true);
			}else if(provider.getTipoOrganizzatore().getGruppo().equalsIgnoreCase("B")){
				//Provider tipo B -> primo anno pagano quota fissa
				pagamento.setImporto(importoBase);
				pagamento.setDataScadenzaPagamento(LocalDate.now().plusDays(90));
			}
		}else{
			if(provider.getTipoOrganizzatore().getGruppo().equalsIgnoreCase("A")){
				//TODO calcolare il numero di eventi realizzati l'anno precedente all'anno di riferimento per il pagamento
				//Provider tipo A -> anni successivi pagano in funzione degli eventi realizzati
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
				//Provider tipo B -> anni successivi pagano quota fissa
				pagamento.setImporto(importoBase);
				//entro il 31 marzo
				pagamento.setDataScadenzaPagamento(LocalDate.of(annoRiferimento, 3, 31));
			}
		}
		
		save(quotaAnnuale);
		pagamentoService.save(pagamento);
		return quotaAnnuale;
	}
	
	@Override
	public String pagaQuotaAnnualeForProvider(Long quotaAnnualeId, String backURL) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Chiamata a EngineeringServiceImpl per pagamento quota per la quota: " + quotaAnnualeId));
		String url = null;
		
		Pagamento pagamento = pagamentoService.getPagamentoByQuotaAnnualeId(quotaAnnualeId);
		if(pagamento != null)
			url = engineeringService.pagaQuotaProvider(pagamento.getId(), backURL);
		
		return url;
	}
	
	@Override
	public Set<Pagamento> getPagamentiProviderDaVerificare() {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di Pagamenti quota di Provider in sospeso"));
		return quotaAnnualeRepository.getPagamentiProviderDaVerificare();
	}
	
	@Override
	public Set<Provider> getAllProviderNotPagamentoEffettuato() {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di Provider con pagamenti in sospeso"));
		return quotaAnnualeRepository.findAllProviderNotPagamentoEffettuato();
	}
	
	@Override
	public Set<Provider> getAllProviderNotPagamentoRegistrato(Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di Provider che non hanno una quota annuale registrata per l'anno: " + annoRiferimento));
		Set<Provider> providerList = quotaAnnualeRepository.findAllProviderNotPagamentoRegistrato(annoRiferimento);

		if(providerList != null)
			LOGGER.info(Utils.getLogMessage("Trovati " + providerList.size() + " provider"));
		
		return providerList; 
	}
	
	@Override
	public Set<QuotaAnnuale> getAllQuotaAnnuale() {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di QuoteAnnuali"));
		return quotaAnnualeRepository.findAll();
	}
	
	@Override
	public Set<QuotaAnnuale> getAllQuotaAnnualeByProviderId(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di QuoteAnnuali per provider: " + providerId));
		return quotaAnnualeRepository.findAllByProviderId(providerId);
	}
	
	@Override
	public void save(QuotaAnnuale quotaAnnuale) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio quotaAnnuale"));
		quotaAnnualeRepository.save(quotaAnnuale);
	}
}
