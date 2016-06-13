package it.tredi.ecm.web;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.PersonaService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.web.bean.AccreditamentoWrapper;
import it.tredi.ecm.web.bean.Message;

@Controller
public class AccreditamentoController {
	
	private static Logger LOGGER = LoggerFactory.getLogger(AccreditamentoController.class);
	
	@Autowired
	private PersonaService personaService;
	@Autowired
	private ProviderService providerService;
	@Autowired
	private AccreditamentoService accreditamentoService;
	@Autowired
	private FileService fileService;
	
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}
	
	/***	Get Lista Accreditamenti per provider CORRENTE	***/
	@RequestMapping("/provider/accreditamento/list")
	public String getAllAccreditamentiForCurrentProvider(RedirectAttributes redirectAttrs) throws Exception{
		try {
			Provider currentProvider = providerService.getProvider();
			if(currentProvider.isNew()){
				throw new Exception("Provider non registrato");
			}else{
				redirectAttrs.addAttribute("providerId",currentProvider.getId());
				return "redirect:/provider/{providerId}/accreditamento/list";
			}
		}catch (Exception ex){
			//TODO gestione eccezione
			LOGGER.error(ex.getMessage(),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	/***	Get Lista Accreditamenti per {providerID}	***/
	@RequestMapping("/provider/{providerId}/accreditamento/list")
	public String getAllAccreditamentiForProvider(@PathVariable("providerId") Long providerId, Model model, RedirectAttributes redirectAttrs){
		try {
			Set<Accreditamento> listaAccreditamenti = accreditamentoService.getAllAccreditamentiForProvider(providerId);
			model.addAttribute("accreditamentoList", listaAccreditamenti);
			model.addAttribute("canProviderCreateAccreditamento", accreditamentoService.canProviderCreateAccreditamento(providerId));
			return "accreditamento/accreditamentoList";
			
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
		}catch (Exception ex){
			//TODO gestione eccezione
			LOGGER.error(ex.getMessage(),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	/***	Get Accreditamento ATTIVO per provider CORRENTE ***/
	@RequestMapping("/provider/accreditamento")
	public String getAccreditamentoAttivo(Model model, RedirectAttributes redirectAttrs){
		try {
			Accreditamento accreditamento = accreditamentoService.getAccreditamento();
			return goToAccreditamento(model, accreditamento);
		}catch (Exception ex){
			//TODO gestione eccezione
			LOGGER.error(ex.getMessage(),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	/***	Get Accreditamento {ID}	***/
	@RequestMapping("/accreditamento/{id}")
	public String getAccreditamento(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		try {
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(id);
			return goToAccreditamento(model, accreditamento);
		}catch (Exception ex){
			//TODO gestione eccezione
			LOGGER.error(ex.getMessage(),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "accreditamento/accreditamentoList";
		}
	}
	
	private String goToAccreditamento(Model model, Accreditamento accreditamento){
		if(accreditamento.isProvvisorio()){
			AccreditamentoWrapper accreditamentoWrapper = prepareAccreditamentoWrapper(accreditamento);
			model.addAttribute("accreditamentoWrapper", accreditamentoWrapper);
		}//TODO gestire differenza con Accreditamento STANDARD
		return "accreditamento/accreditamentoShow";
	}
	
	/*** NEW 	Nuova domanda accreditamento per provider corrente	***/
	@RequestMapping("/provider/accreditamento/new")
	public String getNewAccreditamentoForCurrentProvider(Model model, RedirectAttributes redirectAttrs) {
		try{
			redirectAttrs.addAttribute("id", accreditamentoService.getNewAccreditamentoForCurrentProvider().getId());
			return "redirect:/accreditamento/{id}";
		}catch (Exception ex){
			//TODO gestione eccezione
			LOGGER.error(ex.getMessage(),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/provider/accreditamento/list";
		}
	}
	
	/***	INVIA DOMANDA ALLA SEGRETERIA	***/
	@RequestMapping("/accreditamento/{id}/provider/{providerId}/send")
	public String inviaDomandaAccreditamento(@PathVariable Long id, @PathVariable Long providerId, RedirectAttributes redirectAttrs){
		try{
			accreditamentoService.inviaDomandaAccreditamento(id);
			redirectAttrs.addAttribute("providerId",providerId);
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.domanda_inviata", "success"));
			return "redirect:/provider/{providerId}/accreditamento/list";
		}catch (Exception ex){
			//TODO gestione eccezione
			LOGGER.error(ex.getMessage(),ex);
			redirectAttrs.addAttribute("id",id);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/accreditamento/{id}";
		}
	}
	
	/*** METODI PRIVATI PER IL SUPPORTO ***/
	private AccreditamentoWrapper prepareAccreditamentoWrapper(Accreditamento accreditamento){
		AccreditamentoWrapper accreditamentoWrapper = new AccreditamentoWrapper();
		accreditamentoWrapper.setAccreditamento(accreditamento);
		
		// PROVIDER
		accreditamentoWrapper.setProvider(accreditamento.getProvider());
		
		//SEDE LEGALE
		Sede sede = accreditamento.getProvider().getSedeLegale();
		accreditamentoWrapper.setSedeLegale( sede != null ? sede : new Sede());  
		
		//SEDE OPERATIVA
		sede = accreditamento.getProvider().getSedeOperativa();
		accreditamentoWrapper.setSedeOperativa( sede != null ? sede : new Sede());  
		
		//DATI ACCREDITAMENTO
		DatiAccreditamento datiAccreditamento = accreditamento.getDatiAccreditamento();
		accreditamentoWrapper.setDatiAccreditamento(datiAccreditamento != null ? datiAccreditamento : new DatiAccreditamento());
		
		// LEGALE RAPPRESENTANTE E RESPONSABILI
		for(Persona p : accreditamento.getProvider().getPersone()){
			if(p.isLegaleRappresentante())
				accreditamentoWrapper.setLegaleRappresentante(p);
			else if(p.isDelegatoLegaleRappresentante())
				accreditamentoWrapper.setDelegatoLegaleRappresentante(p);
			else if(p.isResponsabileSegreteria())
				accreditamentoWrapper.setResponsabileSegreteria(p);
			else if(p.isResponsabileAmministrativo())
				accreditamentoWrapper.setResponsabileAmministrativo(p);
			else if(p.isResponsabileSistemaInformatico())
				accreditamentoWrapper.setResponsabileSistemaInformatico(p);
			else if(p.isResponsabileQualita())
				accreditamentoWrapper.setResponsabileQualita(p);
			else if(p.isCoordinatoreComitatoScientifico())
				accreditamentoWrapper.setCoordinatoreComitatoScientifico(p);
			else if(p.isComponenteComitatoScientifico())
				accreditamentoWrapper.getComponentiComitatoScientifico().add(p);
		}
		
		int comitatoScientificoMemebers = accreditamentoWrapper.getComponentiComitatoScientifico().size();
		if(comitatoScientificoMemebers < 4){
			for(int i = comitatoScientificoMemebers; i<4; i++){
				accreditamentoWrapper.getComponentiComitatoScientifico().add(new Persona());
			}
		}
		
		//ALLEGATI
		List<String> listOfFiles = Arrays.asList(Costanti.FILE_ATTO_COSTITUTIVO, Costanti.FILE_ESPERIENZA_FORMAZIONE, Costanti.FILE_UTILIZZO, Costanti.FILE_SISTEMA_INFORMATICO, Costanti.FILE_PIANO_QUALITA, Costanti.FILE_DICHIARAZIONE_LEGALE);
		Set<String> existFiles = fileService.checkFileExists(accreditamento.getProvider().getId(), listOfFiles);

		Long providerId = accreditamento.getProvider().getId();
		Set<Professione> professioniSelezionate = (datiAccreditamento != null && !datiAccreditamento.isNew()) ? datiAccreditamento.getProfessioniSelezionate() : new HashSet<Professione>();
		
		int numeroComponentiComitatoScientifico = personaService.numeroComponentiComitatoScientifico(providerId);
		int numeroProfessionistiSanitarie 		= personaService.numeroComponentiComitatoScientificoConProfessioneSanitaria(providerId);
		int professioniDeiComponenti 			= personaService.numeroProfessioniDistinteDeiComponentiComitatoScientifico(providerId);
		int professioniDeiComponentiAnaloghe 	= (professioniSelezionate.size() > 0) ? personaService.numeroProfessioniDistinteAnalogheAProfessioniSelezionateDeiComponentiComitatoScientifico(providerId, professioniSelezionate) : 0;
		
		LOGGER.info("-----------------NUMERO COMPONENTI: " 				+ numeroComponentiComitatoScientifico);
		LOGGER.info("-----------------NUMERO PROFESSIONISTI SANITARI: " + numeroProfessionistiSanitarie);
		LOGGER.info("-----------------NUMERO PROFESSIONI DISTINTE: " 	+ professioniDeiComponenti);
		LOGGER.info("-----------------NUMERO PROFESSIONI ANALOGHE: "	+ professioniDeiComponentiAnaloghe);
		
		accreditamentoWrapper.checkStati(numeroComponentiComitatoScientifico, numeroProfessionistiSanitarie, professioniDeiComponenti, professioniDeiComponentiAnaloghe, existFiles);
		
		//accreditamentoWrapper.checkCanSend();
		return accreditamentoWrapper;
	}
}
