package it.tredi.ecm.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.service.ProviderService;

@Controller
public class ProviderController {
	
	@Autowired
	private ProviderService providerService;
	
	@InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

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
	
	//TODO completare showallprovider
	@RequestMapping("provider/list")
	public String showAll(Model model){
		model.addAttribute("providerList", providerService.getAll());
		return "provider/showList";
	}
	
}
