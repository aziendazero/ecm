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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
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

	@RequestMapping("/accreditamento/{accreditamentoId}/stato/{stato}")
	public String SetStatoFromBonita(@PathVariable("accreditamentoId") Long accreditamentoId, @PathVariable("stato") AccreditamentoStatoEnum stato) throws Exception{
		//TODO modifica stato della domanda da parte del flusso
		//lo facciamo cosi in modo tale da non dover disabilitare la cache di hibernate
		//accreditamentoService.setStato(accreditamentoId, stato);
		return "";
	}
	
	/***	Get Lista Accreditamenti per provider CORRENTE	***/
	@RequestMapping("/provider/accreditamento/list")
	public String getAllAccreditamentiForCurrentProvider(RedirectAttributes redirectAttrs) throws Exception{
		LOGGER.info(Utils.getLogMessage("GET /provider/accreditamento/list"));
		try {
			Provider currentProvider = providerService.getProvider();
			if(currentProvider.isNew()){
				throw new Exception("Provider non registrato");
			}else{
				redirectAttrs.addAttribute("providerId",currentProvider.getId());
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + currentProvider.getId() + "/accreditamento/list"));
				return "redirect:/provider/{providerId}/accreditamento/list";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/accreditamento/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	/***	Get Lista Accreditamenti per {providerID}	***/
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/accreditamento/list")
	public String getAllAccreditamentiForProvider(@PathVariable("providerId") Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/accreditamento/list"));
		try {
			Set<Accreditamento> listaAccreditamenti = accreditamentoService.getAllAccreditamentiForProvider(providerId);
			model.addAttribute("accreditamentoList", listaAccreditamenti);
			model.addAttribute("canProviderCreateAccreditamento", accreditamentoService.canProviderCreateAccreditamento(providerId));
			model.addAttribute("providerId", providerId);
			LOGGER.info(Utils.getLogMessage("VIEW: accreditamento/accreditamentoList"));
			return "accreditamento/accreditamentoList";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/accreditamento/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	/***	Get show Accreditamento {ID}	***/
	@PreAuthorize("@securityAccessServiceImpl.canShowAccreditamento(principal,#id)")
	@RequestMapping("/accreditamento/{id}/show")
	public String showAccreditamento(@PathVariable Long id, Model model, @RequestParam(required = false) String tab, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + id + "/show"));
		try {
			if (tab != null) {
				model.addAttribute("currentTab", tab);
				LOGGER.info(Utils.getLogMessage("TAB:" + tab));
			}
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(id);
			return goToAccreditamentoShow(model, accreditamento);
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + id + "/show"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: /accreditamento/accreditamentoList"));
			return "accreditamento/accreditamentoList";
		}
	}

	/***	Get edit Accreditamento {ID}	***/
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#id)")
	@RequestMapping("/accreditamento/{id}/edit")
	public String editAccreditamento(@PathVariable Long id, Model model, @RequestParam(required = false) String tab, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + id + "/edit"));
		try {
			if (tab != null) {
				LOGGER.info(Utils.getLogMessage("TAB:" + tab));
			}
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(id);
			return goToAccreditamentoEdit(model, accreditamento, tab);
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + id + "/edit"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: /accreditamento/accreditamentoList"));
			return "accreditamento/accreditamentoList";
		}
	}

	private String goToAccreditamentoShow(Model model, Accreditamento accreditamento){
		AccreditamentoWrapper accreditamentoWrapper = prepareAccreditamentoWrapperShow(accreditamento);
		model.addAttribute("accreditamentoWrapper", accreditamentoWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: /accreditamento/accreditamentoShow"));
		return "accreditamento/accreditamentoShow";
	}

	private String goToAccreditamentoEdit(Model model, Accreditamento accreditamento, String tab){
		AccreditamentoWrapper accreditamentoWrapper = prepareAccreditamentoWrapperEdit(accreditamento);
		model.addAttribute("accreditamentoWrapper", accreditamentoWrapper);
		selectCorrectTab(tab, accreditamentoWrapper, model);
		LOGGER.info(Utils.getLogMessage("VIEW: /accreditamento/accreditamentoEdit"));
		return "accreditamento/accreditamentoEdit";
	}

	//Check per capire in che tab ritornare e con quale messaggio
	private void selectCorrectTab(String tab, AccreditamentoWrapper accreditamentoWrapper, Model model){
		if(tab != null) {
			switch(tab) {

				case "tab1":	model.addAttribute("currentTab", "tab1");
							 	break;

				case "tab2":	if(accreditamentoWrapper.isSezione1Stato()) {
									model.addAttribute("currentTab", "tab2");
									if (accreditamentoWrapper.getResponsabileSegreteria() == null &&
									    accreditamentoWrapper.getResponsabileAmministrativo() == null &&
									    accreditamentoWrapper.getResponsabileSistemaInformatico() == null &&
									    accreditamentoWrapper.getResponsabileQualita() == null) {
										model.addAttribute("message", new Message("message.warning", "message.legale_non_piu_modificabile", "warning"));
									}
								}
								else {
									model.addAttribute("currentTab", "tab1");
									model.addAttribute("message", new Message("message.warning", "message.compilare_tab1", "warning"));
								}
								break;

				case "tab3":	if(accreditamentoWrapper.isSezione2Stato())
									model.addAttribute("currentTab", "tab3");
								else {
									if(accreditamentoWrapper.isSezione1Stato()) {
										model.addAttribute("currentTab", "tab2");
										model.addAttribute("message", new Message("message.warning", "message.compilare_tab2", "warning"));
									}
									else {
										model.addAttribute("currentTab", "tab1");
										model.addAttribute("message", new Message("message.warning", "message.compilare_tab1", "warning"));
									}
								}
								break;

				case "tab4":  	if(accreditamentoWrapper.isCompleta())
									model.addAttribute("currentTab", "tab4");
								else {
									if(accreditamentoWrapper.isSezione2Stato()) {
										model.addAttribute("currentTab", "tab3");
										model.addAttribute("message", new Message("message.warning", "message.compilare_altre_tab", "warning"));
									}
									else {
										if(accreditamentoWrapper.isSezione1Stato()) {
											model.addAttribute("currentTab", "tab2");
											model.addAttribute("message", new Message("message.warning", "message.compilare_tab2", "warning"));
										}
										else {
											model.addAttribute("currentTab", "tab1");
											model.addAttribute("message", new Message("message.warning", "message.compilare_tab1", "warning"));
										}
									}
								}
								break;

				default:		break;
			}
		}
	}

	/*** NEW 	Nuova domanda accreditamento per provider corrente	***/
	@RequestMapping("/provider/accreditamento/new")
	public String getNewAccreditamentoForCurrentProvider(Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /provider/accreditamento/new"));
		try{
			Long accreditamentoId = accreditamentoService.getNewAccreditamentoForCurrentProvider().getId();
			redirectAttrs.addAttribute("id", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
			return "redirect:/accreditamento/{id}/edit";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/accreditamento/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/accreditamento/list"));
			return "redirect:/provider/accreditamento/list";
		}
	}

	/*** NEW 	Nuova domanda accreditamento per provider	***/
	@PreAuthorize("@securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/accreditamento/new")
	public String getNewAccreditamentoForCurrentProvider(@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId +"/accreditamento/new"));
		try{
			Long accreditamentoId = accreditamentoService.getNewAccreditamentoForProvider(providerId).getId();
			redirectAttrs.addAttribute("id", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
			return "redirect:/accreditamento/{id}/edit";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId +"/accreditamento/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addAttribute("providerId",providerId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/{providerId}/accreditamento/list"));
			return "redirect:/provider/{providerId}/accreditamento/list";
		}
	}

	/***	INVIA DOMANDA ALLA SEGRETERIA	***/
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/send")
	public String inviaDomandaAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long providerId, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/" + providerId + "/send"));
		try{
			accreditamentoService.inviaDomandaAccreditamento(accreditamentoId);
			redirectAttrs.addAttribute("providerId",providerId);
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.domanda_inviata", "success"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/accreditamento/list"));
			return "redirect:/provider/{providerId}/accreditamento/list";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/" + providerId + "/send"),ex);
			redirectAttrs.addAttribute("id",accreditamentoId);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId));;
			return "redirect:/accreditamento/{id}";
		}
	}

	/***	INSERISCI PIANO FORMATIVO	***/
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/insertPianoFormativo")
	public String inserisciPianoFormativo(@PathVariable Long accreditamentoId, @PathVariable Long providerId, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/" + providerId + "/insertPianoFormativo"));
		try{
			accreditamentoService.inserisciPianoFormativo(accreditamentoId);
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			redirectAttrs.addAttribute("providerId", providerId);
			redirectAttrs.addAttribute("pianoFormativo", LocalDate.now().getYear());
			redirectAttrs.addFlashAttribute("currentTab", "tab4");
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
			return "redirect:/accreditamento/{accreditamentoId}/edit";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/" + providerId + "/insertPianoFormativo"),ex);
			redirectAttrs.addAttribute("accreditamentoId",accreditamentoId);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
			return "redirect:/accreditamento/{accreditamentoId}/edit";
		}
	}

	/*** METODI PRIVATI PER IL SUPPORTO ***/
	private AccreditamentoWrapper prepareAccreditamentoWrapperEdit(Accreditamento accreditamento){
		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoWrapper(" + accreditamento.getId() + ") - entering"));
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

		//ALLEGATI
		Set<String> filesDelProvider = providerService.getFileTypeUploadedByProviderId(accreditamento.getProvider().getId());

		Long providerId = accreditamento.getProvider().getId();
		Set<Professione> professioniSelezionate = (datiAccreditamento != null && !datiAccreditamento.isNew()) ? datiAccreditamento.getProfessioniSelezionate() : new HashSet<Professione>();

		int numeroComponentiComitatoScientifico = personaService.numeroComponentiComitatoScientifico(providerId);
		int numeroProfessionistiSanitarie 		= personaService.numeroComponentiComitatoScientificoConProfessioneSanitaria(providerId);
		int professioniDeiComponenti 			= personaService.numeroProfessioniDistinteDeiComponentiComitatoScientifico(providerId);
		int professioniDeiComponentiAnaloghe 	= (professioniSelezionate.size() > 0) ? personaService.numeroProfessioniDistinteAnalogheAProfessioniSelezionateDeiComponentiComitatoScientifico(providerId, professioniSelezionate) : 0;

		LOGGER.debug(Utils.getLogMessage("<*>NUMERO COMPONENTI: " + numeroComponentiComitatoScientifico));
		LOGGER.debug(Utils.getLogMessage("<*>NUMERO PROFESSIONISTI SANITARI: " + numeroProfessionistiSanitarie));
		LOGGER.debug(Utils.getLogMessage("<*>NUMERO PROFESSIONI DISTINTE: " + professioniDeiComponenti));
		LOGGER.debug(Utils.getLogMessage("<*>NUMERO PROFESSIONI ANALOGHE: " + professioniDeiComponentiAnaloghe));

		accreditamentoWrapper.checkStati(numeroComponentiComitatoScientifico, numeroProfessionistiSanitarie, professioniDeiComponenti, professioniDeiComponentiAnaloghe, filesDelProvider);

		//PIANO FORMATIVO

		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoWrapper(" + accreditamento.getId() + ") - exiting"));
		return accreditamentoWrapper;
	}

	private AccreditamentoWrapper prepareAccreditamentoWrapperShow(Accreditamento accreditamento){
		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoWrapperShow(" + accreditamento.getId() + ") - entering"));
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

		Long providerId = accreditamento.getProvider().getId();

		//ALLEGATI
		Set<String> filesDelProvider = providerService.getFileTypeUploadedByProviderId(accreditamento.getProvider().getId());

		Set<Professione> professioniSelezionate = (datiAccreditamento != null && !datiAccreditamento.isNew()) ? datiAccreditamento.getProfessioniSelezionate() : new HashSet<Professione>();

		int numeroComponentiComitatoScientifico = personaService.numeroComponentiComitatoScientifico(providerId);
		int numeroProfessionistiSanitarie 		= personaService.numeroComponentiComitatoScientificoConProfessioneSanitaria(providerId);
		int professioniDeiComponenti 			= personaService.numeroProfessioniDistinteDeiComponentiComitatoScientifico(providerId);
		int professioniDeiComponentiAnaloghe 	= (professioniSelezionate.size() > 0) ? personaService.numeroProfessioniDistinteAnalogheAProfessioniSelezionateDeiComponentiComitatoScientifico(providerId, professioniSelezionate) : 0;

		LOGGER.debug(Utils.getLogMessage("<*>NUMERO COMPONENTI: " + numeroComponentiComitatoScientifico));
		LOGGER.debug(Utils.getLogMessage("<*>NUMERO PROFESSIONISTI SANITARI: " + numeroProfessionistiSanitarie));
		LOGGER.debug(Utils.getLogMessage("<*>NUMERO PROFESSIONI DISTINTE: " + professioniDeiComponenti));
		LOGGER.debug(Utils.getLogMessage("<*>NUMERO PROFESSIONI ANALOGHE: " + professioniDeiComponentiAnaloghe));

		accreditamentoWrapper.checkStati(numeroComponentiComitatoScientifico, numeroProfessionistiSanitarie, professioniDeiComponenti, professioniDeiComponentiAnaloghe, filesDelProvider);

		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoWrapperShow(" + accreditamento.getId() + ") - exiting"));
		return accreditamentoWrapper;
	}
}
