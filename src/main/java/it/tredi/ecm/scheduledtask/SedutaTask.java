package it.tredi.ecm.scheduledtask;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import it.tredi.ecm.service.SedutaServiceImpl;

public class SedutaTask {
	private static Logger LOGGER = LoggerFactory.getLogger(SedutaTask.class);

	@Autowired private SedutaServiceImpl sedutaService;

	@Async
	@Transactional
	public void bloccaSedute(){
		sedutaService.eseguiBloccoSeduteDaBloccare();
	}
}
