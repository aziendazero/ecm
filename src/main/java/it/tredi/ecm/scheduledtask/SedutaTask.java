package it.tredi.ecm.scheduledtask;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.tredi.ecm.service.SedutaService;

@Component
public class SedutaTask {
	private static Logger LOGGER = LoggerFactory.getLogger(SedutaTask.class);

	@Autowired private SedutaService sedutaService;

	@Async
	@Transactional
	public void bloccaSedute(){
		LOGGER.info(Thread.currentThread().getName());
		LOGGER.info("bloccaSedute - entering");

		sedutaService.eseguiBloccoSeduteDaBloccare();

		LOGGER.info("bloccaSedute - exiting");
	}
}
