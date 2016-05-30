package it.tredi.ecm.web;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
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
	@RequestMapping("/provider/accreditamento/list")
	public String getAllAccreditamentiForCurrentProvider(RedirectAttributes redirectAttrs) throws Exception{
		Provider currentProvider = providerService.getProvider();
		if(currentProvider.isNew()){
			throw new Exception("Provider non registrato");
		}else{
			redirectAttrs.addAttribute("providerId",currentProvider.getId());
			return "redirect:/provider/{providerId}/accreditamento/list";
		}
	}
	
	/***	Get Lista Accreditamenti per {providerID}	***/
	@RequestMapping("/provider/{providerId}/accreditamento/list")
	public String getAllAccreditamentiForProvider(@PathVariable("providerId") Long providerId, Model model, RedirectAttributes redirectAttrs){
		Set<Accreditamento> listaAccreditamenti = accreditamentoService.getAllAccreditamentiForProvider(providerId);
		model.addAttribute("accreditamentoList", listaAccreditamenti);
		model.addAttribute("canProviderCreateAccreditamento", accreditamentoService.canProviderCreateAccreditamento(providerId));
		return "provider/accreditamentoList";
		
		//TODO per reindirizzare direttamente sulla view è necessario creare un'altra request
		// che non faccia il ricaricamento da db
		/*
	  		if(listaAccreditamenti.size() == 1){
	 
				Accreditamento accreditamento = listaAccreditamenti.iterator().next();
				redirectAttrs.addAttribute("id",accreditamento.getId())
								.addFlashAttribute("accreditamento", accreditamento);
				return "redirect:/provider/accreditamento/{id}";
			}
		*/
	}
	
	/***	Get Accreditamento ***/
	@RequestMapping("/provider/accreditamento")
	public String getAccreditamentoAttivo(Model model){
		Accreditamento accreditamento = accreditamentoService.getAccreditamento();
		return goToAccreditamento(model, accreditamento);
	}
	
	/***	Get Accreditamento {ID}	***/
	@RequestMapping("/accreditamento/{id}")
	public String getAccreditamento(@PathVariable Long id, Model model){
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(id);
		return goToAccreditamento(model, accreditamento);
	}
	
	@RequestMapping("/accreditamento")
	public String test(Model model){
		return "provider/accreditamentoEdit";
	}
	
	
	private String goToAccreditamento(Model model, Accreditamento accreditamento){
		if(accreditamento.isProvvisorio()){
			AccreditamentoWrapper accreditamentoWrapper = prepareAccreditamentoWrapper(accreditamento);
			model.addAttribute("accreditamentoWrapper", accreditamentoWrapper);
		}//TODO gestire differenza con Accreditamento STANDARD
		return "provider/accreditamento";
	}
	
	/***	Nuova domanda accreditamento per provider corrente	***/
	@RequestMapping("/provider/accreditamento/new")
	public String getNewAccreditamentoForCurrentProvider(Model model, RedirectAttributes redirectAttrs) {
		try{
			redirectAttrs.addAttribute("id", accreditamentoService.getNewAccreditamentoForCurrentProvider().getId());
			return "redirect:/accreditamento/{id}";
		}catch (Exception ex){
			//TODO exception
			return "redirect:/provider/accreditamento/list";
		}
	}
	
	private AccreditamentoWrapper prepareAccreditamentoWrapper(Accreditamento accreditamento){
		AccreditamentoWrapper accreditamentoWrapper = new AccreditamentoWrapper();
		accreditamentoWrapper.setProvider(accreditamento.getProvider());
		accreditamentoWrapper.setProviderStato(false);
		
		for(Persona p : accreditamento.getProvider().getPersone()){
			if(p.isLegaleRappresentante())
				accreditamentoWrapper.setLegaleRappresentante(p);
			else if(p.isDelegatoLegaleRappresentante())
				accreditamentoWrapper.setDelegatoLegaleRappresentante(p);
			else if(p.isResponsabileSegreteria())
				accreditamentoWrapper.setResponsabileQualita(p);
			else if(p.isResponsabileSistemaInformatico())
				accreditamentoWrapper.setResponsabileSistemaInformatico(p);
			else if(p.isResponsabileQualita())
				accreditamentoWrapper.setResponsabileQualita(p);
		}
		
		Sede sede = accreditamento.getProvider().getSedeLegale();
		accreditamentoWrapper.setSedeLegale( sede != null ? sede : new Sede());  
		
		sede = accreditamento.getProvider().getSedeOperativa();
		accreditamentoWrapper.setSedeOperativa( sede != null ? sede : new Sede());  
		
		DatiAccreditamento datiAccreditamento = accreditamento.getDatiAccreditamento();
		accreditamentoWrapper.setDatiAccreditamento(datiAccreditamento != null ? datiAccreditamento : new DatiAccreditamento());
		
		accreditamentoWrapper.setAccreditamento(accreditamento);
		return accreditamentoWrapper;
	}
}
