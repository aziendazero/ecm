package it.tredi.ecm.web;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.bean.AccreditamentoWrapper;

@Controller
public class AccreditamentoController {
	@Autowired
	private ProviderService providerService;
	@Autowired
	private AccreditamentoService accreditamentoService;
	
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}
	
	/***	Get Accreditamenti per provider corrente	***/
	@RequestMapping("/provider/accreditamento")
	public String getAccreditamentoCurrentProvider(RedirectAttributes redirectAttrs) throws Exception{
		Provider currentProvider = providerService.getProvider();
		if(currentProvider.isNew()){
			throw new Exception("Provider non registrato");
		}else{
			redirectAttrs.addAttribute("providerId",currentProvider.getId());
			return "redirect:/provider/{providerId}/accreditamento";
		}
	}
	
	/***	Get Accreditamenti per provider {providerID}	***/
	@RequestMapping("/provider/{providerId}/accreditamento")
	public String getAccreditamentoProvider(@PathVariable("providerId") Long providerId, Model model, RedirectAttributes redirectAttrs){
		Set<Accreditamento> listaAccreditamenti = accreditamentoService.getAllAccreditamentiForProvider(providerId);
		if(listaAccreditamenti.size() == 1){
			Accreditamento accreditamento = listaAccreditamenti.iterator().next();
			redirectAttrs.addAttribute("id",accreditamento.getId())
							.addFlashAttribute("accreditamento", accreditamento);
			return "redirect:/provider/accreditamento/{id}";
		}
		
		model.addAttribute("accreditamentoList", listaAccreditamenti);
		return "provider/accreditamentoList";
	}
	
	/***	Get Accreditamento {ID}	***/
	@RequestMapping("/provider/accreditamento/{id}")
	public String getAccreditamento(@ModelAttribute("accreditamento") Accreditamento accreditamento, Model model){
		if(accreditamento.isProvvisorio()){
			AccreditamentoWrapper accreditamentoWrapper = prepareAccreditamentoWrapper(accreditamento);
			model.addAttribute("accreditamentoWrapper", accreditamentoWrapper);
		} 
		return "provider/accreditamento";
	}
	
	/***	Nuova domanda accreditamento per provider corrente	***/
	@RequestMapping("/provider/accreditamento/new")
	public String getNewAccreditamentoCurrentProvider(Model model) throws Exception{
		Provider currentProvider = providerService.getProvider();
		if(currentProvider.isNew()){
			throw new Exception("Provider non registrato");
		}else{
			Accreditamento accreditamento = new Accreditamento(Costanti.ACCREDITAMENTO_PROVVISORIO);
			accreditamento.setProvider(currentProvider);
			AccreditamentoWrapper accreditamentoWrapper = prepareAccreditamentoWrapper(accreditamento);
			model.addAttribute("accreditamentoWrapper", accreditamentoWrapper);
			return "provider/accreditamento";
		}
	}
	
	@RequestMapping("/provider/accreditamento/saveDraft")
	public String salvaAccreditamentoBozza(@ModelAttribute("accreditamentoWrapper") AccreditamentoWrapper accreditamentoWrapper, 
												RedirectAttributes redirectAttrs){
		
		accreditamentoService.save(accreditamentoWrapper.getAccreditamento());
		redirectAttrs.addAttribute("providerId",accreditamentoWrapper.getProvider().getId());
		return "redirect:/provider/{providerId}/accreditamento";
	}
	
	
	
	private AccreditamentoWrapper prepareAccreditamentoWrapper(Accreditamento accreditamento){
		AccreditamentoWrapper accreditamentoWrapper = new AccreditamentoWrapper();
		accreditamentoWrapper.setProvider(accreditamento.getProvider());
		accreditamentoWrapper.setProviderStato(false);
		
		for(Persona p : accreditamento.getProvider().getPersone()){
			switch (p.getIncarico()){
				case Costanti.INCARICO_LEGALERAPPRESENTANTE : accreditamentoWrapper.setLegaleRappresentante(p);
																			break;
				default : break;
			}
		}
		
		Sede sede = accreditamento.getProvider().getSedeLegale();
		if(sede == null){
			accreditamentoWrapper.setSedeLegale(new Sede());
		}else{
			accreditamentoWrapper.setSedeLegale(sede);
		}
		accreditamentoWrapper.setSedeLegaleStato(true);
		
		sede = accreditamento.getProvider().getSedeOperativa();
		if(sede == null){
			accreditamentoWrapper.setSedeOperativa(new Sede());
		}else{
			accreditamentoWrapper.setSedeOperativa(sede);
		}
		accreditamentoWrapper.setSedeOperativaStato(true);
		
		
		accreditamentoWrapper.setAccreditamento(accreditamento);
		return accreditamentoWrapper;
	}
}
