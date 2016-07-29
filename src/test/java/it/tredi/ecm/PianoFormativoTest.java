package it.tredi.ecm;

import static org.junit.Assert.*;

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
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.AccreditamentoRepository;
import it.tredi.ecm.dao.repository.EventoRepository;
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
@Rollback(true)
public class PianoFormativoTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired private PersonaService personaService;
	@Autowired private ProviderService providerService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private AccreditamentoRepository accreditamentoRepository;
	@Autowired private ProviderRepository providerRepository;
	@Autowired private AccountRepository accountRepository;
	@Autowired private DatiAccreditamentoService datiAccreditamentoService;
	@Autowired private PianoFormativoRepository pianoFormativoRepository;
	@Autowired private PianoFormativoService pianoFormativoService;
	@Autowired private EventoRepository eventoRepository;
	@Autowired private EventoService eventoService;
	@Autowired private PagamentoService pagamentoService;
	
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
		Account account = new Account();
		account.setUsername("junit");
		account.setPassword("junit");
		account.setEmail("junit@3di.it");
		accountRepository.save(account);
		
		account = accountRepository.findOne(account.getId());
		
		Provider provider = new Provider();
		provider.setDenominazioneLegale("VR 46");
		provider.setPartitaIva("00464646460");
		provider.setTipoOrganizzatore(TipoOrganizzatore.PRIVATI);
		provider.setStatus(ProviderStatoEnum.INSERITO);
		provider.setAccount(account);
		providerService.save(provider);
		
		Accreditamento accreditamento = null;
		
		try {
			accreditamento = accreditamentoService.getNewAccreditamentoForProvider(provider.getId(),AccreditamentoTipoEnum.PROVVISORIO);
			this.providerId = provider.getId();
			this.accreditamentoId = accreditamento.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		DatiAccreditamento datiAccreditamento = new DatiAccreditamento();
		Set<ProceduraFormativa> procedure = new HashSet<ProceduraFormativa>();
		procedure.add(ProceduraFormativa.FAD);
		procedure.add(ProceduraFormativa.RES);
		procedure.add(ProceduraFormativa.FSC);
		//datiAccreditamento.setProcedureFormative(procedure);
		datiAccreditamentoService.save(datiAccreditamento, accreditamento.getId());
	}
	
	@After
	public void clean(){
//		accreditamentoRepository.delete(this.accreditamentoId);
//		providerRepository.delete(this.providerId);
	}

	@Test
	@Transactional
	@Ignore
	public void createPiano(){
		
		Provider provider = providerService.getProvider(providerId);
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
		
		PianoFormativo pianoFormativo = new PianoFormativo();
		pianoFormativo.setAnnoPianoFormativo(new Integer(2016));
		pianoFormativo.setProvider(provider);
		pianoFormativoRepository.save(pianoFormativo);
		
		//Eventi
		Evento evento = new Evento();
		evento.setCosto(500.99);
		evento.setTitolo("Pippo1");
		evento.setProceduraFormativa(ProceduraFormativa.FAD);
		evento.setPagato(false);
		evento.setProvider(provider);
		evento.setAccreditamento(accreditamento);
		eventoRepository.save(evento);

		Evento evento2 = new Evento();
		evento2.setCosto(200.00);
		evento2.setTitolo("Pippo2");
		evento2.setProceduraFormativa(ProceduraFormativa.RES);
		evento2.setPagato(false);
		evento2.setProvider(provider);
		evento2.setAccreditamento(accreditamento);
		eventoRepository.save(evento2);

		Evento evento3 = new Evento();
		evento3.setCosto(1200.28);
		evento3.setTitolo("Pippo3");
		evento3.setProceduraFormativa(ProceduraFormativa.FSC);
		evento3.setPagato(true);
		evento3.setProvider(provider);
		evento3.setAccreditamento(accreditamento);
		eventoRepository.save(evento3);
		
		pianoFormativo.addEvento(evento);
		pianoFormativo.addEvento(evento2);
		pianoFormativo.addEvento(evento3);
		pianoFormativoRepository.save(pianoFormativo);
		
		PianoFormativo pianoFormativo2 = new PianoFormativo();
		pianoFormativo2.setAnnoPianoFormativo(new Integer(2017));
		pianoFormativo2.setProvider(provider);
		pianoFormativoRepository.save(pianoFormativo2);
		
		pianoFormativo2.addEvento(evento2);
		pianoFormativoRepository.save(pianoFormativo2);
	}

	@Test
	@Transactional
	@Ignore
	public void showPiano() throws Exception{
		PianoFormativo piano1 = pianoFormativoService.getPianoFormativoAnnualeForProvider(this.providerId, new Integer(2016));
		PianoFormativo piano2 = pianoFormativoService.getPianoFormativoAnnualeForProvider(this.providerId, new Integer(2017));
		
		assertEquals(3, piano1.getEventi().size());
		assertEquals(1, piano2.getEventi().size());
		
		Set<Evento> eventi = eventoRepository.findAllByProviderIdAndPianoFormativo(this.providerId, new Integer(2017));
		
		assertEquals(1, eventi.size());
	}

	@Test
	@Ignore
	public void getEventi(){
//		Provider provider = providerService.getProvider(20L);
//		
//		for(it.tredi.ecm.dao.entity.File f :  provider.getFiles())
//			System.out.println(f.getNomeFile());
//		//eventoService.getAllEventiFromProvider(providerId);
		
		Provider provider = datiAccreditamentoService.getDatiAccreditamento(207L).getAccreditamento().getProvider();
		for(it.tredi.ecm.dao.entity.File f :  provider.getFiles())
			System.out.println(f.getNomeFile());
	}
	
	@Test
	public void testPagamento(){
		pagamentoService.getAllProviderNotPagamentoEffettuato(new Integer(2017));
	}
	
}
