package it.tredi.ecm.web;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.FieldEditabile;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.repository.FieldEditabileRepository;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.TestWrapper;

@Controller
@SessionAttributes("wrapper")
public class IntegrazioneController {

	@Autowired AccreditamentoService accreditamentoService;
	@Autowired FieldEditabileRepository repo;

	@RequestMapping("/fieldEditabile/{accreditamentoId}")
	public String edit(@PathVariable Long accreditamentoId, Model model, 
						@RequestParam("subset") SubSetFieldEnum subset,
						 @RequestParam(name = "objRef", required=false) Long objectReference){
		model.addAttribute("wrapper", prepareWrapper(accreditamentoId, subset, objectReference));
		return "test";
	}

	@RequestMapping("/fieldEditabile/save")
	public String save(@ModelAttribute("wrapper") TestWrapper wrapper, Model model, RedirectAttributes redirAttributes){

		Set<IdFieldEnum> listaDaView = wrapper.getSelected();

		Set<FieldEditabile> listaFull = wrapper.getFullLista();
		Set<FieldEditabile> listaSubset = Utils.getSubset(listaFull, wrapper.getSubset());

		Accreditamento accreditamento = accreditamentoService.getAccreditamento(wrapper.getAccreditamentoId());

		listaSubset.forEach(f -> {
			if(listaDaView == null || !listaDaView.contains(f.getIdField())){
				repo.delete(f);
			}
		});

		if(listaDaView != null){
			for(IdFieldEnum id : listaDaView){
				if(Utils.getField(listaFull,id) == null){
					FieldEditabile field = new FieldEditabile();
					field.setAccreditamento(accreditamento);
					field.setIdField(id);
					if(wrapper.getObjRef() != null)
						field.setObjectReference(wrapper.getObjRef());
					
					repo.save(field);
				}
			}
		}

		redirAttributes.addAttribute("accreditamentoId", wrapper.getAccreditamentoId());
		redirAttributes.addAttribute("subset", wrapper.getSubset());
		if(wrapper.getObjRef() != null)
			redirAttributes.addAttribute("objRef", wrapper.getObjRef());
		return "redirect:/fieldEditabile/{accreditamentoId}";
	}

	private TestWrapper prepareWrapper(Long accreditamentoId, SubSetFieldEnum subset, Long objRef){
		TestWrapper wrapper = new TestWrapper();
		
		Set<FieldEditabile> fullLista = null;
		if(objRef == null)
			fullLista = repo.findAllByAccreditamentoId(accreditamentoId);
		else
			fullLista = repo.findAllByAccreditamentoIdAndObjectReference(accreditamentoId, objRef);
		
		wrapper.setAccreditamentoId(accreditamentoId);
		wrapper.setSubset(subset);
		wrapper.setObjRef(objRef);
		wrapper.setFullLista(fullLista);
		wrapper.setSelected(Utils.getSubsetOfIdFieldEnum(fullLista, subset));

		return wrapper;
	}
}
