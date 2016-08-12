package it.tredi.ecm.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.repository.FieldEditabileAccreditamentoRepository;
import it.tredi.ecm.service.FieldValutazioneAccreditamentoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.ValutazioneService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.ProviderWrapper;
import it.tredi.ecm.web.validator.ProviderValidator;
import it.tredi.ecm.web.validator.ValutazioneValidator;

@Controller
public class ProviderController {
	private static final Logger LOGGER = LoggerFactory.getLogger(Provider.class);

	private final String EDIT = "provider/providerEdit";
	private final String SHOW = "provider/providerShow";
	private final String VALIDATE = "provider/providerValidate";

	@Autowired private ProviderService providerService;
	@Autowired private ProviderValidator providerValidator;
	@Autowired private ValutazioneValidator valutazioneValidator;
	@Autowired private FieldEditabileAccreditamentoRepository fieldEditabileRepository;
	@Autowired private FieldValutazioneAccreditamentoService fieldValutazioneAccreditamentoService;
	@Autowired private ValutazioneService valutazioneService;

	@InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

	/*** GLOBAL MODEL ATTRIBUTES***/

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
	@RequestMapping("/provider/show/all")
	public String showProviderFromCurrentUser(Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET: provider/show/all"));
		try {
			return goToShowProvider(model, providerService.getProvider());
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: provider/show/all"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect: /home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#id)")
	@RequestMapping("/provider/{id}/show/all")
	public String showProvider(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET: provider/" + id + "/show/all"));
		try {
			return goToShowProvider(model, providerService.getProvider(id));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: provider/" + id + "/show/all"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: provider/list"));
			return "redirect: /provider/list";
		}
	}

	private String goToShowProvider(Model model, Provider provider){
		model.addAttribute("provider",provider);
		LOGGER.info(Utils.getLogMessage("VIEW: provider/providerShowAll"));
		return "provider/providerShowAll";
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#id)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{id}/edit")
	public String editProviderFromAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/edit"));
		try {
			return goToEdit(model, prepareProviderWrapperEdit(providerService.getProvider(id), accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId));
			return "redirect:/accreditamento/" + accreditamentoId;
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canShowProvider(principal,#id)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{id}/show")
	public String showProviderFromAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long id,
			@RequestParam(required = false) String from, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/show"));
		try {
			if (from != null) {
				redirectAttrs.addFlashAttribute("mode", from);
				return "redirect:/accreditamento/" + accreditamentoId + "/provider/" + id + "/show";
			}
			return goToShow(model, prepareProviderWrapperShow(providerService.getProvider(id), accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/show"));
			return "redirect:/accreditamento/" + accreditamentoId + "/show";
		}
	}

	/*** VALUTAZIONE ***/
//	@PreAuthorize("@securityAccessServiceImpl.canValidateAccreditamento(principal,#accreditamentoId)") TODO
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{id}/validate")
	public String validateProviderFromAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long id,
			Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/validate"));
		try {
			return goToValidate(model, prepareProviderWrapperValidate(providerService.getProvider(id), accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/validate"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/validate"));
			return "redirect:/accreditamento/" + accreditamentoId + "/validate";
		}
	}

	/***	SAVE	***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/save", method = RequestMethod.POST)
	public String salvaProvider(@ModelAttribute("providerWrapper") ProviderWrapper providerWrapper, BindingResult result,
									Model model, RedirectAttributes redirectAttrs, @PathVariable Long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/save"));
		try{
			//validazione del provider
			providerValidator.validateForAccreditamento(providerWrapper.getProvider(), result, "provider.");

			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				providerService.save(providerWrapper.getProvider());
				redirectAttrs.addAttribute("accreditamentoId", providerWrapper.getAccreditamentoId());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.provider_salvato", "success"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
				return "redirect:/accreditamento/{accreditamentoId}/edit";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/save"),ex);
			model.addAttribute("accreditamentoId",providerWrapper.getAccreditamentoId());
			model.addAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
			return EDIT;
		}
	}

	/***	SALVA VALUTAZIONE	***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/validate", method = RequestMethod.POST)
	public String valutaProvider(@ModelAttribute("providerWrapper") ProviderWrapper providerWrapper, BindingResult result,
									Model model, RedirectAttributes redirectAttrs, @PathVariable Long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/validate"));
		try{
			//validazione del provider
			valutazioneValidator.validateValutazione(providerWrapper.getMappa(), result);

			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
				return VALIDATE;
			}else{
				Accreditamento accreditamento = new Accreditamento();
				accreditamento.setId(providerWrapper.getAccreditamentoId());
				providerWrapper.getMappa().forEach((k, v) -> {
					v.setIdField(k);
					v.setAccreditamento(accreditamento);
				});
				Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamento.getId(), Utils.getAuthenticatedUser().getAccount().getId());
				Set<FieldValutazioneAccreditamento> values = new HashSet<FieldValutazioneAccreditamento>(fieldValutazioneAccreditamentoService.saveMapList(providerWrapper.getMappa()));
				valutazione.setValutazioni(values);
				valutazioneService.save(valutazione);
//
				redirectAttrs.addAttribute("accreditamentoId", providerWrapper.getAccreditamentoId());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.valutazione_salvata", "success"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/validate"));
				return "redirect:/accreditamento/{accreditamentoId}/validate";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/validate"),ex);
			model.addAttribute("accreditamentoId",providerWrapper.getAccreditamentoId());
			model.addAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
			return VALIDATE;
		}
	}


	@PreAuthorize("@securityAccessServiceImpl.canShowAllProvider(principal)")
	@RequestMapping("/provider/list")
	public String showAll(Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET: /provider/list"));
		try {
			model.addAttribute("providerList", providerService.getAll());
			LOGGER.info(Utils.getLogMessage("VIEW: /provider/providerList"));
			return "provider/providerList";
		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET: /provider/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	private String goToEdit(Model model, ProviderWrapper providerWrapper){
		model.addAttribute("providerWrapper",providerWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	private String goToShow(Model model, ProviderWrapper providerWrapper){
		model.addAttribute("providerWrapper", providerWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
		return SHOW;
	}

	private String goToValidate(Model model, ProviderWrapper providerWrapper){
		model.addAttribute("providerWrapper", providerWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
		return VALIDATE;
	}

	private ProviderWrapper prepareProviderWrapperEdit(Provider provider, Long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("prepareProviderWrapperEdit("+ provider.getId() + "," + accreditamentoId +") - entering"));
		ProviderWrapper providerWrapper = new ProviderWrapper();
		providerWrapper.setProvider(provider);
		providerWrapper.setAccreditamentoId(accreditamentoId);
		providerWrapper.setIdEditabili(Utils.getSubsetOfIdFieldEnum(fieldEditabileRepository.findAllByAccreditamentoId(accreditamentoId), SubSetFieldEnum.PROVIDER));
		//providerWrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_PROVIDER), accreditamentoService.getIdEditabili(accreditamentoId));
		LOGGER.info(Utils.getLogMessage("prepareProviderWrapperEdit("+ provider.getId() + "," + accreditamentoId +") - exiting"));
		return providerWrapper;
	}

	private ProviderWrapper prepareProviderWrapperShow(Provider provider, Long accreditamentoId) {
		LOGGER.info(Utils.getLogMessage("prepareProviderWrapperShow("+ provider.getId() + "," + accreditamentoId +") - entering"));
		ProviderWrapper providerWrapper = new ProviderWrapper();
		providerWrapper.setProvider(provider);
		providerWrapper.setAccreditamentoId(accreditamentoId);
		LOGGER.info(Utils.getLogMessage("prepareProviderWrapperShow("+ provider.getId() + "," + accreditamentoId +") - exiting"));
		return providerWrapper;
	}

	private ProviderWrapper prepareProviderWrapperValidate(Provider provider, Long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("prepareProviderWrapperValidate("+ provider.getId() + "," + accreditamentoId +") - entering"));
		ProviderWrapper providerWrapper = new ProviderWrapper();

		//carico la valutazione per l'utente
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		if(valutazione != null) {
			for (FieldValutazioneAccreditamento v : valutazione.getValutazioni()) {
				mappa.put(v.getIdField(), v);
			}
		}
		providerWrapper.setMappa(mappa);
		providerWrapper.setProvider(provider);
		providerWrapper.setAccreditamentoId(accreditamentoId);
		LOGGER.info(Utils.getLogMessage("prepareProviderWrapperValidate("+ provider.getId() + "," + accreditamentoId +") - exiting"));
		return providerWrapper;
	}

}
