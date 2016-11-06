package it.tredi.ecm;

import javax.persistence.OneToOne;
import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.junit.Before;
import org.junit.FixMethodOrder;
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
import org.springframework.web.context.WebApplicationContext;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.repository.EventoRepository;
import it.tredi.ecm.service.EventoService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("tom")
@WithUserDetails("provider")
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING) // ordina i test in base al nome crescente
@Rollback(false)
//@Ignore
public class RiedizioneEventoTest {

	@Autowired private WebApplicationContext webApplicationContext;
	@Autowired private EventoService eventoService;

	private Evento eventoPadre;
	private MockMvc mockMvc;

	//@BeforeClass
	@Before
	@Transactional
	public void init() {

		eventoPadre = eventoService.getEvento(446L);

	}

	//@AfterClass
	//@After
	public void clean(){
	}

	@Test
	//@Ignore
	@Transactional
	public void riedizione() throws Exception{
		System.out.println("*** Inizio test ***");

		System.out.println("Evento padre: "+ eventoPadre);

		Evento riedizione;
		switch(eventoPadre.getProceduraFormativa()){
			case FAD: riedizione = new EventoFAD();
				System.out.println("EventoFAD creato: success");
			break;
			case RES: riedizione = new EventoRES();
				System.out.println("EventoRES creato: success");
			break;
			case FSC: riedizione = new EventoFSC();
				System.out.println("EventoFSC creato: success");
			break;
			default: riedizione = new Evento(); break;
		}

		//INIZIO setting delle info dell'Evento generale ****************************************************************************************

		riedizione.setPrefix(eventoPadre.getPrefix());
		int ultimaEdizioneEvento = eventoService.getLastEdizioneEventoByPrefix(eventoPadre.getPrefix());
		if(ultimaEdizioneEvento != -1)
			System.out.println("Recupero ultima edizione: success - " + ultimaEdizioneEvento);
		else
			System.out.println("Recupero ultima edizione: fail");
		riedizione.setEdizione(++ultimaEdizioneEvento);
		System.out.println("Codice identificativo evento rieditato: " + riedizione.getCodiceIdentificativo());

		riedizione.setProceduraFormativa(eventoPadre.getProceduraFormativa());
		System.out.println("Procedura formativa: " + riedizione.getProceduraFormativa());

		riedizione.setTitolo(eventoPadre.getTitolo());
		System.out.println("Titolo: " + riedizione.getTitolo());

		riedizione.setObiettivoNazionale(eventoPadre.getObiettivoNazionale());
		System.out.println("Obt formativo nazionale: " + riedizione.getObiettivoNazionale().getNome());

		riedizione.setObiettivoRegionale(eventoPadre.getObiettivoRegionale());
		System.out.println("Obt formativo regionale: " + riedizione.getObiettivoRegionale().getNome());

		riedizione.setPianoFormativo(eventoPadre.getPianoFormativo());
		System.out.println("Piano formativo: " + riedizione.getPianoFormativo());

		riedizione.setProvider(eventoPadre.getProvider());
		System.out.println("Provider ID: " + riedizione.getProvider().getId());

		riedizione.setAccreditamento(eventoPadre.getAccreditamento());
		System.out.println("Accreditamento ID: " + riedizione.getAccreditamento().getId());

		riedizione.setProfessioniEvento(eventoPadre.getProfessioniEvento());
		System.out.println("ProfessioniEvento: " + riedizione.getProfessioniEvento());

		riedizione.setDiscipline(eventoPadre.getDiscipline());
		System.out.println("Discipline: " + riedizione.getDiscipline());

		riedizione.setEventoPadre(eventoPadre);
		System.out.println("Evento padre: " + riedizione.getEventoPadre());

		riedizione.setDestinatariEvento(eventoPadre.getDestinatariEvento());
		System.out.println("DestinatariEvento: " + riedizione.getDestinatariEvento());

		riedizione.setContenutiEvento(eventoPadre.getContenutiEvento());
		System.out.println("ContenutiEvento: " + riedizione.getContenutiEvento());

		riedizione.setDataInizio(eventoPadre.getDataInizio());
		System.out.println("DataInizio: " + riedizione.getDataInizio());

		riedizione.setDataFine(eventoPadre.getDataFine());
		System.out.println("DataFine: " + riedizione.getDataFine());

		riedizione.setResponsabili(eventoService.copyPersonaListEvento(eventoPadre.getResponsabili()));
		System.out.println("Responsabili evento padre: " + eventoPadre.getResponsabili());
		System.out.println("Responsabili evento rieditato: "+ riedizione.getResponsabili());

		riedizione.setNumeroPartecipanti(eventoPadre.getNumeroPartecipanti());
		System.out.println("Numero partecipanti: " + riedizione.getNumeroPartecipanti());

		riedizione.setBrochureEvento(eventoPadre.getBrochureEvento());
		System.out.println("Brochure: " + riedizione.getBrochureEvento());

		riedizione.setResponsabileSegreteria(eventoService.copyPersonaFullEvento(eventoPadre.getResponsabileSegreteria()));
		System.out.println("ResponsabileSegreteria evento padre: " + eventoPadre.getResponsabileSegreteria());
		System.out.println("ResponsabileSegreteria evento rieditato: "+ riedizione.getResponsabileSegreteria());

		riedizione.setQuotaPartecipazione(eventoPadre.getQuotaPartecipazione());
		System.out.println("Quota partecipazione: " + riedizione.getQuotaPartecipazione());

		riedizione.setEventoSponsorizzato(eventoPadre.getEventoSponsorizzato());
		System.out.println("EventoAutoriazzato: " + riedizione.getEventoSponsorizzato());

		riedizione.setSponsors(eventoService.copySponsorListEvento(eventoPadre.getSponsors()));
		System.out.println("Sponsors evento padre: " + eventoPadre.getSponsors());
		System.out.println("Sponsors evento rieditato: " + riedizione.getSponsors());

		riedizione.setEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia(eventoPadre.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia());
		System.out.println("EventoSponsorizzatoAlimenti: " + riedizione.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia());

		riedizione.setAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia(eventoPadre.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia());
		System.out.println("AutocertificazioneAssenzaAlimenti: " + riedizione.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia());

		riedizione.setAutocertificazioneAutorizzazioneMinisteroSalute(eventoPadre.getAutocertificazioneAutorizzazioneMinisteroSalute());
		System.out.println("AutocertificazioneAutorizzazioneMinistero: " + riedizione.getAutocertificazioneAutorizzazioneMinisteroSalute());

		riedizione.setAltreFormeFinanziamento(eventoPadre.getAltreFormeFinanziamento());
		System.out.println("AltreFormeFinanziamento: " + riedizione.getAltreFormeFinanziamento());

		riedizione.setAutocertificazioneAssenzaFinanziamenti(eventoPadre.getAutocertificazioneAssenzaFinanziamenti());
		System.out.println("AutocertificazioneAssenzaFinanziamenti: " + riedizione.getAutocertificazioneAssenzaFinanziamenti());

		riedizione.setContrattiAccordiConvenzioni(eventoPadre.getContrattiAccordiConvenzioni());
		System.out.println("ContrattiAccordiConvenzioni: " + riedizione.getContrattiAccordiConvenzioni());

		riedizione.setEventoAvvalePartner(eventoPadre.getEventoAvvalePartner());
		System.out.println("AvvalePartner: " + riedizione.getEventoAvvalePartner());

		riedizione.setPartners(eventoService.copyPartnerListEvento(eventoPadre.getPartners()));
		System.out.println("Partners evento padre: " + eventoPadre.getPartners());
		System.out.println("Partners evento rieditato: " + riedizione.getPartners());

		riedizione.setDichiarazioneAssenzaConflittoInteresse(eventoPadre.getDichiarazioneAssenzaConflittoInteresse());
		System.out.println("DichiarazioneAssenzaConflittoInteresse: " + riedizione.getDichiarazioneAssenzaConflittoInteresse());

		//FINE setting delle info dell'Evento generale ******************************************************************************************

		//INIZIO setting delle info dell'Evento particolari alla proceduraFormativa +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		if(riedizione instanceof EventoFAD) {

			((EventoFAD) riedizione).setTipologiaEvento(((EventoFAD) eventoPadre).getTipologiaEvento());
			System.out.println("Tipologia EventoFAD: " + ((EventoFAD) riedizione).getTipologiaEvento());

			((EventoFAD) riedizione).setDocenti(eventoService.copyPersonaListEvento(((EventoFAD) eventoPadre).getDocenti()));
			System.out.println("Docenti EventoFAD padre: " + ((EventoFAD) eventoPadre).getDocenti());
			System.out.println("Docenti EventoFAD rieditato: " + ((EventoFAD) riedizione).getDocenti());

			((EventoFAD) riedizione).setRazionale(((EventoFAD) eventoPadre).getRazionale());
			System.out.println("Razionale EventoFAD: " + ((EventoFAD) riedizione).getRazionale());

			((EventoFAD) riedizione).setRisultatiAttesi(((EventoFAD) eventoPadre).getRisultatiAttesi());
			System.out.println("RisultatiAttesi EventoFAD: " + ((EventoFAD) riedizione).getRisultatiAttesi());

			((EventoFAD) riedizione).setProgrammaFAD(eventoService.copyProgrammaEventoFAD(((EventoFAD) eventoPadre).getProgrammaFAD(), ((EventoFAD) riedizione).getDocenti()));
			System.out.println("Programma EventoFAD padre: " + ((EventoFAD) eventoPadre).getProgrammaFAD());
			System.out.println("Programma EventoFAD rieditato: " + ((EventoFAD) riedizione).getProgrammaFAD());

			((EventoFAD) riedizione).setVerificaApprendimento(((EventoFAD) eventoPadre).getVerificaApprendimento());
			System.out.println("VerificaApprendimento EventoFAD: " + ((EventoFAD) riedizione).getVerificaApprendimento());

			((EventoFAD) riedizione).setConfermatiCrediti(((EventoFAD) eventoPadre).getConfermatiCrediti());
			System.out.println("ConfermatiCrediti EventoFAD: " + ((EventoFAD) riedizione).getConfermatiCrediti());

			((EventoFAD) riedizione).setSupportoSvoltoDaEsperto(((EventoFAD) eventoPadre).getSupportoSvoltoDaEsperto());
			System.out.println("SupportoEsperto EventoFAD: " + ((EventoFAD) riedizione).getSupportoSvoltoDaEsperto());

			((EventoFAD) riedizione).setMaterialeDurevoleRilasciatoAiPratecipanti(((EventoFAD) eventoPadre).getMaterialeDurevoleRilasciatoAiPratecipanti());;
			System.out.println("MaterialeRilasciato EventoFAD: " + ((EventoFAD) riedizione).getMaterialeDurevoleRilasciatoAiPratecipanti());

			((EventoFAD) riedizione).setRequisitiHardwareSoftware(((EventoFAD) eventoPadre).getRequisitiHardwareSoftware());;
			System.out.println("RequisitiSWHW EventoFAD: " + ((EventoFAD) riedizione).getRequisitiHardwareSoftware());

			((EventoFAD) riedizione).setUserId(((EventoFAD) eventoPadre).getUserId());
			System.out.println("UserId EventoFAD: " + ((EventoFAD) riedizione).getUserId());

			((EventoFAD) riedizione).setPassword(((EventoFAD) eventoPadre).getPassword());
			System.out.println("Password EventoFAD: " + ((EventoFAD) riedizione).getPassword());

			((EventoFAD) riedizione).setUrl(((EventoFAD) eventoPadre).getUrl());
			System.out.println("Url EventoFAD: " + ((EventoFAD) riedizione).getUrl());

		}

		//FINE setting delle info dell'Evento particolari alla proceduraFormativa +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		System.out.println("*** Fine test ***");
	}
}