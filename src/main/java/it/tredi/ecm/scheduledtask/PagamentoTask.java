package it.tredi.ecm.scheduledtask;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.tredi.ecm.service.EngineeringServiceImpl;
import it.tredi.ecm.service.QuotaAnnualeServiceImpl;

@Component
public class PagamentoTask {
	private static Logger LOGGER = LoggerFactory.getLogger(PagamentoTask.class);
	
	@Autowired EngineeringServiceImpl engineeringService;
	@Autowired QuotaAnnualeServiceImpl quotaAnnualeService;
	
	@Async
	@Transactional
	public void controllaEsitoPagamenti() throws Exception{
		LOGGER.debug(Thread.currentThread().getName());
		LOGGER.debug("controllaEsitoPagamenti - entering");
		
		engineeringService.esitoPagamentiEventi();
		engineeringService.esitoPagamentiQuoteAnnuali();
		quotaAnnualeService.checkAndCreateQuoteAnnualiPerAnnoInCorso();
		
		LOGGER.debug("controllaEsitoPagamenti - exiting");
	}
}
