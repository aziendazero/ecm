package it.tredi.ecm.scheduledtask;

import java.time.LocalDate;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.tredi.ecm.service.EngineeringService;
import it.tredi.ecm.service.QuotaAnnualeService;

@Component
public class PagamentoTask {
	@Value("${pagamento.giornoInizioCalcolaQuotaAnnuale}")
	private int giornoInizioCalcolaQuotaAnnuale = 01;

	@Value("${pagamento.messeInizioCalcolaQuotaAnnuale}")
	private int messeInizioCalcolaQuotaAnnuale = 04;

	@Value("${pagamento.giornoFinisceCalcolaQuotaAnnuale}")
	private int giornoFinisceCalcolaQuotaAnnuale = 31;

	@Value("${pagamento.messeFinisceCalcolaQuotaAnnuale}")
	private int messeFinisceCalcolaQuotaAnnuale = 12;
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
		int year  = LocalDate.now().getYear();
		LocalDate inizioControllo = LocalDate.of(year, messeInizioCalcolaQuotaAnnuale, giornoInizioCalcolaQuotaAnnuale);
		LocalDate fineControllo = LocalDate.of(year, messeFinisceCalcolaQuotaAnnuale, giornoFinisceCalcolaQuotaAnnuale);
		LocalDate today = LocalDate.now();
		if( (today.isAfter(inizioControllo) || today.isEqual(inizioControllo)) && (today.isBefore(fineControllo) || today.isEqual(fineControllo)) ){
			quotaAnnualeService.checkAndCreateQuoteAnnualiPerAnnoInCorso();
		}

		LOGGER.info("controllaEsitoPagamenti - exiting");
	}
}
