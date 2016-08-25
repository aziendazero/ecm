package it.tredi.ecm;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.FieldEditabileAccreditamento;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.AccreditamentoRepository;
import it.tredi.ecm.dao.repository.EventoRepository;
import it.tredi.ecm.dao.repository.FieldEditabileAccreditamentoRepository;
import it.tredi.ecm.dao.repository.PianoFormativoRepository;
import it.tredi.ecm.dao.repository.ProviderRepository;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.DatiAccreditamentoService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.PagamentoService;
import it.tredi.ecm.service.PersonaService;
import it.tredi.ecm.service.PianoFormativoService;
import it.tredi.ecm.service.ProviderService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@WithUserDetails("admin")
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING) // ordina i test in base al nome crescente
@Rollback(false)
@Ignore
public class GenericTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private FieldEditabileAccreditamentoRepository repo;
	
	private Long providerId;
	private Long accreditamentoId;
	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}
	
	//@Before
	@Transactional
	public void init() {
	}
	
	@After
	public void clean(){
//		accreditamentoRepository.delete(this.accreditamentoId);
//		providerRepository.delete(this.providerId);
	}
	
	@Test
	//@Ignore
	public void saveIdIntegrazione() throws Exception{
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(206L);
		
		Set<IdFieldEnum> listEnum = new HashSet<IdFieldEnum>();
		listEnum.add(IdFieldEnum.PROVIDER__DENOMINAZIONE_LEGALE);
		listEnum.add(IdFieldEnum.PROVIDER__PARTITA_IVA);
		listEnum.add(IdFieldEnum.RESPONSABILE_SEGRETERIA__COGNOME);
		listEnum.add(IdFieldEnum.RESPONSABILE_SEGRETERIA__NOME);
		
		for(IdFieldEnum idEcm : listEnum){
			
		}
		
//		IntegrazioneField f1 = new IntegrazioneField();
//		f1.setAccreditamento(accreditamento);
//		f1.setIdEditabile(IdEditabileEnum.RESPONSABILE_SEGRETERIA__COGNOME);
//		repo.save(f1);
//		
//		IntegrazioneField f2 = new IntegrazioneField();
//		f2.setAccreditamento(accreditamento);
//		f2.setIdEditabile(IdEditabileEnum.RESPONSABILE_SEGRETERIA__NOME);
//		repo.save(f2);
		
		FieldEditabileAccreditamento f3 = new FieldEditabileAccreditamento();
		f3.setAccreditamento(accreditamento);
		f3.setIdField(IdFieldEnum.PROVIDER__DENOMINAZIONE_LEGALE);
		repo.save(f3);
	}
	
	@Test
	@Ignore
	public void getIdIntegrazione() throws Exception{
		Set<FieldEditabileAccreditamento> idEditAll = repo.findAllByAccreditamentoId(accreditamentoId);
		Set<FieldEditabileAccreditamento> idEditComponente = repo.findAllByAccreditamentoId(accreditamentoId);
	}
	
	private void addIdEcm(){
		
	}
	
}
