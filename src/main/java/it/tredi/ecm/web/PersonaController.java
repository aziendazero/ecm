package it.tredi.ecm.web;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.Provider;
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

	private final String EDIT = "persona/personaEdit";

	@Autowired
	private PersonaService personaService;
	@Autowired
	private ProviderService providerService;
	@Autowired
	private ProfessioneService professioneService;
	@Autowired
	private AnagraficaService anagraficaService;

	@Autowired
	private FileService fileService;
	@Autowired
	private AccreditamentoService accreditamentoService;

	@Autowired
	private PersonaValidator personaValidator;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@RequestMapping("/provider/{providerId}/anagraficaList")
	@ResponseBody
	public Set<Anagrafica>getElencoComuni(@PathVariable Long providerId){
		Set<Anagrafica> anagrafiche = personaService.getAllAnagraficheByProviderId(providerId); 
		return anagrafiche;
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
			if(anagraficaId == null){
				//NUOVA ANGARFICA
				persona.setAnagrafica(null);
			}else if(anagraficaId != persona.getAnagrafica().getId()){
				//LOOKUP ANAGRAFICA ESISTENTE
				persona.setAnagrafica(anagraficaService.getAnagrafica(anagraficaId));
			}

			return preparePersonaWrapper(persona); 	
		}
		return new PersonaWrapper();
	}

	/***	NUOVA PERSONA ***/
	/* (passando ruolo e providerId) */	
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/new")
	public String newPersona(@PathVariable Long accreditamentoId, @PathVariable Long providerId, Model model,
			@RequestParam(name="ruolo", required = true) String ruolo, RedirectAttributes redirectAttrs){

		try {
			return goToEdit(model, preparePersonaWrapper(createPersona(providerId, ruolo), accreditamentoId, providerId));
		}catch (Exception ex){
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/accreditamento" + accreditamentoId;
		}
	}

	/***	SET ANAGRAFICA	***/
	/*
	 * Agganciamo una Angrafica diversa alla Persona
	 * 
	 * */
	//@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/setAnagrafica")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/{ruolo}/setAnagrafica")
	public String setAnagrafica(@PathVariable Long accreditamentoId, @PathVariable Long providerId, Model model,
									@PathVariable("ruolo") String ruolo,
										@RequestParam(name="anagraficaId", required = false) Long anagraficaId){
		try {
			Persona persona = null;
			
			if(!ruolo.equals(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO.name()))
				persona = personaService.getPersonaByRuolo(Ruolo.valueOf(ruolo), providerId);

			if(persona == null){
				persona = createPersona(providerId, ruolo);
			}
			if(anagraficaId == null){
				persona.setAnagrafica(new Anagrafica());
			}else{
				persona.setAnagrafica(anagraficaService.getAnagrafica(anagraficaId));
			}
			return goToEdit(model, preparePersonaWrapper(persona, accreditamentoId, providerId));
		}catch (Exception ex){
			//TODO gestione eccezione
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return EDIT;
		}
	}

	/***	EDIT PERSONA ***/
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/{id}/edit")
	public String editPersona(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long id, Model model){
		try {	
			Persona persona = personaService.getPersona(id);
			if(persona == null){
				persona = createPersona(providerId);
			}
			return goToEdit(model, preparePersonaWrapper(persona, accreditamentoId, providerId));
		}catch (Exception ex){
			//TODO gestione eccezione
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return EDIT;
		}
	}

	/***	SAVE PERSONA ***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/{providerId}/persona/save", method = RequestMethod.POST)
	public String savePersona(@ModelAttribute("personaWrapper") PersonaWrapper personaWrapper, BindingResult result,
			RedirectAttributes redirectAttrs, Model model,
			@RequestParam(value = "attoNomina_multipart", required = false) MultipartFile attoNomina_multiPartFile,
			@RequestParam(value = "cv_multipart", required = false) MultipartFile cv_multiPartFile,
			@RequestParam(value = "delega_multipart", required = false) MultipartFile delega_multiPartFile){
		try {
			if(personaWrapper.getPersona().isNew()){
				Persona persona = personaWrapper.getPersona(); 
				persona.setRuolo(personaWrapper.getRuolo());
				Provider provider = providerService.getProvider(personaWrapper.getProviderId());
				persona.setProvider(provider);
			}

			if(attoNomina_multiPartFile != null && !attoNomina_multiPartFile.isEmpty())
				personaWrapper.setAttoNomina(Utils.convertFromMultiPart(attoNomina_multiPartFile));
			if(cv_multiPartFile != null && !cv_multiPartFile.isEmpty())
				personaWrapper.setCv(Utils.convertFromMultiPart(cv_multiPartFile));
			if(delega_multiPartFile != null && !delega_multiPartFile.isEmpty())
				personaWrapper.setDelega(Utils.convertFromMultiPart(delega_multiPartFile));

			personaValidator.validate(personaWrapper.getPersona(), result, "persona.",personaWrapper.getFiles());

			try{
				if(result.hasErrors()){

					if(!personaWrapper.getPersona().isNew()){
						//salvataggio dei file modificati per evitare che in casi di errore di validazione sui dati
						//l'utente debba rifare l'upload
						if(!result.hasFieldErrors("delega*") && delega_multiPartFile != null && !delega_multiPartFile.isEmpty()){
							fileService.save(personaWrapper.getDelega());
						}

						if(!result.hasFieldErrors("attoNomina*") && attoNomina_multiPartFile != null && !attoNomina_multiPartFile.isEmpty()){
							fileService.save(personaWrapper.getAttoNomina());
						}

						if(!result.hasFieldErrors("cv*") && cv_multiPartFile != null && !cv_multiPartFile.isEmpty()){
							fileService.save(personaWrapper.getCv());
						}
					}
					model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
					return EDIT;
				}else{
					personaService.save(personaWrapper.getPersona());
					saveFiles(personaWrapper, attoNomina_multiPartFile, cv_multiPartFile, delega_multiPartFile);

					// Durante la compilazione della domanda di accreditamento, se si inizia l'inserimento dei responsabili non e' piu'
					// consentita la modifica del legale rappresentante 
					if(personaWrapper.getPersona().isResponsabileSegreteria() || personaWrapper.getPersona().isResponsabileAmministrativo() || 
							personaWrapper.getPersona().isComponenteComitatoScientifico() || personaWrapper.getPersona().isCoordinatoreComitatoScientifico()|| 
							personaWrapper.getPersona().isResponsabileSistemaInformatico() || personaWrapper.getPersona().isResponsabileQualita())
						accreditamentoService.removeIdEditabili(personaWrapper.getAccreditamentoId(), Arrays.asList(22,23,24,24,25,26,27,28,29));

					redirectAttrs.addAttribute("accreditamentoId", personaWrapper.getAccreditamentoId());
					redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.inserito", "success"));
					return "redirect:/accreditamento/{accreditamentoId}";
				}
			}catch(Exception ex){
				//TODO gestione eccezione
				model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
				return EDIT;
			}


		}catch (Exception ex){
			//TODO gestione eccezione
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return EDIT;
		}
	}
	
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/{personaId}/remove")
	public String removeComponenteComitatoScientifico(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long personaId, 
														Model model, RedirectAttributes redirectAttrs){
		try{
			personaService.delete(personaId);
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.componente_comitato_eliminato", "success"));
		}catch (Exception ex){
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
		}
		
		redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
		return "redirect:/accreditamento/{accreditamentoId}";
	}
	
	private void saveFiles(PersonaWrapper personaWrapper, MultipartFile attoNomina_multiPartFile, MultipartFile cv_multiPartFile, MultipartFile delega_multiPartFile){
		if(attoNomina_multiPartFile != null && !attoNomina_multiPartFile.isEmpty()){
			fileService.save(personaWrapper.getAttoNomina());
		}
		if(cv_multiPartFile != null && !cv_multiPartFile.isEmpty()){
			fileService.save(personaWrapper.getCv());
		}
		if(delega_multiPartFile != null && !delega_multiPartFile.isEmpty()){
			fileService.save(personaWrapper.getDelega());
		}
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
		return EDIT;
	}

	private PersonaWrapper preparePersonaWrapper(Persona persona){
		return preparePersonaWrapper(persona,0,0);
	}

	private PersonaWrapper preparePersonaWrapper(Persona persona, long accreditamentoId, long providerId){
		PersonaWrapper personaWrapper = new PersonaWrapper();

		personaWrapper.setPersona(persona);
		personaWrapper.setAccreditamentoId(accreditamentoId);
		personaWrapper.setProviderId(providerId);
		personaWrapper.setRuolo(persona.getRuolo());

		if(!persona.isNew()){
			Set<File> files = fileService.getFileFromPersona(persona.getId());
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
				personaWrapper.setOffsetAndIds(22, new LinkedList<Integer>(Arrays.asList(22,23,24,24,25,26,27,28,29)), accreditamentoIdEditabili);		
			else if(persona.isDelegatoLegaleRappresentante())
				personaWrapper.setOffsetAndIds(30, new LinkedList<Integer>(Arrays.asList(30,31,32,33,34,35,36,37)), accreditamentoIdEditabili);
			else if(persona.isResponsabileSegreteria())
				personaWrapper.setOffsetAndIds(46, new LinkedList<Integer>(Arrays.asList(46,47,48,49,50,51,52)), accreditamentoIdEditabili);
			else if(persona.isResponsabileAmministrativo())
				personaWrapper.setOffsetAndIds(53, new LinkedList<Integer>(Arrays.asList(53,54,55,56,57,58,59,60)), accreditamentoIdEditabili);
			else if(persona.isComponenteComitatoScientifico())
				personaWrapper.setOffsetAndIds(61, new LinkedList<Integer>(Arrays.asList(61,62,63,64,65,66,67,68,69,70)), accreditamentoIdEditabili);
			else if(persona.isResponsabileSistemaInformatico())
				personaWrapper.setOffsetAndIds(71, new LinkedList<Integer>(Arrays.asList(71,72,73,74,75,76,77,78)), accreditamentoIdEditabili);
			else if(persona.isResponsabileQualita())
				personaWrapper.setOffsetAndIds(79, new LinkedList<Integer>(Arrays.asList(79,80,81,82,83,84,85,86)), accreditamentoIdEditabili);
		}

		return personaWrapper;
	}
}
