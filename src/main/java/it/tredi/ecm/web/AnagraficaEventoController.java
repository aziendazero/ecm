package it.tredi.ecm.web;

import java.util.Set;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.AnagraficaEvento;
import it.tredi.ecm.dao.entity.AnagraficaFullEvento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.service.AnagraficaEventoService;
import it.tredi.ecm.service.AnagraficaFullEventoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AnagraficaEventoWrapper;
import it.tredi.ecm.web.bean.AnagraficaFullEventoWrapper;
import it.tredi.ecm.web.bean.AnagraficaWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.AnagraficaValidator;

@Controller
public class AnagraficaEventoController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnagraficaEventoController.class);

	private static String SHOW = "anagraficaEvento/anagraficaEventoShow";
	private static String EDIT = "anagraficaEvento/anagraficaEventoEdit";
	private static String LIST = "anagraficaEvento/anagraficaEventoList";

	@Autowired private AnagraficaEventoService anagraficaEventoService;
	@Autowired private AnagraficaFullEventoService anagraficaFullEventoService;
	@Autowired private ProviderService providerService;
	@Autowired private FileService fileService;
	@Autowired private AnagraficaValidator anagraficaValidator;

	@InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

	@ModelAttribute("anagraficaEventoWrapper")
	public AnagraficaEventoWrapper getAnagraficaPreRequest(@RequestParam(value="editId",required = false) Long id){
		if(id != null)
			return prepareWrapperForReloadByEditId(anagraficaEventoService.getAnagraficaEvento(id));
		return new AnagraficaEventoWrapper();
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/anagraficaEventoList")
	@ResponseBody
	public Set<AnagraficaEvento>getAnagraficheEventoDelProvider(@PathVariable Long providerId){
		Set<AnagraficaEvento> lista = anagraficaEventoService.getAllAnagaficheByProvider(providerId);
		return lista;
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/anagraficaFullEventoList")
	@ResponseBody
	public Set<AnagraficaFullEvento>getAnagraficheFullEventoDelProvider(@PathVariable Long providerId){
		Set<AnagraficaFullEvento> lista = anagraficaFullEventoService.getAllAnagraficheFullEventoByProvider(providerId);
		return lista;
	}

	@RequestMapping(value="/provider/{providerId}/createAnagraficaEvento", method=RequestMethod.POST)
	@ResponseBody
	public String saveAnagraficaEvento(@PathVariable("providerId") Long providerId, AnagraficaEvento anagrafica){
		anagrafica.setProvider(providerService.getProvider(providerId));
		anagraficaEventoService.save(anagrafica);
		return "OK";
	}

	@RequestMapping("/provider/anagraficaEvento/list")
	public String showAnagraficaList(Model model, RedirectAttributes redirectAttrs){
		try {
			LOGGER.info(Utils.getLogMessage("GET /provider/anagraficaEvento/list"));
			Provider provider = providerService.getProvider();
			return "redirect:/provider/" + provider.getId() + "/anagraficaEvento/list";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/anagraficaEvento/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/anagraficaEvento/list")
	public String showAnagraficaListForProvider(@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		try {
			LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/anagraficaEvento/list"));
			model.addAttribute("anagraficaEventoList", anagraficaEventoService.getAllAnagaficheByProvider(providerId));
			model.addAttribute("anagraficaFullEventoList", anagraficaFullEventoService.getAllAnagraficheFullEventoByProvider(providerId));
			LOGGER.info(Utils.getLogMessage("VIEW: " + LIST));
			return LIST;
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/anagrafica/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/anagraficaEvento/{anagraficaEventoId}/show")
	public String showAnagraficaEventoForProvider(@PathVariable Long providerId, @PathVariable Long anagraficaEventoId, Model model, RedirectAttributes redirectAttrs){
		try {
			LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/anagraficaEvento/" + anagraficaEventoId + "/show"));
			return goToShow(model, prepareAnagraficaEventoWrapper(anagraficaEventoService.getAnagraficaEvento(anagraficaEventoId), providerId, false));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/anagraficaEvento/" + anagraficaEventoId + "/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/provider/" + providerId + "/anagraficaEvento/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/anagraficaEvento/{anagraficaId}/edit")
	public String editAnagraficaEvento(@PathVariable Long providerId, @PathVariable Long anagraficaId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/{providerId}/anagraficaEvento/{anagraficaId}/edit"));
		try {
			return goToEdit(model, prepareAnagraficaEventoWrapper(anagraficaEventoService.getAnagraficaEvento(anagraficaId),providerId,false));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/anagraficaEvento/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/provider/" + providerId + "/anagraficaEvento/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping(value = "/provider/{providerId}/anagraficaEvento/save", method = RequestMethod.POST)
	public String saveAnagrafica(@ModelAttribute("anagraficaEventoWrapper") AnagraficaEventoWrapper wrapper, BindingResult result,
									@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /provider/{providerId}/anagraficaEvento/save"));
		try {

			File file = wrapper.getCv();
			if(file != null && !file.isNew())
				wrapper.getAnagraficaEvento().getAnagrafica().setCv(fileService.getFile(file.getId()));

			anagraficaValidator.validateAnagraficaEvento(wrapper.getAnagraficaEvento(), result, "anagraficaEvento.anagrafica.", providerId);
			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				anagraficaEventoService.save(wrapper.getAnagraficaEvento());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.anagrafica_salvata", "success"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/anagraficaEvento/list"));
				return "redirect:/provider/" + providerId + "/anagraficaEvento/list";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /provider/{providerId}/anagraficaEvento/save"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/provider/" + providerId + "/anagraficaEvento/list";
		}
	}

	private String goToEdit(Model model, AnagraficaEventoWrapper wrapper){
		model.addAttribute("anagraficaEventoWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	private String goToShow(Model model, AnagraficaEventoWrapper wrapper){
		model.addAttribute("anagraficaEventoWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
		return SHOW;
	}

	//utilizzato save (passa editId come hidden param)
	private AnagraficaEventoWrapper prepareWrapperForReloadByEditId(AnagraficaEvento anagraficaEvento){
		return prepareAnagraficaEventoWrapper(anagraficaEvento, anagraficaEvento.getProvider().getId(),true);
	}

	private AnagraficaEventoWrapper prepareAnagraficaEventoWrapper(AnagraficaEvento anagraficaEvento, Long providerId,boolean reloadById){
		LOGGER.info(Utils.getLogMessage("prepareAnagraficaWrapper(" + anagraficaEvento.getId() + "," + providerId + "," + reloadById + " - entering"));
		AnagraficaEventoWrapper wrapper = new AnagraficaEventoWrapper();

		wrapper.setAnagraficaEvento(anagraficaEvento);
		wrapper.setProviderId(providerId);

		if(!reloadById)
			wrapper.setCv(anagraficaEvento.getAnagrafica().getCv());

		LOGGER.info(Utils.getLogMessage("prepareAnagraficaWrapper(" + anagraficaEvento.getId() + "," + providerId + ") - exiting"));
		return wrapper;
	}


}
