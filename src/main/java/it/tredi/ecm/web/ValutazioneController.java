package it.tredi.ecm.web;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.service.ValutazioneService;
import it.tredi.ecm.utils.Utils;

@Controller
public class ValutazioneController {
	private static Logger LOGGER = LoggerFactory.getLogger(ValutazioneController.class);
		
	@Autowired private ValutazioneService valutazioneService;

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
		Set<Valutazione> valutazioni = valutazioneService.getAllValutazioniForAccreditamentoId(accreditamentoId);
		LinkedList<Map<String,String>> result = new LinkedList<>();
		
		for(Valutazione v : valutazioni){
			Map<String,String> mappa = new HashMap<String, String>();
			String header = v.getAccount().isSegreteria() ? "Valutazione Segreteria ECM - " : "Valutazione Referee - ";
			String value = v.getValutazioneComplessiva() == null ? "" : v.getValutazioneComplessiva();
			mappa.put("header", header + v.getAccount().getFullName());
			mappa.put("value", value);
			
			result.add(mappa);
		}
		
		return result;
	}
}
