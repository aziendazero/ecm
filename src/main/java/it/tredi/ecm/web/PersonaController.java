package it.tredi.ecm.web;

import java.util.Arrays;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.PersonaService;
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
	private FileService fileService;
	
	@Autowired
	private PersonaValidator personaValidator;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("personaWrapper")
	public PersonaWrapper getPersonaWrapper(@RequestParam(value="editId",required = false) Long id,
											@RequestParam(value="editId_Anagrafica",required = false) Long anagraficaId){
		if(id != null){
			Persona persona = personaService.getPersona(id);
			if(anagraficaId == null)
				persona.setAnagrafica(null);
				
			return preparePersonaWrapper(persona); 	
		}
		return new PersonaWrapper();
	}
	
	/***	NUOVA PERSONA ***/
	/* (passando ruolo e providerId) */	
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/new")
	public String newPersona(@PathVariable Long accreditamentoId, @PathVariable Long providerId, Model model,
								@RequestParam(name="ruolo", required = true) String ruolo){
		
		return goToEdit(model, preparePersonaWrapper(createPersona(providerId, ruolo), accreditamentoId, providerId));
	}
	
	/***	NUOVA ANAGRAFICA ***/
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/newAnagrafica")
	public String newAnagrafica(@PathVariable Long accreditamentoId, @PathVariable Long providerId, Model model,
									@RequestParam(name="ruolo", required = true) String ruolo){
		
		Persona persona = providerService.getPersonaByRuolo(Ruolo.valueOf(ruolo), providerId);
		if(persona == null){
			persona = createPersona(providerId, ruolo);
		}else{
			persona.setAnagrafica(new Anagrafica());
		}
		
		return goToEdit(model, preparePersonaWrapper(persona, accreditamentoId, providerId));
	}

	/***	EDIT PERSONA ***/
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/{id}/edit")
	public String editPersona(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long id, Model model){
		Persona persona = personaService.getPersona(id);
		if(persona == null){
			persona = createPersona(providerId);
		}

		return goToEdit(model, preparePersonaWrapper(persona, accreditamentoId, providerId));
	}

	/***	SAVE PERSONA ***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/{providerId}/persona/save", method = RequestMethod.POST)
	public String savePersona(@ModelAttribute("personaWrapper") PersonaWrapper personaWrapper, BindingResult result,
								RedirectAttributes redirectAttrs, Model model,
								@RequestParam(value = "attoNomina_multipart", required = false) MultipartFile attoNomina_multiPartFile,
								@RequestParam(value = "cv_multipart", required = false) MultipartFile cv_multiPartFile,
								@RequestParam(value = "delega_multipart", required = false) MultipartFile delega_multiPartFile){
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
				
				model.addAttribute("message",new Message("Errore", "message.conferma_registrazione", "error"));
				return EDIT;
			}else{
					personaService.save(personaWrapper.getPersona());
					saveFiles(personaWrapper, attoNomina_multiPartFile, cv_multiPartFile, delega_multiPartFile);
					
					redirectAttrs.addAttribute("accreditamentoId", personaWrapper.getAccreditamentoId());
					return "redirect:/accreditamento/{accreditamentoId}";
			}
		}catch(Exception ex){
			//TODO Exception
		}
		
		return EDIT;
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
		
		//TODO logica per recuperare idEditabili ed idOffset
		personaWrapper.setOffsetAndIds();
		if(persona.isLegaleRappresentante())
			personaWrapper.setIdEditabili(Arrays.asList(22,23,24,24,25,26,27,28,29));
		else if(persona.isDelegatoLegaleRappresentante())
			personaWrapper.setIdEditabili(Arrays.asList(30,31,32,33,34,35,36,37));
		else if(persona.isResponsabileSegreteria())
			personaWrapper.setIdEditabili(Arrays.asList(46,47,48,49,50,52));
		else if(persona.isResponsabileAmministrativo())
			personaWrapper.setIdEditabili(Arrays.asList(53,54,55,56,57,58,59,60));
		else if(persona.isResponsabileSistemaInformatico())
			personaWrapper.setIdEditabili(Arrays.asList(71,72,73,74,75,76,77,78));
		else if(persona.isResponsabileQualita())
			personaWrapper.setIdEditabili(Arrays.asList(79,80,81,82,83,84,85,86));

		return personaWrapper;
	}
}
