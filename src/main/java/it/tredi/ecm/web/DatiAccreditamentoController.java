package it.tredi.ecm.web;

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
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.DatiAccreditamentoService;
import it.tredi.ecm.service.DisciplinaService;
import it.tredi.ecm.service.FieldEditabileAccreditamentoService;
import it.tredi.ecm.service.FieldValutazioneAccreditamentoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.ProfessioneService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.ValutazioneService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.DatiAccreditamentoWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.PersonaWrapper;
import it.tredi.ecm.web.validator.DatiAccreditamentoValidator;
import it.tredi.ecm.web.validator.ValutazioneValidator;

@Controller
public class DatiAccreditamentoController {
	private static Logger LOGGER = LoggerFactory.getLogger(DatiAccreditamentoController.class);

	private final String EDIT = "accreditamento/datiAccreditamentoEdit";
	private final String SHOW = "accreditamento/datiAccreditamentoShow";
	private final String VALIDATE = "accreditamento/datiAccreditamentoValidate";

	@Autowired private DatiAccreditamentoService datiAccreditamentoService;
	@Autowired private DisciplinaService disciplinaService;
	@Autowired private ProfessioneService professioneService;
	@Autowired private FileService fileService;
	@Autowired private ProviderService providerService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private FieldEditabileAccreditamentoService fieldEditabileService;
	@Autowired private DatiAccreditamentoValidator datiAccreditamentoValidator;
	@Autowired private ValutazioneValidator valutazioneValidator;
	@Autowired private FieldValutazioneAccreditamentoService fieldValutazioneAccreditamentoService;
	@Autowired private ValutazioneService valutazioneService;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("datiAccreditamentoWrapper")
	public DatiAccreditamentoWrapper getDatiAccreditamentoWrapper(@RequestParam(value="editId",required = false) Long id){
		if(id != null){
			return prepareDatiAccreditamentoWrapperEdit(datiAccreditamentoService.getDatiAccreditamento(id));
		}
		return new DatiAccreditamentoWrapper();
	}

	@ModelAttribute("proceduraFormativaList")
	public ProceduraFormativa[] getListaProceduraFormativa(){
		return ProceduraFormativa.values();
	}

	@ModelAttribute("professioneList")
	public Set<Professione> getAllProfessioni(){
		return professioneService.getAllProfessioni();
	}

	@ModelAttribute("disciplinaList")
	public Set<Disciplina> getAllDiscipline(){
		return disciplinaService.getAllDiscipline();
	}

	/*** NEW ***/
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/dati/new")
	public String newDatiAccreditamento(@PathVariable Long accreditamentoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/new"));
		try {
			return goToEdit(model, prepareDatiAccreditamentoWrapperEdit(new DatiAccreditamento(),accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId));
			return "redirect:/accreditamento/{accreditamentoId}";
		}
	}

	/*** EDIT ***/
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/dati/{id}/edit")
	public String editDatiAccreditamento(@PathVariable Long id, @PathVariable Long accreditamentoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/"+ id +"/edit"));
		try{
			return goToEdit(model, prepareDatiAccreditamentoWrapperEdit(datiAccreditamentoService.getDatiAccreditamento(id),accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/"+ id +"/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId));
			return "redirect:/accreditamento/{accreditamentoId}";
		}
	};

	/*** SHOW ***/
	@PreAuthorize("@securityAccessServiceImpl.canShowAccreditamento(principal,#accreditamentoId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/dati/{id}/show")
	public String showDatiAccreditamento(@PathVariable Long id, @PathVariable Long accreditamentoId,
			@RequestParam(required = false) String from, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/"+ id +"/show"));
		try{
			if(from != null) {
				redirectAttrs.addFlashAttribute("mode", from);
				return "redirect:/accreditamento/"+ accreditamentoId + "/dati/" + id + "/show";
			}
			return goToShow(model, prepareDatiAccreditamentoWrapperShow(datiAccreditamentoService.getDatiAccreditamento(id),accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/"+ id +"/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId));
			return "redirect:/accreditamento/{accreditamentoId}";
		}
	};

	/***   VALIDATE   ***/
//	@PreAuthorize("@securityAccessServiceImpl.canValidateAccreditamento(principal,#accreditamentoId)") TODO
	@RequestMapping("/accreditamento/{accreditamentoId}/dati/{id}/validate")
	public String validateDatiAccreditamento(@PathVariable Long id, @PathVariable Long accreditamentoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/"+ id +"/validate"));
		try{
			return goToValidate(model, prepareDatiAccreditamentoWrapperValidate(datiAccreditamentoService.getDatiAccreditamento(id),accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/"+ id +"/validate"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/validate"));
			return "redirect:/accreditamento/{accreditamentoId}/validate";
		}
	};


	/*** SAVE ***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/dati/save", method = RequestMethod.POST)
	public String saveDatiAccreditamento(@ModelAttribute("datiAccreditamentoWrapper") DatiAccreditamentoWrapper wrapper, BindingResult result,
											@PathVariable Long accreditamentoId, RedirectAttributes redirectAttrs, Model model){
		LOGGER.info(Utils.getLogMessage("POST /accreditamento/" + accreditamentoId + "/dati/save"));
		try {
			if(wrapper.getDatiAccreditamento().isNew()){
				wrapper.setProvider(providerService.getProvider(wrapper.getProvider().getId()));
			}

			//TODO getFile da testare se funziona anche senza reload
			//reload degli allegati perchè se è stato fatto un upload ajax...il wrapper non ha i byte[] aggiornati e nemmeno il ref a providerId
			for(File file : wrapper.getFiles()){
				if(file != null && !file.isNew()){
					if(file.isESTRATTOBILANCIOFORMAZIONE())
						wrapper.setEstrattoBilancioFormazione(fileService.getFile(file.getId()));
					else if(file.isESTRATTOBILANCIOCOMPLESSIVO())
						wrapper.setEstrattoBilancioComplessivo(fileService.getFile(file.getId()));
					else if(file.isFUNZIONIGRAMMA())
						wrapper.setFunzionigramma(fileService.getFile(file.getId()));
					else if(file.isORGANIGRAMMA())
						wrapper.setOrganigramma(fileService.getFile(file.getId()));
				}
			}

			datiAccreditamentoValidator.validate(wrapper.getDatiAccreditamento(), result, "datiAccreditamento.", wrapper.getFiles());

			if(result.hasErrors()){
				LOGGER.debug(Utils.getLogMessage("Validazione Fallita"));
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				LOGGER.debug(Utils.getLogMessage("Salvataggio DatiAccrdeditmento"));
				providerService.save(wrapper.getProvider());
				datiAccreditamentoService.save(wrapper.getDatiAccreditamento(), accreditamentoId);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.dati_attivita_inseriti", "success"));
				redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
				LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
				return "redirect:/accreditamento/{accreditamentoId}/edit";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /accreditamento/" + accreditamentoId + "/dati/save"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
			return EDIT;
		}
	}

	/*** SAVE VALUTAZIONE ***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/dati/validate", method = RequestMethod.POST)
	public String valutaDatiAccreditamento(@ModelAttribute("datiAccreditamentoWrapper") DatiAccreditamentoWrapper wrapper, BindingResult result,
											@PathVariable Long accreditamentoId, RedirectAttributes redirectAttrs, Model model){
		LOGGER.info(Utils.getLogMessage("POST /accreditamento/" + accreditamentoId + "/dati/validate"));
		try {
			//validazione del provider
			valutazioneValidator.validateValutazione(wrapper.getMappa(), result);

			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
				return VALIDATE;
			}else{
				Accreditamento accreditamento = new Accreditamento();
				accreditamento.setId(wrapper.getAccreditamentoId());
				wrapper.getMappa().forEach((k, v) -> {
					v.setIdField(k);
					v.setAccreditamento(accreditamento);
				});

				Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamento.getId(), Utils.getAuthenticatedUser().getAccount().getId());
				Set<FieldValutazioneAccreditamento> values = new HashSet<FieldValutazioneAccreditamento>(fieldValutazioneAccreditamentoService.saveMapList(wrapper.getMappa()));
				valutazione.getValutazioni().addAll(values);
				valutazioneService.save(valutazione);

				redirectAttrs.addAttribute("accreditamentoId", wrapper.getAccreditamentoId());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.valutazione_salvata", "success"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/validate"));
				return "redirect:/accreditamento/{accreditamentoId}/validate";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/dati/validate"),ex);
			model.addAttribute("accreditamentoId", wrapper.getAccreditamentoId());
			model.addAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
			return VALIDATE;
		}
	}

	private String goToEdit(Model model, DatiAccreditamentoWrapper wrapper){
		model.addAttribute("datiAccreditamentoWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	private String goToShow(Model model, DatiAccreditamentoWrapper wrapper){
		model.addAttribute("datiAccreditamentoWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
		return SHOW;
	}

	private String goToValidate(Model model, DatiAccreditamentoWrapper wrapper){
		model.addAttribute("datiAccreditamentoWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
		return VALIDATE;
	}

	private DatiAccreditamentoWrapper prepareDatiAccreditamentoWrapperEdit(DatiAccreditamento datiAccreditamento){
		return prepareDatiAccreditamentoWrapperEdit(datiAccreditamento, 0);
	}

	private DatiAccreditamentoWrapper prepareDatiAccreditamentoWrapperEdit(DatiAccreditamento datiAccreditamento, long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("prepareDatiAccreditamentoWrapperEdit(" + datiAccreditamento.getId() + "," + accreditamentoId + ") - entering"));
		DatiAccreditamentoWrapper wrapper = new DatiAccreditamentoWrapper();
		wrapper.setDatiAccreditamento(datiAccreditamento);
		wrapper.setAccreditamentoId(accreditamentoId);

		if(datiAccreditamento.isNew()){
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
			wrapper.setProvider(accreditamento.getProvider());
		}else{
			wrapper.setProvider(datiAccreditamento.getAccreditamento().getProvider());
			Set<File> files = wrapper.getProvider().getFiles();
			for(File file : files){
				if(file.isESTRATTOBILANCIOFORMAZIONE())
					wrapper.setEstrattoBilancioFormazione(file);
				else if(file.isESTRATTOBILANCIOCOMPLESSIVO())
					wrapper.setEstrattoBilancioComplessivo(file);
				else if(file.isFUNZIONIGRAMMA())
					wrapper.setFunzionigramma(file);
				else if(file.isORGANIGRAMMA())
					wrapper.setOrganigramma(file);
			}
		}

		wrapper.setIdEditabili(Utils.getSubsetOfIdFieldEnum(fieldEditabileService.getAllFieldEditabileForAccreditamento(accreditamentoId), SubSetFieldEnum.DATI_ACCREDITAMENTO));
		LOGGER.info(Utils.getLogMessage("prepareDatiAccreditamentoWrapperEdit(" + datiAccreditamento.getId() + "," + accreditamentoId + ") - exiting"));
		return wrapper;
	};

	private DatiAccreditamentoWrapper prepareDatiAccreditamentoWrapperShow(DatiAccreditamento datiAccreditamento, long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("prepareDatiAccreditamentoWrapperShow(" + datiAccreditamento.getId() + "," + accreditamentoId + ") - entering"));
		DatiAccreditamentoWrapper wrapper = new DatiAccreditamentoWrapper();
		wrapper.setDatiAccreditamento(datiAccreditamento);
		wrapper.setAccreditamentoId(accreditamentoId);
		Set<File> files = datiAccreditamento.getAccreditamento().getProvider().getFiles();
		for(File file : files){
			if(file.isESTRATTOBILANCIOFORMAZIONE())
				wrapper.setEstrattoBilancioFormazione(file);
			else if(file.isESTRATTOBILANCIOCOMPLESSIVO())
				wrapper.setEstrattoBilancioComplessivo(file);
			else if(file.isFUNZIONIGRAMMA())
				wrapper.setFunzionigramma(file);
			else if(file.isORGANIGRAMMA())
				wrapper.setOrganigramma(file);
		}

		LOGGER.info(Utils.getLogMessage("prepareDatiAccreditamentoWrapperShow(" + datiAccreditamento.getId() + "," + accreditamentoId + ") - exiting"));
		return wrapper;
	};

	private DatiAccreditamentoWrapper prepareDatiAccreditamentoWrapperValidate(DatiAccreditamento datiAccreditamento, long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("prepareDatiAccreditamentoWrapperValidate(" + datiAccreditamento.getId() + "," + accreditamentoId + ") - entering"));
		DatiAccreditamentoWrapper wrapper = new DatiAccreditamentoWrapper();

		//carico la valutazione per l'utente
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		if(valutazione != null) {
			mappa = fieldValutazioneAccreditamentoService.putSetFieldValutazioneInMap(valutazione.getValutazioni());
		}
		wrapper.setMappa(mappa);

		wrapper.setDatiAccreditamento(datiAccreditamento);
		wrapper.setAccreditamentoId(accreditamentoId);

		if(datiAccreditamento.isNew()){
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
			wrapper.setProvider(accreditamento.getProvider());
		}else{
			wrapper.setProvider(datiAccreditamento.getAccreditamento().getProvider());
			Set<File> files = wrapper.getProvider().getFiles();
			for(File file : files){
				if(file.isESTRATTOBILANCIOFORMAZIONE())
					wrapper.setEstrattoBilancioFormazione(file);
				else if(file.isESTRATTOBILANCIOCOMPLESSIVO())
					wrapper.setEstrattoBilancioComplessivo(file);
				else if(file.isFUNZIONIGRAMMA())
					wrapper.setFunzionigramma(file);
				else if(file.isORGANIGRAMMA())
					wrapper.setOrganigramma(file);
			}
		}

		LOGGER.info(Utils.getLogMessage("prepareDatiAccreditamentoWrapperValidate(" + datiAccreditamento.getId() + "," + accreditamentoId + ") - exiting"));
		return wrapper;
	};



}
