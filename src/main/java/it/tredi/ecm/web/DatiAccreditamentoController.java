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
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.service.DatiAccreditamentoService;
import it.tredi.ecm.service.DisciplinaService;
import it.tredi.ecm.service.ProfessioneService;
import it.tredi.ecm.web.bean.DatiAccreditamentoWrapper;
import it.tredi.ecm.web.validator.DatiAccreditamentoValidator;

@Controller
public class DatiAccreditamentoController {
	private final String EDIT = "provider/accreditamentoEdit";
	
	@Autowired
	private DatiAccreditamentoService datiAccreditamentoService;
	@Autowired
	private DatiAccreditamentoValidator datiAccreditamentoValidator;
	@Autowired
	private DisciplinaService disciplinaService;
	@Autowired
	private ProfessioneService professioneService;
	
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
	
<<<<<<< HEAD
	@ModelAttribute("professioneList")
	public Set<Professione> getAllProfessioni(){
		return professioneService.getAllProfessioni();
	}
	
	@ModelAttribute("disciplinaList")
=======
	@ModelAttribute("professioneLista")
	public Set<Professione> getAllProfessioni(){
		return professioneService.getAllProfessioni();
	}
	
	@ModelAttribute("disciplinaLista")
>>>>>>> branch 'master' of http://gitlab.bo.priv/ecm/it.tredi.ecm.git
	public Set<Disciplina> getAllDiscipline(){
		return disciplinaService.getAllDiscipline();
	}

	@RequestMapping("/accreditamento/{accreditamentoId}/dati/new")
	public String newDatiAccreditamento(@PathVariable Long accreditamentoId, Model model){
		return goToEdit(model, prepareDatiAccreditamentoWrapper(new DatiAccreditamento(),accreditamentoId));
	}
	
	@RequestMapping("/accreditamento/{accreditamentoId}/dati/{id}/edit")
	public String editDatiAccreditamento(@PathVariable Long id, @PathVariable Long accreditamentoId, Model model){
		try{
			return goToEdit(model, prepareDatiAccreditamentoWrapper(datiAccreditamentoService.getDatiAccreditamento(id),accreditamentoId));
		}catch (Exception ex){
			//TODO gestione eccezione
		}
		
		return "redirect:/accreditamento/" + accreditamentoId;
	};
	
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/dati/save", method = RequestMethod.POST)
	public String saveDatiAccreditamento(@ModelAttribute("datiAccreditamentoWrapper") DatiAccreditamentoWrapper wrapper, BindingResult result,
											@PathVariable Long accreditamentoId, Model model){

		datiAccreditamentoValidator.validate(wrapper.getDatiAccreditamento(), result, "datiAccreditamento.", null);
		
		if(result.hasErrors()){
			return EDIT;
		}else{
			datiAccreditamentoService.save(wrapper.getDatiAccreditamento(), accreditamentoId);
			return "redirect:/accreditamento/" + accreditamentoId;
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
		
		wrapper.setOffsetAndIds();
		wrapper.setIdEditabili(Arrays.asList(39,40,41,42,43,44,45));
		
		return wrapper;
	};
}
