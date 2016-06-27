package it.tredi.ecm;

import java.io.FileInputStream;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.aspectj.AspectJAdviceParameterNameDiscoverer.AmbiguousBindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.PersonaService;
import it.tredi.ecm.web.bean.PersonaWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@WithUserDetails("admin")
public class PersonaTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired PersonaService personaService;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}

	@Test
	public void editPersona() throws Exception {
		ResultActions actions = this.mockMvc.perform(get("/accreditamento/148/provider/147/persona/151/edit"));
		ModelAndView modelAndView =  actions.andExpect(status().isOk())
											.andReturn().getModelAndView();
		
		PersonaWrapper wrapper = (PersonaWrapper) modelAndView.getModel().get("personaWrapper");
		printWrapper(wrapper);

		wrapper.getPersona().getAnagrafica().setCognome("");
		wrapper.getPersona().getAnagrafica().setNome("PLUTO");
		salvaPersona(wrapper);

		uploadFile();
		
		wrapper.getPersona().getAnagrafica().setCognome("Romano");
		wrapper.getPersona().getAnagrafica().setNome("");
		salvaPersona(wrapper);
		
		wrapper.getPersona().getAnagrafica().setCognome("Romano");
		wrapper.getPersona().getAnagrafica().setNome("Tommasino");
		salvaPersona(wrapper);
	}

	public void salvaPersona(PersonaWrapper wrapper) throws Exception{
		String idEditabili = wrapper.getIdEditabili().toString();
		idEditabili = idEditabili.substring(1, idEditabili.length()-1);
		ModelAndView modelAndView = this.mockMvc.perform(post("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/save",wrapper.getAccreditamentoId(),wrapper.getProviderId())
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.param("editId", wrapper.getPersona().getId().toString())
				.param("editId_Anagrafica", wrapper.getPersona().getAnagrafica().getId().toString())
				.param("persona.anagrafica.cognome", wrapper.getPersona().getAnagrafica().getCognome())
				.param("persona.anagrafica.nome", wrapper.getPersona().getAnagrafica().getNome())
				.param("idEditabili", idEditabili)
				.param("idOffset", String.valueOf(wrapper.getIdOffset()))
				)
				.andReturn().getModelAndView();

		Persona persona = personaService.getPersona(wrapper.getPersona().getId());
		assertEquals(wrapper.getPersona().getId(), persona.getId());

		if(modelAndView.getViewName().equals("persona/personaEdit")){
			System.out.println("SALVATAGGIO NON RIUSCITO");
			System.out.println("----DATABASE----");
			printPersona(persona);
			printWrapper(wrapper);
		}
		else{ 
			System.out.println("SALVATAGGIO RIUSCITO CORRETTAMENTE");
			System.out.println("----DATABASE----");
			printPersona(persona);
			printWrapper(wrapper);
		}
	}

	private void printWrapper(PersonaWrapper wrapper){
		System.out.println("----WRAPPER---");
		printPersona(wrapper.getPersona());
	}

	private void printPersona(Persona persona){
		if(persona != null){
			System.out.println("PERSONA ID: " + persona.getId());
			printAnagrafica(persona.getAnagrafica());
		}else{
			System.out.println("PERSONA is NULL");
		}

	}

	private void printAnagrafica(Anagrafica anagrafica){
		if(anagrafica != null){
			System.out.println("ANAGRAFICA ID: " + anagrafica.getId());
			System.out.println("COGNOME: " + anagrafica.getCognome());
			System.out.println("NOME: " + anagrafica.getNome());
		}else{
			System.out.println("ANAGRAFICA is NULL");
		}
	}

	public void uploadFile() throws Exception {
		FileInputStream inputFile = new FileInputStream("C:\\Users\\dpranteda\\Pictures\\Balocco.jpg");  
		MockMultipartFile multiPartFile = new MockMultipartFile("multiPartFile", "wlf.jpg", "multipart/form-data", inputFile); 

		ResultActions actions = this.mockMvc.perform(fileUpload("/file/upload")
				.file(multiPartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.param("fileId", "156")
				.param("tipo", FileEnum.FILE_DELEGA.name())
				);

		String response = actions.andReturn().getResponse().getContentAsString();

		System.out.println(response);
	}

}
