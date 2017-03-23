package it.tredi.ecm.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.repository.FieldIntegrazioneAccreditamentoRepository;
import it.tredi.ecm.service.IntegrazioneService;
import it.tredi.ecm.web.bean.ApprovaIntegrazioneWrapper;
import it.tredi.ecm.web.bean.Message;

@Controller
@SessionAttributes("wrapper")
public class ApprovaIntegrazioneController {
	@Autowired private FieldIntegrazioneAccreditamentoRepository repoIntegrazione;
	@Autowired private IntegrazioneService integrazioneService;

//	@RequestMapping("/accreditamento/{accreditamentoId}/integrazione")
//	public String integrazione(@PathVariable Long accreditamentoId, Model model){
//		model.addAttribute("wrapper", prepareIntegrazioneWrapper(accreditamentoId));
//		return "segreteria/integrazione";
//	}
//
//	@RequestMapping("/accreditamento/{accreditamentoId}/integrazione/save")
//	public String saveIntegrazione(@ModelAttribute("wrapper") IntegrazioneWrapper_old wrapper, Model model, RedirectAttributes redirAttributes){
//		try{
//			if(wrapper.getFullLista() != null && !wrapper.getFullLista().isEmpty()){
//				for(FieldIntegrazioneAccreditamento field : wrapper.getFullLista()){
//					if(field.getNewValue() != null)
//						repoIntegrazione.save(field);
//				}
//			}
//			model.addAttribute("message",new Message("message.success", "message.salvatggio", "success"));
//		}catch (Exception ex){
//			model.addAttribute("message",new Message("message.error", "message.exception", "error"));
//		}
//
//		return "segreteria/applicaIntegrazione";
//	}

//	private IntegrazioneWrapper_old prepareIntegrazioneWrapper(Long accreditamentoId){
//		IntegrazioneWrapper_old wrapper = new IntegrazioneWrapper_old();
//
//		Set<FieldEditabileAccreditamento> idEditabili = repoEditabile.findAllByAccreditamentoId(accreditamentoId);
//
//		List<FieldIntegrazioneAccreditamento> idIntegrazione = new ArrayList<FieldIntegrazioneAccreditamento>();
//		Map<IdFieldEnum,FieldIntegrazioneAccreditamento> map = new HashMap<IdFieldEnum, FieldIntegrazioneAccreditamento>();
//		for(FieldEditabileAccreditamento field : idEditabili){
//			idIntegrazione.add(new FieldIntegrazioneAccreditamento(field.getIdField(), field.getAccreditamento(), null, TipoIntegrazioneEnum.MODIFICA));
//			map.put(field.getIdField(), new FieldIntegrazioneAccreditamento(field.getIdField(), field.getAccreditamento(), null, TipoIntegrazioneEnum.MODIFICA));
//		}
//
//		wrapper.setAccreditamentoId(accreditamentoId);
//		wrapper.setFullLista(idIntegrazione);
//		wrapper.setMap(map);
//
//		return wrapper;
//	}


//	@RequestMapping("/accreditamento/{accreditamentoId}/approvaIntegrazione")
//	public String approvaIntegrazione(@PathVariable Long accreditamentoId, Model model){
//		model.addAttribute("wrapper", prepareApprovaIntegrazioneWrapper(accreditamentoId));
//		return "segreteria/applicaIntegrazione";
//	}

//	@RequestMapping("/accreditamento/{accreditamentoId}/approvaIntegrazione/save")
//	public String saveApprovaIntegrazione(@ModelAttribute("wrapper") ApprovaIntegrazioneWrapper wrapper, Model model, RedirectAttributes redirAttributes){
//		try{
//			if(wrapper.getSelected() != null && !wrapper.getSelected().isEmpty()){
//				integrazioneService.applyIntegrazioneAccreditamentoAndSave(wrapper.getAccreditamentoId(), wrapper.getSelected());
//				repoIntegrazione.delete(wrapper.getSelected());
//			}
//			model.addAttribute("message",new Message("message.success", "message.salvatggio", "success"));
//		}catch (Exception ex){
//			ex.printStackTrace();
//			model.addAttribute("message",new Message("message.error", "message.exception", "error"));
//		}
//
//		return "redirect:/accreditamento/" + wrapper.getAccreditamentoId() + "/approvaIntegrazione";
//	}

//	private ApprovaIntegrazioneWrapper prepareApprovaIntegrazioneWrapper(Long accreditamentoId){
//		ApprovaIntegrazioneWrapper wrapper = new ApprovaIntegrazioneWrapper();
//
//		wrapper.setAccreditamentoId(accreditamentoId);
//		wrapper.setFullLista(repoIntegrazione.findAllByAccreditamentoIdAndFittizioFalse(accreditamentoId));
//
//		return wrapper;
//	}

}
