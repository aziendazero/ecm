package it.tredi.ecm.web;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.ValutazioneService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.FieldValutazioniRipetibiliWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.ValutazioneWrapper;

@Controller
public class ValutazioneController {
	private static Logger LOGGER = LoggerFactory.getLogger(ValutazioneController.class);

	@Autowired private ValutazioneService valutazioneService;
	@Autowired private AccreditamentoService accreditamentoService;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
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
}
