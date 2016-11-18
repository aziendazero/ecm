package it.tredi.ecm.web;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.exception.EcmException;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.PianoFormativoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AccreditamentoWrapper;
import it.tredi.ecm.web.bean.EventoWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.PianoFormativoWrapper;

@Controller
public class PianoFormativoController {
	public static final Logger LOGGER = LoggerFactory.getLogger(PianoFormativoController.class);
	private final String NEW = "pianoFormativo/pianoFormativoNew";
	private final String EDIT = "pianoFormativo/pianoFormativoEdit";
	private final String SHOW = "pianoFormativo/pianoFormativoShow";
	private final String LIST = "pianoFormativo/pianoFormativoList";

	@Autowired private PianoFormativoService pianoFormativoService;
	@Autowired private ProviderService providerService;
	@Autowired private FileService fileService;

	@Autowired private HttpServletRequest request;

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
			return goToNew(model, preparePianoFormativoWrapper(new PianoFormativo(), providerId));
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
	@RequestMapping(value = "/provider/{providerId}/pianoFormativo/save", method = RequestMethod.POST)
	public String savePianoFormativo(@ModelAttribute("pianoFormativoWrapper") PianoFormativoWrapper wrapper, @PathVariable Long providerId,
			Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/pianoFormativo/save"));
		String referrer = request.getHeader("referer");
		try{
			if(pianoFormativoService.exist(wrapper.getProviderId(), wrapper.getPianoFormativo().getAnnoPianoFormativo())){
				redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.piano_formativo_anno_gia_inserito(${#numbers.formatInteger("+ wrapper.getPianoFormativo().getAnnoPianoFormativo() +",0)})", "error"));
				return "redirect:"+referrer;
			}else{
				pianoFormativoService.create(wrapper.getProviderId(), wrapper.getPianoFormativo().getAnnoPianoFormativo());
				return "redirect:"+referrer;
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
	@PreAuthorize("@securityAccessServiceImpl.canEditPianoFormativo(principal, #pianoFormativoId)")
	@RequestMapping("/provider/{providerId}/pianoFormativo/{pianoFormativoId}/edit")
	public String editPianoFormativo(@PathVariable Long providerId, @PathVariable Long pianoFormativoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/edit"));
		try{
			goToEdit(model, preparePianoFormativoWrapper(pianoFormativoService.getPianoFormativo(pianoFormativoId), providerId));
			return EDIT;
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
	public String showListaPianiFormativi(@PathVariable Long providerId, @RequestParam(required = false) String accordion, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/list"));
		try{
			PianoFormativoWrapper pianoFormativoWrapper = new PianoFormativoWrapper();
			pianoFormativoWrapper.setImportEventiDaCsvFile(new File(FileEnum.FILE_EVENTI_PIANO_FORMATIVO));
			model.addAttribute("pianoFormativoWrapper", pianoFormativoWrapper);
			model.addAttribute("pianoFormativoList", pianoFormativoService.getAllPianiFormativiForProvider(providerId));
			model.addAttribute("pianiIdFromAccreditamento", pianoFormativoService.getAllPianiFormativiIdInAccreditamentoForProvider(providerId));
			model.addAttribute("canInsertPianoFormativo", providerService.canInsertPianoFormativo(providerId));
			model.addAttribute("providerId", providerId);
			if (accordion != null) {
				model.addAttribute("accordion", accordion);
			}
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

	private String goToEdit(Model model, PianoFormativoWrapper pianoFormativoWrapper){
		model.addAttribute("pianoFormativoWrapper", pianoFormativoWrapper);
		return EDIT;
	}

	private PianoFormativoWrapper preparePianoFormativoWrapper(PianoFormativo pianoFormativo, Long providerId){
		PianoFormativoWrapper wrapper = new PianoFormativoWrapper();

		wrapper.setPianoFormativo(pianoFormativo);
		wrapper.setProviderId(providerId);
		wrapper.setAnniDisponibiliList(getAnniDisponibiliList());

		return wrapper;
	}

	private Set<Integer> getAnniDisponibiliList(){
		int annoCorrente = LocalDate.now().getYear();
		Set<Integer> anniDisponibiliList = new HashSet<Integer>();
		anniDisponibiliList.add(new Integer(annoCorrente + 1));
		return anniDisponibiliList;
	}
	
	@PreAuthorize("@securityAccessServiceImpl.canEditPianoFormativo(principal,#pianoFormativoId)")
	@RequestMapping(value = "/provider/{providerId}/pianoFormativo/{pianoFormativoId}/importaEventiDaCSV", method = RequestMethod.POST)
	public String importaEventiDaCSV(@PathVariable Long providerId,
			@PathVariable Long pianoFormativoId, @ModelAttribute("pianoFormativoWrapper") PianoFormativoWrapper wrapper, BindingResult result,
			Model model, RedirectAttributes redirectAttrs) {
		try{
			LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/importaEventiDaCSV"));
			model.addAttribute("returnLink", "/provider/" + providerId + "/evento/list");
			
			if(wrapper.getImportEventiDaCsvFile().getId() == null)
				redirectAttrs.addFlashAttribute("message", new Message("message.warning", "message.inserire_il_rendiconto", "alert"));
			else {
				File file = wrapper.getImportEventiDaCsvFile();
				if(file != null && !file.isNew()){
					LOGGER.info(Utils.getLogMessage("Ricevuto File id: " + file.getId() + " da importare"));
					String fileName = file.getNomeFile().trim().toUpperCase();
					if (fileName.endsWith(".CSV")) {
						wrapper.setImportEventiDaCsvFile(fileService.getFile(file.getId()));
						pianoFormativoService.importaEventiDaCSV(pianoFormativoId, wrapper.getImportEventiDaCsvFile());
						redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.xml_evento_validation_ok", "success"));
					}
					else {
						redirectAttrs.addFlashAttribute("message", new Message("message.errore", "error.formatNonAcceptedXML", "error"));
					}
				}
			}
			return "redirect:/provider/{providerId}/pianoFormativo/list?accordion=" + pianoFormativoService.getPianoFormativo(pianoFormativoId).getAnnoPianoFormativo();
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/importaEventiDaCSV"), ex);
				if (ex instanceof EcmException) //errore gestito
					redirectAttrs.addFlashAttribute("message", new Message(((EcmException) ex).getMessageTitle(), ((EcmException) ex).getMessageDetail(), "alert"));
				else
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/importaEventiDaCSV"));
			return "redirect:/provider/{providerId}/pianoFormativo/list?accordion=" + pianoFormativoService.getPianoFormativo(pianoFormativoId).getAnnoPianoFormativo();
		}
	}
	
	@PreAuthorize("@securityAccessServiceImpl.canEditPianoFormativo(principal,#pianoFormativoId)")
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/{providerId}/pianoFormativo/{pianoFormativoId}/importaEventiDaCSV", method = RequestMethod.POST)
	public String importaEventiDaCSVAccreditamnto(@PathVariable Long accreditamentoId, @PathVariable Long providerId,
			@PathVariable Long pianoFormativoId, @ModelAttribute("accreditamentoWrapper") AccreditamentoWrapper wrapper, BindingResult result,
			Model model, RedirectAttributes redirectAttrs) {
		try{
			LOGGER.info(Utils.getLogMessage("POST /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/importaEventiDaCSV"));
			
			if(wrapper.getImportEventiDaCsvFile().getId() == null)
				redirectAttrs.addFlashAttribute("message", new Message("message.warning", "message.inserire_il_rendiconto", "alert"));
			else {
				File file = wrapper.getImportEventiDaCsvFile();
				if(file != null && !file.isNew()){
					LOGGER.info(Utils.getLogMessage("Ricevuto File id: " + file.getId() + " da importare"));
					String fileName = file.getNomeFile().trim().toUpperCase();
					if (fileName.endsWith(".CSV")) {
						wrapper.setImportEventiDaCsvFile(fileService.getFile(file.getId()));
						pianoFormativoService.importaEventiDaCSV(pianoFormativoId, wrapper.getImportEventiDaCsvFile());
						redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.xml_evento_validation_ok", "success"));
					}
					else {
						redirectAttrs.addFlashAttribute("message", new Message("message.errore", "error.formatNonAcceptedXML", "error"));
					}
				}
			}
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/importaEventiDaCSV"), ex);
				if (ex instanceof EcmException) //errore gestito
					redirectAttrs.addFlashAttribute("message", new Message(((EcmException) ex).getMessageTitle(), ((EcmException) ex).getMessageDetail(), "alert"));
			}
		redirectAttrs.addFlashAttribute("currentTab","tab4");
		LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
		return "redirect:/accreditamento/{accreditamentoId}/edit";
	}

}