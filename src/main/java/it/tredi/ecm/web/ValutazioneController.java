package it.tredi.ecm.web;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Equivalence.Wrapper;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.QuotaAnnuale;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.entity.ValutazioneCommissione;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.ValutazioneService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AccreditamentoWrapper;
import it.tredi.ecm.web.bean.FieldValutazioniRipetibiliWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.ScadenzaPagamentoProviderWrapper;
import it.tredi.ecm.web.bean.ValutazioneWrapper;
import scala.collection.immutable.HashSet;

@Controller
@SessionAttributes("valutazioneWrapper")
public class ValutazioneController {
	private static Logger LOGGER = LoggerFactory.getLogger(ValutazioneController.class);

	@Autowired private ValutazioneService valutazioneService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private AccountService accountService;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("valutazioneWrapper")
	public ValutazioneWrapper getValutazioneWrapper(@ModelAttribute("valutazioneWrapper") ValutazioneWrapper wrapper) {
		if(wrapper != null)
			return wrapper;
		return new ValutazioneWrapper();
	}
	
	@RequestMapping("/valutazioneDemo")
	public String showValutazioneDemo(RedirectAttributes redirectAttrs) throws Exception{
		LOGGER.info(Utils.getLogMessage("GET /valutazioneDemo"));
		return "segreteria/valutazione";
	}

	@RequestMapping("/accreditamento/{accreditamentoId}/valutazioniComplessive")
	@ResponseBody
	public LinkedList<Map<String,String>> getValutazioniComplessive(@PathVariable Long accreditamentoId){
		Set<Valutazione> valutazioni = valutazioneService.getAllValutazioniForAccreditamentoIdAndNotStoricizzato(accreditamentoId);
		LinkedList<Map<String,String>> result = new LinkedList<>();

		for(Valutazione v : valutazioni){
			Map<String,String> mappa = new HashMap<String, String>();
			String header = v.getAccount().isSegreteria() ? "Valutazione Segreteria ECM" : "Valutazione Referee";
			String value = v.getValutazioneComplessiva() == null ? "" : v.getValutazioneComplessiva();
			Account currentUser = Utils.getAuthenticatedUser().getAccount();
			if(currentUser.isReferee() || currentUser.isSegreteria())
				mappa.put("header", header + " - " + v.getAccount().getFullName());
			else
				mappa.put("header", header);
			mappa.put("value", value);

			result.add(mappa);
		}

		return result;
	}
	
	//TODO permission
	@RequestMapping("/accreditamento/{accreditamentoId}/valutazione/{valutazioneId}/show")
	public String showValutazione (@PathVariable Long accreditamentoId, @PathVariable Long valutazioneId, Model model, RedirectAttributes redirectAttrs) {
		try{
			ValutazioneWrapper wrapper = new ValutazioneWrapper();
			//prendo valutazione
			Valutazione valutazione = valutazioneService.getValutazione(valutazioneId);
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
			wrapper.setValutazione(valutazione);
			//costruisco la mappa <argomento, Map<IdFielEnum, fiedlValutazione> per i subset, la sede legale e il coordinatore del comitato
			Map<String, Map<IdFieldEnum, FieldValutazioneAccreditamento>> valutazioneSingoli = valutazioneService.getMapAllValutazioneSingoli(valutazione, accreditamento);
			wrapper.setValutazioneSingoli(valutazioneSingoli);
			//costruisco il set di FieldValutazioneRipetibiliWrapper per i ripetibili come sedi operative e componenti del comitato scientifico
			Map<String, FieldValutazioniRipetibiliWrapper> valutazioneRipetibili = valutazioneService.getMapAllValutazioneRipetibili(valutazione, accreditamento);
			wrapper.setValutazioneRipetibili(valutazioneRipetibili);
			model.addAttribute("valutazioneWrapper", wrapper);
			return "valutazione/valutazioneShowAllById :: showAllValutazioneById";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/valutazione/" + valutazioneId + "/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "valutazione/valutazioneShowAllById :: showAllValutazioneById";
		}
	}
	
	//Solo responsabile segreteria_ECM riassegnaAccountValutazione
	@PreAuthorize("@securityAccessServiceImpl.isUserSegreteria(principal)")
	@RequestMapping(value ="/accreditamento/{accreditamentoId}/riassegnaAccountValutazione")
	public String riassegna(@PathVariable Long accreditamentoId,@ModelAttribute("valutazioneWrapper") ValutazioneWrapper wrapper, Model model) throws Exception{
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/riassegnaAccountValutazione"));
		try{
			String currentAccount = "";
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
			Set<Valutazione> valutazioneNonStoriccizate = valutazioneService.getAllValutazioniForAccreditamentoIdAndNotStoricizzato(accreditamentoId);
			
			for(Valutazione v : valutazioneNonStoriccizate ) {	
				 if(!v.getStoricizzato()) {
					currentAccount = v.getAccount().getNome();
					 
				 } 
			}
			Set<Account> accountProfileSegreteria = accountService.getUserByProfileEnum(ProfileEnum.SEGRETERIA);

			wrapper.setAccreditamentoId(accreditamentoId);
			wrapper.setAllAccountProfileSegreteria(accountProfileSegreteria);
			model.addAttribute("accreditamento",accreditamento);
			model.addAttribute("valutazioneNonStoriccizate", valutazioneNonStoriccizate);
			model.addAttribute("valutazioneWrapper", wrapper);
			model.addAttribute("currentAccount", currentAccount);
			return "accreditamento/accreditamentoRiassegnaAccountValutazione";
			
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/riassegnaAccountValutazione"),ex);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/show"));
			return "redirect:/accreditamento/{accreditamentoId}/show";
		}
	}
	
	@RequestMapping(value="/accreditamento/{accreditamentoId}/riassegnaAccountValutazione/riassegna", method = RequestMethod.POST)
	public String riassegnaAccountValutazione(@PathVariable Long accreditamentoId, @ModelAttribute("valutazioneWrapper") ValutazioneWrapper wrapper, Model model, RedirectAttributes redirectAttrs, HttpServletRequest request) {
		try{
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
			Set<Account> accountProfileSegreteria = accountService.getUserByProfileEnum(ProfileEnum.SEGRETERIA);
			wrapper.setAllAccountProfileSegreteria(accountProfileSegreteria);
			Set<Valutazione> valutazioneNonStoriccizate = valutazioneService.getAllValutazioniForAccreditamentoIdAndNotStoricizzato(accreditamentoId);	
			for(Valutazione valutazione : valutazioneNonStoriccizate) {
				if(!valutazione.getStoricizzato()) {		
					valutazioneService.valutazioneIdNotStoricizzatoAndAccountId(valutazione.getId(), wrapper.getAccountSelected());
				}
			}
			model.addAttribute("accreditamento", accreditamento);
			model.addAttribute("accreditamentoId",accreditamentoId);
			model.addAttribute("valutazioneWrapper", wrapper);
			model.addAttribute("valutazioneNonStoriccizate", valutazioneNonStoriccizate);	
			LOGGER.info(Utils.getLogMessage("REDIRECT success:/accreditamento/{accreditamentoId}/show"));
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.riassegna", "success"));
			return "redirect:/accreditamento/{accreditamentoId}/show";	
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("Errore: /accreditamento/{accreditamentoId}/riassegnaAccountValutazione/riassegna"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/accreditamento/{accreditamentoId}/riassegnaAccountValutazione/riassegna";
		}
	}	
}
