package it.tredi.ecm.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.WorkflowInfo;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.TipoWorkflowEnum;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.TokenService;
import it.tredi.ecm.service.ValutazioneService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.ResponseState;
import it.tredi.ecm.web.bean.ResponseUsername;

@Controller
public class WorkflowController {
	private static Logger LOGGER = LoggerFactory.getLogger(WorkflowController.class);

	@Autowired private TokenService tokenService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private ValutazioneService valutazioneService;
	@Autowired private ProviderService providerService;

	@RequestMapping("/workflow/token/{token}/accreditamento/{accreditamentoId}/stato/{stato}")
	@ResponseBody
	public ResponseState SetStatoFromBonita(@PathVariable("token") String token, @PathVariable("accreditamentoId") Long accreditamentoId, @PathVariable("stato") AccreditamentoStatoEnum stato,
			@RequestParam(required = false) Integer numeroValutazioniNonDate, @RequestParam(required = false) String dataOraScadenzaPossibiltaValutazione,
			@RequestParam(required = false) Boolean eseguitoDaUtente) throws Exception{
		String msgInfo =  " token: " + token + "; accreditamentoId: " + accreditamentoId + "; stato: " + stato;
		if(numeroValutazioniNonDate != null)
			msgInfo += "; numeroValutazioniNonDate: " + numeroValutazioniNonDate;
		else
			msgInfo += "; numeroValutazioniNonDate: null";
		if(dataOraScadenzaPossibiltaValutazione != null)
			msgInfo += "; dataOraScadenzaPossibiltaValutazione: " + dataOraScadenzaPossibiltaValutazione;
		else
			msgInfo += "; dataOraScadenzaPossibiltaValutazione: null";
		if(eseguitoDaUtente != null)
			msgInfo += "; eseguitoDaUtente: " + eseguitoDaUtente;
		else
			msgInfo += "; eseguitoDaUtente: null";
		LOGGER.info(Utils.getLogMessage("GET /workflow/token/{token}/accreditamento/{accreditamentoId}/stato/{stato}" + msgInfo));

		if(!tokenService.checkTokenAndDelete(token)) {
			String msg = "Impossibile trovare il token passato token: " + token;
			LOGGER.error(msg);
			return new ResponseState(true, msg);
		}

		Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
		WorkflowInfo workflowInCorso = accreditamento.getWorkflowInCorso();
		if(workflowInCorso == null)
			throw new Exception("WorkflowController - SetStatoFromBonita: Impossibile ricavare il workflow in corso per l'accreaditamento id: " + accreditamento.getId());
		if(workflowInCorso.getTipo() == TipoWorkflowEnum.ACCREDITAMENTO) {
			if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.PROVVISORIO) {
				if(numeroValutazioniNonDate != null && numeroValutazioniNonDate.intValue() > 0){
					valutazioneService.updateValutazioniNonDate(accreditamentoId);
				}
				if(dataOraScadenzaPossibiltaValutazione != null && !dataOraScadenzaPossibiltaValutazione.isEmpty()) {
					//la data viene passata come stringa in formato yyyy-MM-dd'T'HH:mm:ss
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					Date date = df.parse(dataOraScadenzaPossibiltaValutazione);
					LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
					valutazioneService.dataOraScadenzaPossibilitaValutazioneCRECM(accreditamentoId, ldt);
				}
			} else if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.STANDARD) {
				if(dataOraScadenzaPossibiltaValutazione != null && !dataOraScadenzaPossibiltaValutazione.isEmpty()) {
					//la data viene passata come stringa in formato yyyy-MM-dd'T'HH:mm:ss
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					Date date = df.parse(dataOraScadenzaPossibiltaValutazione);
					LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
					valutazioneService.dataOraScadenzaPossibilitaValutazione(accreditamentoId, ldt);
				}
			}
		}else if(workflowInCorso.getTipo() == TipoWorkflowEnum.VARIAZIONE_DATI) {
			if(numeroValutazioniNonDate != null && numeroValutazioniNonDate.intValue() > 0){
				valutazioneService.updateValutazioniNonDate(accreditamentoId);
			}
			if(dataOraScadenzaPossibiltaValutazione != null && !dataOraScadenzaPossibiltaValutazione.isEmpty()) {
				//la data viene passata come stringa in formato yyyy-MM-dd'T'HH:mm:ss
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				Date date = df.parse(dataOraScadenzaPossibiltaValutazione);
				LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
				valutazioneService.dataOraScadenzaPossibilitaValutazioneCRECM(accreditamentoId, ldt);
			}
		}

		//modifico lo stato
		if(eseguitoDaUtente != null){
			accreditamentoService.changeState(accreditamentoId, stato, eseguitoDaUtente);
		} else {
			accreditamentoService.changeState(accreditamentoId, stato);
		}

		return new ResponseState(false, "Stato modificato");

/*
		Account account = accountRepository.findOneByUsername("provider").orElse(null);
		if(account != null) {
			workflowService.saveOrUpdateBonitaUserByAccount(account);
		}
 */
		//TODO modifica stato della domanda da parte del flusso
		//lo facciamo cosi in modo tale da non dover disabilitare la cache di hibernate
		//accreditamentoService.setStato(accreditamentoId, stato);
	}

	/*** WORKFLOW ***/
	@RequestMapping("/workflow/token/{token}/provider/{providerId}")
	@ResponseBody
	public ResponseState GetProviderUsers(@PathVariable("token") String token, @PathVariable("providerId") Long providerId) throws Exception{
		LOGGER.info(Utils.getLogMessage("GET /workflow/token/{token}/provider/{providerId} token: " + token + "; providerId: " + providerId));

		if(!tokenService.checkTokenAndDelete(token)) {
			String msg = "Impossibile trovare il token passato token: " + token;
			LOGGER.error(msg);
			return new ResponseState(true, msg);
		}
		//recupero la lista degli usernamebonita degli utenti del provider
		Provider provider = providerService.getProvider(providerId);

		if(provider == null) {
			String msg = "Impossibile trovare il provider passato providerId: " + providerId;
			LOGGER.error(msg);
			return new ResponseState(true, msg);
		}

		ResponseUsername responseUsername = new ResponseUsername();
		Set<String> usernames = new HashSet<>();
		//if(provider.getAccount() != null && provider.getAccount().getUsernameWorkflow() != null && !provider.getAccount().getUsernameWorkflow().isEmpty())
		//	usernames.add(provider.getAccount().getUsernameWorkflow());
		if(provider.getAccounts() != null && !provider.getAccounts().isEmpty()) {
			for(Account account : provider.getAccounts()) {
				usernames.add(account.getUsernameWorkflow());
			}
		}

		responseUsername.setUserNames(usernames);
		ResponseState responseState = new ResponseState(false, "Elenco usernames");
		ObjectMapper objMapper = new ObjectMapper();
	    String responseUsernameJson = objMapper.writeValueAsString(responseUsername);
	    responseState.setJsonObject(responseUsernameJson);

		return responseState;

/*
		Account account = accountRepository.findOneByUsername("provider").orElse(null);
		if(account != null) {
			workflowService.saveOrUpdateBonitaUserByAccount(account);
		}
 */
		//TODO modifica stato della domanda da parte del flusso
		//lo facciamo cosi in modo tale da non dover disabilitare la cache di hibernate
		//accreditamentoService.setStato(accreditamentoId, stato);
	}


}
