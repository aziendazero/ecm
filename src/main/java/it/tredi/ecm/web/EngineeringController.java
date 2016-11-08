package it.tredi.ecm.web;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.net.URLConnection;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.service.EngineeringService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EngineeringWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.EngineeringValidator;
import it.veneto.regione.firma.DOCUMENT;
import it.veneto.regione.firma.DOCUMENTS;
import it.veneto.regione.firma.FILE;
import it.veneto.regione.firma.FIRMAFILES;
import it.veneto.regione.firma.SOURCE;

@Controller
public class EngineeringController {

	private static Logger LOGGER = LoggerFactory.getLogger(EngineeringController.class);
	
	private static final String FIRMA_URL = "http://svilcomune.ve.eng.it/FirmaWeb/servlet/AdapterHTTP";
	
//	private static final String FIRMA_URL = "https://servizi.collaudo.regione.veneto.it/FirmaWeb/servlet/AdapterHTTP";
	
	private static final String REFERER = "http://192.168.44.171:8080/ecm/*";
	
	private static final String ID_CLASSIFICAZIONE = "60.00.05.00.00-C.120.21.1.B4";

	@Autowired private ProviderService providerService;
	@Autowired private FileService fileService;
	@Autowired private EventoService eventoService;
	@Autowired private EngineeringValidator engineeringValidator;
	
	@Autowired private EngineeringService engineeringService;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("engineeringWrapper")
	public EngineeringWrapper getEngineeringWrapper(@RequestParam(value="editId",required = false) Long id){
		if(id != null){
			return prepareEngineeringWrapper(providerService.getProvider(id));
		}
		return new EngineeringWrapper();
	}

	/*** TEST FIRMA ***/
	@RequestMapping("/engineering/test/firma")
	public String engineeringTestFirma(Model model, RedirectAttributes redirectAttrs){
		try {
			model.addAttribute("engineeringWrapper", prepareEngineeringWrapper(providerService.getProvider()));
			return "engineering/firmaTest";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("Errore redirect firma"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	/*** TEST FIRMA ***/
	@RequestMapping("/engineering/test/firma/invio")
	public String engineeringTestFirmaInvio(@ModelAttribute("engineeringWrapper") 
			EngineeringWrapper wrapper, HttpServletRequest request, BindingResult result,
			RedirectAttributes redirectAttrs, Model model) {

		try {
			model.addAttribute("engineeringWrapper", prepareEngineeringWrapper(providerService.getProvider()));
			
			String xmlMetadataToSign = "<archiviazione_metadati></archiviazione_metadati>";
			model.addAttribute("xmlMetadataToSign", StringEscapeUtils.escapeXml(xmlMetadataToSign));
			model.addAttribute("xmlDocumentToSign", prepareDocumentToSign(wrapper.getFileDaFirmare().getId()));
			model.addAttribute("firmaWebUrl", FIRMA_URL);
			model.addAttribute("referer", REFERER);
			model.addAttribute("informazioni", wrapper.getFileDaFirmare().getId()); // Sfrutto per recuperare l'ID del file

			return "engineering/firmaSubmitForm";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("Errore redirect firma"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	/*** TEST FIRMA ***/
	@RequestMapping(value = "/engineering/test/firma/back", method = RequestMethod.POST)
	public String engineeringTestFirmaBack(Model model, HttpServletRequest request, RedirectAttributes redirectAttrs) {
		try {
			
			String documentsToSign = request.getParameter("documentsToSign");
			LOGGER.debug("Ricevuta da firmaWeb:\n" + documentsToSign);
			engineeringService.saveFileFirmato(documentsToSign);

			model.addAttribute("engineeringWrapper", prepareEngineeringWrapper(providerService.getProvider()));
			return "engineering/firmaTest";

		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("Errore redirect mypay"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	private String prepareDocumentToSign(Long idFile) throws Exception {
		
		File file = fileService.getFile(idFile);
		
		String mimeType = URLConnection.guessContentTypeFromName(file.getNomeFile()); // TODO CHECK
		
		SOURCE source = new SOURCE();
		source.setMimeType(mimeType);

		source.setFileType("D");
		source.setStyleSheetName("mandati");

		FILE f = new FILE();

		f.setBytes(Base64Utils.encodeToString(file.getData())); 
		f.setName(file.getNomeFile());

		f.setINFORMAZIONI(file.getId().toString());
		f.setMETADATA("<Doc_da_firmare>mandato</Doc_da_firmare>"); // Fissa
		f.setSOURCE(source);

		DOCUMENT doc = new DOCUMENT();
		doc.setIdClassificazione(ID_CLASSIFICAZIONE); 
		doc.getFILE().add(f);

		DOCUMENTS docs = new DOCUMENTS();
		docs.getDOCUMENT().add(doc);
		docs.setRiepilogo("Riepilogo");
		docs.setTestata("Testata");

		FIRMAFILES busta = new FIRMAFILES();
		busta.setDOCUMENTS(docs);

		StringWriter writer = new StringWriter();

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "no");

		ByteArrayOutputStream w = new ByteArrayOutputStream();
		transformer.transform(new JAXBSource(JAXBContext.newInstance(FIRMAFILES.class), busta), new StreamResult(writer));

		String document = StringEscapeUtils.escapeXml(writer.toString());
		
		return document;
	}
	
	

	/*** TEST MYPAY ***/
	@RequestMapping("/engineering/test/mypay")
	public String engineeringTestMypay(Model model, RedirectAttributes redirectAttrs){
		try {
			model.addAttribute("eventoList", eventoService.getAllEventiForProviderId(providerService.getProvider().getId()));
			return "engineering/mypayTest";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("Errore redirect mypay"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	/*** MYPAY PAGA ***/
	@RequestMapping(value = "/engineering/test/mypay/paga", method = RequestMethod.POST)
	public String engineeringTestPagaConMypay(@ModelAttribute("engineeringWrapper") 
			EngineeringWrapper wrapper, HttpServletRequest request, BindingResult result,
			RedirectAttributes redirectAttrs, Model model) {
		try {
			
			String rootUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
			
			Long id = wrapper.getIdEvento();
			String url = engineeringService.pagaEvento(id,rootUrl+ request.getContextPath() + "/engineering/test/mypay");
			
			if (StringUtils.hasText(url)) {
				return "redirect:" + url;
			}
			
			
			model.addAttribute("eventoList", eventoService.getAllEventiForProviderId(providerService.getProvider().getId()));
			
			return "engineering/mypayTest";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("Errore redirect mypay"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	/*** MYPAY CHIEDI PAGATI ***/
	@RequestMapping(value = "/engineering/test/mypay/chiedipagati", method = RequestMethod.POST)
	public String engineeringTestChiediPagati(@RequestHeader(value = "referer", required = false) final String referer,
			@ModelAttribute("engineeringWrapper") EngineeringWrapper wrapper, BindingResult result,
			RedirectAttributes redirectAttrs, Model model) {
		
		try {
			// questo metodo andrebbe chiamato ogni TOT (da dimensionare in base al carico previsto) via scheduler.
			// in questo prototipo ho inserito un pulsante per invocarlo a piacere.
			engineeringService.esitoPagamentiEventi();
			model.addAttribute("eventoList", eventoService.getAllEventiForProviderId(providerService.getProvider().getId()));
			
			return "engineering/mypayTest";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("Errore redirect mypay"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
		
	}
	
	/*** MYPAY AZZERA PAGAMENTI ***/
	@RequestMapping(value = "/engineering/test/mypay/azzera", method = RequestMethod.POST)
	public String engineeringTestChiediPagati(@ModelAttribute("engineeringWrapper") EngineeringWrapper wrapper, BindingResult result,
			RedirectAttributes redirectAttrs, Model model) {
		// resetto gli eventi e le tabelle di log per poter ripetere i test
		// se ho effettuato un pagamento su MyPay questo ovviamente non viene cancellato. 
		// Posso comunque ripeterlo. MyPay lo registrerà come nuovo pagamento.
		try {
			engineeringService.azzeraPagamenti(providerService.getProvider().getId());
			model.addAttribute("eventoList", eventoService.getAllEventiForProviderId(providerService.getProvider().getId()));
			
			return "engineering/mypayTest";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("Errore redirect mypay"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
		
	}
	
	
	
	/*** SAVE ***/
	@RequestMapping(value = "/engineering/test/firma/save", method = RequestMethod.POST)
	public String saveEngineeringTestSave(@ModelAttribute("engineeringWrapper") EngineeringWrapper wrapper, BindingResult result,
											RedirectAttributes redirectAttrs, Model model){
		try {

			//TODO getFile da testare se funziona anche senza reload
			//reload degli allegati perchè se è stato fatto un upload ajax...il wrapper non ha i byte[] aggiornati e nemmeno il ref a providerId
			File file = wrapper.getFileDaFirmare();
			if(file != null && !file.isNew()){
				if(file.isFILEDAFIRMARE())
					wrapper.setFileDaFirmare(fileService.getFile(file.getId()));
			}

			engineeringValidator.validate(wrapper.getProvider(), result, "testFirma.", wrapper.getFileDaFirmare());

			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				return "engineering/firmaTest";
			}else{
				providerService.save(wrapper.getProvider());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "engineering.dati_inseriti", "success"));
				return "redirect:/home";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("Errore Salvataggio"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "engineering/firmaTest";
		}
	}

	private EngineeringWrapper prepareEngineeringWrapper(Provider provider) {
		EngineeringWrapper wrapper = new EngineeringWrapper();
		wrapper.setProvider(provider);

		Set<File> files = provider.getFiles();
		for(File file : files){
			if(file.isFILEDAFIRMARE())
				wrapper.setFileDaFirmare(file);
		}

		return wrapper;
	}

}