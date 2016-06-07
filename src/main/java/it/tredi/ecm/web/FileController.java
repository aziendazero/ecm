package it.tredi.ecm.web;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.web.bean.Message;

@Controller
//TODO sistemare meglio la gestione dei metadati degli allegati
public class FileController {
	
	@Autowired
	private FileService fileService;
	
	@RequestMapping(value = "/files/{fileId}", method = RequestMethod.GET)
	public void getFile(@PathVariable("fileId") Long id, HttpServletResponse response, Model model) throws IOException {
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
			//TODO gestione eccezione
		}
	}
}
