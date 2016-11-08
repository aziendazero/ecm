package it.tredi.ecm.web;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.RelazioneAnnuale;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.RelazioneAnnualeService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;

@Controller
public class RelazioneAnnualeController {
	public static final Logger LOGGER = LoggerFactory.getLogger(RelazioneAnnualeController.class);
	
	private final String LIST = "relazioneAnnuale/relazioneAnnualeList";
	private final String EDIT = "relazioneAnnuale/relazioneAnnualeEdit";
	private final String SHOW = "relazioneAnnuale/relazioneAnnualeShow";
	
	@Autowired private RelazioneAnnualeService relazioneAnnualeService;
	@Autowired private ProviderService providerService;
	
	@InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }
	
	@RequestMapping("/provider/relazioneAnnuale/list")
	public String getListRelazioniAnnualiForCurrentProvider(Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/relazioneAnnuale/list"));
		try {
			Provider currentProvider = providerService.getProvider();
			long providerId = currentProvider.getId();
			return "redirect:/provider/" + providerId + "/relazioneAnnuale/list";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/relazioneAnnuale/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/relazioneAnnuale/list")
	public String getListRelazioniAnnualiForProvider(@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/relazioneAnnuale/list"));
		try {
			model.addAttribute(providerId);
			model.addAttribute("canInsertRelazioneAnnuale", providerService.canInsertRelazioneAnnuale(providerId));
			model.addAttribute("relazioneList", relazioneAnnualeService.getAllRelazioneAnnualeByProviderId(providerId));
			LOGGER.info(Utils.getLogMessage("VIEW: " + LIST));
			return LIST;
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/relazioneAnnuale/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@PreAuthorize("@securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/relazioneAnnuale/insert")
	public String inserisciRelazioniAnnualiForProvider(@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/relazioneAnnuale/insert"));
		try {
			RelazioneAnnuale relazioneAnnuale = relazioneAnnualeService.createRelazioneAnnuale(providerId, LocalDate.now().getYear());
			relazioneAnnuale.elabora();
			relazioneAnnualeService.save(relazioneAnnuale);
			return "redirect:/provider/{providerId}/relazioneAnnuale/list";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/relazioneAnnuale/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
}
