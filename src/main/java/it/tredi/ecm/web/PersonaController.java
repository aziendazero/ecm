package it.tredi.ecm.web;

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

import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.service.PersonaService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.web.bean.PersonaWrapper;

@Controller
public class PersonaController {
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
		model.addAttribute("persona", persona);
		return "persona/editPersona";
	}
	
	@RequestMapping(value = "/provider/{providerId}/accreditamento/persona/{id}/edit", method = RequestMethod.POST)
	public String savePersona(@ModelAttribute("persona") Persona persona, BindingResult result){
		//TODO validazione persona
		if(result.hasErrors()){
			return "/provider/" + persona.getProvider().getId() +"/persona/" + persona.getId() + "/edit";
		}else{
			try{
				personaService.save(persona);
				return "redirect:/provider/" + persona.getProvider().getId() +"/accreditamento/";
			}catch(Exception ex){
				return "/provider/" + persona.getProvider().getId() +"/persona/" + persona.getId() + "/edit";
			}
		}
	}
	
	private Persona newPersona(Long providerId){
		Provider provider = providerService.getProvider(providerId);
		Persona persona = new Persona();
		persona.setProvider(provider);
		return persona;
	}
	
}
