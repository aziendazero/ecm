package it.tredi.ecm.web;

import java.util.LinkedList;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.enumlist.CategoriaObiettivoNazionale;
import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.ObiettivoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.web.bean.EventoWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.EventoValidator;

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

	@Autowired
	private EventoValidator eventoValidator;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("categoriaObiettivoNazionaleList")
	public CategoriaObiettivoNazionale[] getCategoriaObiettivoNazionaleList(){
		return CategoriaObiettivoNazionale.values();
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

	/*
	 * INSERIMENTO EVENTO IN PIANO FORMATIVO
	 * */
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/evento/new")
	public String newEvento(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @RequestParam(name="pianoFormativo", required = true) int pianoFormativo,
			Model model, RedirectAttributes redirectAttrs){
		try{
			return goToEdit(model, prepareEventoWrapper(createEvento(pianoFormativo), providerId, accreditamentoId),redirectAttrs);
		}catch (Exception ex){
			LOGGER.error(ex.getMessage(), ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	/*
	 * MODIFICA EVENTO IN PIANO FORMATIVO
	 * */
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/evento/{id}/edit")
	public String editEvento(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long id,
			Model model, RedirectAttributes redirectAttrs){
		try{
			return goToEdit(model, prepareEventoWrapper(eventoService.getEvento(id),0L,accreditamentoId),redirectAttrs);
		}catch (Exception ex){
			LOGGER.error(ex.getMessage(), ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	/*
	 * SALVATAGGIO EVENTO IN PIANO FORMATIVO
	 * */
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/{providerId}/evento/save", method=RequestMethod.POST)
	public String saveEvento(@ModelAttribute("eventoWrapper") EventoWrapper wrapper, BindingResult result, 
			Model model, RedirectAttributes redirectAttrs){
		try{
			if(wrapper.getEvento().isNew()){
				Evento evento = wrapper.getEvento();
				evento.setProvider(providerService.getProvider(wrapper.getProviderId()));
				evento.setAccreditamento(accreditamentoService.getAccreditamento(wrapper.getAccreditamentoId()));
			}

			eventoValidator.validate(wrapper.getEvento(), result, "evento.", true);

			if(result.hasErrors()){
				model.addAttribute("message", new Message("message.errore", "message.inserire_campi_required", "error"));
				populateListFromAccreditamento(model, wrapper.getAccreditamentoId());
				return EDIT;
			}else{
				eventoService.save(wrapper.getEvento());
				redirectAttrs.addAttribute("accreditamentoId", wrapper.getAccreditamentoId());
				redirectAttrs.addAttribute("providerId", wrapper.getProviderId());
				redirectAttrs.addAttribute("pianoFormativo", wrapper.getEvento().getPianoFormativo());
				return "redirect:/accreditamento/{accreditamentoId}/provider/{providerId}/pianoFormativo/{pianoFormativo}/edit";
			}
		}catch (Exception ex){
			LOGGER.error(ex.getMessage(), ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return EDIT;//TODO tornare in home...non in EDIT
		}
	}
	
	/*
	 * ELIMINAZIONE DI UN EVENTO IN PIANO FORMATIVO
	 * */
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/evento/{id}/delete")
	public String removeEvento(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long id,
			Model model, RedirectAttributes redirectAttrs){
		try{
			eventoService.delete(id);
			return "redirect:/accreditamento/{accreditamentoId}/provider/{providerId}/pianoFormativo/{pianoFormativo}/edit";
		}catch (Exception ex){
			LOGGER.error(ex.getMessage(), ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	private String goToEdit(Model model, EventoWrapper wrapper, RedirectAttributes redirectAttrs){
		try {
			model.addAttribute("eventoWrapper", wrapper);
			populateListFromAccreditamento(model, wrapper.getAccreditamentoId());
			return EDIT;
		}catch (Exception ex){
			LOGGER.error(ex.getMessage(),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	private void populateListFromAccreditamento(Model model, Long accreditamentoId) throws Exception{
		DatiAccreditamento datiAccreditamento = accreditamentoService.getDatiAccreditamentoForAccreditamento(accreditamentoId);
		model.addAttribute("proceduraFormativaList", datiAccreditamento.getProcedureFormative());
		model.addAttribute("professioneList", datiAccreditamento.getProfessioniSelezionate());
		model.addAttribute("disciplinaList", datiAccreditamento.getDiscipline());
	}

	private Evento createEvento(Integer pianoFormativo){
		Evento evento = new Evento();
		if(pianoFormativo != null)
			evento.setPianoFormativo(pianoFormativo);
		return evento;
	}

	//utilizzato nel caso di save
	private EventoWrapper prepareEventoWrapper(Evento evento){
		return prepareEventoWrapper(evento, 0L, 0L);
	}

	private EventoWrapper prepareEventoWrapper(Evento evento, long providerId){
		return prepareEventoWrapper(evento, providerId, 0L);
	}

	//utilizzato nel caso di edit e new
	private EventoWrapper prepareEventoWrapper(Evento evento, long providerId, long accreditamentoId){
		EventoWrapper wrapper = new EventoWrapper();
		wrapper.setEvento(evento);

		if(evento.isNew()){
			wrapper.setProviderId(providerId);
		}else{
			wrapper.setProviderId(evento.getProvider().getId());
		}
		
		if(accreditamentoId != 0){
			wrapper.setAccreditamentoId(accreditamentoId);
			wrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_EVENTO_PIANO_FORMATIVO), evento.getIdEditabili());
		}
		else{
			wrapper.setAccreditamentoId(evento.getAccreditamento().getId());
			wrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_EVENTO), evento.getIdEditabili());
		}
		
		return wrapper;
	}

}
