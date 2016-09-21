package it.tredi.ecm.web;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.FieldEditabileAccreditamento;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.FieldEditabileAccreditamentoService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.RichiestaIntegrazioneWrapper;

@Controller
public class IntegrazioneController {

	@Autowired AccreditamentoService accreditamentoService;
	@Autowired FieldEditabileAccreditamentoService fieldEditabileAccreditamentoService;
	//@Autowired FieldIntegrazioneAccreditamentoRepository integrazioneRepo;
/** PRIMA VERSIONE IN CUI SI PASSAVA TUTTO DA URL
 		fieldEditabile/416?SUBSET_PROVIDER **/
	@RequestMapping("/fieldEditabile/{accreditamentoId}")
	public String edit(@PathVariable Long accreditamentoId, Model model, 
						@RequestParam("subset") SubSetFieldEnum subset,
						 @RequestParam(name = "objRef", required=false) Long objectReference){
		model.addAttribute("wrapper", prepareWrapper(accreditamentoId, subset, objectReference));
		return "test";
	}

	@RequestMapping("/fieldEditabile/save")
	public String save(@ModelAttribute("richiestaIntegrazioneWrapper") RichiestaIntegrazioneWrapper wrapper, Model model, RedirectAttributes redirAttributes){
		Set<IdFieldEnum> listaDaView = wrapper.getSelected();
		Set<FieldEditabileAccreditamento> listaFull = fieldEditabileAccreditamentoService.getFullLista(wrapper.getAccreditamentoId(), wrapper.getObjRef());
		Set<FieldEditabileAccreditamento> listaSubset = Utils.getSubset(listaFull, wrapper.getSubset());

		listaSubset.forEach(f -> {
			if(listaDaView == null || !listaDaView.contains(f.getIdField())){
				//repo.delete(f);
				fieldEditabileAccreditamentoService.delete(f);
			}
		});

//		if(listaDaView != null){
//			for(IdFieldEnum id : listaDaView){
//				if(Utils.getField(listaFull,id) == null){
//					FieldEditabileAccreditamento field = new FieldEditabileAccreditamento();
//					field.setAccreditamento(accreditamento);
//					field.setIdField(id);
//					if(wrapper.getObjRef() != null)
//						field.setObjectReference(wrapper.getObjRef());
//					repo.save(field);
//				}
//			}
//		}

		fieldEditabileAccreditamentoService.insertFieldEditabileForAccreditamento(wrapper.getAccreditamentoId(), wrapper.getObjRef(), wrapper.getSubset(), listaDaView);
		
		redirAttributes.addAttribute("accreditamentoId", wrapper.getAccreditamentoId());
		redirAttributes.addAttribute("subset", wrapper.getSubset());
		if(wrapper.getObjRef() != null)
			redirAttributes.addAttribute("objRef", wrapper.getObjRef());
		return "redirect:/fieldEditabile/{accreditamentoId}";
	}
	
	private RichiestaIntegrazioneWrapper prepareWrapper(Long accreditamentoId, SubSetFieldEnum subset, Long objRef){
		RichiestaIntegrazioneWrapper wrapper = new RichiestaIntegrazioneWrapper();
		Set<FieldEditabileAccreditamento> fullLista = fieldEditabileAccreditamentoService.getFullLista(accreditamentoId, objRef);
		
		wrapper.setAccreditamentoId(accreditamentoId);
		wrapper.setSubset(subset);
		wrapper.setObjRef(objRef);
		wrapper.setSelected(Utils.getSubsetOfIdFieldEnum(fullLista, subset));

		return wrapper;
	}
}
