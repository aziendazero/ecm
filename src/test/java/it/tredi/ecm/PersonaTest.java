package it.tredi.ecm;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.AccreditamentoRepository;
import it.tredi.ecm.dao.repository.ProviderRepository;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.PersonaService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.web.bean.PersonaWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@WithUserDetails("admin")
public class PersonaTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired private PersonaService personaService;
	@Autowired private ProviderService providerService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private AccreditamentoRepository accreditamentoRepository;
	@Autowired private ProviderRepository providerRepository;
	@Autowired private AccountRepository accountRepository;
	
	private Long personaId;
	private Long providerId;
	private Long accreditamentoId;
	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}
	
	@Before
	public void init() {
		Persona persona = new Persona();
		persona.getAnagrafica().setCognome("Rossi");
		persona.getAnagrafica().setNome("Valentino");
		persona.getAnagrafica().setCodiceFiscale("VLNRSS79B16V466R");
		persona.getAnagrafica().setCellulare("3331234567");
		persona.getAnagrafica().setTelefono("0517654321");
		persona.getAnagrafica().setEmail("vrossi@3di.com");
		persona.getAnagrafica().setPec("vrossi@pec.com");
		persona.setRuolo(Ruolo.RESPONSABILE_SEGRETERIA);
		personaService.save(persona);
		
		Account account = new Account();
		account.setUsername("junit");
		account.setPassword("junit");
		account.setEmail("junit@3di.it");
		accountRepository.save(account);
		
		Provider provider = new Provider();
		provider.setDenominazioneLegale("VR 46");
		provider.setPartitaIva("00464646460");
		provider.setTipoOrganizzatore(TipoOrganizzatore.PRIVATI);
		provider.setStatus(ProviderStatoEnum.INSERITO);
		provider.addPersona(persona);
		provider.setAccount(account);
		providerService.save(provider);
		
		personaService.save(persona);
		
		try {
			Accreditamento accreditamento = accreditamentoService.getNewAccreditamentoForProvider(provider.getId());
			this.personaId = persona.getId();
			this.providerId = persona.getProvider().getId();
			this.accreditamentoId = accreditamento.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void clean(){
		accreditamentoRepository.delete(this.accreditamentoId);
		personaService.delete(this.personaId);
		providerRepository.delete(this.providerId);
	}

	@Test
	public void editPersona() throws Exception {
		ResultActions actions = this.mockMvc.perform(get("/accreditamento/{accreditamentoId}/provider/{providerId}/persona/{personaId}/edit",accreditamentoId,providerId,personaId));
		ModelAndView modelAndView =  actions.andExpect(status().isOk()).andReturn().getModelAndView();
		
		PersonaWrapper wrapper = (PersonaWrapper) modelAndView.getModel().get("personaWrapper");
		assertEquals(personaId, wrapper.getPersona().getId());
		assertEquals("Rossi", wrapper.getPersona().getAnagrafica().getCognome());
		assertEquals("Valentino", wrapper.getPersona().getAnagrafica().getNome());
		assertEquals("VLNRSS79B16V466R", wrapper.getPersona().getAnagrafica().getCodiceFiscale());
		assertEquals(0, wrapper.getFiles().size());
		
		//lancio salvataggio con errori
		wrapper.getPersona().getAnagrafica().setCognome("");
		wrapper.getPersona().getAnagrafica().setNome("PLUTO");
		salvaPersona(wrapper);
		
		assertEquals(personaId, wrapper.getPersona().getId());
		assertEquals("", wrapper.getPersona().getAnagrafica().getCognome());
		assertEquals("PLUTO", wrapper.getPersona().getAnagrafica().getNome());
		assertEquals("VLNRSS79B16V466R", wrapper.getPersona().getAnagrafica().getCodiceFiscale());
		assertEquals(0, wrapper.getFiles().size());

		//Upload file + lancio salvataggio con errori
		uploadFile(FileEnum.FILE_DELEGA,0L);
		wrapper.getPersona().getAnagrafica().setCognome("Romano");
		wrapper.getPersona().getAnagrafica().setNome("");
		salvaPersona(wrapper);
		
		assertEquals(personaId, wrapper.getPersona().getId());
		assertEquals("Romano", wrapper.getPersona().getAnagrafica().getCognome());
		assertEquals("", wrapper.getPersona().getAnagrafica().getNome());
		assertEquals("VLNRSS79B16V466R", wrapper.getPersona().getAnagrafica().getCodiceFiscale());
		assertEquals(0, wrapper.getFiles().size());
		assertNotNull(wrapper.getDelega().getId());
				
		wrapper.getPersona().getAnagrafica().setCognome("Jorge");
		wrapper.getPersona().getAnagrafica().setNome("Lorenzo");
		uploadFile(FileEnum.FILE_CV,0L);
		salvaPersona(wrapper);
		
		assertEquals(personaId, wrapper.getPersona().getId());
		assertEquals("Jorge", wrapper.getPersona().getAnagrafica().getCognome());
		assertEquals("Lorenzo", wrapper.getPersona().getAnagrafica().getNome());
		assertEquals("VLNRSS79B16V466R", wrapper.getPersona().getAnagrafica().getCodiceFiscale());
		assertEquals(0, wrapper.getFiles().size());
		assertNotNull(wrapper.getDelega().getId());
		assertNotNull(wrapper.getCv().getId());
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

	public void uploadFile(FileEnum tipo, Long fileId) throws Exception {
		FileInputStream inputFile = new FileInputStream("C:\\Users\\dpranteda\\Pictures\\Balocco.jpg");  
		MockMultipartFile multiPartFile = new MockMultipartFile("multiPartFile", "wlf.jpg", "multipart/form-data", inputFile); 

		ResultActions actions = this.mockMvc.perform(fileUpload("/file/upload")
				.file(multiPartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.param("fileId", String.valueOf(fileId))
				.param("tipo", tipo.name())
				);

		String response = actions.andReturn().getResponse().getContentAsString();

		System.out.println(response);
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
}
