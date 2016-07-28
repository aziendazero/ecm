package it.tredi.ecm.web;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.service.AnagraficaService;
import it.tredi.ecm.service.FieldEditabileService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.PersonaService;
import it.tredi.ecm.service.ProfessioneService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.PersonaWrapper;
import it.tredi.ecm.web.validator.PersonaValidator;

@Controller
public class PersonaController {
	private static Logger LOGGER = LoggerFactory.getLogger(PersonaController.class);

	private final String EDIT = "persona/personaEdit";
	private final String SHOW = "persona/personaShow";

	@Autowired private PersonaService personaService;
	@Autowired private AnagraficaService anagraficaService;
	@Autowired private ProviderService providerService;
	@Autowired private ProfessioneService professioneService;
	@Autowired private FileService fileService;
	@Autowired private FieldEditabileService fieldEditabileService;
	@Autowired private PersonaValidator personaValidator;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/anagraficaList")
	@ResponseBody
	public Set<Anagrafica>getAnagraficheRegistrateDalProvider(@PathVariable Long providerId){
		return personaService.getAllAnagraficheByProviderId(providerId);
	}

	@ModelAttribute("professioneList")
	public Set<Professione> getAllProfessioni(){
		return professioneService.getAllProfessioni();
	}

	@ModelAttribute("personaWrapper")
	public PersonaWrapper getPersonaWrapper(@RequestParam(value="editId",required = false) Long id,
			@RequestParam(value="editId_Anagrafica",required = false) Long anagraficaId){
		if(id != null || anagraficaId != null){
			Persona persona = (id != null) ? personaService.getPersona(id) : new Persona();
			boolean isLookup = false;
			if(anagraficaId == null){
				//NUOVA ANGARFICA
				persona.setAnagrafica(null);
				isLookup = false;
			}else if(!anagraficaId.equals(persona.getAnagrafica().getId())){
				//LOOKUP ANAGRAFICA ESISTENTE
				persona.setAnagrafica(anagraficaService.getAnagrafica(anagraficaId));
				isLookup = true;
			}

			return preparePersonaWrapperEdit(persona, isLookup);
		}
		return new PersonaWrapper();
	}

	/***	NUOVA PERSONA ***/
	/* (passando ruolo e providerId) */
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/new")
	public String newPersona(@PathVariable Long accreditamentoId, @PathVariable Long providerId, Model model,
			@RequestParam(name="ruolo", required = true) String ruolo, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/new"));
		try {
			return goToEdit(model, preparePersonaWrapperEdit(createPersona(providerId, ruolo), accreditamentoId, providerId, false));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId));
			return "redirect:/accreditamento/{accreditamentoId}";
		}
	}

	/***	SET ANAGRAFICA	***/
	/*
	 * Agganciamo una Angrafica diversa alla Persona
	 *
	 * */
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/{ruolo}/setAnagrafica")
	public String setAnagrafica(@PathVariable Long accreditamentoId, @PathVariable Long providerId, Model model,
									@PathVariable("ruolo") String ruolo,
										@RequestParam(name="anagraficaId", required = false) Long anagraficaId){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/" + ruolo + "/setAnagrafica"));
		try {
			Persona persona = null;
			boolean isLookup = false;

			//Ogni provider HA più persone con il ruolo COMPONENTE_COMITATO_SCIENTIFICO...mentre per tutti gli altri ruoli esiste solo 1 persona
			if(!ruolo.equals(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO.name()))
				persona = personaService.getPersonaByRuolo(Ruolo.valueOf(ruolo), providerId);

			if(persona == null){
				persona = createPersona(providerId, ruolo);
			}
			else {
				persona.setProfessione(new Professione());
			}
			if(anagraficaId == null){
				persona.setAnagrafica(new Anagrafica());
				isLookup = false;
			}else{
				persona.setAnagrafica(anagraficaService.getAnagrafica(anagraficaId));
				isLookup = true;
			}
			return goToEdit(model, preparePersonaWrapperEdit(persona, accreditamentoId, providerId, isLookup));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/" + ruolo + "/setAnagrafica"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
			return EDIT;
		}
	}

	/***	EDIT PERSONA ***/
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/{id}/edit")
	public String editPersona(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long id, Model model, HttpServletRequest req){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/" + id + "/edit"));
		try {
			Persona persona = personaService.getPersona(id);
			if(persona == null){
				persona = createPersona(providerId);
			}
			return goToEdit(model, preparePersonaWrapperEdit(persona, accreditamentoId, providerId, false));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/" + id + "/edit"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
			return EDIT;
		}
	}

	/***	SHOW PERSONA ***/
	@PreAuthorize("@securityAccessServiceImpl.canShowAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/{id}/show")
	public String showPersona(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long id,
			@RequestParam(required = false) String from, Model model, RedirectAttributes redirectAttrs, HttpServletRequest req){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/" + id + "/show"));
		try {
			Persona persona = personaService.getPersona(id);
			if(from != null) {
				redirectAttrs.addFlashAttribute("mode", from);
				return "redirect:/accreditamento/"+ accreditamentoId + "/provider/" + providerId + "/persona/" + id +"/show";
			}
			if(model.containsAttribute("mode")) {
				return goToShowFromEdit(model, preparePersonaWrapperShow(persona, accreditamentoId));
			}
			return goToShow(model, preparePersonaWrapperShow(persona, accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/" + id + "/show"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/show"));
			return "redirect: /accreditamento/" + accreditamentoId + "/show";
		}
	}

	/***	SAVE PERSONA ***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/{providerId}/persona/save", method = RequestMethod.POST)
	public String savePersona(@ModelAttribute("personaWrapper") PersonaWrapper personaWrapper, BindingResult result,
			RedirectAttributes redirectAttrs, Model model, @PathVariable Long accreditamentoId, @PathVariable Long providerId){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/save"));
		try {
			if(personaWrapper.getPersona().isNew()){
				Persona persona = personaWrapper.getPersona();
				persona.setRuolo(personaWrapper.getRuolo());
				Provider provider = providerService.getProvider(personaWrapper.getProviderId());
				persona.setProvider(provider);
			}

			//TODO getFile da testare se funziona anche senza reload
			//reload degli allegati perchè se è stato fatto un upload ajax...il wrapper non ha i byte[] aggiornati e nemmeno il ref a personaId
			for(File file : personaWrapper.getFiles()){
				if(file != null && !file.isNew()){
					if(file.isCV())
						personaWrapper.setCv(fileService.getFile(file.getId()));
					else if(file.isDELEGA())
						personaWrapper.setDelega(fileService.getFile(file.getId()));
					else if(file.isATTONOMINA())
						personaWrapper.setAttoNomina(fileService.getFile(file.getId()));
				}
			}

			personaValidator.validate(personaWrapper.getPersona(), result, "persona.", personaWrapper.getFiles());

			try{
				if(result.hasErrors()){
					model.addAttribute("message", new Message("message.errore", "message.inserire_campi_required", "error"));
					model.addAttribute("returnLink", calcolaLink(personaWrapper, "edit"));
					LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
					return EDIT;
				}else{
					
					if(personaWrapper.getPersona().isNew() && !personaWrapper.getPersona().isComponenteComitatoScientifico()){
						Persona persona = personaService.getPersonaByRuolo(personaWrapper.getPersona().getRuolo(), providerId);
						if(persona != null)
							personaService.delete(persona.getId());
					}
					
					boolean insertFieldEditabile = (personaWrapper.getPersona().isNew()) ? true : false; 
					personaService.save(personaWrapper.getPersona());

					//inserimento nuova persona in Domanda di Accreditamento
					//inseriamo gli IdEditabili (con riferimento all'id nel caso di multi-istanza)
					if(insertFieldEditabile){
						SubSetFieldEnum subset = Utils.getSubsetFromRuolo(personaWrapper.getPersona().getRuolo());
						if(personaWrapper.getPersona().isComponenteComitatoScientifico())
							fieldEditabileService.insertFieldEditabileForAccreditamento(accreditamentoId, personaWrapper.getPersona().getId(), subset, IdFieldEnum.getAllForSubset(subset));
						else
							fieldEditabileService.insertFieldEditabileForAccreditamento(accreditamentoId, null, subset, IdFieldEnum.getAllForSubset(subset));
					}

					// Durante la compilazione della domanda di accreditamento, se si inizia l'inserimento dei responsabili non e' piu'
					// consentita la modifica del legale rappresentante
					if(personaWrapper.getPersona().isResponsabileSegreteria() || personaWrapper.getPersona().isResponsabileAmministrativo() ||
							personaWrapper.getPersona().isComponenteComitatoScientifico() || personaWrapper.getPersona().isCoordinatoreComitatoScientifico()||
							personaWrapper.getPersona().isResponsabileSistemaInformatico() || personaWrapper.getPersona().isResponsabileQualita())
						fieldEditabileService.removeFieldEditabileForAccreditamento(accreditamentoId, null, SubSetFieldEnum.LEGALE_RAPPRESENTANTE);

					redirectAttrs.addAttribute("accreditamentoId", personaWrapper.getAccreditamentoId());
					redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.inserito('"+ personaWrapper.getPersona().getRuolo().getNome() +"')", "success"));

					if(!personaWrapper.getPersona().isLegaleRappresentante() && !personaWrapper.getPersona().isDelegatoLegaleRappresentante())
						redirectAttrs.addFlashAttribute("currentTab","tab2");
					LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
					return "redirect:/accreditamento/{accreditamentoId}/edit";
				}
			}catch(Exception ex){
				LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/save"),ex);
				model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/save"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
			return EDIT;
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/{personaId}/delete")
	public String removeComponenteComitatoScientifico(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long personaId,
														Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/" + personaId + "/delete"));
		try{
			personaService.delete(personaId);

			//rimozione persona multi-istanza dalla Domanda di Accreditamento
			//rimuoviamo gli IdEditabili
			fieldEditabileService.removeFieldEditabileForAccreditamento(accreditamentoId, personaId, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO);

			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.componente_comitato_eliminato", "success"));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/" + personaId + "/delete"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
		}

		redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
		redirectAttrs.addFlashAttribute("currentTab","tab2");
		LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
		return "redirect:/accreditamento/{accreditamentoId}/edit";
	}

	/***	Metodi privati di supporto	***/
	private Persona createPersona(Long providerId){
		Persona persona = new Persona();
		return persona;
	}

	private Persona createPersona(Long providerId, String ruolo){
		Persona persona = createPersona(providerId);
		persona.setRuolo(Ruolo.valueOf(ruolo));
		return persona;
	}

	private String goToEdit(Model model, PersonaWrapper personaWrapper){
		model.addAttribute("personaWrapper", personaWrapper);
		model.addAttribute("returnLink", calcolaLink(personaWrapper, "edit"));
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	private String goToShow(Model model, PersonaWrapper personaWrapper){
		model.addAttribute("personaWrapper", personaWrapper);
		model.addAttribute("returnLink", calcolaLink(personaWrapper, "show"));
		LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
		return SHOW;
	}

	private String goToShowFromEdit(Model model, PersonaWrapper personaWrapper) {
		model.addAttribute("personaWrapper", personaWrapper);
		model.addAttribute("returnLink", calcolaLink(personaWrapper, "edit"));
		LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
		return SHOW;
	}

	private String calcolaLink(PersonaWrapper wrapper, String mode) {
		String tab;

		if(wrapper.getRuolo().equals(Ruolo.LEGALE_RAPPRESENTANTE) || wrapper.getRuolo().equals(Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE))
			tab = "tab1";
		else
			tab = "tab2";

		return "/accreditamento/" + wrapper.getAccreditamentoId() + "/" + mode + "?tab=" + tab;
	}

	@RequestMapping(value= "/personaRedirect/{personaId}/{target}/{targetId}/{mode}")
	public String personaRedirect(@PathVariable Long personaId, @PathVariable String target, @PathVariable Long targetId, @PathVariable String mode, RedirectAttributes redirectAttrs) {
		Persona persona = personaService.getPersona(personaId);
		if(!persona.isLegaleRappresentante() && !persona.isDelegatoLegaleRappresentante())
			redirectAttrs.addFlashAttribute("currentTab", "tab2");
		else
			redirectAttrs.addFlashAttribute("currentTab", "tab1");
		return "redirect:/" + target + "/" + targetId + "/" + mode ;
	}

	private PersonaWrapper preparePersonaWrapperEdit(Persona persona, boolean isLookup){
		return preparePersonaWrapperEdit(persona,0,0, isLookup);
	}

	private PersonaWrapper preparePersonaWrapperEdit(Persona persona, long accreditamentoId, long providerId, boolean isLookup){
		LOGGER.info(Utils.getLogMessage("preparePersonaWrapperEdit(" + persona.getId() + "," + accreditamentoId +","+ providerId + "," + isLookup + ") - entering"));
		PersonaWrapper personaWrapper = new PersonaWrapper();

		personaWrapper.setPersona(persona);
		personaWrapper.setAccreditamentoId(accreditamentoId);
		personaWrapper.setProviderId(providerId);
		personaWrapper.setRuolo(persona.getRuolo());

		if(!persona.isNew()){
			Set<File> files = persona.getFiles();
			for(File file : files){
				if(file.isCV())
					personaWrapper.setCv(file);
				else if(file.isDELEGA())
					personaWrapper.setDelega(file);
				else if(file.isATTONOMINA())
					personaWrapper.setAttoNomina(file);
			}
		}

		if(accreditamentoId != 0){
			SubSetFieldEnum subset = Utils.getSubsetFromRuolo(persona.getRuolo());
			if(persona.isNew()){
				personaWrapper.setIdEditabili(IdFieldEnum.getAllForSubset(subset));
			}else{
				if(persona.isComponenteComitatoScientifico()){
					personaWrapper.setIdEditabili(Utils.getSubsetOfIdFieldEnum(fieldEditabileService.getAllFieldEditabileForAccreditamentoAndObject(accreditamentoId, persona.getId()), subset));
				}else{
					personaWrapper.setIdEditabili(Utils.getSubsetOfIdFieldEnum(fieldEditabileService.getAllFieldEditabileForAccreditamento(accreditamentoId), subset));
				}
			}
		}

		personaWrapper.setIsLookup(isLookup);
		LOGGER.info(Utils.getLogMessage("preparePersonaWrapperEdit(" + persona.getId() + "," + accreditamentoId +","+ providerId + "," + isLookup + ") - exiting"));
		return personaWrapper;
	}

	private PersonaWrapper preparePersonaWrapperShow(Persona persona, long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("preparePersonaWrapperShow(" + persona.getId() + ") - entering"));
		PersonaWrapper personaWrapper = new PersonaWrapper();
		personaWrapper.setAccreditamentoId(accreditamentoId);
		personaWrapper.setPersona(persona);
		personaWrapper.setRuolo(persona.getRuolo());
		if(!persona.isNew()){
			Set<File> files = persona.getFiles();
			for(File file : files){
				if(file.isCV())
					personaWrapper.setCv(file);
				else if(file.isDELEGA())
					personaWrapper.setDelega(file);
				else if(file.isATTONOMINA())
					personaWrapper.setAttoNomina(file);
			}
		}
		LOGGER.info(Utils.getLogMessage("preparePersonaWrapperShow(" + persona.getId() + ") - exiting"));
		return personaWrapper;
	}

	//TODO domenico (check se fa la query di tutto il provider)
	/*** LIST PERSONA ***/
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/persona/list")
	public String listPersona(@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/"+ providerId + "/persona/list"));
		try {
			Provider provider = providerService.getProvider(providerId);
			model.addAttribute("personaList", provider.getPersone());
			model.addAttribute("titolo", provider.getDenominazioneLegale());
			LOGGER.info(Utils.getLogMessage("VIEW: /persona/personaList"));
			return "persona/personaList";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/"+ providerId + "/persona/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/show"));
			return "redirect:provider/show";
		}
	}
}
