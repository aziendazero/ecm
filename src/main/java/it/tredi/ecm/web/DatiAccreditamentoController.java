package it.tredi.ecm.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoWrapperModeEnum;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.DatiAccreditamentoService;
import it.tredi.ecm.service.DisciplinaService;
import it.tredi.ecm.service.FieldEditabileAccreditamentoService;
import it.tredi.ecm.service.FieldIntegrazioneAccreditamentoService;
import it.tredi.ecm.service.FieldValutazioneAccreditamentoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.IntegrazioneService;
import it.tredi.ecm.service.ProfessioneService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.ValutazioneService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.DatiAccreditamentoWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.RichiestaIntegrazioneWrapper;
import it.tredi.ecm.web.validator.DatiAccreditamentoValidator;
import it.tredi.ecm.web.validator.ValutazioneValidator;

@Controller
public class DatiAccreditamentoController {
	private static Logger LOGGER = LoggerFactory.getLogger(DatiAccreditamentoController.class);

	private final String EDIT = "accreditamento/datiAccreditamentoEdit";
	private final String SHOW = "accreditamento/datiAccreditamentoShow";
	private final String VALIDATE = "accreditamento/datiAccreditamentoValidate";
	private final String ENABLEFIELD = "accreditamento/datiAccreditamentoEnableField";

	@Autowired private DatiAccreditamentoService datiAccreditamentoService;
	@Autowired private DatiAccreditamentoValidator datiAccreditamentoValidator;
	@Autowired private DisciplinaService disciplinaService;
	@Autowired private ProfessioneService professioneService;
	@Autowired private FileService fileService;
	@Autowired private ProviderService providerService;

	@Autowired private ValutazioneService valutazioneService;
	@Autowired private ValutazioneValidator valutazioneValidator;
	@Autowired private FieldEditabileAccreditamentoService fieldEditabileService;
	@Autowired private FieldValutazioneAccreditamentoService fieldValutazioneAccreditamentoService;

	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private IntegrazioneService integrazioneService;
	@Autowired private FieldIntegrazioneAccreditamentoService fieldIntegrazioneAccreditamentoService;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("datiAccreditamentoWrapper")
	public DatiAccreditamentoWrapper getDatiAccreditamentoWrapper(@RequestParam(value="editId",required = false) Long id,
			@RequestParam(value="statoAccreditamento",required = false) AccreditamentoStatoEnum statoAccreditamento,
			@RequestParam(value="wrapperMode",required = false) AccreditamentoWrapperModeEnum wrapperMode,
			@RequestParam(value="accreditamentoId",required = false) Long accreditamentoId,
			@RequestParam(value="sezione",required = false) Integer sezione) throws Exception{
		if(id != null){
			//return prepareDatiAccreditamentoWrapperEdit(datiAccreditamentoService.getDatiAccreditamento(id), statoAccreditamento, true);
			return prepareWrapperForReloadByEditId(datiAccreditamentoService.getDatiAccreditamento(id), accreditamentoId, statoAccreditamento, wrapperMode, sezione);
		}
		return new DatiAccreditamentoWrapper();
	}

	private DatiAccreditamentoWrapper prepareWrapperForReloadByEditId(DatiAccreditamento datiAccreditamento, Long accreditamentoId,
				AccreditamentoStatoEnum statoAccreditamento, AccreditamentoWrapperModeEnum wrapperMode, int sezione) throws Exception{
		if(wrapperMode == AccreditamentoWrapperModeEnum.EDIT)
			return prepareDatiAccreditamentoWrapperEdit(datiAccreditamento, statoAccreditamento, true, sezione);
		if(wrapperMode == AccreditamentoWrapperModeEnum.VALIDATE)
			return prepareDatiAccreditamentoWrapperValidate(datiAccreditamento, accreditamentoId, statoAccreditamento, false, sezione);

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
	@RequestMapping("/accreditamento/{accreditamentoId}/dati/new/{sezione}")
	public String newDatiAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Integer sezione, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/new/" + sezione));
		try {
			return goToEdit(model, prepareDatiAccreditamentoWrapperEdit(new DatiAccreditamento(),accreditamentoId,accreditamentoService.getStatoAccreditamento(accreditamentoId),false,sezione));
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
	@RequestMapping("/accreditamento/{accreditamentoId}/dati/{id}/edit/{sezione}")
	public String editDatiAccreditamento(@PathVariable Long id, @PathVariable Long accreditamentoId, @PathVariable Integer sezione, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/"+ id +"/edit/" + sezione));
		try{
			return goToEdit(model, prepareDatiAccreditamentoWrapperEdit(datiAccreditamentoService.getDatiAccreditamento(id),accreditamentoId,accreditamentoService.getStatoAccreditamento(accreditamentoId),false, sezione));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/"+ id +"/edit/" + sezione),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId));
			return "redirect:/accreditamento/{accreditamentoId}";
		}
	};

	/*** SHOW ***/
	@PreAuthorize("@securityAccessServiceImpl.canShowAccreditamento(principal,#accreditamentoId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/dati/{id}/show/{sezione}")
	public String showDatiAccreditamento(@PathVariable Long id, @PathVariable Long accreditamentoId, @PathVariable Integer sezione,
			@RequestParam(required = false) String from, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/"+ id +"/show/" + sezione));
		try{
			if(from != null) {
				redirectAttrs.addFlashAttribute("mode", from);
				return "redirect:/accreditamento/"+ accreditamentoId + "/dati/" + id + "/show/" + sezione;
			}
			return goToShow(model, prepareDatiAccreditamentoWrapperShow(datiAccreditamentoService.getDatiAccreditamento(id),accreditamentoId,sezione));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/"+ id +"/show/" + sezione),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId));
			return "redirect:/accreditamento/{accreditamentoId}";
		}
	};

	/***   VALIDATE   ***/
	@PreAuthorize("@securityAccessServiceImpl.canValidateAccreditamento(principal,#accreditamentoId,#showRiepilogo)")
	@RequestMapping("/accreditamento/{accreditamentoId}/dati/{id}/validate/{sezione}")
	public String validateDatiAccreditamento(@RequestParam(name = "showRiepilogo", required = false) Boolean showRiepilogo,
			@PathVariable Long id, @PathVariable Long accreditamentoId, @PathVariable Integer sezione, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/"+ id +"/validate/" + sezione));
		try{
			//controllo se è possibile modificare la valutazione o meno
			model.addAttribute("canValutaDomanda", accreditamentoService.canUserValutaDomanda(accreditamentoId, Utils.getAuthenticatedUser()));
			return goToValidate(model, prepareDatiAccreditamentoWrapperValidate(datiAccreditamentoService.getDatiAccreditamento(id), accreditamentoId, accreditamentoService.getStatoAccreditamento(accreditamentoId), false, sezione));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/"+ id +"/validate/" + sezione),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/validate"));
			return "redirect:/accreditamento/{accreditamentoId}/validate";
		}
	};

	/***	ENABLEFIELD	***/
	@PreAuthorize("@securityAccessServiceImpl.canEnableField(principal,#accreditamentoId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/dati/{id}/enableField/{sezione}")
	public String enabelFieldDatiAccreditamento(@PathVariable Long id, @PathVariable Long accreditamentoId, @PathVariable Integer sezione, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/"+ id +"/enableField/" + sezione));
		try{
			return goToEnableField(model, prepareDatiAccreditamentoWrapperEnableField(datiAccreditamentoService.getDatiAccreditamento(id),accreditamentoId,sezione));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/"+ id +"/enableField/" + sezione),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/enableField"));
			return "redirect:/accreditamento/{accreditamentoId}/enableField";
		}
	}

	/*** SAVE ***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/dati/save", method = RequestMethod.POST)
	public String saveDatiAccreditamento(@ModelAttribute("datiAccreditamentoWrapper") DatiAccreditamentoWrapper wrapper, BindingResult result,
											@PathVariable Long accreditamentoId, RedirectAttributes redirectAttrs, Model model){
		LOGGER.info(Utils.getLogMessage("POST /accreditamento/" + accreditamentoId + "/dati/save"));
		try {
//TODO - TEST
//			if(wrapper.getDatiAccreditamento().isNew()){
//				wrapper.setProvider(providerService.getProvider(wrapper.getProvider().getId()));
//			}

			//TODO getFile da testare se funziona anche senza reload
			//reload degli allegati perchè se è stato fatto un upload ajax...il wrapper non ha i byte[] aggiornati e nemmeno il ref a providerId
			List<FileEnum> tuttiFileGestiti = Arrays.asList(FileEnum.FILE_ESTRATTO_BILANCIO_FORMAZIONE, FileEnum.FILE_ESTRATTO_BILANCIO_COMPLESSIVO,
					FileEnum.FILE_FUNZIONIGRAMMA, FileEnum.FILE_ORGANIGRAMMA);
			Set<FileEnum> fileNonCancellati = new HashSet<FileEnum>();
			for(File file : wrapper.getFiles()){
				if(file != null && !file.isNew()){
					if(file.isESTRATTOBILANCIOFORMAZIONE()) {
						wrapper.setEstrattoBilancioFormazione(fileService.getFile(file.getId()));
						fileNonCancellati.add(FileEnum.FILE_ESTRATTO_BILANCIO_FORMAZIONE);
					}
					else if(file.isESTRATTOBILANCIOCOMPLESSIVO()) {
						wrapper.setEstrattoBilancioComplessivo(fileService.getFile(file.getId()));
						fileNonCancellati.add(FileEnum.FILE_ESTRATTO_BILANCIO_COMPLESSIVO);
					}
					else if(file.isFUNZIONIGRAMMA()) {
						wrapper.setFunzionigramma(fileService.getFile(file.getId()));
						fileNonCancellati.add(FileEnum.FILE_FUNZIONIGRAMMA);
					}
					else if(file.isORGANIGRAMMA())	{
						wrapper.setOrganigramma(fileService.getFile(file.getId()));
						fileNonCancellati.add(FileEnum.FILE_ORGANIGRAMMA);
					}
				}
			}
			//i files non trovati vanno rimossi perche' sono stati cancellati
			for(FileEnum fe : tuttiFileGestiti) {
				if(!fileNonCancellati.contains(fe)) {
					//cancello il file
					switch (fe) {
						case FILE_ESTRATTO_BILANCIO_FORMAZIONE:
							wrapper.setEstrattoBilancioFormazione(null);
							break;
						case FILE_ESTRATTO_BILANCIO_COMPLESSIVO:
							wrapper.setEstrattoBilancioComplessivo(null);
							break;
						case FILE_FUNZIONIGRAMMA:
							wrapper.setFunzionigramma(null);
							break;
						case FILE_ORGANIGRAMMA:
							wrapper.setOrganigramma(null);
							break;
					}
				}
			}

			//Ri-effettuo il detach..non sarebbe indispensabile...ma e' una precauzione a eventuali modifiche future
			//ci assicuriamo che effettivamente qualsiasi modifica alla entity in INTEGRAZIONE non venga flushata su DB
			AccreditamentoStatoEnum statoAccreditamento = accreditamentoService.getStatoAccreditamento(wrapper.getAccreditamentoId());
			if(statoAccreditamento == AccreditamentoStatoEnum.INTEGRAZIONE || statoAccreditamento == AccreditamentoStatoEnum.PREAVVISO_RIGETTO){
				/*
				 * 20161216 abarducci
				 * correzzione bug POST /accreditamento/3445/dati/save
				 * org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: it.tredi.ecm.dao.entity.File.fileData, could not initialize proxy - no Session
				 * at it.tredi.ecm.dao.entity.File.getData(File.java:81)
				 * at it.tredi.ecm.web.validator.FileValidator.validateData(FileValidator.java:40)
				 * at it.tredi.ecm.web.validator.FileValidator.validate(FileValidator.java:30)
				 * at it.tredi.ecm.web.validator.FileValidator.validateWithCondition(FileValidator.java:114)
				 * at it.tredi.ecm.web.validator.DatiAccreditamentoValidator.validateFilesConCondizione(DatiAccreditamentoValidator.java:85)
				 * at it.tredi.ecm.web.validator.DatiAccreditamentoValidator.validate(DatiAccreditamentoValidator.java:26)
				 * che si verifica in INTEGRAZIONE e PREAVVISO_RIGETTO a causa del successivo detach (vedi sotto)
				 * integrazioneService.detach(wrapper.getDatiAccreditamento());
				 * che detach anche i file restituiti da wrapper.getFiles() causando l'eccezione in validazione sul campo file.getFileData() che risulta lazy
				 */
				for(File file: wrapper.getFiles()) {
					if(file != null && !file.isNew())
						file.getFileData().size();
				}
				integrazioneService.detach(wrapper.getDatiAccreditamento());
//TODO - TEST
//				integrazioneService.detach(wrapper.getProvider());
			}
			LOGGER.debug(Utils.getLogMessage("MANAGED ENTITY: DatiAccreditamentoSave:__AFTER SET__"));
			//integrazioneService.isManaged(wrapper.getDatiAccreditamento());

			datiAccreditamentoValidator.validate(wrapper.getDatiAccreditamento(), result, "datiAccreditamento.", wrapper.getFiles(), wrapper.getProviderId(), wrapper.getSezione());

			if(result.hasErrors()){
				LOGGER.debug(Utils.getLogMessage("Validazione Fallita"));
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				if(wrapper.getStatoAccreditamento() == AccreditamentoStatoEnum.INTEGRAZIONE || statoAccreditamento == AccreditamentoStatoEnum.PREAVVISO_RIGETTO){
					integra(wrapper);
				}else{
					salva(wrapper);
				}
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

	private void integra(DatiAccreditamentoWrapper wrapper) throws Exception{
		LOGGER.info(Utils.getLogMessage("Integrazione DatiAccreditamento"));
		Accreditamento accreditamento = new Accreditamento();
		accreditamento.setId(wrapper.getAccreditamentoId());

		List<FieldIntegrazioneAccreditamento> fieldIntegrazioneList = new ArrayList<FieldIntegrazioneAccreditamento>();
		for(IdFieldEnum idField : wrapper.getIdEditabili()){
			if(idField.getGruppo().isEmpty()){
				if(!idField.isFileFromSet()){
					fieldIntegrazioneList.add(new FieldIntegrazioneAccreditamento(idField, accreditamento, integrazioneService.getField(wrapper.getDatiAccreditamento(), idField.getNameRef()), TipoIntegrazioneEnum.MODIFICA));
				}else{
					//gestione particolare per i file...
					//gestiamo la cancellazione dei file facoltativi
					Object fileId = integrazioneService.getField(wrapper.getDatiAccreditamento(), idField.getNameRef());
					if(fileId == null){
						fieldIntegrazioneList.add(new FieldIntegrazioneAccreditamento(idField, accreditamento, integrazioneService.getField(wrapper.getDatiAccreditamento(), idField.getNameRef()), TipoIntegrazioneEnum.ELIMINAZIONE));
					}else{
						fieldIntegrazioneList.add(new FieldIntegrazioneAccreditamento(idField, accreditamento, integrazioneService.getField(wrapper.getDatiAccreditamento(), idField.getNameRef()), TipoIntegrazioneEnum.MODIFICA));
					}
				}
			}
		}

		fieldIntegrazioneAccreditamentoService.update(wrapper.getFieldIntegrazione(), fieldIntegrazioneList);
	}

	private void salva(DatiAccreditamentoWrapper wrapper){
		LOGGER.debug(Utils.getLogMessage("Salvataggio DatiAccrdeditmento"));
//TODO - TEST
//		providerService.save(wrapper.getProvider());
		datiAccreditamentoService.save(wrapper.getDatiAccreditamento(), wrapper.getAccreditamentoId());
	}

	/*** SAVE VALUTAZIONE
	 * @throws Exception ***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/dati/validate", method = RequestMethod.POST)
	public String valutaDatiAccreditamento(@ModelAttribute("datiAccreditamentoWrapper") DatiAccreditamentoWrapper wrapper, BindingResult result,
											@PathVariable Long accreditamentoId, RedirectAttributes redirectAttrs, Model model) throws Exception{
		LOGGER.info(Utils.getLogMessage("POST /accreditamento/" + accreditamentoId + "/dati/validate"));
		try {
			//valutazioneValidator.validateValutazione(wrapper.getMappa(), result);
			valutazioneValidator.validateValutazioneDatiAccreditamento(wrapper.getMappa(), result, wrapper.getSezione());

			if(result.hasErrors()){
				model.addAttribute("canValutaDomanda", accreditamentoService.canUserValutaDomanda(accreditamentoId, Utils.getAuthenticatedUser()));
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

				Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamento.getId(), Utils.getAuthenticatedUser().getAccount().getId());
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
			model.addAttribute("canValutaDomanda", accreditamentoService.canUserValutaDomanda(accreditamentoId, Utils.getAuthenticatedUser()));
			model.addAttribute("accreditamentoId", wrapper.getAccreditamentoId());
			model.addAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
			return VALIDATE;
		}
	}

	/*** 	SAVE  ENABLEFIELD   ***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/dati/enableField", method = RequestMethod.POST)
	public String enableFieldDatiAccreditamento(@ModelAttribute("richiestaIntegrazioneWrapper") RichiestaIntegrazioneWrapper richiestaIntegrazioneWrapper, @PathVariable Long accreditamentoId,
												Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /accreditamento/" + accreditamentoId + "/dati/enableField"));
		try{
			integrazioneService.saveEnableField(richiestaIntegrazioneWrapper);
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.campi_salvati", "success"));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/dati/enableField"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/enableField"));
		}
		return "redirect:/accreditamento/{accreditamentoId}/enableField";
	};


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

	private String goToEnableField(Model model, DatiAccreditamentoWrapper wrapper){
		model.addAttribute("datiAccreditamentoWrapper", wrapper);
		model.addAttribute("richiestaIntegrazioneWrapper", integrazioneService.prepareRichiestaIntegrazioneWrapper(wrapper.getAccreditamentoId(), SubSetFieldEnum.DATI_ACCREDITAMENTO, null));
		LOGGER.info(Utils.getLogMessage("VIEW: " + ENABLEFIELD));
		return ENABLEFIELD;
	}

	private DatiAccreditamentoWrapper prepareDatiAccreditamentoWrapperEdit(DatiAccreditamento datiAccreditamento, AccreditamentoStatoEnum statoAccreditamento, boolean reloadByEditId, int sezione) throws Exception{
		return prepareDatiAccreditamentoWrapperEdit(datiAccreditamento, 0, statoAccreditamento, reloadByEditId, sezione);
	}

	/*
	 * Se INTEGRAZIONE:
	 * caso 1: MODIFICA SINGOLO CAMPO
	 * 		(+) Saranno sbloccati SOLO gli IdFieldEnum eslpicitamente abilitati dalla segreteria (creazione di FieldEditabileAccreditamento)
	 * 		(+) Vengono applicati eventuali fieldIntegrazioneAccreditamento già salvati per visualizzare correttamente lo stato attuale delle modifiche
	 */
	private DatiAccreditamentoWrapper prepareDatiAccreditamentoWrapperEdit(DatiAccreditamento datiAccreditamento, long accreditamentoId, AccreditamentoStatoEnum statoAccreditamento, boolean reloadByEditId, int sezione) throws Exception{
		LOGGER.info(Utils.getLogMessage("prepareDatiAccreditamentoWrapperEdit(" + datiAccreditamento.getId() + "," + accreditamentoId + ") - entering"));

		SubSetFieldEnum subset = SubSetFieldEnum.DATI_ACCREDITAMENTO;

		DatiAccreditamentoWrapper wrapper = new DatiAccreditamentoWrapper(datiAccreditamento, accreditamentoId, accreditamentoService.getProviderIdForAccreditamento(accreditamentoId));
		//la Segreteria se non è in uno stato di integrazione/preavviso rigetto può sempre modificare
		if (Utils.getAuthenticatedUser().getAccount().isSegreteria() && statoAccreditamento != AccreditamentoStatoEnum.INTEGRAZIONE && statoAccreditamento != AccreditamentoStatoEnum.PREAVVISO_RIGETTO)
			wrapper.setIdEditabili(IdFieldEnum.getAllForSubset(subset));
		else
			wrapper.setIdEditabili(Utils.getSubsetOfIdFieldEnum(fieldEditabileService.getAllFieldEditabileForAccreditamento(accreditamentoId), subset));
		wrapper.setStatoAccreditamento(statoAccreditamento);
		wrapper.setWrapperMode(AccreditamentoWrapperModeEnum.EDIT);
		wrapper.setSezione(sezione);

//TODO - TEST

		if(datiAccreditamento.isNew()){
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
//			wrapper.setProvider(accreditamento.getProvider());
		}else{
//			wrapper.setProvider(datiAccreditamento.getAccreditamento().getProvider());
			if(!reloadByEditId)
				wrapper.setFiles(wrapper.getDatiAccreditamento().getFiles());
		}

		if(statoAccreditamento == AccreditamentoStatoEnum.INTEGRAZIONE || statoAccreditamento == AccreditamentoStatoEnum.PREAVVISO_RIGETTO){
			prepareApplyIntegrazione(wrapper, subset, reloadByEditId);
		}

		if(!reloadByEditId && !wrapper.getDatiAccreditamento().isNew()){
			wrapper.setFiles(wrapper.getDatiAccreditamento().getFiles());
		}

		LOGGER.info(Utils.getLogMessage("prepareDatiAccreditamentoWrapperEdit(" + datiAccreditamento.getId() + "," + accreditamentoId + ") - exiting"));
		return wrapper;
	}

	private void prepareApplyIntegrazione(DatiAccreditamentoWrapper wrapper, SubSetFieldEnum subset, boolean reloadByEditId) throws Exception{
		wrapper.setFieldIntegrazione(Utils.getSubset(fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazioneForAccreditamento(wrapper.getAccreditamentoId()), subset));
//		wrapper.getProvider().getFiles().size();
		wrapper.getDatiAccreditamento().getFiles().size();
		wrapper.getDatiAccreditamento().getProcedureFormative().size();
		integrazioneService.detach(wrapper.getDatiAccreditamento());
//		integrazioneService.detach(wrapper.getProvider());
		if(!reloadByEditId){
			integrazioneService.applyIntegrazioneObject(wrapper.getDatiAccreditamento(), wrapper.getFieldIntegrazione());
		}
	}

	private DatiAccreditamentoWrapper prepareDatiAccreditamentoWrapperShow(DatiAccreditamento datiAccreditamento, long accreditamentoId, int sezione){
		LOGGER.info(Utils.getLogMessage("prepareDatiAccreditamentoWrapperShow(" + datiAccreditamento.getId() + "," + accreditamentoId + ") - entering"));

		DatiAccreditamentoWrapper wrapper = new DatiAccreditamentoWrapper(datiAccreditamento, accreditamentoId, accreditamentoService.getProviderIdForAccreditamento(accreditamentoId));
		wrapper.setFiles(datiAccreditamento.getFiles());
		wrapper.setSezione(sezione);
		LOGGER.info(Utils.getLogMessage("prepareDatiAccreditamentoWrapperShow(" + datiAccreditamento.getId() + "," + accreditamentoId + ") - exiting"));
		return wrapper;
	};

	private DatiAccreditamentoWrapper prepareDatiAccreditamentoWrapperValidate(DatiAccreditamento datiAccreditamento, long accreditamentoId, AccreditamentoStatoEnum statoAccreditamento, boolean reloadByEditId, int sezione) throws Exception{
		LOGGER.info(Utils.getLogMessage("prepareDatiAccreditamentoWrapperValidate(" + datiAccreditamento.getId() + "," + accreditamentoId + ") - entering"));

		SubSetFieldEnum subset = SubSetFieldEnum.DATI_ACCREDITAMENTO;

		DatiAccreditamentoWrapper wrapper = new DatiAccreditamentoWrapper(datiAccreditamento, accreditamentoId, accreditamentoService.getProviderIdForAccreditamento(accreditamentoId));
//		wrapper.setProvider(datiAccreditamento.getAccreditamento().getProvider());
		wrapper.setStatoAccreditamento(statoAccreditamento);
		wrapper.setWrapperMode(AccreditamentoWrapperModeEnum.VALIDATE);
		wrapper.setSezione(sezione);

		//carico la valutazione per l'utente
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		if(valutazione != null) {
			mappa = fieldValutazioneAccreditamentoService.filterFieldValutazioneBySubSetAsMap(valutazione.getValutazioni(), subset);
		}

		//cerco tutte le valutazioni del subset datiAccreditamento per ciascun valutatore dell'accreditamento
		Map<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>> mappaValutatoreValutazioni = new HashMap<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>>();
		mappaValutatoreValutazioni  = valutazioneService.getMapValutatoreValutazioniByAccreditamentoIdAndSubSet(accreditamentoId, subset);

		//prendo tutti gli id del subset
		Set<IdFieldEnum> idEditabili = new HashSet<IdFieldEnum>();
		idEditabili = IdFieldEnum.getAllForSubset(subset);

		wrapper.setIdEditabili(idEditabili);
		wrapper.setMappaValutatoreValutazioni(mappaValutatoreValutazioni);
		wrapper.setMappa(mappa);

		if(statoAccreditamento == AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA){
			prepareApplyIntegrazione(wrapper, subset, reloadByEditId);
		}

		wrapper.setFiles(wrapper.getDatiAccreditamento() .getFiles());

		LOGGER.info(Utils.getLogMessage("prepareDatiAccreditamentoWrapperValidate(" + datiAccreditamento.getId() + "," + accreditamentoId + ") - exiting"));
		return wrapper;
	};

	private DatiAccreditamentoWrapper prepareDatiAccreditamentoWrapperEnableField(DatiAccreditamento datiAccreditamento, long accreditamentoId, int sezione){
		LOGGER.info(Utils.getLogMessage("prepareDatiAccreditamentoWrapperEnanleField(" + datiAccreditamento.getId() + "," + accreditamentoId + ") - entering"));
		DatiAccreditamentoWrapper wrapper = prepareDatiAccreditamentoWrapperShow(datiAccreditamento, accreditamentoId,sezione);
		LOGGER.info(Utils.getLogMessage("prepareDatiAccreditamentoWrapperEnanleField(" + datiAccreditamento.getId() + "," + accreditamentoId + ") - exiting"));
		return wrapper;
	};

}
