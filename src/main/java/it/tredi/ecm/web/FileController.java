package it.tredi.ecm.web;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.FileValidator;

@Controller
//TODO sistemare meglio la gestione dei metadati degli allegati
public class FileController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

	@Autowired private FileService fileService;
	@Autowired private ProviderService providerService;
	@Autowired private FileValidator fileValidator;

	//TODO security su file download/upload
	@PreAuthorize("@securityAccessServiceImpl.canShowFile(principal,#fileId)")
	@RequestMapping(value = "/file/{fileId}", method = RequestMethod.GET)
	public void getFile(@PathVariable("fileId") Long id, HttpServletResponse response, Model model) throws IOException {
		LOGGER.info(Utils.getLogMessage("GET /file/" + id));
		try {
			if(id == null){
				model.addAttribute("message",new Message("A","B","C"));
			}
			else{
				File file = fileService.getFile(id);

				if(file == null){
					throw new FileNotFoundException();
				}

				//response.setContentType(mimeType);

				/* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser
		            while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
				response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getNomeFile() +"\""));


				/* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
				//response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));

				response.setContentLength((int)file.getData().length);

				InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(file.getData()));

				//Copy bytes from source to destination(outputstream in this example), closes both streams.
				FileCopyUtils.copy(inputStream, response.getOutputStream());
			}
		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /file/" + id), ex);
		}
	}

	@RequestMapping(value = "/file/upload", method = RequestMethod.POST)
	@ResponseBody
	public Object uploadFile(@RequestParam(value = "multiPartFile", required = false) MultipartFile multiPartFile,
			@RequestParam(value = "fileId", required = false) Long fileId,
			@RequestParam(value = "tipo", required = true) String tipo){
		LOGGER.info(Utils.getLogMessage("GET /file/upload"));
		LOGGER.debug(Utils.getLogMessage("tipo: " + tipo));
		File file = new File(FileEnum.valueOf(tipo));
		try{
			if(multiPartFile != null && !multiPartFile.isEmpty()){
				file = Utils.convertFromMultiPart(multiPartFile);
				file.setNomeFile(file.getNomeFile().substring(file.getNomeFile().lastIndexOf("\\") + 1));
				file.setTipo(FileEnum.valueOf(tipo));
				if(fileId != null && !fileId.equals(0L))
					file.setId(fileId);

				String error = fileValidator.validate(file, multiPartFile.getContentType());
				if(error.isEmpty())
					fileService.save(file);
				else
					return error;
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /file/upload"), ex);
		}
		return file;
	}

	//TODO domenico (check se fa la query di tutto il provider)
	/*** LIST PERSONA ***/
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/allegato/list")
	public String listPersona(@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" +providerId + "/allegato/list"));
		try {
			Provider provider = providerService.getProvider(providerId);
			model.addAttribute("allegatoList", provider.getFiles());
			model.addAttribute("titolo", provider.getDenominazioneLegale());
			LOGGER.info(Utils.getLogMessage("VIEW: /allegato/list"));
			return "allegato/allegatoList";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" +providerId + "/allegato/list"), ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/show"));
			return "redirect:/provider/show";
		}
	}

}
