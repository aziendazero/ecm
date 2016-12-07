package it.tredi.ecm;

import java.time.LocalDate;
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

import it.rve.protocollo.xsd.protocolla_arrivo.Mittente;
import it.rve.protocollo.xsd.protocolla_arrivo.Vettore;
import it.rve.protocollo.xsd.richiesta_protocollazione.Destinatari.Destinatario;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Protocollo;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.dao.repository.ProtocolloRepository;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.ProtocolloService;
import it.tredi.ecm.service.ValutazioneService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@WithUserDetails("LBENEDETTI")
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING) // ordina i test in base al nome crescente
@Rollback(false)
@Ignore
public class ProtocolloTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private FileService fileService;
	@Autowired private ProtocolloService protocolloService;
	@Autowired private ProtocolloRepository protocolloRepo;

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
	}

	@Test
	@Transactional
	@Ignore
	public void creaProtocollo() throws Exception{
		File file = fileService.getFile(30280L);
		Protocollo p = new Protocollo();
		p.setFile(file);
		p.setData(LocalDate.now());
		p.setNumero(new Integer(123456));
		protocolloRepo.save(p);
	}

	@Test
	@Transactional
	@Ignore
	public void testProtocollaDomandaInEntrata() throws Exception{
		protocolloService.protocollaDomandaInArrivo(11617L, 30106L);
	}
}
