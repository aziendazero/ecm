package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import it.tredi.ecm.dao.entity.Pagamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.QuotaAnnuale;
import it.tredi.ecm.dao.repository.QuotaAnnualeRepository;
import it.tredi.ecm.utils.Utils;
import scala.noinline;

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
		
		QuotaAnnuale qA = getQuotaAnnualeForProviderIdAndAnnoRiferimento(providerId, annoRiferimento);
		if(qA != null){
			LOGGER.debug("Impossiible creare la quota annuale in quanto gia registrata!");
		}else{
			Double importoBase = 258.22;
			
			Provider provider = providerService.getProvider(providerId);
			QuotaAnnuale quotaAnnuale = new QuotaAnnuale();
			quotaAnnuale.setProvider(provider);
			quotaAnnuale.setAnnoRiferimento(annoRiferimento);
			quotaAnnuale.setPrimoAnno(primoAnno);
			
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
				
				/* Se la quota si riferisce al primo anno -> blocco le funzionalita' finche non paga*/
				providerService.bloccaFunzionalitaForPagamento(providerId);
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
		return null;
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
		LOGGER.debug("Recupero lista di Pagamenti quota di Provider in sospeso");
		Set<Pagamento> pagamenti = quotaAnnualeRepository.getPagamentiProviderDaVerificare(); 
		LOGGER.debug("Trovati: " + ((pagamenti!=null) ? pagamenti.size() : "0")  + " Pagamenti in sospeso");
		return pagamenti;
	}
	
	@Override
	public Set<Provider> getAllProviderNotPagamentoEffettuatoAllaScadenza() {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di Provider con pagamenti non effettuati"));
		return quotaAnnualeRepository.findAllProviderNotPagamentoEffettuatoAllaScadenza(LocalDate.now());
	}
	
	@Override
	public int countProviderNotPagamentoEffettuatoAllaScadenza() {
		Set<Provider> list = getAllProviderNotPagamentoEffettuatoAllaScadenza();
		if(list != null)
			return list.size();
		return 0;
	}
	
	@Override
	public boolean hasProviderPagamentiNonEffettuati(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Controllo Provider: " + providerId + " ha pagamenti non effettuati"));
		
		int count = quotaAnnualeRepository.countByProviderIdAndPagatoFalse(providerId);
		if(count == 0)
			return false;

		return true;
	}
	
	@Override
	public Set<Provider> getAllProviderNotPagamentoRegistrato(Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di Provider Accreditati che non hanno una quota annuale registrata per l'anno: " + annoRiferimento));
		Set<Provider> providerList = quotaAnnualeRepository.findAllProviderNotPagamentoRegistrato(annoRiferimento);

		if(providerList != null)
			LOGGER.info(Utils.getLogMessage("Trovati " + providerList.size() + " provider"));
		
		return providerList; 
	}
	
	@Override
	public QuotaAnnuale getQuotaAnnualeForProviderIdAndAnnoRiferimento(Long providerId, Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage("Recupero QuoteAnnuale per provider: " + providerId + " per l'anno " + annoRiferimento));
		return quotaAnnualeRepository.findOneByProviderIdAndAnnoRiferimento(providerId, annoRiferimento);
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
	
	@Override
	public void checkAndCreateQuoteAnnualiPerAnnoInCorso() {
		LOGGER.debug("Creazione QuotaAnnuale per anno in corso");
		int annoInCorso = LocalDate.now().getYear();
		Set<Provider> provideList = getAllProviderNotPagamentoRegistrato(annoInCorso);
		if(provideList == null){
			LOGGER.debug("Nessun provider senza pagamento trovato");
		}else{
			LOGGER.debug("Trovati " + provideList.size() + " provider senza pagamento");	
			for(Provider p : provideList)
				createPagamentoProviderPerQuotaAnnuale(p.getId(), annoInCorso, false);
		}
	}
}
