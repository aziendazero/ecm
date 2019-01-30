package it.tredi.ecm;

import static org.junit.Assert.*;

import java.time.LocalDate;
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
import it.tredi.ecm.dao.entity.EventoPianoFormativo;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.EventoStatoEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.AccreditamentoRepository;
import it.tredi.ecm.dao.repository.EventoPianoFormativoRepository;
import it.tredi.ecm.dao.repository.EventoRepository;
import it.tredi.ecm.dao.repository.PianoFormativoRepository;
import it.tredi.ecm.dao.repository.ProviderRepository;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.DatiAccreditamentoService;
import it.tredi.ecm.service.EventoPianoFormativoService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.PagamentoService;
import it.tredi.ecm.service.PersonaService;
import it.tredi.ecm.service.PianoFormativoService;
import it.tredi.ecm.service.ProviderService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("demo")
@WithUserDetails("test1")
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING) // ordina i test in base al nome crescente
@Ignore
public class AttuazioneEventoTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired private EventoPianoFormativoRepository eventoPianoFormativoRepository;
	@Autowired private EventoRepository eventoRepository;

	@Autowired private EventoPianoFormativoService eventoPianoFormativoService;
	@Autowired private EventoService eventoService;
	@Autowired private PianoFormativoService pianoFormativoService;

	private MockMvc mockMvc;

	private Long epfId = 200536L; //evento piano formativo
	private Long evId = 200552L; //evento
	private Long evRiedizioneId = 200559L; // riedizione evento
	private Long providerId = 218L;
	private int annoCorrente = 2019;
	private int annoSuccessivo = annoCorrente + 1;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

		EventoPianoFormativo epf = eventoPianoFormativoService.getEvento(epfId);
		epf.setAttuato(false);
		epf.setPianoFormativo(epf.getPianoFormativoNativo().intValue());
		eventoPianoFormativoRepository.save(epf);

		Evento ev = eventoService.getEvento(evId);
		ev.setEventoPianoFormativo(null);
		ev.setStato(EventoStatoEnum.VALIDATO);
		ev.setDataInizio(LocalDate.parse(annoCorrente + "-08-01"));
		eventoRepository.save(ev);

		Evento evRiedizione = eventoService.getEvento(evRiedizioneId);
		evRiedizione.setEventoPianoFormativo(null);
		evRiedizione.setStato(EventoStatoEnum.VALIDATO);
		evRiedizione.setDataInizio(LocalDate.parse(annoCorrente + "-08-01"));
		evRiedizione.setDataFine(LocalDate.parse(annoSuccessivo + "-08-01"));
		eventoRepository.save(evRiedizione);

		PianoFormativo pfaSuccessivo = pianoFormativoService.getPianoFormativoAnnualeForProvider(providerId, annoSuccessivo);
		if(pfaSuccessivo != null) {
			pfaSuccessivo.removeEvento(epfId);
			pianoFormativoService.save(pfaSuccessivo);
		}

//		assertNotNull(epf);
//		assertNotNull(ev);
//		assertEquals(epf.getProceduraFormativa(), ev.getProceduraFormativa());
	}

	@After
	public void clean(){
//		accreditamentoRepository.delete(this.accreditamentoId);
//		providerRepository.delete(this.providerId);
	}

	@Test
	@Transactional
	public void testNuovaLogicaAttuazioneEventoPianoFormativo() throws Exception {
		EventoPianoFormativo epf = eventoPianoFormativoService.getEvento(epfId);
		Evento ev = eventoService.getEvento(evId);

		/* 1. ATTUAZIONE CON DATA FINE EVENTO NELLO STESSO ANNO */
		ev.setEventoPianoFormativo(epf);
		ev.setDataFine(LocalDate.parse(annoCorrente + "-08-01"));
		eventoService.save(ev);

		// epf deve essere nel piano formativo anno corrente
		PianoFormativo pfa = pianoFormativoService.getPianoFormativoAnnualeForProvider(providerId, annoCorrente);

		assertEquals(epf.getPianoFormativo().intValue(), annoCorrente);
		assertTrue(pfa.getEventiPianoFormativo().contains(epf));
		assertEquals(epf.getPianoFormativo().intValue(), ev.getPianoFormativo().intValue());

		/* 2. ATTUAZIONE CON DATA FINE EVENTO NEL ANNO SUCCESSIVO */
		ev.setEventoPianoFormativo(epf);
		ev.setDataFine(LocalDate.parse(annoSuccessivo + "-08-01"));
		eventoService.save(ev);

		// epf deve essere nel piano formativo anno successivo (ma presente nella lista di entrambi)
		pfa = pianoFormativoService.getPianoFormativoAnnualeForProvider(providerId, annoCorrente);

		assertEquals(epf.getPianoFormativo().intValue(), annoSuccessivo);
		assertTrue(pfa.getEventiPianoFormativo().contains(epf));
		assertEquals(epf.getPianoFormativo().intValue(), ev.getPianoFormativo().intValue());
		PianoFormativo pfaSuccessivo = pianoFormativoService.getPianoFormativoAnnualeForProvider(providerId, annoSuccessivo);
		assertTrue(pfaSuccessivo.getEventiPianoFormativo().contains(epf));


		/* 3. ATTUAZIONE CON DATA FINE EVENTO NELLO STESSO ANNO (RIMOZIONE DA PFA ANNO SUCCESSIVO) */
		ev.setEventoPianoFormativo(epf);
		ev.setDataFine(LocalDate.parse(annoCorrente + "-08-01"));
		eventoService.save(ev);

		// epf deve essere nel piano formativo anno corrente (rimosso dalla lista di pfa successivo)
		pfa = pianoFormativoService.getPianoFormativoAnnualeForProvider(providerId, annoCorrente);

		assertEquals(epf.getPianoFormativo().intValue(), annoCorrente);
		assertTrue(pfa.getEventiPianoFormativo().contains(epf));
		assertEquals(epf.getPianoFormativo().intValue(), ev.getPianoFormativo().intValue());
		pfaSuccessivo = pianoFormativoService.getPianoFormativoAnnualeForProvider(providerId, annoSuccessivo);
		assertFalse(pfaSuccessivo.getEventiPianoFormativo().contains(epf));

		/* 4. ELIMINAZIONE EVENTO PADRE - ASSEGNAZIONE COME EVENTO ATTUATO LA SUA PRIMA RIEDIZIONE */
		ev.setStato(EventoStatoEnum.CANCELLATO);
		eventoService.save(ev);

		// epf deve essere assegnato alla prima riedizione
		// epf deve essere nel piano formativo della data fine della riedizione (nel nostro caso pfa successivo)
		Evento evRiedizione = eventoService.getEvento(evRiedizioneId);
		pfa = pianoFormativoService.getPianoFormativoAnnualeForProvider(providerId, annoCorrente);
		pfaSuccessivo = pianoFormativoService.getPianoFormativoAnnualeForProvider(providerId, annoSuccessivo);

		assertEquals(evRiedizione.getEventoPianoFormativo(), epf);
		assertEquals(epf.getPianoFormativo().intValue(), annoSuccessivo);
		assertTrue(pfa.getEventiPianoFormativo().contains(epf));
		assertTrue(pfaSuccessivo.getEventiPianoFormativo().contains(epf));

		assertEquals(epf.getPianoFormativo().intValue(), evRiedizione.getPianoFormativo().intValue());
	}

}
