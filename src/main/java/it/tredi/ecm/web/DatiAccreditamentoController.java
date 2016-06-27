package it.tredi.ecm.web;

import java.util.LinkedList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.DatiAccreditamentoService;
import it.tredi.ecm.service.DisciplinaService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.ProfessioneService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.web.bean.DatiAccreditamentoWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.DatiAccreditamentoValidator;

@Controller
public class DatiAccreditamentoController {
	private static Logger LOGGER = LoggerFactory.getLogger(DatiAccreditamentoController.class);
	
	private final String EDIT = "accreditamento/datiAccreditamentoEdit";
	
	@Autowired
	private DatiAccreditamentoService datiAccreditamentoService;
	@Autowired
	private DatiAccreditamentoValidator datiAccreditamentoValidator;
	@Autowired
	private DisciplinaService disciplinaService;
	@Autowired
	private ProfessioneService professioneService;
	@Autowired
	private FileService fileService;
	@Autowired
	private ProviderService providerService;
	@Autowired
	private AccreditamentoService accreditamentoService;
	
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}
	
	@ModelAttribute("datiAccreditamentoWrapper")
	public DatiAccreditamentoWrapper getDatiAccreditamentoWrapper(@RequestParam(value="editId",required = false) Long id){
		if(id != null){
			return prepareDatiAccreditamentoWrapper(datiAccreditamentoService.getDatiAccreditamento(id));
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
	@RequestMapping("/accreditamento/{accreditamentoId}/dati/new")
	public String newDatiAccreditamento(@PathVariable Long accreditamentoId, Model model, RedirectAttributes redirectAttrs){
		try {
			return goToEdit(model, prepareDatiAccreditamentoWrapper(new DatiAccreditamento(),accreditamentoId));
		}catch (Exception ex){
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			return "redirect:/accreditamento/{accreditamentoId}";
		}
	}
	
	/*** EDIT ***/
	@RequestMapping("/accreditamento/{accreditamentoId}/dati/{id}/edit")
	public String editDatiAccreditamento(@PathVariable Long id, @PathVariable Long accreditamentoId, Model model, RedirectAttributes redirectAttrs){
		try{
			return goToEdit(model, prepareDatiAccreditamentoWrapper(datiAccreditamentoService.getDatiAccreditamento(id),accreditamentoId));
		}catch (Exception ex){
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			return "redirect:/accreditamento/{accreditamentoId}";
		}
	};
	
	/*** SAVE ***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/dati/save", method = RequestMethod.POST)
	public String saveDatiAccreditamento(@ModelAttribute("datiAccreditamentoWrapper") DatiAccreditamentoWrapper wrapper, BindingResult result,
											@PathVariable Long accreditamentoId, RedirectAttributes redirectAttrs, Model model){
		try {
			//TODO getFile da testare se funziona anche senza reload
			//reload degli allegati perchè se è stato fatto un upload ajax...il wrapper non ha i byte[] aggiornati e nemmeno il ref a providerId
			for(File file : wrapper.getFiles()){
				if(file != null && !file.isNew()){
					if(file.isESTRATTOBILANCIOFORMAZIONE())
						wrapper.setEstrattoBilancioFormazione(fileService.getFile(file.getId()));
					else if(file.isBUDGETPREVISIONALE())
						wrapper.setBudgetPrevisionale(fileService.getFile(file.getId()));
					else if(file.isFUNZIONIGRAMMA())
						wrapper.setFunzionigramma(fileService.getFile(file.getId()));
					else if(file.isORGANIGRAMMA())
						wrapper.setOrganigramma(fileService.getFile(file.getId()));
				}
			}
			
			datiAccreditamentoValidator.validate(wrapper.getDatiAccreditamento(), result, "datiAccreditamento.", wrapper.getFiles());
			
			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				return EDIT;
			}else{
				providerService.save(wrapper.getProvider());
				datiAccreditamentoService.save(wrapper.getDatiAccreditamento(), accreditamentoId);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.dati_attivita_inseriti", "success"));
				redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
				return "redirect:/accreditamento/{accreditamentoId}";
			}
		}catch (Exception ex){
			//TODO gestione eccezione
			LOGGER.error("Errore Salvataggio", ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return EDIT;
		}
	}
	
	private String goToEdit(Model model, DatiAccreditamentoWrapper wrapper){
		model.addAttribute("datiAccreditamentoWrapper", wrapper);
		return EDIT;
	}
	
	private DatiAccreditamentoWrapper prepareDatiAccreditamentoWrapper(DatiAccreditamento datiAccreditamento){
		return prepareDatiAccreditamentoWrapper(datiAccreditamento, 0);
	}
	
	private DatiAccreditamentoWrapper prepareDatiAccreditamentoWrapper(DatiAccreditamento datiAccreditamento, long accreditamentoId){
		DatiAccreditamentoWrapper wrapper = new DatiAccreditamentoWrapper();
		wrapper.setAccreditamentoId(accreditamentoId);
		wrapper.setDatiAccreditamento(datiAccreditamento);
		
		if(datiAccreditamento.isNew()){
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
			wrapper.setProvider(accreditamento.getProvider());
		}else{
			wrapper.setProvider(datiAccreditamento.getAccreditamento().getProvider());
			Set<File> files = wrapper.getProvider().getFiles();
			for(File file : files){
				if(file.isESTRATTOBILANCIOFORMAZIONE())
					wrapper.setEstrattoBilancioFormazione(file);
				else if(file.isBUDGETPREVISIONALE())
					wrapper.setBudgetPrevisionale(file);
				else if(file.isFUNZIONIGRAMMA())
					wrapper.setFunzionigramma(file);
				else if(file.isORGANIGRAMMA())
					wrapper.setOrganigramma(file);
			}
		}
		
		wrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_DATI_ACCREDITAMENTO), accreditamentoService.getIdEditabili(accreditamentoId));
		
		return wrapper;
	};
}
