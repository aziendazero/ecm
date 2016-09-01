package it.tredi.ecm.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.JsonViewModel;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.AnagraficaService;
import it.tredi.ecm.service.FieldEditabileAccreditamentoService;
import it.tredi.ecm.service.FieldIntegrazioneAccreditamentoService;
import it.tredi.ecm.service.FieldValutazioneAccreditamentoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.IntegrazioneService;
import it.tredi.ecm.service.PersonaService;
import it.tredi.ecm.service.ProfessioneService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.ValutazioneService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.PersonaWrapper;
import it.tredi.ecm.web.validator.PersonaValidator;
import it.tredi.ecm.web.validator.ValutazioneValidator;

@Controller
public class PersonaController {
	private static Logger LOGGER = LoggerFactory.getLogger(PersonaController.class);

	private final String EDIT = "persona/personaEdit";
	private final String SHOW = "persona/personaShow";
	private final String VALIDATE = "persona/personaValidate";

	@PersistenceContext
	EntityManager entityManager;

	@Autowired private PersonaService personaService;
	@Autowired private AnagraficaService anagraficaService;
	@Autowired private ProviderService providerService;
	@Autowired private ProfessioneService professioneService;
	@Autowired private FileService fileService;
	@Autowired private FieldEditabileAccreditamentoService fieldEditabileAccreditamentoService;
	@Autowired private PersonaValidator personaValidator;
	@Autowired private ValutazioneValidator valutazioneValidator;
	@Autowired private ValutazioneService valutazioneService;

	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private IntegrazioneService integrazioneService;
	@Autowired private FieldIntegrazioneAccreditamentoService fieldIntegrazioneAccreditamentoService;
	@Autowired private FieldValutazioneAccreditamentoService fieldValutazioneAccreditamentoService;

	@Autowired private ObjectMapper jacksonObjectMapper;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/anagraficaList")
	@ResponseBody
	public Set<Anagrafica>getAnagraficheRegistrateDalProvider(@PathVariable Long providerId){
		return personaService.getAllAnagraficheAttiveByProviderId(providerId);
	}

	@ModelAttribute("professioneList")
	public Set<Professione> getAllProfessioni(){
		return professioneService.getAllProfessioni();
	}

	@ModelAttribute("personaWrapper")
	public PersonaWrapper getPersonaWrapper(@RequestParam(value="editId",required = false) Long id,
			@RequestParam(value="editId_Anagrafica",required = false) Long anagraficaId,
			@RequestParam(value="statoAccreditamento",required = false) AccreditamentoStatoEnum statoAccreditamento,
			@RequestParam(value="ruolo",required = false) Ruolo ruolo) throws Exception{
		//se sto chiamando il SAVE vedo se caricare da DB le entity per fare il merge con il wrapper arrivato dal form
		//il wrapper ha scopo REQUEST e quindi tutti i campi non agganciati al form arrivano NULL dal client
		if(id != null || anagraficaId != null){
			Persona persona = (id != null) ? personaService.getPersona(id) : new Persona(ruolo);

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

			return preparePersonaWrapperEdit(persona, isLookup, statoAccreditamento);
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
			return goToEdit(model, preparePersonaWrapperEdit(new Persona(Ruolo.valueOf(ruolo)), accreditamentoId, providerId, false, accreditamentoService.getStatoAccreditamento(accreditamentoId)));
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
				persona = new Persona(Ruolo.valueOf(ruolo));
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
			return goToEdit(model, preparePersonaWrapperEdit(persona, accreditamentoId, providerId, isLookup, accreditamentoService.getStatoAccreditamento(accreditamentoId)));
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
			return goToEdit(model, preparePersonaWrapperEdit(personaService.getPersona(id), accreditamentoId, providerId, false, accreditamentoService.getStatoAccreditamento(accreditamentoId)));
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
			if(from != null) {
				redirectAttrs.addFlashAttribute("mode", from);
				return "redirect:/accreditamento/"+ accreditamentoId + "/provider/" + providerId + "/persona/" + id +"/show";
			}
			if(model.containsAttribute("mode")) {
				return goToShowFromMode(model, preparePersonaWrapperShow(personaService.getPersona(id), accreditamentoId));
			}
			return goToShow(model, preparePersonaWrapperShow(personaService.getPersona(id), accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/" + id + "/show"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/show"));
			return "redirect: /accreditamento/" + accreditamentoId + "/show";
		}
	}

	/***	VALIDATE PERSONA ***/
	//	@PreAuthorize("@securityAccessServiceImpl.canValidateAccreditamento(principal,#accreditamentoId) TODO
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/{id}/validate")
	public String validatePersona(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long id, Model model, HttpServletRequest req){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/" + id + "/validate"));
		try {
			//controllo se è possibile modificare la valutazione o meno
			model.addAttribute("canValutaDomanda", accreditamentoService.canUserValutaDomanda(accreditamentoId, Utils.getAuthenticatedUser()));
			return goToValidate(model, preparePersonaWrapperValidate(personaService.getPersona(id), accreditamentoId, providerId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/" + id + "/validate"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
			return VALIDATE;
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

			//TODO getFile da testare se funziona anche senza reload -> NON è possibile finchè c'è il validator dei file nel salvatggio della persona (file.data = null)
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

					if(personaWrapper.getStatoAccreditamento() == AccreditamentoStatoEnum.INTEGRAZIONE){
						integraPersona(personaWrapper,false);
					}else{
						savePersona(personaWrapper);
					}

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
				model.addAttribute("returnLink", calcolaLink(personaWrapper, "edit"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/save"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			model.addAttribute("returnLink", calcolaLink(personaWrapper, "edit"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
			return EDIT;
		}
	}

	/***	SAVE  VALUTAZIONE PERSONA ***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/{providerId}/persona/validate", method = RequestMethod.POST)
	public String valutaPersona(@ModelAttribute("personaWrapper") PersonaWrapper personaWrapper, BindingResult result,
			RedirectAttributes redirectAttrs, Model model, @PathVariable Long accreditamentoId, @PathVariable Long providerId){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/validate"));
		try {
			//validazione della persona
			valutazioneValidator.validateValutazione(personaWrapper.getMappa(), result);

			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				model.addAttribute("canValutaDomanda", accreditamentoService.canUserValutaDomanda(accreditamentoId, Utils.getAuthenticatedUser()));
				model.addAttribute("returnLink", calcolaLink(personaWrapper, "validate"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
				return VALIDATE;
			}else{
				Accreditamento accreditamento = new Accreditamento();
				accreditamento.setId(personaWrapper.getAccreditamentoId());
				personaWrapper.getMappa().forEach((k, v) -> {
					v.setIdField(k);
					v.setAccreditamento(accreditamento);
					if(personaWrapper.getPersona().isComponenteComitatoScientifico())
						v.setObjectReference(personaWrapper.getPersona().getId());
				});

				Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamento.getId(), Utils.getAuthenticatedUser().getAccount().getId());
				Set<FieldValutazioneAccreditamento> values = new HashSet<FieldValutazioneAccreditamento>(fieldValutazioneAccreditamentoService.saveMapList(personaWrapper.getMappa()));
				valutazione.getValutazioni().addAll(values);
				valutazioneService.save(valutazione);

				redirectAttrs.addAttribute("accreditamentoId", personaWrapper.getAccreditamentoId());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.valutazione_salvata", "success"));
				if(!personaWrapper.getPersona().isLegaleRappresentante() && !personaWrapper.getPersona().isDelegatoLegaleRappresentante())
					redirectAttrs.addFlashAttribute("currentTab","tab2");
				LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/validate"));
				return "redirect:/accreditamento/{accreditamentoId}/validate";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/persona/validate"),ex);
			model.addAttribute("accreditamentoId",personaWrapper.getAccreditamentoId());
			model.addAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			model.addAttribute("canValutaDomanda", accreditamentoService.canUserValutaDomanda(accreditamentoId, Utils.getAuthenticatedUser()));
			model.addAttribute("returnLink", calcolaLink(personaWrapper, "validate"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
			return VALIDATE;
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/{personaId}/delete")
	public String removeComponenteComitatoScientifico(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long personaId,
			Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/persona/" + personaId + "/delete"));
		try{
			AccreditamentoStatoEnum statoAccreditamento = accreditamentoService.getStatoAccreditamento(accreditamentoId);
			if(statoAccreditamento == AccreditamentoStatoEnum.INTEGRAZIONE){
				Persona persona = personaService.getPersona(personaId);
				integraPersona(new PersonaWrapper(persona, accreditamentoId, persona.getRuolo()), true);
			}else{
				//rimozione persona multi-istanza dalla Domanda di Accreditamento e relativi IdEditabili
				personaService.delete(personaId);
				fieldEditabileAccreditamentoService.removeFieldEditabileForAccreditamento(accreditamentoId, personaId, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO);
			}

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
	private String goToEdit(Model model, PersonaWrapper personaWrapper){
		model.addAttribute("personaWrapper", personaWrapper);
		model.addAttribute("returnLink", calcolaLink(personaWrapper, "edit"));
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	private String goToValidate(Model model, PersonaWrapper personaWrapper) {
		model.addAttribute("personaWrapper", personaWrapper);
		model.addAttribute("returnLink", calcolaLink(personaWrapper, "validate"));
		LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
		return VALIDATE;
	}

	private String goToShow(Model model, PersonaWrapper personaWrapper){
		model.addAttribute("personaWrapper", personaWrapper);
		model.addAttribute("returnLink", calcolaLink(personaWrapper, "show"));
		LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
		return SHOW;
	}

	private String goToShowFromMode(Model model, PersonaWrapper personaWrapper) {
		model.addAttribute("personaWrapper", personaWrapper);
		String mode = (String) model.asMap().get("mode");
		model.addAttribute("returnLink", calcolaLink(personaWrapper, mode));
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

	private PersonaWrapper preparePersonaWrapperEdit(Persona persona, boolean isLookup, AccreditamentoStatoEnum statoAccreditamento) throws Exception{
		return preparePersonaWrapperEdit(persona,0,0, isLookup, statoAccreditamento);
	}

	/*
	 * Se INTEGRAZIONE:
	 * 
	 * caso 1: MODIFICA SINGOLO CAMPO
	 * 		(+) Saranno sbloccati SOLO gli IdFieldEnum eslpicitamente abilitati dalla segreteria (creazione di FieldEditabileAccreditamento)
	 * 		(+) Vengono applicati eventuali fieldIntegrazioneAccreditamento già salvati per visualizzare correttamente lo stato attuale delle modifiche
	 * 
	 * caso 2: ASSEGNAMENTO a persone no multi-istanza 
	 * 		(*) ASSEGNAMENTO nuova anagrafica
	 * 			(+) L'unico IdFieldEnum esplicitamente abilitato dalla segreteria dovrebbe essere il FULL
	 * 			(+) In realtà conviene abilitare anche esplicitamente i campi per permettere eventuali modifiche
	 * 			(+) Lato applicativo vengono manualmente abilitati gli IdFieldEnum per permettere il corretto inserimento della nuova persona (files, professione, ecc.. in funzione del RUOLO)
	 * 			(+) Non vengono applicate eventuali integrazioni già presente sull'oggetto, perchè non possono esserci (l'unica è relativa a FULL con il json completo)
	 * 		(*) ASSEGNAMENTO lookup anagrafica esistente
	 * 			(+) L'unico IdFieldEnum esplicitamente abilitato dalla segreteria dovrebbe essere il FULL
	 * 			(+) Lato applicativo vengono manualmente abilitati gli IdFieldEnum per permettere il corretto inserimento della nuova persona (files, professione, ecc.. in funzione del RUOLO)
	 * 	 		(+) Lato applicativo vengono manualmente DISabilitati gli IdFieldEnum relativi all'anagrafica che non sarà possibile modificare
	 * 			(+) Non vengono applicate eventuali integrazioni già presente sull'oggetto, perchè non possono esserci (l'unica è relativa a FULL con il json completo)
	 * 		(**) PER VEDERE LA PERSONA MODIFICATA MA NON ANCORA VALIDATA E'NECESSARIO ANDARE IN MODIFICA, PERCHE' IN VISUALIZZAZIONE VIENE MOSTRATA QUELLA UFFICIALE SU DB
	 * 		(**) ANDANDO IN MODIFICA VIENE APPLICATA L'UNICA INTEGRAZIONE PRESENTE (FULL) E QUINDI SI VEDE LA PERSONA INSERITA
	 * 		(**) ANDANDO IN MODIFICA SE SONO ABILITATI CAMPI EXTRA (files, professione ...) è possibile modificarli e al salvataggio si aggiorna il json di fieldIntegrazione
	 * */
	private PersonaWrapper preparePersonaWrapperEdit(Persona persona, long accreditamentoId, long providerId, boolean isLookup, AccreditamentoStatoEnum statoAccreditamento) throws Exception{
		LOGGER.info(Utils.getLogMessage("preparePersonaWrapperEdit(" + persona.getId() + "," + accreditamentoId +","+ providerId + "," + isLookup + "," + statoAccreditamento +") - entering"));
		PersonaWrapper personaWrapper = new PersonaWrapper(persona, accreditamentoId, persona.getRuolo());
		personaWrapper.setProviderId(providerId);
		personaWrapper.setStatoAccreditamento(accreditamentoService.getStatoAccreditamento(personaWrapper.getAccreditamentoId()));
		personaWrapper.setIsLookup(isLookup);

		if(accreditamentoId != 0){
			SubSetFieldEnum subset = Utils.getSubsetFromRuolo(persona.getRuolo());
			if(persona.isNew()){
				personaWrapper.setIdEditabili(IdFieldEnum.getAllForSubset(subset));
			}else{
				if(persona.isComponenteComitatoScientifico()){
					personaWrapper.setIdEditabili(Utils.getSubsetOfIdFieldEnum(fieldEditabileAccreditamentoService.getAllFieldEditabileForAccreditamentoAndObject(accreditamentoId, persona.getId()), subset));
					personaWrapper.setFieldIntegrazione(Utils.getSubset(fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazioneForAccreditamentoAndObject(accreditamentoId, persona.getId()), subset));
				}else{
					personaWrapper.setIdEditabili(Utils.getSubsetOfIdFieldEnum(fieldEditabileAccreditamentoService.getAllFieldEditabileForAccreditamento(accreditamentoId), subset));
					personaWrapper.setFieldIntegrazione(Utils.getSubset(fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazioneForAccreditamento(accreditamentoId), subset));
				}
			}
		}

		if(statoAccreditamento == AccreditamentoStatoEnum.INTEGRAZIONE){
			//detach dei files...fatto qui perchè altrimenti hibernate segnala che è stato modificato l'ID di una entity (file) e durante un salvataggio da errore.
			//Inoltre una volta fatto il detach non è possibile cariare i LazyField...quindi facciamo una chiamata prima per caricarli
			persona.getFiles().size();
			integrazioneService.detach(persona);
			//caso 2 (B): ASSEGNAMENTO lookup anagrafica esistente
			if(isLookup){
				/*
				applico le integrazioni solo ai campi di persona e non di anagrafica
				personaWrapper.getFieldIntegrazione().remove(Utils.getField(personaWrapper.getFieldIntegrazione(), IdFieldEnum.RESPONSABILE_SEGRETERIA__FULL));
				integrazioneService.applyIntegrazioneObject(personaWrapper.getPersona(), personaWrapper.getFieldIntegrazione());
				 */
				//aggiunta manuale degli IdFieldEnum per permettere il corretto salvataggio della persona
				//inserisci nuova anagrafica e quindi non applico nessuna integrazione
				//rimozione manuale degli IdFieldEnum relativi all'anagrafica
				personaWrapper.getIdEditabili().addAll(IdFieldEnum.getAllForSubset(Utils.getSubsetFromRuolo(personaWrapper.getPersona().getRuolo())));
				personaWrapper.getIdEditabili().removeAll(IdFieldEnum.getAllForSubsetWithNameRefPrefix(Utils.getSubsetFromRuolo(personaWrapper.getPersona().getRuolo()),"anagrafica"));
			}else{
				//caso 2 (A): ASSEGNAMENTO nuova anagrafica
				if(personaWrapper.getPersona().getAnagrafica() == null || personaWrapper.getPersona().getAnagrafica().getId() == null){
					//aggiunta manuale degli IdFieldEnum per permettere il corretto salvataggio della persona
					//inserisci nuova anagrafica e quindi non applico nessuna integrazione
					personaWrapper.getIdEditabili().addAll(IdFieldEnum.getAllForSubset(Utils.getSubsetFromRuolo(personaWrapper.getPersona().getRuolo())));
				}else{
					//caso 1: MODIFICA SINGOLO CAMPO
					integrazioneService.applyIntegrazioneObject(personaWrapper.getPersona(), personaWrapper.getFieldIntegrazione());
				}
			}
		}

		//set dei files sul wrapper, per allinearmi nel caso ci fossero dei fieldIntegrazione relativi a files
		if(!personaWrapper.getPersona().isNew()){
			personaWrapper.setFiles(personaWrapper.getPersona().getFiles());
		}

		LOGGER.info(Utils.getLogMessage("preparePersonaWrapperEdit(" + persona.getId() + "," + accreditamentoId +","+ providerId + "," + isLookup + "," + statoAccreditamento +") - exiting"));
		return personaWrapper;
	}

	private PersonaWrapper preparePersonaWrapperShow(Persona persona, long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("preparePersonaWrapperShow(" + persona.getId() + ") - entering"));
		PersonaWrapper personaWrapper = new PersonaWrapper(persona, accreditamentoId, persona.getRuolo());
		personaWrapper.setFiles(persona.getFiles());
		LOGGER.info(Utils.getLogMessage("preparePersonaWrapperShow(" + persona.getId() + ") - exiting"));
		return personaWrapper;
	}

	private PersonaWrapper preparePersonaWrapperValidate(Persona persona, long accreditamentoId, long providerId){
		LOGGER.info(Utils.getLogMessage("preparePersonaWrapperValidate(" + persona.getId() + ") - entering"));
		PersonaWrapper personaWrapper = new PersonaWrapper(persona, accreditamentoId, persona.getRuolo());
		personaWrapper.setProviderId(providerId);
		personaWrapper.setFiles(persona.getFiles());

		//carico la valutazione per l'utente
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();

		//cerco tutte le valutazioni del subset o oggetto persona per ciascun valutatore dell'accreditamento
		Map<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>> mappaValutatoreValutazioni = new HashMap<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>>();

		//prendo tutti gli id del subset
		Set<IdFieldEnum> idEditabili = new HashSet<IdFieldEnum>();

		//per distinguere il multistanza delle persone del comitato scientifico
		if(valutazione != null) {
			if(persona.isComponenteComitatoScientifico()) {
				mappa = fieldValutazioneAccreditamentoService.filterFieldValutazioneByObjectAsMap(valutazione.getValutazioni(), persona.getId());
				mappaValutatoreValutazioni = valutazioneService.getMapValutatoreValutazioniByAccreditamentoIdAndObjectId(accreditamentoId, persona.getId());
				idEditabili = IdFieldEnum.getAllForSubset(SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO);
			}
			else
				switch(persona.getRuolo()) {
				case LEGALE_RAPPRESENTANTE: mappa = fieldValutazioneAccreditamentoService.filterFieldValutazioneBySubSetAsMap(valutazione.getValutazioni(), SubSetFieldEnum.LEGALE_RAPPRESENTANTE);
				mappaValutatoreValutazioni = valutazioneService.getMapValutatoreValutazioniByAccreditamentoIdAndSubSet(accreditamentoId, SubSetFieldEnum.LEGALE_RAPPRESENTANTE);
				idEditabili = IdFieldEnum.getAllForSubset(SubSetFieldEnum.LEGALE_RAPPRESENTANTE);
				break;
				case DELEGATO_LEGALE_RAPPRESENTANTE: mappa = fieldValutazioneAccreditamentoService.filterFieldValutazioneBySubSetAsMap(valutazione.getValutazioni(), SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE);
				mappaValutatoreValutazioni = valutazioneService.getMapValutatoreValutazioniByAccreditamentoIdAndSubSet(accreditamentoId, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE);
				idEditabili = IdFieldEnum.getAllForSubset(SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE);
				break;
				case RESPONSABILE_SEGRETERIA: mappa = fieldValutazioneAccreditamentoService.filterFieldValutazioneBySubSetAsMap(valutazione.getValutazioni(), SubSetFieldEnum.RESPONSABILE_SEGRETERIA);
				mappaValutatoreValutazioni = valutazioneService.getMapValutatoreValutazioniByAccreditamentoIdAndSubSet(accreditamentoId, SubSetFieldEnum.RESPONSABILE_SEGRETERIA);
				idEditabili = IdFieldEnum.getAllForSubset(SubSetFieldEnum.RESPONSABILE_SEGRETERIA);
				break;
				case RESPONSABILE_AMMINISTRATIVO: mappa = fieldValutazioneAccreditamentoService.filterFieldValutazioneBySubSetAsMap(valutazione.getValutazioni(), SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO);
				mappaValutatoreValutazioni = valutazioneService.getMapValutatoreValutazioniByAccreditamentoIdAndSubSet(accreditamentoId, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO);
				idEditabili = IdFieldEnum.getAllForSubset(SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO);
				break;
				case RESPONSABILE_SISTEMA_INFORMATICO: mappa = fieldValutazioneAccreditamentoService.filterFieldValutazioneBySubSetAsMap(valutazione.getValutazioni(), SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO);
				mappaValutatoreValutazioni = valutazioneService.getMapValutatoreValutazioniByAccreditamentoIdAndSubSet(accreditamentoId, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO);
				idEditabili = IdFieldEnum.getAllForSubset(SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO);
				break;
				case RESPONSABILE_QUALITA:  mappa = fieldValutazioneAccreditamentoService.filterFieldValutazioneBySubSetAsMap(valutazione.getValutazioni(), SubSetFieldEnum.RESPONSABILE_QUALITA);
				mappaValutatoreValutazioni = valutazioneService.getMapValutatoreValutazioniByAccreditamentoIdAndSubSet(accreditamentoId, SubSetFieldEnum.RESPONSABILE_QUALITA);
				idEditabili = IdFieldEnum.getAllForSubset(SubSetFieldEnum.RESPONSABILE_QUALITA);
				break;
				default: mappa = fieldValutazioneAccreditamentoService.putSetFieldValutazioneInMap(valutazione.getValutazioni()); break;
				}
		}

		personaWrapper.setMappaValutatoreValutazioni(mappaValutatoreValutazioni);
		personaWrapper.setIdEditabili(idEditabili);
		personaWrapper.setMappa(mappa);

		LOGGER.info(Utils.getLogMessage("preparePersonaWrapperValidate(" + persona.getId() + ") - exiting"));
		return personaWrapper;
	}

	/***	LOGICA PER SALVATAGGIO PERSONA	***/
	private void savePersona(PersonaWrapper personaWrapper) throws Exception{
		LOGGER.info(Utils.getLogMessage("Salvataggio persona"));
		//non possono e non devono esistere più persone con lo stesso ruolo per ogni provider (tranne per i componenti del comitato scientifico)
		if(personaWrapper.getPersona().isNew() && !personaWrapper.getPersona().isComponenteComitatoScientifico()){
			Persona persona = personaService.getPersonaByRuolo(personaWrapper.getPersona().getRuolo(), personaWrapper.getProviderId());
			if(persona != null)
				personaService.delete(persona.getId());
		}

		boolean insertFieldEditabile = (personaWrapper.getPersona().isNew()) ? true : false;
		personaService.save(personaWrapper.getPersona());

		//inserimento nuova persona in Domanda di Accreditamento
		//inseriamo gli IdEditabili (con riferimento all'id nel caso di multi-istanza) per consentire le modifiche successive
		if(insertFieldEditabile){
			SubSetFieldEnum subset = Utils.getSubsetFromRuolo(personaWrapper.getPersona().getRuolo());
			if(personaWrapper.getPersona().isComponenteComitatoScientifico())
				fieldEditabileAccreditamentoService.insertFieldEditabileForAccreditamento(personaWrapper.getAccreditamentoId(), personaWrapper.getPersona().getId(), subset, IdFieldEnum.getAllForSubset(subset));
			else
				fieldEditabileAccreditamentoService.insertFieldEditabileForAccreditamento(personaWrapper.getAccreditamentoId(), null, subset, IdFieldEnum.getAllForSubset(subset));
		}

		// Durante la compilazione della domanda di accreditamento, se si inizia l'inserimento dei responsabili non e' piu'
		// consentita la modifica del legale rappresentante
		if(personaWrapper.getPersona().isResponsabileSegreteria() || personaWrapper.getPersona().isResponsabileAmministrativo() ||
				personaWrapper.getPersona().isComponenteComitatoScientifico() || personaWrapper.getPersona().isCoordinatoreComitatoScientifico()||
				personaWrapper.getPersona().isResponsabileSistemaInformatico() || personaWrapper.getPersona().isResponsabileQualita())
			fieldEditabileAccreditamentoService.removeFieldEditabileForAccreditamento(personaWrapper.getAccreditamentoId(), null, SubSetFieldEnum.LEGALE_RAPPRESENTANTE);
	}

	/*
	 * Se INTEGRAZIONE:
	 * 
	 * caso 1: MODIFICA SINGOLO CAMPO
	 * 		(+) Viene salvato un fieldIntegrazione per ogni fieldEditabile abilitato
	 * 		(+) Ogni fieldIntegrazione contiene il nuovo valore serializzato in funzione del setField/getField di IntegrazioneService 
	 * 
	 * caso 2/3: ASSEGNAMENTO a persone no/si multi-istanza 
	 * 		(*) ASSEGNAMENTO nuova anagrafica
	 * 			(+) Viene salvato un unico fieldIntegrazione per l'unico fieldEditabile presente (FULL)
	 * 			(+) Il fieldIntegrazione contiene il json della persona
	 *			(+) La nuova anagrafica viene creata ma marcata come dirty
	 * 		(*) ASSEGNAMENTO lookup anagrafica esistente
	 * 			(+) Viene salvatao un unico fieldIntegrazione per l'unico fieldEditabile presente (FULL)
	 * 			(+) Il fieldIntegrazione contiene il json della persona
	 * 
	 * caso 2: CREAZIONE persona multi-istanza
	 * 		(*) ASSEGNAMENTO nuova anagrafica
	 * 			(+) Uguale a caso 2, l'unica differenza è che viene creata anche la persona con il flag dirty
	 * 		(*) ASSEGNAMENTO lookup anagrafica esistente
	 * 			(+) Uguale a caso 2, l'unica differenza è che viene creata anche la persona con il flag dirty
	 *   
	 * */
	@Transactional
	private void integraPersona(PersonaWrapper personaWrapper, boolean eliminazione) throws Exception{
		LOGGER.info(Utils.getLogMessage("Integrazione persona"));
		Accreditamento accreditamento = new Accreditamento();
		accreditamento.setId(personaWrapper.getAccreditamentoId());

		List<FieldIntegrazioneAccreditamento> fieldIntegrazioneList = new ArrayList<FieldIntegrazioneAccreditamento>();
		IdFieldEnum idFieldFull = Utils.getFullFromRuolo(personaWrapper.getPersona().getRuolo());

		if(!eliminazione){
			//caso 3: CREAZIONE persona multi-istanza
			if(personaWrapper.getPersona().isNew()){
				//registriamo inserimento nuova persona come dirty object
				if(personaWrapper.getPersona().isComponenteComitatoScientifico()){
					personaWrapper.getPersona().setDirty(true);
					//(A) nuova anagrafica, registriamo l'anagrafica come dirty object
					//(B) lookup anagrafica esistente -> facciamo il reload per riattaccarla alla sessione di Hibernate
					if(personaWrapper.getPersona().getAnagrafica().isNew())
						personaWrapper.getPersona().getAnagrafica().setDirty(true);
					else
						personaWrapper.getPersona().setAnagrafica(anagraficaService.getAnagrafica(personaWrapper.getPersona().getAnagrafica().getId()));
					personaService.save(personaWrapper.getPersona());
					String json = jacksonObjectMapper.writerWithView(JsonViewModel.Integrazione.class).writeValueAsString(personaWrapper.getPersona());
					LOGGER.info(Utils.getLogMessage("Salvataggio fieldIntegrazione per creazione persona: " + json));
					fieldIntegrazioneList.add(new FieldIntegrazioneAccreditamento(idFieldFull, accreditamento, personaWrapper.getPersona().getId(), json, TipoIntegrazioneEnum.CREAZIONE));
				}
			}else{
				//caso 2: ASSEGNAMENTO a persone no multi-istanza (nuovo lookup anagrafica o lookup anagrafica esistente)
				if(personaWrapper.getIdEditabili().contains(idFieldFull)){
					//(A) nuova anagrafica, registriamo l'anagrafica come dirty object
					//(B) lookup anagrafica esistente -> non facciamo nulla
					if(personaWrapper.getPersona().getAnagrafica().isNew()){
						personaWrapper.getPersona().getAnagrafica().setDirty(true);
						anagraficaService.save(personaWrapper.getPersona().getAnagrafica());
					}

					//salvataggio della persona come json nel fieldIntegrazione
					String json = jacksonObjectMapper.writerWithView(JsonViewModel.Integrazione.class).writeValueAsString(personaWrapper.getPersona());
					LOGGER.info(Utils.getLogMessage("Salvataggio fieldIntegrazione per assegnamento persona: " + json));

					if(personaWrapper.getPersona().isComponenteComitatoScientifico()){
						fieldIntegrazioneList.add(new FieldIntegrazioneAccreditamento(idFieldFull, accreditamento, personaWrapper.getPersona().getId(), json, TipoIntegrazioneEnum.ASSEGNAMENTO));
					}else{
						fieldIntegrazioneList.add(new FieldIntegrazioneAccreditamento(idFieldFull, accreditamento, json, TipoIntegrazioneEnum.ASSEGNAMENTO));
					}
				}else{
					//caso 1: MODIFICA SINGOLO CAMPO
					for(IdFieldEnum idField : personaWrapper.getIdEditabili()){
						if(personaWrapper.getPersona().isComponenteComitatoScientifico())
							fieldIntegrazioneList.add(new FieldIntegrazioneAccreditamento (idField, accreditamento, personaWrapper.getPersona().getId(), integrazioneService.getField(personaWrapper.getPersona(), idField.getNameRef()), TipoIntegrazioneEnum.MODIFICA));
						else
							fieldIntegrazioneList.add(new FieldIntegrazioneAccreditamento(idField, accreditamento, integrazioneService.getField(personaWrapper.getPersona(), idField.getNameRef()), TipoIntegrazioneEnum.MODIFICA));
					}
				}
			}
		}else{
			LOGGER.info(Utils.getLogMessage("Salvataggio fieldIntegrazione per eliminazione persona: " + personaWrapper.getPersona().getId()));
			fieldIntegrazioneList.add(new FieldIntegrazioneAccreditamento(idFieldFull, accreditamento, personaWrapper.getPersona().getId(), personaWrapper.getPersona().getId(), TipoIntegrazioneEnum.ELIMINAZIONE));
		}

		fieldIntegrazioneAccreditamentoService.update(personaWrapper.getFieldIntegrazione(), fieldIntegrazioneList);
	}
}
