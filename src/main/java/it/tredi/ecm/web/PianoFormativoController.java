package it.tredi.ecm.web;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.service.PianoFormativoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.PianoFormativoWrapper;

@Controller
public class PianoFormativoController {
	public static final Logger LOGGER = LoggerFactory.getLogger(PianoFormativoController.class);
	private final String NEW = "evento/pianoFormativoNew";
	private final String EDITPIANO = "evento/pianoFormativoEdit";
	private final String SHOW = "evento/pianoFormativoShow";
	private final String LIST = "evento/pianoFormativoList";

	@Autowired private PianoFormativoService pianoFormativoService; 
	@Autowired private ProviderService providerService; 

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}
	
	/*
	 * CREAZIONE PIANO FORMATIVO
	 * */
	@PreAuthorize("@securityAccessServiceImpl.canInsertPianoFormativo(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/pianoFormativo/new")
	public String newPianoFormativo(@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/new"));
		try{
			return goToNew(model, preparePianoFormativoWrapper(providerId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}
	
	/*
	 * SALVATAGGIO PIANO FORMATIVO
	 * */
	@PreAuthorize("@securityAccessServiceImpl.canInsertPianoFormativo(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/pianoFormativo/save")
	public String savePianoFormativo(@ModelAttribute("pianoFormativoWrapper") PianoFormativoWrapper wrapper, @PathVariable Long providerId,	
			Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/pianoFormativo/save"));
		try{
			if(pianoFormativoService.exist(wrapper.getProviderId(), wrapper.getAnnoPianoFormativo())){
				model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
				return NEW;
			}else{
				pianoFormativoService.create(wrapper.getProviderId(), wrapper.getAnnoPianoFormativo());
				return EDITPIANO;
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/pianoFormativo/save"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}
	
	/*
	 * EDIT PIANO FORMATIVO
	 * */
	@PreAuthorize("@securityAccessServiceImpl.canEditPianoFormativo(principal,#providerId, #pianoFormativoId)")
	@RequestMapping("/provider/{providerId}/pianoFormativo/{pianoFormativoId}/edit")
	public String editPianoFormativo(@PathVariable Long providerId, @PathVariable Long pianoFormativoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/edit"));
		try{
			return EDITPIANO;
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}
	
	/*
	 * SHOW PIANO FORMATIVO
	 * */
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/pianoFormativo/{pianoFormativoId}/show")
	public String showPianoFormativo(@PathVariable Long providerId, @PathVariable Long pianoFormativoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/show"));
		try{
			model.addAttribute("pianoFormativo", pianoFormativoService.getPianoFormativo(pianoFormativoId));
			return SHOW;
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}
	
	/*
	 * SHOW LISTA PIANI FORMATIVI
	 * */
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/pianoFormativo/list")
	public String showListaPianiFormativi(@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/list"));
		try{
			model.addAttribute("pianoFormativoList", pianoFormativoService.getAllPianiFormativiForProvider(providerId));
			model.addAttribute("canInsertPianoFormativo", providerService.canInsertPianoFormativo(providerId));
			return LIST;
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}
	
	/*
	 * SHOW LISTA PIANI FORMATIVI (current Provider)
	 * */
	@RequestMapping("/provider/pianoFormativo/list")
	public String showListaPianiFormativiForCurrentProvider(Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/pianoFormativo/list"));
		try{
			Provider provider = providerService.getProvider();
			redirectAttrs.addAttribute("providerId", provider.getId());
			return "redirect:/provider/{providerId}/pianoFormativo/list";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/pianoFormativo/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}
	
	private String goToNew(Model model, PianoFormativoWrapper pianoFormativoWrapper){
		model.addAttribute("pianoFormativoWrapper", pianoFormativoWrapper);
		return NEW;
	}
	
	private PianoFormativoWrapper preparePianoFormativoWrapper(Long providerId){
		PianoFormativoWrapper wrapper = new PianoFormativoWrapper();
		
		wrapper.setProviderId(providerId);
		wrapper.setAnniDisponibiliList(getAnniDisponibiliList());
		
		return wrapper;
	}
	
	private Set<Integer> getAnniDisponibiliList(){
		int annoCorrente = LocalDate.now().getYear();
		Set<Integer> anniDisponibiliList = new HashSet<Integer>();
		anniDisponibiliList.add(new Integer(annoCorrente));
		anniDisponibiliList.add(new Integer(annoCorrente + 1));
		anniDisponibiliList.add(new Integer(annoCorrente + 2));
		anniDisponibiliList.add(new Integer(annoCorrente + 3));
		return anniDisponibiliList;
	}
	
}