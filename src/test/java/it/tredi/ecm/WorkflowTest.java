package it.tredi.ecm;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import it.tredi.bonita.api.model.ActivityDataModel;
import it.tredi.bonita.api.model.ProcessInstanceDataModel;
import it.tredi.bonita.api.model.TaskInstanceDataModel;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.AccreditamentoRepository;
import it.tredi.ecm.dao.repository.ProfileRepository;
import it.tredi.ecm.dao.repository.ProviderRepository;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.CurrentUserDetailsService;
import it.tredi.ecm.service.PersonaService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.WorkflowService;
import it.tredi.ecm.service.WorkflowServiceImpl;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.service.bean.ProcessInstanceDataModelComplete;
import it.tredi.ecm.web.bean.PersonaWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
//@Ignore
@ActiveProfiles("dev")
@WithUserDetails("admin")
@Rollback(false)
public class WorkflowTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired 
	private AccountRepository accountRepository;
	@Autowired 
	private WorkflowService workflowService;
	@Autowired
	private CurrentUserDetailsService currentUserDetailsService;
	@Autowired
	private ProviderService providerService;
	@Autowired
	private AccreditamentoService accreditamentoService;
	@Autowired
	private ProfileRepository profileRepository;
	
	/*
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
			Accreditamento accreditamento = accreditamentoService.getNewAccreditamentoForProvider(provider.getId(),AccreditamentoTipoEnum.PROVVISORIO);
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
	*/

	@Test
	@Ignore
	public void saveOrUpdateBonitaUserByAccount() throws Exception {
		//lancio salvataggio con errori
		Account account = accountRepository.findOneByUsername("segreteria").orElse(null);
		if(account != null) {
			workflowService.saveOrUpdateBonitaUserByAccount(account);
		}
	}
	
	@Test
	@Ignore
	@Transactional //Aggiunto transactional per poter caricare il lazy accreditamento.getprovider()
	public void creaAccount() throws Exception {
		String userName = "segreteria";
		Profile profileSegreteria = profileRepository.findOne(15L);
		Account segreteria = new Account();
		segreteria.setUsername(userName);
		segreteria.setPassword("$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.");
		//admin.setPassword("admin");
		segreteria.setEmail("abarducci@3di.it");
		segreteria.setChangePassword(false);
		segreteria.setEnabled(true);
		segreteria.setExpiresDate(null);
		segreteria.setLocked(false);
		segreteria.getProfiles().add(profileSegreteria);
		segreteria.setDataScadenzaPassword(LocalDate.now());
		
		accountRepository.save(segreteria);
		
		CurrentUser currentUser = currentUserDetailsService.loadUserByUsername(userName);
	}
	
	@Test
	@Ignore
	@Transactional //Aggiunto transactional per poter caricare il lazy accreditamento.getprovider()
	public void GetWorkflowAccreditamentoProvvisorio() throws Exception {
		CurrentUser currentUser = currentUserDetailsService.loadUserByUsername("segreteria");
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(306L);
		if(currentUser != null) {
			ProcessInstanceDataModelComplete processInstanceDataModelComplete = workflowService.getProcessInstanceDataModelComplete(accreditamento.getWorkflowInfoAccreditamento().getProcessInstanceId(), currentUser.getWorkflowUserDataModel());
			printprocessInstanceDataModelComplete("", processInstanceDataModelComplete);
		}
	}

	@Test
	@Ignore
	@Transactional //Aggiunto transactional per poter caricare il lazy accreditamento.getprovider()
	public void GetStatiPossibiliPerInserimentoEsitoOdg() throws Exception {
		CurrentUser currentUser = currentUserDetailsService.loadUserByUsername("segreteria");
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(306L);
		
		List<AccreditamentoStatoEnum> stati = workflowService.getInserimentoEsitoOdgStatiPossibiliAccreditamento(accreditamento.getWorkflowInfoAccreditamento().getProcessInstanceId());
		System.out.println(stati);
	}

	@Test
	@Ignore
	@Transactional //Aggiunto transactional per poter caricare il lazy accreditamento.getprovider()
	public void getTaskPerUtenteEAccreditamentoConControlloDelloStato() throws Exception {
		//CurrentUser currentUser = currentUserDetailsService.loadUserByUsername("provider");
		CurrentUser currentUser = currentUserDetailsService.loadUserByUsername("segreteria");
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(306L);
		
		TaskInstanceDataModel task = workflowService.userGetTaskForState(currentUser, accreditamento);
		System.out.println("-- getTaskPerUtenteEAccreditamentoConControlloDelloStato --");
		if(task != null)
			System.out.println("task: " + task.getName() + " - isAssigned: " + task.isAssigned());
		printTaskInstanceDataModel("", task);
	}

	@Test
	@Ignore
	@Transactional //Aggiunto transactional per poter caricare il lazy accreditamento.getprovider()
	public void CreateWorkflowAccreditamentoProvvisorio() throws Exception {
		CurrentUser currentUser = currentUserDetailsService.loadUserByUsername("provider");
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(306L);
		if(currentUser != null) {
			workflowService.createWorkflowAccreditamentoProvvisorio(currentUser, accreditamento);
		}
	}

	@Test
	@Ignore
	@Transactional //Aggiunto transactional per poter caricare il lazy accreditamento.getprovider()
	public void eseguiTaskValutazioneAssegnazioneCrecmForUser() throws Exception {
		//CurrentUser currentUser = currentUserDetailsService.loadUserByUsername("provider");
		CurrentUser currentUser = currentUserDetailsService.loadUserByUsername("segreteria");
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(306L);
		
		List<String> usernameWorkflowValutatoriCrecm = new ArrayList<String>();
		usernameWorkflowValutatoriCrecm.add("referee1");
		usernameWorkflowValutatoriCrecm.add("referee2");
		usernameWorkflowValutatoriCrecm.add("referee3");
		workflowService.eseguiTaskValutazioneAssegnazioneCrecmForUser(currentUser, accreditamento, usernameWorkflowValutatoriCrecm, usernameWorkflowValutatoriCrecm.size() - 1);
		System.out.println("-- eseguiTaskValutazioneAssegnazioneCrecmForCurrentUser --");
		System.out.println("OK");
	}
	
	@Test
	//@Ignore
	@Transactional //Aggiunto transactional per poter caricare il lazy accreditamento.getprovider()
	public void eseguiTaskValutazioneCrecm() throws Exception {
		//CurrentUser currentUser = currentUserDetailsService.loadUserByUsername("provider");
		CurrentUser referee1 = currentUserDetailsService.loadUserByUsername("referee3");
		//CurrentUser referee2 = currentUserDetailsService.loadUserByUsername("referee2");
		//CurrentUser referee3 = currentUserDetailsService.loadUserByUsername("referee3");
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(306L);
		
		workflowService.eseguiTaskValutazioneCrecmForUser(referee1, accreditamento);
		System.out.println("-- eseguiTaskValutazioneCrecm --");
		System.out.println("OK");
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

	private void printprocessInstanceDataModelComplete(String start, ProcessInstanceDataModelComplete pic){
		if(pic != null){
			printProcessInstanceDataModel(start, pic.getProcessInstanceDataModel());
			System.out.println("getActivitieDataModels().size(): " + pic.getActivitieDataModels().size());
			int i = 1;
			for(ActivityDataModel adm : pic.getActivitieDataModels()) {
				printActivityDataModel(i++ + ") ", adm);				
			}
			System.out.println("taskInstanceDataModels().size(): " + pic.getTaskInstanceDataModels().size());
			i = 1;
			for(TaskInstanceDataModel tidm : pic.getTaskInstanceDataModels()) {
				printTaskInstanceDataModel(i++ + ") ", tidm);
			}
		}else{
			System.out.println("ANAGRAFICA is NULL");
		}
	}

	private void printActivityDataModel(String start, ActivityDataModel obj){
		if(obj != null){
			if(start==null)
				start = "";
			System.out.println(start + "Id: " + obj.getId() + " - " +
				"Name: " + obj.getName() + " - " +
				"DisplayName: " + obj.getDisplayName() + " - " +
				"state: " + obj.getState() + " - " +
				"LastUpdateDate: " + obj.getLastUpdateDate() + " - " +
				"ExecutedBy: " + obj.getExecutedBy() + " - " +
				"ExecutedByName: " + obj.getExecutedByName() + " - " +
				"ExecutedBySubstitute: " + obj.getExecutedBySubstitute() + " - " +
				"ExecutedBySubstituteName: " + obj.getExecutedBySubstituteName());
		}else{
			System.out.println("TaskInstanceDataModel is NULL");
		}
	}

	private void printTaskInstanceDataModel(String start, TaskInstanceDataModel obj){
		if(obj != null){
			if(start==null)
				start = "";
			System.out.println(start + "Id: " + obj.getId() + " - " +
				"Name: " + obj.getName() + " - " +
				"ProcessDefinitionId: " + obj.getProcessDefinitionId() + " - " +
				"Description: " + obj.getDescription() + " - " +
				"DisplayName: " + obj.getDisplayName() + " - " +
				"state: " + obj.getState() + " - " +
				"isAssigned: " + obj.isAssigned() + " - " +
				"assigneeId: " + obj.getAssigneeId() + " - " +
				"ExecutedBy: " + obj.getExecutedBy() + " - " +
				"ExecutedBySubstitute: " + obj.getExecutedBySubstitute());
		}else{
			System.out.println("TaskInstanceDataModel is NULL");
		}
	}
	
	private void printProcessInstanceDataModel(String start, ProcessInstanceDataModel pidm){
		if(pidm != null){
			if(start==null)
				start = "";
			System.out.println(start + "Id: " + pidm.getId() + " - " +
				"Name: " + pidm.getName() + " - " +
				"ProcessDefinitionId: " + pidm.getProcessDefinitionId() + " - " +
				"StartedBy: " + pidm.getStartedBy() + " - " +
				"StartedBySubstitute: " + pidm.getStartedBySubstitute() + " - " +
				"state: " + pidm.getState() + " - " +
				"Start: " + pidm.getStartDate() + " - " +
				"Last: " + pidm.getLastUpdate() + " - " +
				"End: " + pidm.getEndDate());
		}else{
			System.out.println("ProcessInstanceDataModel is NULL");
		}
	}
}
