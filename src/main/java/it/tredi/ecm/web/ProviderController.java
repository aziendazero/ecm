package it.tredi.ecm.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
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
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.ProviderWrapper;
import it.tredi.ecm.web.validator.ProviderValidator;

@Controller
public class ProviderController {
	
	private final String EDIT = "provider/providerEdit";
	
	@Autowired
	private ProviderService providerService;
	@Autowired
	private AccreditamentoService accreditamentoService;
	
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
	public String showProviderFromCurrentUser(Model model, RedirectAttributes redirectAttrs){
		try {
			return goToShowProvider(model, providerService.getProvider());
		}catch (Exception ex){
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:provider/list";
		}
	}
	
	@RequestMapping("provider/{id}/show")
	public String showProvider(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		try {
			return goToShowProvider(model, providerService.getProvider(id));
		}catch (Exception ex){
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:provider/list";
		}
	}
	
	private String goToShowProvider(Model model, Provider provider){
		model.addAttribute("provider",provider);
		return "provider/showProvider";
	}
	
	/***	EDIT	***/
	@RequestMapping("provider/{id}/edit")
	public String editProvider(@PathVariable Long id, Model model){
		try {	
			model.addAttribute("provider",providerService.getProvider(id));
			return "provider/providerEdit";
		}catch (Exception ex){
			//TODO gestione eccezione
			return "TODO";
		}
	}
	
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{id}/edit")
	public String editProviderFromAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		try {
			return goToEdit(model, prepareProviderWrapper(providerService.getProvider(id), accreditamentoId));
		}catch (Exception ex){
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/accreditamento/" + accreditamentoId;
		}
	}
	
	/***	SAVE	***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/save", method = RequestMethod.POST)
	public String salvaProvider(@ModelAttribute("providerWrapper") ProviderWrapper providerWrapper, BindingResult result, 
									Model model, RedirectAttributes redirectAttrs){
		try{
			//validazione del provider
			providerValidator.validateForAccreditamento(providerWrapper.getProvider(), result, "provider.");	
			
			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				return EDIT;
			}else{
				providerService.save(providerWrapper.getProvider());
				redirectAttrs.addAttribute("accreditamentoId", providerWrapper.getAccreditamentoId());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.provider_salvato", "success"));
				return "redirect:/accreditamento/{accreditamentoId}";
			}
		}catch (Exception ex){
			model.addAttribute("accreditamentoId",providerWrapper.getAccreditamentoId());
			return EDIT;
		}
	}
	
	@RequestMapping("/provider/list")
	public String showAll(Model model, RedirectAttributes redirectAttrs){
		try {
			model.addAttribute("providerList", providerService.getAll());
			return "provider/providerList";
		}catch (Exception ex) {
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	private String goToEdit(Model model, ProviderWrapper providerWrapper){
		model.addAttribute("providerWrapper",providerWrapper);
		return EDIT;
	}
	
	private ProviderWrapper prepareProviderWrapper(Provider provider, Long accreditamentoId){
		ProviderWrapper providerWrapper = new ProviderWrapper();
		providerWrapper.setProvider(provider);
		providerWrapper.setAccreditamentoId(accreditamentoId);
		providerWrapper.setOffsetAndIds(1, new LinkedList<Integer>(Arrays.asList(1,2,5,6,7)), accreditamentoService.getIdEditabili(accreditamentoId));
		
		return providerWrapper;
	}
	
}
