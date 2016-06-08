package it.tredi.ecm.web;

import java.util.Arrays;
import java.util.Set;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.DatiAccreditamentoService;
import it.tredi.ecm.service.DisciplinaService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.ProfessioneService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.DatiAccreditamentoWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.DatiAccreditamentoValidator;

@Controller
public class DatiAccreditamentoController {
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

	@RequestMapping("/accreditamento/{accreditamentoId}/dati/new")
	public String newDatiAccreditamento(@PathVariable Long accreditamentoId, Model model){
		try {
			return goToEdit(model, prepareDatiAccreditamentoWrapper(new DatiAccreditamento(),accreditamentoId));
		}catch (Exception ex){
			//TODO gestione eccezione
			return "redirect:/accreditamento/" + accreditamentoId;
		}
	}
	
	@RequestMapping("/accreditamento/{accreditamentoId}/dati/{id}/edit")
	public String editDatiAccreditamento(@PathVariable Long id, @PathVariable Long accreditamentoId, Model model){
		try{
			return goToEdit(model, prepareDatiAccreditamentoWrapper(datiAccreditamentoService.getDatiAccreditamento(id),accreditamentoId));
		}catch (Exception ex){
			//TODO gestione eccezione
			return "redirect:/accreditamento/" + accreditamentoId;
		}
	};
	
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/dati/save", method = RequestMethod.POST)
	public String saveDatiAccreditamento(@ModelAttribute("datiAccreditamentoWrapper") DatiAccreditamentoWrapper wrapper, BindingResult result,
											@PathVariable Long accreditamentoId, RedirectAttributes redirectAttrs, Model model,
											@RequestParam(value = "estrattoBilancioFormazione_multipart", required = false) MultipartFile estrattoBilancioFormazione_multipart,
											@RequestParam(value = "budgetPrevisionale_multipart", required = false) MultipartFile budgetPrevisionale_multipart,
											@RequestParam(value = "funzionigramma_multipart", required = false) MultipartFile funzionigramma_multipart,
											@RequestParam(value = "organigramma_multipart", required = false) MultipartFile organigramma_multipart){
		
		try {
			if(estrattoBilancioFormazione_multipart != null && !estrattoBilancioFormazione_multipart.isEmpty())
				wrapper.setEstrattoBilancioFormazione(Utils.convertFromMultiPart(estrattoBilancioFormazione_multipart));
			if(budgetPrevisionale_multipart != null && !budgetPrevisionale_multipart.isEmpty())
				wrapper.setBudgetPrevisionale(Utils.convertFromMultiPart(budgetPrevisionale_multipart));
			if(funzionigramma_multipart != null && !funzionigramma_multipart.isEmpty())
				wrapper.setFunzionigramma(Utils.convertFromMultiPart(funzionigramma_multipart));
			if(organigramma_multipart != null && !organigramma_multipart.isEmpty())
				wrapper.setOrganigramma(Utils.convertFromMultiPart(organigramma_multipart));
			
			datiAccreditamentoValidator.validate(wrapper.getDatiAccreditamento(), result, "datiAccreditamento.", wrapper.getFiles());
			
			if(result.hasErrors()){
				
				if(!result.hasFieldErrors("estrattoBilancioFormazione*") && estrattoBilancioFormazione_multipart != null && !estrattoBilancioFormazione_multipart.isEmpty()){
					fileService.save(wrapper.getEstrattoBilancioFormazione());
				}
				if(!result.hasFieldErrors("budgetPrevisionale*") && budgetPrevisionale_multipart != null && !budgetPrevisionale_multipart.isEmpty()){
					fileService.save(wrapper.getBudgetPrevisionale());
				}
				if(!result.hasFieldErrors("funzionigramma*") && funzionigramma_multipart != null && !funzionigramma_multipart.isEmpty()){
					fileService.save(wrapper.getFunzionigramma());
				}
				if(!result.hasFieldErrors("organigramma*") && organigramma_multipart != null && !organigramma_multipart.isEmpty()){
					fileService.save(wrapper.getOrganigramma());
				}
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				return EDIT;
			}else{
				datiAccreditamentoService.save(wrapper.getDatiAccreditamento(), accreditamentoId);
				saveFiles(wrapper, estrattoBilancioFormazione_multipart, budgetPrevisionale_multipart, funzionigramma_multipart, organigramma_multipart);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.dati_attivita_inseriti", "success"));
				return "redirect:/accreditamento/" + accreditamentoId;
			}
		}catch (Exception ex){
			//TODO gestione eccezione
			return EDIT;
		}
	}
	
	private void saveFiles(DatiAccreditamentoWrapper wrapper, MultipartFile estrattoBilancioFormazione_multipart, MultipartFile budgetPrevisionale_multipart,
			MultipartFile funzionigramma_multipart, MultipartFile organigramma_multipart){
		if(estrattoBilancioFormazione_multipart != null && !estrattoBilancioFormazione_multipart.isEmpty()){
			fileService.save(wrapper.getEstrattoBilancioFormazione());
		}
		if(budgetPrevisionale_multipart != null && !budgetPrevisionale_multipart.isEmpty()){
			fileService.save(wrapper.getBudgetPrevisionale());
		}
		if(funzionigramma_multipart != null && !funzionigramma_multipart.isEmpty()){
			fileService.save(wrapper.getFunzionigramma());
		}
		if(organigramma_multipart != null && !organigramma_multipart.isEmpty()){
			fileService.save(wrapper.getOrganigramma());
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
			Set<File> files = fileService.getFileFromProvider(datiAccreditamento.getAccreditamento().getProvider().getId());
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
		
		wrapper.setOffsetAndIds();
		wrapper.setIdEditabili(Arrays.asList(39,40,41,42,43,44,45,46,47,48,49));
		
		return wrapper;
	};
}
