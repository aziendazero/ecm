package it.tredi.ecm.web;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.ProviderWrapper;
import it.tredi.ecm.web.validator.ProviderValidator;

@Controller
public class ProviderController {
	private static final Logger LOGGER = LoggerFactory.getLogger(Provider.class);
	
	private final String EDIT = "provider/providerEdit";

	@Autowired private ProviderService providerService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private ProviderValidator providerValidator;

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
	@RequestMapping("/provider/show")
	public String showProviderFromCurrentUser(Model model, RedirectAttributes redirectAttrs){
		Utils.logInfo(LOGGER, "GET: provider/show");
		try {
			return goToShowProvider(model, providerService.getProvider());
		}catch (Exception ex){
			Utils.logError(LOGGER, "GET: provider/show",ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			Utils.logInfo(LOGGER, "REDIRECT: /provider/list");
			return "redirect: /provider/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#id)")
	@RequestMapping("/provider/{id}/show")
	public String showProvider(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		Utils.logInfo(LOGGER, "GET: provider/" + id + "/show");
		try {
			return goToShowProvider(model, providerService.getProvider(id));
		}catch (Exception ex){
			Utils.logError(LOGGER, "GET: provider/" + id + "/show",ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			Utils.logInfo(LOGGER, "REDIRECT: provider/list");
			return "redirect: /provider/list";
		}
	}

	private String goToShowProvider(Model model, Provider provider){
		model.addAttribute("provider",provider);
		Utils.logInfo(LOGGER, "VIEW: provider/providerShow");
		return "provider/providerShow";
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#id)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{id}/edit")
	public String editProviderFromAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		Utils.logInfo(LOGGER, "GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/edit");
		try {
			return goToEdit(model, prepareProviderWrapper(providerService.getProvider(id), accreditamentoId));
		}catch (Exception ex){
			Utils.logError(LOGGER, "GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/edit",ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			Utils.logInfo(LOGGER, "REDIRECT: /accreditamento/" + accreditamentoId);
			return "redirect:/accreditamento/" + accreditamentoId;
		}
	}

	/***	SAVE	***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/save", method = RequestMethod.POST)
	public String salvaProvider(@ModelAttribute("providerWrapper") ProviderWrapper providerWrapper, BindingResult result,
									Model model, RedirectAttributes redirectAttrs, @PathVariable Long accreditamentoId){
		Utils.logInfo(LOGGER, "GET: /accreditamento/" + accreditamentoId + "/provider/save");
		try{
			//validazione del provider
			providerValidator.validateForAccreditamento(providerWrapper.getProvider(), result, "provider.");

			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				Utils.logInfo(LOGGER, "VIEW: " + EDIT);
				return EDIT;
			}else{
				providerService.save(providerWrapper.getProvider());
				redirectAttrs.addAttribute("accreditamentoId", providerWrapper.getAccreditamentoId());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.provider_salvato", "success"));
				Utils.logInfo(LOGGER, "REDIRECT: /accreditamento/" + accreditamentoId);
				return "redirect:/accreditamento/{accreditamentoId}";
			}
		}catch (Exception ex){
			Utils.logError(LOGGER, "GET: /accreditamento/" + accreditamentoId + "/provider/save",ex);
			model.addAttribute("accreditamentoId",providerWrapper.getAccreditamentoId());
			model.addAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			Utils.logInfo(LOGGER, "VIEW: " + EDIT);
			return EDIT;
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowAllProvider(principal)")
	@RequestMapping("/provider/list")
	public String showAll(Model model, RedirectAttributes redirectAttrs){
		Utils.logInfo(LOGGER, "GET: /provider/list");
		try {
			model.addAttribute("providerList", providerService.getAll());
			Utils.logInfo(LOGGER, "VIEW: /provider/providerList");
			return "provider/providerList";
		}catch (Exception ex) {
			Utils.logError(LOGGER, "GET: /provider/list",ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			Utils.logInfo(LOGGER, "REDIRECT: /home");
			return "redirect:/home";
		}
	}

	private String goToEdit(Model model, ProviderWrapper providerWrapper){
		model.addAttribute("providerWrapper",providerWrapper);
		Utils.logInfo(LOGGER, "VIEW: " + EDIT);
		return EDIT;
	}

	private ProviderWrapper prepareProviderWrapper(Provider provider, Long accreditamentoId){
		Utils.logInfo(LOGGER, "prepareProviderWrapper("+ provider.getId() + "," + accreditamentoId +") - entering");
		ProviderWrapper providerWrapper = new ProviderWrapper();
		providerWrapper.setProvider(provider);
		providerWrapper.setAccreditamentoId(accreditamentoId);
		providerWrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_PROVIDER), accreditamentoService.getIdEditabili(accreditamentoId));
		Utils.logInfo(LOGGER, "prepareProviderWrapper("+ provider.getId() + "," + accreditamentoId +") - exiting");
		return providerWrapper;
	}

}
