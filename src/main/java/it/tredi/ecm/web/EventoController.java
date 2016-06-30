package it.tredi.ecm.web;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Provider_;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.ObiettivoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.web.bean.DatiAccreditamentoWrapper;
import it.tredi.ecm.web.bean.EventoWrapper;

@Controller
public class EventoController {
	public static final Logger LOGGER = Logger.getLogger(EventoController.class);
	private final String EDIT = "evento/eventoEdit";

	@Autowired
	private EventoService eventoService;
	@Autowired
	private ProviderService providerService;
	@Autowired
	private AccreditamentoService accreditamentoService;
	@Autowired
	private ObiettivoService obietivoService;

	//TODO
	//	@Autowired
	//	private EventoValidator eventoValidator;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("obiettivoNazionaleList")
	public Set<Obiettivo> getObiettiviNazionali(){
		return obietivoService.getObiettiviNazionali();
	}

	@ModelAttribute("obiettivoRegionaleList")
	public Set<Obiettivo> getObiettiviRegionali(){
		return obietivoService.getObiettiviRegionali();
	}

	@ModelAttribute("eventoWrapper")
	public EventoWrapper getEventoWrapper(@RequestParam(value="editId",required = false) Long id){
		if(id != null){
			return prepareEventoWrapper(eventoService.getEvento(id));
		}
		return new EventoWrapper();
	}

	@RequestMapping("/provider/{providerId}/evento/new")
	public String newEvento(){
		
		
		return "";
	}

	@RequestMapping("/provider/{providerId}/evento/{id}/edit")
	public String editEvento(){
		return "";
	}
	
	@RequestMapping(name = "/provider/{providerId}/evento/save", method=RequestMethod.POST)
	public String saveEvento(){
		return "";
	}

	
	private String goToEdit(Model model, EventoWrapper wrapper){
		model.addAttribute("eventoWrapper", wrapper);
		return EDIT;
	}

	//utilizzato nel caso di save
	private EventoWrapper prepareEventoWrapper(Evento evento){
		return prepareEventoWrapper(evento, 0);
	}
	
	//utilizzato nel caso di edit e new
	private EventoWrapper prepareEventoWrapper(Evento evento, long providerId){
		EventoWrapper wrapper = new EventoWrapper();
		wrapper.setEvento(evento);
		
		if(evento.isNew()){
			wrapper.setProviderId(providerId);
		}else{
			wrapper.setProviderId(evento.getProvider().getId());
		}
		
		Accreditamento accreditamento = accreditamentoService.getAccreditamentoAttivoForProvider(wrapper.getProviderId());
		DatiAccreditamento datiAccreditamento = accreditamento.getDatiAccreditamento();

		wrapper.setDiscipline(datiAccreditamento.getDiscipline());
		wrapper.setProcedureFormative(datiAccreditamento.getProcedureFormative());
		
		return wrapper;
	}

}
