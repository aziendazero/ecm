package it.tredi.ecm.web;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.PersonaService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AccreditamentoWrapper;
import it.tredi.ecm.web.bean.Message;

@Controller
public class AccreditamentoController {
	private static Logger LOGGER = LoggerFactory.getLogger(AccreditamentoController.class);

	@Autowired private PersonaService personaService;
	@Autowired private ProviderService providerService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private EventoService eventoService;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	/***	Get Lista Accreditamenti per provider CORRENTE	***/
	@RequestMapping("/provider/accreditamento/list")
	public String getAllAccreditamentiForCurrentProvider(RedirectAttributes redirectAttrs) throws Exception{
		Utils.logInfo(LOGGER, "GET /provider/accreditamento/list");
		try {
			Provider currentProvider = providerService.getProvider();
			if(currentProvider.isNew()){
				throw new Exception("Provider non registrato");
			}else{
				redirectAttrs.addAttribute("providerId",currentProvider.getId());
				Utils.logInfo(LOGGER, "REDIRECT: /provider/" + currentProvider.getId() + "/accreditamento/list");
				return "redirect:/provider/{providerId}/accreditamento/list";
			}
		}catch (Exception ex){
			Utils.logError(LOGGER, "GET /provider/accreditamento/list", ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			Utils.logInfo(LOGGER, "REDIRECT: /home");
			return "redirect:/home";
		}
	}

	/***	Get Lista Accreditamenti per {providerID}	***/
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/accreditamento/list")
	public String getAllAccreditamentiForProvider(@PathVariable("providerId") Long providerId, Model model, RedirectAttributes redirectAttrs){
		Utils.logInfo(LOGGER, "GET /provider/" + providerId + "/accreditamento/list");
		try {
			Set<Accreditamento> listaAccreditamenti = accreditamentoService.getAllAccreditamentiForProvider(providerId);
			model.addAttribute("accreditamentoList", listaAccreditamenti);
			model.addAttribute("canProviderCreateAccreditamento", accreditamentoService.canProviderCreateAccreditamento(providerId));
			Utils.logInfo(LOGGER, "VIEW: accreditamento/accreditamentoList");
			return "accreditamento/accreditamentoList";
		}catch (Exception ex){
			Utils.logError(LOGGER, "GET /provider/" + providerId + "/accreditamento/list", ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			Utils.logInfo(LOGGER, "REDIRECT: /home");
			return "redirect:/home";
		}
	}

	/***	Get Accreditamento {ID}	***/
	@PreAuthorize("@securityAccessServiceImpl.canShowAccreditamento(principal,#id)")
	@RequestMapping("/accreditamento/{id}")
	public String getAccreditamento(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		Utils.logInfo(LOGGER, "GET /accreditamento/" + id);	
		try {
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(id);
			return goToAccreditamento(model, accreditamento);
		}catch (Exception ex){
			Utils.logError(LOGGER, "GET /accreditamento/" + id, ex);	
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			Utils.logInfo(LOGGER, "VIEW: /accreditamento/accreditamentoList");	
			return "accreditamento/accreditamentoList";
		}
	}

	private String goToAccreditamento(Model model, Accreditamento accreditamento){
		if(accreditamento.isProvvisorio()){
			AccreditamentoWrapper accreditamentoWrapper = prepareAccreditamentoWrapper(accreditamento);
			model.addAttribute("accreditamentoWrapper", accreditamentoWrapper);
		}//TODO gestire differenza con Accreditamento STANDARD
		Utils.logInfo(LOGGER, "VIEW: /accreditamento/accreditamentoShow");	
		return "accreditamento/accreditamentoShow";
	}

	/*** NEW 	Nuova domanda accreditamento per provider corrente	***/
	@RequestMapping("/provider/accreditamento/new")
	public String getNewAccreditamentoForCurrentProvider(Model model, RedirectAttributes redirectAttrs) {
		Utils.logInfo(LOGGER, "GET /provider/accreditamento/new");	
		try{
			Long accreditamentoId = accreditamentoService.getNewAccreditamentoForCurrentProvider().getId();
			redirectAttrs.addAttribute("id", accreditamentoId);
			Utils.logInfo(LOGGER, "REDIRECT: /accreditamento/" + accreditamentoId);	
			return "redirect:/accreditamento/{id}";
		}catch (Exception ex){
			Utils.logError(LOGGER, "GET /provider/accreditamento/new", ex);	
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			Utils.logInfo(LOGGER, "REDIRECT: /provider/accreditamento/list");	
			return "redirect:/provider/accreditamento/list";
		}
	}

	/***	INVIA DOMANDA ALLA SEGRETERIA	***/
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/send")
	public String inviaDomandaAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long providerId, RedirectAttributes redirectAttrs){
		Utils.logInfo(LOGGER, "GET /accreditamento/" + accreditamentoId +"/provider/" + providerId + "/send");	
		try{
			accreditamentoService.inviaDomandaAccreditamento(accreditamentoId);
			redirectAttrs.addAttribute("providerId",providerId);
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.domanda_inviata", "success"));
			Utils.logInfo(LOGGER, "REDIRECT: /provider/" + providerId + "/accreditamento/list");
			return "redirect:/provider/{providerId}/accreditamento/list";
		}catch (Exception ex){
			Utils.logError(LOGGER, "GET /accreditamento/" + accreditamentoId +"/provider/" + providerId + "/send", ex);
			redirectAttrs.addAttribute("id",accreditamentoId);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			Utils.logInfo(LOGGER, "REDIRECT: /accreditamento/" + accreditamentoId);
			return "redirect:/accreditamento/{id}";
		}
	}

	/***	INSERISCI PIANO FORMATIVO	***/
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/insertPianoFormativo")
	public String inserisciPianoFormativo(@PathVariable Long accreditamentoId, @PathVariable Long providerId, RedirectAttributes redirectAttrs){
		Utils.logInfo(LOGGER, "GET /accreditamento/" + accreditamentoId +"/provider/" + providerId + "/insertPianoFormativo");
		try{
			accreditamentoService.inserisciPianoFormativo(accreditamentoId);
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			redirectAttrs.addAttribute("providerId", providerId);
			redirectAttrs.addAttribute("pianoFormativo", LocalDate.now().getYear());
			redirectAttrs.addFlashAttribute("currentTab", "tab4");
			Utils.logInfo(LOGGER, "REDIRECT: /accreditamento/" + accreditamentoId);
			return "redirect:/accreditamento/{accreditamentoId}";
		}catch (Exception ex){
			Utils.logError(LOGGER, "GET /accreditamento/" + accreditamentoId +"/provider/" + providerId + "/insertPianoFormativo", ex);
			redirectAttrs.addAttribute("accreditamentoId",accreditamentoId);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			Utils.logInfo(LOGGER, "REDIRECT: /accreditamento/" + accreditamentoId);
			return "redirect:/accreditamento/{accreditamentoId}";
		}
	}

	/*** METODI PRIVATI PER IL SUPPORTO ***/
	private AccreditamentoWrapper prepareAccreditamentoWrapper(Accreditamento accreditamento){
		Utils.logInfo(LOGGER, "prepareAccreditamentoWrapper(" + accreditamento.getId() + ") - entering");
		AccreditamentoWrapper accreditamentoWrapper = new AccreditamentoWrapper();
		accreditamentoWrapper.setAccreditamento(accreditamento);

		// PROVIDER
		accreditamentoWrapper.setProvider(accreditamento.getProvider());

		//SEDE LEGALE
		Sede sede = accreditamento.getProvider().getSedeLegale();
		accreditamentoWrapper.setSedeLegale( sede != null ? sede : new Sede());

		//SEDE OPERATIVA
		sede = accreditamento.getProvider().getSedeOperativa();
		accreditamentoWrapper.setSedeOperativa( sede != null ? sede : new Sede());

		//DATI ACCREDITAMENTO
		DatiAccreditamento datiAccreditamento = accreditamento.getDatiAccreditamento();
		accreditamentoWrapper.setDatiAccreditamento(datiAccreditamento != null ? datiAccreditamento : new DatiAccreditamento());

		// LEGALE RAPPRESENTANTE E RESPONSABILI
		for(Persona p : accreditamento.getProvider().getPersone()){
			if(p.isLegaleRappresentante())
				accreditamentoWrapper.setLegaleRappresentante(p);
			else if(p.isDelegatoLegaleRappresentante())
				accreditamentoWrapper.setDelegatoLegaleRappresentante(p);
			else if(p.isResponsabileSegreteria())
				accreditamentoWrapper.setResponsabileSegreteria(p);
			else if(p.isResponsabileAmministrativo())
				accreditamentoWrapper.setResponsabileAmministrativo(p);
			else if(p.isResponsabileSistemaInformatico())
				accreditamentoWrapper.setResponsabileSistemaInformatico(p);
			else if(p.isResponsabileQualita())
				accreditamentoWrapper.setResponsabileQualita(p);
			else if(p.isCoordinatoreComitatoScientifico())
				accreditamentoWrapper.setCoordinatoreComitatoScientifico(p);
			else if(p.isComponenteComitatoScientifico())
				accreditamentoWrapper.getComponentiComitatoScientifico().add(p);
		}

		int comitatoScientificoMemebers = accreditamentoWrapper.getComponentiComitatoScientifico().size();
		if(comitatoScientificoMemebers < 4){
			for(int i = comitatoScientificoMemebers; i<4; i++){
				accreditamentoWrapper.getComponentiComitatoScientifico().add(new Persona());
			}
		}

		//ALLEGATI
		Set<String> filesDelProvider = providerService.getFileTypeUploadedByProviderId(accreditamento.getProvider().getId());

		Long providerId = accreditamento.getProvider().getId();
		Set<Professione> professioniSelezionate = (datiAccreditamento != null && !datiAccreditamento.isNew()) ? datiAccreditamento.getProfessioniSelezionate() : new HashSet<Professione>();

		int numeroComponentiComitatoScientifico = personaService.numeroComponentiComitatoScientifico(providerId);
		int numeroProfessionistiSanitarie 		= personaService.numeroComponentiComitatoScientificoConProfessioneSanitaria(providerId);
		int professioniDeiComponenti 			= personaService.numeroProfessioniDistinteDeiComponentiComitatoScientifico(providerId);
		int professioniDeiComponentiAnaloghe 	= (professioniSelezionate.size() > 0) ? personaService.numeroProfessioniDistinteAnalogheAProfessioniSelezionateDeiComponentiComitatoScientifico(providerId, professioniSelezionate) : 0;

		Utils.logDebug(LOGGER, "<*>NUMERO COMPONENTI: " + numeroComponentiComitatoScientifico);
		Utils.logDebug(LOGGER, "<*>NUMERO PROFESSIONISTI SANITARI: " + numeroProfessionistiSanitarie);
		Utils.logDebug(LOGGER, "<*>NUMERO PROFESSIONI DISTINTE: " + professioniDeiComponenti);
		Utils.logDebug(LOGGER, "<*>NUMERO PROFESSIONI ANALOGHE: " + professioniDeiComponentiAnaloghe);

		accreditamentoWrapper.checkStati(numeroComponentiComitatoScientifico, numeroProfessionistiSanitarie, professioniDeiComponenti, professioniDeiComponentiAnaloghe, filesDelProvider);

		//PIANO FORMATIVO
		if(accreditamento.getPianoFormativo() != null)
			accreditamentoWrapper.setListaEventi(eventoService.getAllEventiFromProviderInPianoFormativo(providerId, accreditamento.getPianoFormativo()));

		Utils.logInfo(LOGGER, "prepareAccreditamentoWrapper(" + accreditamento.getId() + ") - exiting");
		return accreditamentoWrapper;
	}
}
