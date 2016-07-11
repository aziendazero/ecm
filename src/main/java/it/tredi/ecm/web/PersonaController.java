package it.tredi.ecm.web;

import java.util.LinkedList;
import java.util.List;
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
import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.AnagraficaService;
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
	@Autowired private AccreditamentoService accreditamentoService;
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
	public String showPersona(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long id, Model model, HttpServletRequest req){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/" + id + "/show"));
		try {
			Persona persona = personaService.getPersona(id);
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
					LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
					return EDIT;
				}else{
					personaService.save(personaWrapper.getPersona());

					// Durante la compilazione della domanda di accreditamento, se si inizia l'inserimento dei responsabili non e' piu'
					// consentita la modifica del legale rappresentante
					if(personaWrapper.getPersona().isResponsabileSegreteria() || personaWrapper.getPersona().isResponsabileAmministrativo() ||
							personaWrapper.getPersona().isComponenteComitatoScientifico() || personaWrapper.getPersona().isCoordinatoreComitatoScientifico()||
							personaWrapper.getPersona().isResponsabileSistemaInformatico() || personaWrapper.getPersona().isResponsabileQualita())
						accreditamentoService.removeIdEditabili(personaWrapper.getAccreditamentoId(), Costanti.IDS_LEGALE_RAPPRESENTANTE);

					redirectAttrs.addAttribute("accreditamentoId", personaWrapper.getAccreditamentoId());
					redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.inserito('"+ personaWrapper.getPersona().getRuolo().getNome() +"')", "success"));

					if(!personaWrapper.getPersona().isLegaleRappresentante() && !personaWrapper.getPersona().isDelegatoLegaleRappresentante())
						redirectAttrs.addFlashAttribute("currentTab","tab2");
					LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId));
					return "redirect:/accreditamento/{accreditamentoId}";
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
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.componente_comitato_eliminato", "success"));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/" + personaId + "/delete"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
		}

		redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
		redirectAttrs.addFlashAttribute("currentTab","tab2");
		LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId));
		return "redirect:/accreditamento/{accreditamentoId}";
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
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	private String goToShow(Model model, PersonaWrapper personaWrapper){
		model.addAttribute("personaWrapper", personaWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
		return SHOW;
	}

	private PersonaWrapper preparePersonaWrapperEdit(Persona persona, boolean isLookup){
		return preparePersonaWrapperEdit(persona,0,0, isLookup);
	}

	private PersonaWrapper preparePersonaWrapperEdit(Persona persona, long accreditamentoId, long providerId, boolean isLookup){
		LOGGER.info(Utils.getLogMessage("preparePersonaWrapper(" + persona.getId() + "," + accreditamentoId +","+ providerId + "," + isLookup + ") - entering"));
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
			List<Integer> accreditamentoIdEditabili = accreditamentoService.getIdEditabili(accreditamentoId);
			if(persona.isLegaleRappresentante())
				personaWrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_LEGALE_RAPPRESENTANTE), accreditamentoIdEditabili);
			else if(persona.isDelegatoLegaleRappresentante())
				personaWrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_DELEGATO_LEGALE_RAPPRESENTANTE), accreditamentoIdEditabili);
			else if(persona.isResponsabileSegreteria())
				personaWrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_RESPONSABILE_SEGRETERIA), accreditamentoIdEditabili);
			else if(persona.isResponsabileAmministrativo())
				personaWrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_RESPONSABILE_AMMINISTRATIVO), accreditamentoIdEditabili);
			else if(persona.isComponenteComitatoScientifico())
				personaWrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_COMPONENTE_COMITATO_SCIENTIFICO), accreditamentoIdEditabili);
			else if(persona.isResponsabileSistemaInformatico())
				personaWrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_RESPONSABILE_SISTEMA_INFORMATICO), accreditamentoIdEditabili);
			else if(persona.isResponsabileQualita())
				personaWrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_RESPONSABILE_QUALITA), accreditamentoIdEditabili);
		}

		personaWrapper.setIsLookup(isLookup);
		LOGGER.info(Utils.getLogMessage("preparePersonaWrapper(" + persona.getId() + "," + accreditamentoId +","+ providerId + "," + isLookup + ") - exiting"));
		return personaWrapper;
	}

	private PersonaWrapper preparePersonaWrapperShow(Persona persona, long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("preparePersonaWrapperShow(" + persona.getId() + ") - entering"));
		PersonaWrapper personaWrapper = new PersonaWrapper();
		personaWrapper.setAccreditamentoId(accreditamentoId);
		personaWrapper.setPersona(persona);
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
