package it.tredi.ecm.scheduledtask;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.tredi.ecm.service.AlertEmailService;
import it.tredi.ecm.service.SedutaService;

@Component
public class AlertScadenzeTask {
	private static Logger LOGGER = LoggerFactory.getLogger(AlertScadenzeTask.class);

	@Autowired private AlertEmailService alertEmailService;

	@Async
	@Transactional
	public void inviaAlert() throws Exception{
		alertEmailService.inviaAlertsEmail();
	}

}
