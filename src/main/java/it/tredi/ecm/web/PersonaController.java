package it.tredi.ecm.web;

import java.util.Arrays;

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

import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.service.PersonaService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.web.bean.PersonaWrapper;

@Controller
public class PersonaController {

	private final String EDIT = "persona/personaEdit";

	@Autowired
	private PersonaService personaService;
	@Autowired
	private ProviderService providerService;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("personaWrapper")
	public PersonaWrapper getPersonaWrapper(@RequestParam(value="editId",required = false) Long id){
		PersonaWrapper personaWrapper = new PersonaWrapper();
		if(id != null){
			personaWrapper.setPersona(personaService.getPersona(id));
			return personaWrapper;
		}
		return new PersonaWrapper();
	}

	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/{id}/edit")
	public String editPersona(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long id, Model model){
		Persona persona = personaService.getPersona(id);
		if(persona == null){
			persona = newPersona(providerId);
		}

		return goToEdit(model, preparePersonaWrapper(persona, accreditamentoId, providerId));
	}

	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/{providerId}/persona/save", method = RequestMethod.POST)
	public String savePersona(@ModelAttribute("personaWrapper") PersonaWrapper personaWrapper, BindingResult result,
								RedirectAttributes redirectAttrs,
								@RequestParam(value = "nomina_persona", required = false) MultipartFile multiPartFile){
		//TODO validazione persona
		try{
			if(result.hasErrors()){
				return EDIT;
			}else{
				personaService.save(personaWrapper.getPersona());
				redirectAttrs.addAttribute("accreditamentoId", personaWrapper.getAccreditamentoId());
				return "redirect:/accreditamento/{accreditamentoId}";
			}
		}catch(Exception ex){
			//TODO Exception
		}
		
		return EDIT;
	}

	private Persona newPersona(Long providerId){
		Provider provider = providerService.getProvider(providerId);
		Persona persona = new Persona();
		persona.setProvider(provider);
		return persona;
	}

	private String goToEdit(Model model, PersonaWrapper personaWrapper){
		model.addAttribute("personaWrapper", personaWrapper);
		return EDIT;
	}

	private PersonaWrapper preparePersonaWrapper(Persona persona, long accreditamentoId, long providerId){
		PersonaWrapper personaWrapper = new PersonaWrapper();

		personaWrapper.setPersona(persona);
		personaWrapper.setAccreditamentoId(accreditamentoId);
		personaWrapper.setProviderId(providerId);

		//TODO logica per recuperare idEditabili ed idOffset
		personaWrapper.setOffsetAndIds();
		if(persona.isLegaleRappresentante())
			personaWrapper.setIdEditabili(Arrays.asList(17,18,19,20,21,22,23,24,24,25,26));
		else if(persona.isDelegatoLegaleRappresentante())
			personaWrapper.setIdEditabili(Arrays.asList(27,28,29,30,31,32,33,34,35));

		return personaWrapper;
	}
}
