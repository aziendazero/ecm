package it.tredi.ecm.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.SedeService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.SedeWrapper;
import it.tredi.ecm.web.validator.SedeValidator;

@Controller
public class SedeController {
	public static final Logger LOGGER = LoggerFactory.getLogger(SedeController.class);

	private final String EDIT = "sede/sedeEdit";

	@Autowired private SedeService sedeService;
	@Autowired private ProviderService providerService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private SedeValidator sedeValidator;

	/***	GLOBAL MODEL ATTRIBUTES	***/
	@ModelAttribute("elencoProvince")
	public List<String> getElencoProvince(){
		List<String> elencoProvince = new ArrayList<String>();

		elencoProvince.add("Belluno");
		elencoProvince.add("Padova");
		elencoProvince.add("Rovigo");
		elencoProvince.add("Treviso");
		elencoProvince.add("Venezia");
		elencoProvince.add("Verona");
		elencoProvince.add("Vicenza");

		return elencoProvince;
	}

	@RequestMapping("/comuni")
	@ResponseBody
	public List<String>getElencoComuni(@RequestParam String provincia){
		HashMap<String, List<String>> elencoComuni = new HashMap<String, List<String>>();

		List<String> provinciaA = new ArrayList<String>();
		provinciaA.add("comuneA - 1");
		provinciaA.add("comuneA - 2");
		provinciaA.add("comuneA - 3");

		List<String> provinciaB = new ArrayList<String>();
		provinciaB.add("comuneB - 1");
		provinciaB.add("comuneB - 2");
		provinciaB.add("comuneB - 3");

		List<String> provinciaC = new ArrayList<String>();
		provinciaC.add("comuneC - 1");
		provinciaC.add("comuneC - 2");
		provinciaC.add("comuneC - 3");

		elencoComuni.put("Venezia", provinciaA);
		elencoComuni.put("Padova", provinciaB);
		elencoComuni.put("Trieste", provinciaC);

		return elencoComuni.get(provincia);
	}

	@RequestMapping("/cap")
	@ResponseBody
	public List<String>getElencoCap(@RequestParam String comune){
		HashMap<String, List<String>> elencoCap = new HashMap<String, List<String>>();

		List<String> capVenezia = new ArrayList<String>();
		capVenezia.add("11111");
		capVenezia.add("22222");
		capVenezia.add("33333");

		List<String> capPadova = new ArrayList<String>();
		capPadova.add("44444");
		capPadova.add("55555");
		capPadova.add("66666");

		List<String> capTrieste = new ArrayList<String>();
		capTrieste.add("77777");
		capTrieste.add("88888");
		capTrieste.add("99999");

		elencoCap.put("comuneA - 1", capVenezia);
		elencoCap.put("comuneB - 1", capPadova);
		elencoCap.put("comuneC - 1", capTrieste);

		return elencoCap.get(comune);
	}

	@ModelAttribute("sedeWrapper")
	public SedeWrapper getSede(@RequestParam(name = "editId", required = false) Long id){
		if(id != null){
			SedeWrapper sedeWrapper = new SedeWrapper(); 
			sedeWrapper.setSede(sedeService.getSede(id));
			return sedeWrapper;
		}
		return new SedeWrapper();
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/sede/copia")
	public String getCopiaSedeLegale(@PathVariable Long accreditamentoId, @PathVariable Long providerId, Model model){
		Utils.logInfo(LOGGER, "GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/sede/copia");
		try {
			Sede sedeLegale = new Sede();
			Provider provider = providerService.getProvider(providerId);
			if(provider != null){
				sedeLegale = provider.getSedeLegale();
			}

			if(sedeLegale != null){
				//preparo il wrapper specificando che è una sedeOperativa
				//magic number (rende non editabili tutti i campi della sede operativa, ma consente ugualmente il salvataggio)
				//refresh solo del fragment della view
				SedeWrapper sedeWrapper = prepareSedeWrapper(sedeLegale, Costanti.SEDE_OPERATIVA, accreditamentoId, providerId);
				sedeWrapper.setIdEditabili(Arrays.asList(0));
				return goToEditWhitFragment(model, sedeWrapper, "content");
			}else{
				model.addAttribute("message", new Message("message.warning", "message.manca_sedeLegale", "warning"));
				Utils.logInfo(LOGGER, "VIEW: " + EDIT);
				return EDIT + ":: content";
			}
		}catch (Exception ex) {
			Utils.logError(LOGGER, "GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/sede/copia",ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			Utils.logInfo(LOGGER, "VIEW: " + EDIT);
			return EDIT;
		}
	}

	/***	NEW / EDIT 	***/
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/sede/new")
	public String getNewSedeCurrentProvider(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @RequestParam("tipologiaSede") String tipologiaSede, 
			Model model, RedirectAttributes redirectAttrs) throws Exception{
		Utils.logInfo(LOGGER, "GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/sede/new");
		try {
			return goToEdit(model, prepareSedeWrapper(new Sede(), tipologiaSede, accreditamentoId, providerId));
		}catch (Exception ex){
			Utils.logError(LOGGER, "GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/sede/new",ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			Utils.logInfo(LOGGER, "GET /accreditamento/" + accreditamentoId);
			return "redirect:/accreditamento" + accreditamentoId;
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/sede/{id}/edit")
	public String editSede(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long id, 
			@RequestParam("tipologiaSede") String tipologiaSede, Model model){
		Utils.logInfo(LOGGER, "GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/sede/" + id + "/edit");
		try {
			SedeWrapper sedeWrapper = prepareSedeWrapper(sedeService.getSede(id), tipologiaSede, accreditamentoId, providerId);

			if(tipologiaSede.equals(Costanti.SEDE_OPERATIVA)){
				Provider provider = providerService.getProvider(providerId);
				if(provider != null){
					if(provider.getSedeLegale() != null && provider.getSedeOperativa() != null){
						if(provider.getSedeLegale().getId().equals(provider.getSedeOperativa().getId())){
							List<Integer> list = new ArrayList<Integer>();
							list.add(0);
							sedeWrapper.setIdEditabili(list);
						}
					}
				}
			}
			return goToEdit(model, sedeWrapper);
		}catch (Exception ex){
			Utils.logError(LOGGER, "GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/sede/" + id + "/edit",ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			Utils.logInfo(LOGGER, "VIEW: " + EDIT);
			return EDIT;
		}
	}

	/***	SAVE 	***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/{providerId}/sede/save", method = RequestMethod.POST)
	public String saveSede(@ModelAttribute("sedeWrapper") SedeWrapper sedeWrapper, BindingResult result,  
			Model model, @PathVariable Long providerId, RedirectAttributes redirectAttrs, @PathVariable Long accreditamentoId){
		Utils.logInfo(LOGGER, "GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/sede/save");
		try{
			sedeValidator.validate(sedeWrapper.getSede(), result, "sede.");
			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				Utils.logInfo(LOGGER, "VIEW: " + EDIT);
				return EDIT;
			}else{
				sedeService.save(sedeWrapper.getSede(), providerService.getProvider(providerId), sedeWrapper.getTipologiaSede());	
				redirectAttrs.addAttribute("accreditamentoId", sedeWrapper.getAccreditamentoId());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.sede_salvata", "success"));
				Utils.logInfo(LOGGER, "REDIRECT: /accreditamento/" + accreditamentoId);
				return "redirect:/accreditamento/{accreditamentoId}";
			}
		}catch(Exception ex){
			Utils.logError(LOGGER, "GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/sede/save",ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			Utils.logInfo(LOGGER, "VIEW: " + EDIT);
			return EDIT;
		}
	}

	private String goToEdit(Model model, SedeWrapper sedeWrapper){
		model.addAttribute("sedeWrapper", sedeWrapper);
		Utils.logInfo(LOGGER, "VIEW: " + EDIT);
		return EDIT;
	}

	private String goToEditWhitFragment(Model model, SedeWrapper sedeWrapper, String fragment){
		model.addAttribute("sedeWrapper", sedeWrapper);
		Utils.logInfo(LOGGER, "VIEW: " + EDIT + " :: " + fragment);
		return EDIT + " :: " + fragment;
	}

	private SedeWrapper prepareSedeWrapper(Sede sede, String tipologiaSede, long accreditamentoId, long providerId){
		Utils.logInfo(LOGGER, "prepareSedeWrapper(" + sede.getId() + "," + tipologiaSede + "," + accreditamentoId + "," + providerId +") - entering");
		SedeWrapper sedeWrapper = new SedeWrapper();

		sedeWrapper.setSede(sede);
		sedeWrapper.setTipologiaSede(tipologiaSede);

		if(tipologiaSede.equals(Costanti.SEDE_LEGALE)) 
			sedeWrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_SEDE_LEGALE), accreditamentoService.getIdEditabili(accreditamentoId));
		else 
			sedeWrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_SEDE_OPERATIVA), accreditamentoService.getIdEditabili(accreditamentoId));

		sedeWrapper.setAccreditamentoId(accreditamentoId);
		sedeWrapper.setProviderId(providerId);
		Utils.logInfo(LOGGER, "prepareSedeWrapper(" + sede.getId() + "," + tipologiaSede + "," + accreditamentoId + "," + providerId +") - exiting");
		return sedeWrapper;
	}
}
