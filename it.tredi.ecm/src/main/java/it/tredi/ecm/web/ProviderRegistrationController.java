package it.tredi.ecm.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.service.bean.ProviderRegistrationWrapper;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.validator.ProviderRegistrationWrapperValidator;

@Controller
public class ProviderRegistrationController {
	
	@Autowired
	private ProviderService providerService;
	
	@Autowired
	private ProviderRegistrationWrapperValidator providerRegistrationValidator;
	
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}
	
	@ModelAttribute("tipoOrganizzatoreList")
	public TipoOrganizzatore[] getListTipoOrganizzatore(){
		return TipoOrganizzatore.values();
	}
	
	@ModelAttribute("providerForm")
	public ProviderRegistrationWrapper getProviderRegistrationWrapperPreRequest(@RequestParam(value="editId",required = false) Long id){
		return providerService.getProviderRegistrationWrapper();
	}

	/** Public provider registration form. */
	@RequestMapping(value = "/providerRegistration", method = RequestMethod.GET)
	public String providerRegistration(Model model) {
		model.addAttribute("providerForm", providerService.getProviderRegistrationWrapper());
		model.addAttribute("stepToShow", 0);
		return "providerRegistration";
	}
	
	/** Private provider registration form for authenticated users. */
	@RequestMapping("/providerRegistration/edit")
	public String editProviderRegistration(Model model){
		model.addAttribute("providerForm", providerService.getProviderRegistrationWrapper());
		return "provider/editProvider";
	}

	@RequestMapping(value = "/providerRegistration", method = RequestMethod.POST)
	public String registraProvider(@ModelAttribute("providerForm") ProviderRegistrationWrapper providerRegistrationWrapper, 
									BindingResult result, Model model, 
									@RequestParam( value="saveTypeMinimal",required = false) String saveTypeMinimal,
									@RequestParam( value="saveTypeFull",required = false) String saveTypeFull){
		try{
			//validazione solo del provider oppure dell'intero form?
			boolean saveMinimal = (saveTypeMinimal != null && saveTypeFull == null) ? true : false; 
			providerRegistrationValidator.validate(providerRegistrationWrapper, result, saveMinimal);		
			
			if(result.hasErrors()){
				model.addAttribute("stepToShow", evaluateErrorStep(result));
				return returnToProviderRegistrationForm();
			}else{
				providerService.saveProviderRegistrationWrapper(providerRegistrationWrapper, saveMinimal);
				return "redirect:home";
			}
		}catch (Exception ex){
			System.out.println("ECCEZIONEEEEE");
			model.addAttribute("stepToShow", evaluateErrorStep(result));
			return returnToProviderRegistrationForm();
		}
	}
	
	private int evaluateErrorStep(BindingResult result){
		if(result.hasFieldErrors("provider*"))
			return 0;
		if(result.hasFieldErrors("richiedente*"))
			return 1;
		if(result.hasFieldErrors("legale*"))
			return 2;
		return 0;
	}
			
	private String returnToProviderRegistrationForm(){
		CurrentUser currentUser = Utils.getAuthenticatedUser();
		if(currentUser != null){
			return "provider/editProvider";
		}else{
			return "providerRegistration";
		}
	}
}
