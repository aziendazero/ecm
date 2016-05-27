package it.tredi.ecm.web;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.web.bean.ProviderWrapper;
import it.tredi.ecm.web.validator.ProviderValidator;

@Controller
public class ProviderController {
	
	private final String EDIT = "provider/providerEdit";
	
	@Autowired
	private ProviderService providerService;
	@Autowired
	private ProviderValidator providerValidator;
	
	@InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

	/*** GLOBAL MODEL ATTRIBUTES***/
	@ModelAttribute("elencoRagioniSociali")
	public List<String> getListRagioniSociali(){
		//TODO recuperare elenco ragioni sociali
		List<String> ragioniSociali = new ArrayList<String>();
		ragioniSociali.add("srl");
		ragioniSociali.add("snc");
		ragioniSociali.add("spa");
		ragioniSociali.add("sas");
		return ragioniSociali;
	}
	
	@ModelAttribute("tipoOrganizzatoreList")
	public TipoOrganizzatore[] getListTipoOrganizzatore(){
		return TipoOrganizzatore.values();
	}
	
	@ModelAttribute("providerWrapper")
	public ProviderWrapper getProvider(@RequestParam(name = "editId", required = false) Long id){
		if(id != null){
			ProviderWrapper providerWrapper = new ProviderWrapper();
			providerWrapper.setProvider(providerService.getProvider(id));
			return providerWrapper;
		}
		return new ProviderWrapper();
	}
	/*** GLOBAL MODEL ATTRIBUTES***/
	
	/***	SHOW	***/
	@RequestMapping("provider/show")
	public String showProviderFromCurrentUser(Model model){
		return goToShowProvider(model, providerService.getProvider());
	}
	
	@RequestMapping("provider/{id}/show")
	public String showProvider(@PathVariable Long id, Model model){
		return goToShowProvider(model, providerService.getProvider(id));
	}
	
	private String goToShowProvider(Model model, Provider provider){
		model.addAttribute("provider",provider);
		return "provider/showProvider";
	}
	
	/***	EDIT	***/
	@RequestMapping("provider/{id}/edit")
	public String editProvider(@PathVariable Long id, Model model){
		model.addAttribute("provider",providerService.getProvider(id));
		return "provider/providerEdit";
	}
	
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{id}/edit")
	public String editProviderFromAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long id, Model model){
		return goToEdit(model, prepareProviderWrapper(providerService.getProvider(id), accreditamentoId));
	}
	
	/***	SAVE	***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/save", method = RequestMethod.POST)
	public String salvaProvider(@ModelAttribute("providerWrapper") ProviderWrapper providerWrapper, BindingResult result, 
									Model model, RedirectAttributes redirectAttrs){
		try{
			//validazione del provider
			providerValidator.validateForAccreditamento(providerWrapper.getProvider(), result, "provider.");	
			
			if(result.hasErrors()){
				return EDIT;
			}else{
				providerService.save(providerWrapper.getProvider());
				redirectAttrs.addAttribute("accreditamentoId", providerWrapper.getAccreditamentoId());
				return "redirect:/accreditamento/{accreditamentoId}";
			}
		}catch (Exception ex){
			model.addAttribute("accreditamentoId",providerWrapper.getAccreditamentoId());
			return "provider/providerEdit";
		}
	}
	
	@RequestMapping("/provider/list")
	public String showAll(Model model){
		model.addAttribute("providerList", providerService.getAll());
		return "provider/providerList";
	}
	
	private String goToEdit(Model model, ProviderWrapper providerWrapper){
		model.addAttribute("providerWrapper",providerWrapper);
		return EDIT;
	}
	
	private ProviderWrapper prepareProviderWrapper(Provider provider, Long accreditamentoId){
		ProviderWrapper providerWrapper = new ProviderWrapper();
		providerWrapper.setProvider(provider);
		providerWrapper.setAccreditamentoId(accreditamentoId);
		return providerWrapper;
	}
	
}
