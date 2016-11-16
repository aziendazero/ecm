package it.tredi.ecm.web;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Comunicazione;
import it.tredi.ecm.dao.entity.ComunicazioneResponse;
import it.tredi.ecm.dao.entity.JsonViewModel;
import it.tredi.ecm.service.ComunicazioneService;
import it.tredi.ecm.service.SecurityAccessService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.ComunicazioneWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.ComunicazioneValidator;

@Controller
public class ComunicazioneController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ComunicazioneController.class);

	private final String MAIN = "comunicazione/comunicazioneDashboard";
	private final String NEW = "comunicazione/comunicazioneNew";
	private final String SHOW = "comunicazione/comunicazioneShow";
	private final String LIST = "comunicazione/comunicazioneList";

	@Autowired private ComunicazioneService comunicazioneService;
	@Autowired private ComunicazioneValidator comunicazioneValidator;
	@Autowired private SecurityAccessService securityAccessService;

	@ModelAttribute("comunicazioneWrapper")
	public ComunicazioneWrapper getComunicazioneWrapperPreRequest(@RequestParam(value="editId", required = false) Long id){
		if(id != null){
			ComunicazioneWrapper comunicazioneWrapper = new ComunicazioneWrapper();
			comunicazioneWrapper.setComunicazione(comunicazioneService.getComunicazioneById(id));
			return comunicazioneWrapper;
		}
		return new ComunicazioneWrapper();
	}

	/*** get pannello controllo comunicazioni ***/

//	@PreAuthorize("@securityAccessServiceImpl.canShowComunicazioni(principal)") TODO
	@RequestMapping("/comunicazione/dashboard")
	public String getComunicazioneDashboard(Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET:  /comunicazione/dashboard"));
		try {
			Long currentAccountId = Utils.getAuthenticatedUser().getAccount().getId();
			model.addAttribute("currentAccountId", currentAccountId);
			model.addAttribute("numeroComunicazioniRicevute", comunicazioneService.countAllComunicazioniRicevuteByAccountId(currentAccountId));
			model.addAttribute("numeroComunicazioniInviate", comunicazioneService.countAllComunicazioniInviateByAccountId(currentAccountId));
			model.addAttribute("numeroComunicazioniBloccate", comunicazioneService.countAllComunicazioniBloccateByAccountId(currentAccountId));
			model.addAttribute("numeroComunicazioniAll", comunicazioneService.countAllComunicazioniByAccountId(currentAccountId));
			model.addAttribute("ultimiMessaggiNonLetti", comunicazioneService.getUltimi10MessaggiNonLetti(currentAccountId));
			model.addAttribute("numeroMessaggiNonLetti", comunicazioneService.countAllMessaggiNonLetti(currentAccountId));
			model.addAttribute("idUltimoMessaggioNonLetto", comunicazioneService.getIdUltimaComunicazioneRicevuta(currentAccountId));
			model.addAttribute("canSendComunicazione", securityAccessService.canSendComunicazioni(Utils.getAuthenticatedUser()));
			//TODO ultima comunicazione response
			LOGGER.info(Utils.getLogMessage("VIEW: " + MAIN));
			return MAIN;
		}
		catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET:  /comunicazione/dashboard"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canSendComunicazioni(principal)")
	@RequestMapping("/comunicazione/new")
	public String sendComunicazione(Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET: /comunicazione/new"));
		try {
			Account mittente = Utils.getAuthenticatedUser().getAccount();
			return goToNewComunicazione(model, prepareComunicazioneWrapperNew(new Comunicazione(mittente)));
		}
		catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /comunicazione/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /comunicazione/dashboard"));
			return "redirect:/comunicazione/dashboard";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canSendComunicazioni(principal)")
	@RequestMapping(value = "/comunicazione/send", method = RequestMethod.POST)
	public String newComunicazione(@ModelAttribute("comunicazioneWrapper") ComunicazioneWrapper comunicazioneWrapper, BindingResult result,
			Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("POST: /comunicazione/send"));
		try {
			//validazione del provider
			comunicazioneValidator.validate(comunicazioneWrapper.getComunicazione(), result, "comunicazione.");

			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				comunicazioneWrapper.setDestinatariDisponibili(comunicazioneService.getAllDestinatariDisponibili(comunicazioneWrapper.getComunicazione().getMittente().getId()));
				LOGGER.info(Utils.getLogMessage("VIEW: " + NEW));
				return NEW;
			}else{
				comunicazioneService.send(comunicazioneWrapper.getComunicazione(), comunicazioneWrapper.getAllegatoComunicazione());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.comunicazione_inviata", "success"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /comunicazione/dashboard"));
				return "redirect:/comunicazione/dashboard";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("POST: /comunicazione/send"),ex);
			model.addAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + NEW));
			return NEW;
		}
	}

//	@PreAuthorize("@securityAccessServiceImpl.canShowComunicazioni(principal)") TODO
	@RequestMapping("/comunicazione/{id}/show")
	public String showComunicazione(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET: /comunicazione/"+ id + "/show"));
		try {
			return goToShowComunicazione(model, prepareComunicazioneWrapperShow(comunicazioneService.getComunicazioneById(id)));
		}
		catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /comunicazione/" + id + "/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /comunicazione/dashboard"));
			return "redirect:/comunicazione/dashboard";
		}
	}

//	@PreAuthorize("@securityAccessServiceImpl.canShowComunicazioni(principal)") TODO
	@RequestMapping("/comunicazione/{id}/read")
	public String readComunicazione(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET: /comunicazione/"+ id + "/read"));
		try {
			comunicazioneService.contrassegnaComeLetta(id);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /comunicazione/dashboard"));
			return "redirect:/comunicazione/dashboard";
		}
		catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /comunicazione/" + id + "/read"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /comunicazione/dashboard"));
			return "redirect:/comunicazione/dashboard";
		}
	}

//	@PreAuthorize("@securityAccessServiceImpl.canSendComunicazioniReply(principal)") TODO
	@RequestMapping(value = "/comunicazione/{id}/reply", method = RequestMethod.POST)
	public String replyComunicazione(@ModelAttribute("comunicazioneWrapper") ComunicazioneWrapper comunicazioneWrapper, @PathVariable Long id, BindingResult result,
			Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("POST: /comunicazione/" + id + "/reply"));
		try {
			//validazione del provider
			comunicazioneValidator.validateReply(comunicazioneWrapper.getRisposta(), result, "risposta.");

			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				model.addAttribute("replyError", true);
				LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
				return SHOW;
			}else{
				comunicazioneService.reply(comunicazioneWrapper.getRisposta(), id, comunicazioneWrapper.getAllegatoRisposta());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.risposta_inviata", "success"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /comunicazione/" + id + "/show" ));
				return "redirect:/comunicazione/" + id + "/show";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("POST: /comunicazione/" + id + "/reply"),ex);
			model.addAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
			return SHOW;
		}
	}

//	@PreAuthorize("@securityAccessServiceImpl.canCloseComunicazione(principal)") TODO
	@RequestMapping("/comunicazione/{id}/close")
	public String closeComunicazione(@PathVariable Long id,	Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET: /comunicazione/" + id + "/close"));
		try {
			//chiusura evento
			comunicazioneService.chiudiComunicazioneById(id);
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.comunicazione_chiusa", "success"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /comunicazione/dashboard"));
			return "redirect:/comunicazione/dashboard";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /comunicazione/" + id + "/close"),ex);
			model.addAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
			return SHOW;
		}
	}

//	@PreAuthorize("@securityAccessServiceImpl.canShowComunicazioni(principal)") TODO
	@RequestMapping("/comunicazione/{tipo}/list")
	public String listaComunicazioniRicevute(@PathVariable String tipo, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET: /comunicazione/" + tipo + "/list"));
		try {
			Set<Comunicazione> listaComunicazioni;
			String tipologiaLista;
			switch (tipo) {
				case "received":
					listaComunicazioni = comunicazioneService.getAllComunicazioniRicevuteByAccount(Utils.getAuthenticatedUser().getAccount());
					tipologiaLista = "label.ricevute";
					break;
				case "sent":
					listaComunicazioni =  comunicazioneService.getAllComunicazioniInviateByAccount(Utils.getAuthenticatedUser().getAccount());
					tipologiaLista = "label.inviate";
					break;
				case "locked":
					listaComunicazioni =  comunicazioneService.getAllComunicazioniChiuseByAccount(Utils.getAuthenticatedUser().getAccount());
					tipologiaLista = "label.chiuse";
					break;
				case "all":
					listaComunicazioni = comunicazioneService.getAllComunicazioniByAccount(Utils.getAuthenticatedUser().getAccount());
					tipologiaLista = "label.storico";
					break;
				case "notRead":
					listaComunicazioni = comunicazioneService.getAllComunicazioniNonLetteByAccount(Utils.getAuthenticatedUser().getAccount());
					tipologiaLista = "label.non_ancora_lette";
					break;
				default: throw new Exception("Tipologia non riconosciuta");
			}
			model.addAttribute("listaComunicazioni", listaComunicazioni);
			model.addAttribute("tipologiaLista", tipologiaLista);
			return LIST;
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /comunicazione/received/list"),ex);
			redirectAttrs.addFlashAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /comunicazione/dashboard"));
			return "redirect:/comunicazione/dashboard";
		}
	}

	// metodi privati di supporto

	private ComunicazioneWrapper prepareComunicazioneWrapperNew(Comunicazione comunicazione) throws JsonProcessingException {
		LOGGER.info(Utils.getLogMessage("prepareComunicazioneWrapperNew() - entering"));
		ComunicazioneWrapper wrapper = new ComunicazioneWrapper(comunicazione);
		wrapper.setDestinatariDisponibili(comunicazioneService.getAllDestinatariDisponibili(comunicazione.getMittente().getId()));
		LOGGER.info(Utils.getLogMessage("prepareComunicazioneWrapperNew() - exiting"));
		return wrapper;
	}

	private String goToNewComunicazione(Model model, ComunicazioneWrapper wrapper) {
		model.addAttribute("comunicazioneWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + NEW));
		return NEW;
	}

	private ComunicazioneWrapper prepareComunicazioneWrapperShow(Comunicazione comunicazione) {
		LOGGER.info(Utils.getLogMessage("prepareComunicazioneWrapperShow() - entering"));
		Account currentUser = Utils.getAuthenticatedUser().getAccount();
		ComunicazioneWrapper wrapper = new ComunicazioneWrapper(comunicazione);
		wrapper.setCanRespond(comunicazioneService.canAccountRespondToComunicazione(currentUser, comunicazione));
		wrapper.setCanCloseComunicazione(comunicazioneService.canAccountCloseComunicazione(currentUser, comunicazione));
		wrapper.setRisposta(new ComunicazioneResponse(currentUser, comunicazione));
		LOGGER.info(Utils.getLogMessage("prepareComunicazioneWrapperShow() - exiting"));
		return wrapper;
	}

	private String goToShowComunicazione(Model model, ComunicazioneWrapper wrapper) {
		model.addAttribute("comunicazioneWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
		return SHOW;
	}
}

