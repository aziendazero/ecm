package it.tredi.ecm.web;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Comunicazione;
import it.tredi.ecm.dao.entity.ComunicazioneResponse;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.service.ComunicazioneService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.SecurityAccessService;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.ComunicazioneWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.RicercaComunicazioneWrapper;
import it.tredi.ecm.web.validator.ComunicazioneValidator;

@Controller
@SessionAttributes(value = {"returnLink","listaComunicazioni"})
public class ComunicazioneController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ComunicazioneController.class);

	private final String MAIN = "comunicazione/comunicazioneDashboard";
	private final String NEW = "comunicazione/comunicazioneNew";
	private final String SHOW = "comunicazione/comunicazioneShow";
	private final String LIST = "comunicazione/comunicazioneList";
	private final String RICERCA = "ricerca/ricercaComunicazione";

	@Autowired private ComunicazioneService comunicazioneService;
	@Autowired private ComunicazioneValidator comunicazioneValidator;
	@Autowired private SecurityAccessService securityAccessService;
	@Autowired private ProviderService providerService;
	@Autowired private EventoService eventoService;

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
	public String getComunicazioneDashboard(Model model, RedirectAttributes redirectAttrs, SessionStatus sessionStatus) {
		LOGGER.info(Utils.getLogMessage("GET:  /comunicazione/dashboard"));
		try {
			//cleanup della sessione
//			sessionStatus.setComplete();
			Long currentAccountId = Utils.getAuthenticatedUser().getAccount().getId();
			model.addAttribute("currentAccountId", currentAccountId);
			model.addAttribute("numeroComunicazioniRicevute", comunicazioneService.countAllComunicazioniRicevuteByAccountId(currentAccountId));
			model.addAttribute("numeroComunicazioniInviate", comunicazioneService.countAllComunicazioniInviateByAccountId(currentAccountId));
			model.addAttribute("numeroComunicazioniBloccate", comunicazioneService.countAllComunicazioniChiuseByAccountId(currentAccountId));
			model.addAttribute("numeroComunicazioniAll", comunicazioneService.countAllComunicazioniStoricoByAccountId(currentAccountId));
			model.addAttribute("numeroComunicazioniNonRisposte", comunicazioneService.countAllComunicazioniNonRisposteByAccountId(currentAccountId));
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

//			if(model.asMap().containsKey("returnLink")) {
//				String returnLink = (String) model.asMap().get("returnLink");
//				updateComunicazioneList(listaComunicazioni, id, session);
//				return "redirect:"+returnLink;
//			}
//			else {
				LOGGER.info(Utils.getLogMessage("REDIRECT: /comunicazione/dashboard"));
				return "redirect:/comunicazione/dashboard";
//			}
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
	public String closeComunicazione(@PathVariable Long id,	Model model, RedirectAttributes redirectAttrs,
			@ModelAttribute("listaComunicazioni") Set<Comunicazione> listaComunicazioni,
			HttpSession session) {
		LOGGER.info(Utils.getLogMessage("GET: /comunicazione/" + id + "/close"));
		try {
			//chiusura evento
			comunicazioneService.chiudiComunicazioneById(id);
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.comunicazione_chiusa", "success"));
			if(model.asMap().containsKey("returnLink")) {
				String returnLink = (String) model.asMap().get("returnLink");
				updateComunicazioneList(listaComunicazioni, id, session);
				return "redirect:"+returnLink;
			}
			else {
				LOGGER.info(Utils.getLogMessage("REDIRECT: /comunicazione/dashboard"));
				return "redirect:/comunicazione/dashboard";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /comunicazione/" + id + "/close"),ex);
			model.addAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
			return SHOW;
		}
	}

//	@PreAuthorize("@securityAccessServiceImpl.canShowComunicazioni(principal)") TODO
	@RequestMapping("/comunicazione/{tipo}/list")
	public String listaComunicazioniRicevute(@PathVariable String tipo, Model model, RedirectAttributes redirectAttrs,
			HttpServletRequest request) {
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
				case "nonRisposte":
					listaComunicazioni = comunicazioneService.getAllComunicazioniNonRisposteByAccount(Utils.getAuthenticatedUser().getAccount());
					tipologiaLista = "label.non_ancora_lette";
					break;
				case "cerca":
					listaComunicazioni = (Set<Comunicazione>) model.asMap().get("listaComunicazioni");
					tipologiaLista = "label.esito_ricerca";
					break;
				default: throw new Exception("Tipologia non riconosciuta");
			}
			model.addAttribute("listaComunicazioni", listaComunicazioni);
			model.addAttribute("tipologiaLista", tipologiaLista);
			model.addAttribute("tipo", tipo);
			model.addAttribute("returnLink", "/comunicazione/dashboard");

			return goToComunicazioneList(request, model);
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /comunicazione/received/list"),ex);
			redirectAttrs.addFlashAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /comunicazione/dashboard"));
			return "redirect:/comunicazione/dashboard";
		}
	}

//	@PreAuthorize("@securityAccessServiceImpl.canShowComunicazioniProvider(principal)") TODO
	@RequestMapping("/provider/{providerId}/comunicazione/list")
	public String listaComunicazioniRicevuteProvider(@PathVariable Long providerId, @RequestParam(required = false) Boolean nonRisposte,
			Model model, RedirectAttributes redirectAttrs, HttpServletRequest request) {
		LOGGER.info(Utils.getLogMessage("GET: /provider/" + providerId + "/comunicazione/list"));
		try {
			Provider provider = providerService.getProvider(providerId);

			if(model.asMap().get("listaComunicazioni") == null){
				if(nonRisposte != null && nonRisposte == true) {
					model.addAttribute("listaComunicazioni", comunicazioneService.getAllComunicazioniNonRisposteFromProviderBySegreteria(provider));
					model.addAttribute("tipologiaLista", "label.non_risposte_al_provider");
				}
				else {
					model.addAttribute("listaComunicazioni", comunicazioneService.getAllComunicazioniByProvider(provider));
					model.addAttribute("tipologiaLista", "label.comunicazioni_con_provider");
				}
			}
			else
				model.addAttribute("tipologiaLista", "label.comunicazioni_con_provider");

			if(Utils.getAuthenticatedUser().isSegreteria())
				model.addAttribute("returnLink", "/provider/list");
			else
				model.addAttribute("returnLink", "/home");

			return goToComunicazioneList(request, model);
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /provider/" + providerId + "/comunicazione/list"),ex);
			redirectAttrs.addFlashAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/list"));
			return "redirect:/provider/list";
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
		wrapper.setMappaVisibilitaResponse(comunicazioneService.createMappaVisibilitaResponse(currentUser, comunicazione));
		LOGGER.info(Utils.getLogMessage("prepareComunicazioneWrapperShow() - exiting"));
		return wrapper;
	}

	private String goToShowComunicazione(Model model, ComunicazioneWrapper wrapper) {
		model.addAttribute("comunicazioneWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
		return SHOW;
	}

	@RequestMapping("/comunicazione/ricerca")
	public String ricercaComunicazione(Model model,RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /comunicazione/ricerca"));
		try {
			RicercaComunicazioneWrapper wrapper = prepareRicercaComunicazioneWrapper();

			CurrentUser currentUser = Utils.getAuthenticatedUser();
			if(currentUser.isProvider()){
				wrapper.setProviderId(currentUser.getAccount().getProvider().getId());
			}else{
				wrapper.setProviderId(null);
			}

			model.addAttribute("ricercaComunicazioneWrapper", wrapper);
			LOGGER.info(Utils.getLogMessage("VIEW: " + RICERCA));
			return RICERCA;
		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /comunicazione/ricerca"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/comunicazione/ricerca", method = RequestMethod.POST)
	public String executeRicercaComunicazione(@ModelAttribute("ricercaComunicazioneWrapper") RicercaComunicazioneWrapper wrapper,
									BindingResult result, RedirectAttributes redirectAttrs, Model model, HttpServletRequest request){
		LOGGER.info(Utils.getLogMessage("POST /comunicazione/ricerca"));
		try {

			String returnRedirect = "";

			if(wrapper.getProviderId() != null){
				wrapper.setCampoIdProvider(wrapper.getProviderId());
				returnRedirect = "redirect:/provider/" + wrapper.getProviderId() + "/comunicazione/list";
			}else{
				returnRedirect = "redirect:/comunicazione/cerca/list";
			}


			Set<Comunicazione> listaComunicazioni = new HashSet<>();
			listaComunicazioni.addAll(comunicazioneService.cerca(wrapper));

			redirectAttrs.addFlashAttribute("listaComunicazioni", listaComunicazioni);

			return returnRedirect;
		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /comunicazione/ricerca"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/comunicazione/ricerca";
		}
	}

	private RicercaComunicazioneWrapper prepareRicercaComunicazioneWrapper(){
		RicercaComunicazioneWrapper wrapper = new RicercaComunicazioneWrapper();
		return wrapper;
	}

	@RequestMapping("/comunicazione/{comunicazioneId}/evento/{codiceEvento}/redirect")
	public String gotoEventoFromComunicazione(@PathVariable("comunicazioneId") Long comunicazioneId,
			@PathVariable("codiceEvento") String codiceEvento, @ModelAttribute("comunicazioneWrapper") ComunicazioneWrapper wrapper, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /comunicazione/"+comunicazioneId+"/evento/"+codiceEvento+"/redirect"));
		try {
			Account user = Utils.getAuthenticatedUser().getAccount();
			Evento evento = eventoService.getEventoByCodiceIdentificativo(codiceEvento);
			if(evento == null) {
				model.addAttribute("message", new Message("message.errore", "message.errore_evento_non_esiste", "error"));
				return goToShowComunicazione(model, prepareComunicazioneWrapperShow(comunicazioneService.getComunicazioneById(comunicazioneId)));
			}
			else {
				Provider providerEvento = evento.getProvider();
				Provider providerUser = null;
				if(user.isProviderVisualizzatore())
					providerUser = user.getProvider();
				if(user.isSegreteria() || providerEvento.equals(providerUser)) {
					LOGGER.info(Utils.getLogMessage("REDIRECT /evento/"+evento.getId()));
					return "redirect:/evento/"+evento.getId();
				}
				else {
					LOGGER.info(Utils.getLogMessage("REDIRECT /comunicazione/"+comunicazioneId+"/show"));
					redirectAttrs.addFlashAttribute("message", new Message("message.warning", "message.warning_no_privilegi_necessari_risorsa", "warnign"));
					return "redirect:/comunicazione/dashboard";
				}
			}
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /comunicazione/"+comunicazioneId+"/evento/"+codiceEvento+"/redirect"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/comunicazione/dashboard";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/comunicazione/notRead/list/archiviaSelezionate", method = RequestMethod.POST)
	public void archiviaSelezionate(@RequestBody Set<Long> ids) {
		try{
			LOGGER.info(Utils.getLogMessage("POST /comunicazione/notRead/list/archiviaSelezionate"));
			comunicazioneService.archiviaSelezionati(ids);
			LOGGER.info(Utils.getLogMessage("REDIRECT success: /comunicazione/notRead/list"));
		}catch (Exception ex){
			LOGGER.info(Utils.getLogMessage("Errore: /comunicazione/notRead/list/archiviaSelezionate"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
		}
	}

	private String goToComunicazioneList(HttpServletRequest request, Model model) {
		LOGGER.info(Utils.getLogMessage("VIEW: "+LIST));
		//tasto indietro
		String returnLink = request.getRequestURI().substring(request.getContextPath().length());
	    if(request.getQueryString() != null)
	    	returnLink+="?"+request.getQueryString();
	    model.addAttribute("returnLink", returnLink);
		return LIST;
	}

	//update della comunicazione modificata nella lista in sessione
	private void updateComunicazioneList(Set<Comunicazione> listaComunicazioni, Long comunicazioneId, HttpSession session) {
		Comunicazione comunicazioneToUpdate = comunicazioneService.getComunicazioneById(comunicazioneId);
		//se il provider è nella lista in sessione la aggiorna
		if(listaComunicazioni.remove(comunicazioneToUpdate)) {
			listaComunicazioni.add(comunicazioneToUpdate);
			session.setAttribute("listaComunicazioni", listaComunicazioni);
		}
	}
}

