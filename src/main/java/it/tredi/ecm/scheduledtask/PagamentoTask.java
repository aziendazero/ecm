package it.tredi.ecm.scheduledtask;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.tredi.ecm.service.EngineeringService;
import it.tredi.ecm.service.QuotaAnnualeService;

@Component
public class PagamentoTask {
	private static Logger LOGGER = LoggerFactory.getLogger(PagamentoTask.class);

	@Autowired EngineeringService engineeringService;
	@Autowired QuotaAnnualeService quotaAnnualeService;

	@Async
	@Transactional
	public void controllaEsitoPagamenti() throws Exception{
		LOGGER.info(Thread.currentThread().getName());
		LOGGER.info("controllaEsitoPagamenti - entering");

		engineeringService.esitoPagamentiEventi();
		engineeringService.esitoPagamentiQuoteAnnuali();
		quotaAnnualeService.checkAndCreateQuoteAnnualiPerAnnoInCorso();

		LOGGER.info("controllaEsitoPagamenti - exiting");
	}
}
