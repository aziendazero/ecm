package it.tredi.ecm;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.TabSettings;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfWriter;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.FieldEditabileAccreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.entity.ValutazioneCommissione;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.ValutazioneTipoEnum;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.AccreditamentoRepository;
import it.tredi.ecm.dao.repository.ProfileRepository;
import it.tredi.ecm.pdf.FooterWithInfo;
import it.tredi.ecm.pdf.PdfAccreditamentoProvvisorioAccreditatoInfo;
import it.tredi.ecm.pdf.PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo;
import it.tredi.ecm.pdf.PdfAccreditamentoProvvisorioRigettoInfo;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.CurrentUserDetailsService;
import it.tredi.ecm.service.FieldEditabileAccreditamentoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.PdfService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.SedutaService;
import it.tredi.ecm.service.ValutazioneService;
import it.tredi.ecm.service.bean.CurrentUser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("demo")
@WithUserDetails("test1")
@Rollback(false)
@Ignore
public class PdfTest {

	@Autowired
	private PdfService pdfService;
	@Autowired
	private FileService fileService;

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private CurrentUserDetailsService currentUserDetailsService;
	@Autowired
	private ProviderService providerService;
	@Autowired
	private AccreditamentoService accreditamentoService;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired private MessageSource messageSource;

	@Autowired
	private AccreditamentoRepository accreditamentoRepository;

	@Autowired
	private FieldEditabileAccreditamentoService fieldEditabileService;

	@Autowired
	private ValutazioneService valutazioneService;

	@Autowired SedutaService sedutaService;

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
	public void testMessageSource() throws Exception {
		System.out.println(messageSource.getMessage("IdFieldEnum." + IdFieldEnum.PROVIDER__TIPO_ORGANIZZATORE.name() , null, Locale.getDefault()));
	}

	@Test
	@Transactional
	@Ignore
	public void testNewDiniegoStandard() throws Exception{
		Long accreditamentoId = 160068L;
		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		AccreditamentoStatoEnum stato = AccreditamentoStatoEnum.DINIEGO;
		Boolean eseguitoDaUtente = false;
		//Ricavo la seduta
		Seduta sedutaRigetto = null;
		Seduta sedutaIntegrazione = null;
		Seduta sedutaPreavvisoRigetto = null;
		for (ValutazioneCommissione valCom : accreditamento.getValutazioniCommissione()) {
			if(valCom.getStato() == AccreditamentoStatoEnum.DINIEGO) {
				sedutaRigetto = valCom.getSeduta();
			} else if (valCom.getStato() == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE) {
				sedutaIntegrazione = valCom.getSeduta();
			} else if (valCom.getStato() == AccreditamentoStatoEnum.RICHIESTA_PREAVVISO_RIGETTO) {
				sedutaPreavvisoRigetto = valCom.getSeduta();
			}
		}
		PdfAccreditamentoProvvisorioRigettoInfo rigettoInfo = new PdfAccreditamentoProvvisorioRigettoInfo(accreditamento, sedutaRigetto, sedutaIntegrazione, sedutaPreavvisoRigetto);
		File file = null;
		if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.PROVVISORIO)
			file = pdfService.creaPdfAccreditamentoProvvisiorioDiniego(rigettoInfo);
		else if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.STANDARD)
			file = pdfService.creaPdfAccreditamentoStandardDiniego(rigettoInfo);
		System.out.println(file.getId());
	}

	@Test
	@Transactional
	public void creazionePdfPerAccreditamento() throws Exception {
		Long accreditamentoId = 218206L;
		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		AccreditamentoStatoEnum stato = AccreditamentoStatoEnum.ACCREDITATO;
		Boolean eseguitoDaUtente = false;

		//In alcuni stati devono essere effettuate altre operazioni
		//Creazione pdf
		if(stato == AccreditamentoStatoEnum.INTEGRAZIONE) {
			//Ricavo la seduta
			Seduta seduta = null;
			for (ValutazioneCommissione valCom : accreditamento.getValutazioniCommissione()) {
				//TODO nel caso vengano aggancxiati piu' flussi alla domanda occorre prendere l'ultima ValutazioneCommissionew
				if(valCom.getStato() == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE) {
					seduta= valCom.getSeduta();
				}
			}
			Set<FieldEditabileAccreditamento> fieldEditabiliAccreditamento = fieldEditabileService.getAllFieldEditabileForAccreditamento(accreditamento.getId());
			List<String> listaCriticita = new ArrayList<String>();
			fieldEditabiliAccreditamento.forEach(v -> {
				if(!v.getIdField().hasGruppo()) {
		            //Richiesta
		            //Riepilogo_Consegne_ECM_20.10.2016.docx - Modulo 7 - 40 - a [inserire singole note sui campi] (pag 4)
					if(v.getNota() == null || v.getNota().isEmpty())
						listaCriticita.add(messageSource.getMessage("IdFieldEnum." + v.getIdField().name(), null, Locale.getDefault()));
					else
						listaCriticita.add(messageSource.getMessage("IdFieldEnum." + v.getIdField().name(), null, Locale.getDefault()) + "\n" + v.getNota());
				}
			});
			PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo integrazioneInfo = new PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo(accreditamento, seduta, listaCriticita);
			integrazioneInfo.setGiorniIntegrazionePreavvisoRigetto(accreditamento.getGiorniIntegrazione());
			File file = pdfService.creaPdfAccreditamentoProvvisiorioIntegrazione(integrazioneInfo);
			accreditamento.setRichiestaIntegrazione(file);
		} else if(stato == AccreditamentoStatoEnum.PREAVVISO_RIGETTO) {
			//Ricavo la seduta
			Seduta seduta = null;
			for (ValutazioneCommissione valCom : accreditamento.getValutazioniCommissione()) {
				if(valCom.getStato() == AccreditamentoStatoEnum.RICHIESTA_PREAVVISO_RIGETTO) {
					seduta= valCom.getSeduta();
				}
			}
			Set<FieldEditabileAccreditamento> fieldEditabiliAccreditamento = fieldEditabileService.getAllFieldEditabileForAccreditamento(accreditamento.getId());
			List<String> listaCriticita = new ArrayList<String>();
			fieldEditabiliAccreditamento.forEach(v -> {
				listaCriticita.add(messageSource.getMessage("IdFieldEnum." + v.getIdField().name(), null, Locale.getDefault()) + " - " + v.getNota());
			});
			PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo preavvisoRigettoInfo = new PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo(accreditamento, seduta, listaCriticita);
			preavvisoRigettoInfo.setGiorniIntegrazionePreavvisoRigetto(accreditamento.getGiorniPreavvisoRigetto());
			File file = pdfService.creaPdfAccreditamentoProvvisiorioPreavvisoRigetto(preavvisoRigettoInfo);
			accreditamento.setRichiestaPreavvisoRigetto(file);
		} else if(stato == AccreditamentoStatoEnum.DINIEGO) {
			//Ricavo la seduta
			Seduta sedutaRigetto = null;
			Seduta sedutaIntegrazione = null;
			Seduta sedutaPreavvisoRigetto = null;
			for (ValutazioneCommissione valCom : accreditamento.getValutazioniCommissione()) {
				if(valCom.getStato() == AccreditamentoStatoEnum.DINIEGO) {
					sedutaRigetto = valCom.getSeduta();
				} else if (valCom.getStato() == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE) {
					sedutaIntegrazione = valCom.getSeduta();
				} else if (valCom.getStato() == AccreditamentoStatoEnum.RICHIESTA_PREAVVISO_RIGETTO) {
					sedutaPreavvisoRigetto = valCom.getSeduta();

				}
			}
			/*
			Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneAccreditamento = fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazioneForAccreditamento(accreditamento.getId());
			List<String> listaCriticita = new ArrayList<String>();
			fieldIntegrazioneAccreditamento.forEach(v -> {
				listaCriticita.add(messageSource.getMessage("IdFieldEnum." + v.getIdField().name(), null, Locale.getDefault()));
			});*/
			PdfAccreditamentoProvvisorioRigettoInfo rigettoInfo = new PdfAccreditamentoProvvisorioRigettoInfo(accreditamento, sedutaRigetto, sedutaIntegrazione, sedutaPreavvisoRigetto);
			File file = pdfService.creaPdfAccreditamentoStandardDiniego(rigettoInfo);
			saveFile(file);
			//accreditamento.setDecretoDiniego(file);
		} else if(stato == AccreditamentoStatoEnum.ACCREDITATO) {
			//Ricavo la seduta
			Seduta sedutaAccreditamento = null;
			Seduta sedutaIntegrazione = null;
			Seduta sedutaPreavvisoRigetto = null;
			for (ValutazioneCommissione valCom : accreditamento.getValutazioniCommissione()) {
				if(valCom.getStato() == AccreditamentoStatoEnum.ACCREDITATO) {
					sedutaAccreditamento = valCom.getSeduta();
				} else if (valCom.getStato() == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE) {
					sedutaIntegrazione = valCom.getSeduta();
				} else if (valCom.getStato() == AccreditamentoStatoEnum.RICHIESTA_PREAVVISO_RIGETTO) {
					sedutaPreavvisoRigetto = valCom.getSeduta();

				}
			}
			//Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneAccreditamento = fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazioneForAccreditamento(accreditamento.getId());
			PdfAccreditamentoProvvisorioAccreditatoInfo accreditatoInfo = new PdfAccreditamentoProvvisorioAccreditatoInfo(accreditamento, sedutaAccreditamento, sedutaIntegrazione, sedutaPreavvisoRigetto);
			File file = pdfService.creaPdfAccreditamentoStandardAccreditato(accreditatoInfo);
			saveFile(file);
			//accreditamento.setDecretoAccreditamento(file);
		} else if(stato == AccreditamentoStatoEnum.INS_ODG) {
			//Cancelliamo le Valutazioni non completate
			Set<Valutazione> valutazioni = valutazioneService.getAllValutazioniForAccreditamentoIdAndNotStoricizzato(accreditamentoId);
			for(Valutazione v : valutazioni){
				if(v.getTipoValutazione() == ValutazioneTipoEnum.REFEREE && v.getDataValutazione() == null){
					valutazioneService.delete(v);
				}
			}
		} else if(stato == AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA) {
			if(accreditamento.getStato() == AccreditamentoStatoEnum.INTEGRAZIONE)
				accreditamento.setIntegrazioneEseguitaDaProvider(eseguitoDaUtente);
			else if(accreditamento.getStato() == AccreditamentoStatoEnum.PREAVVISO_RIGETTO)
				accreditamento.setPreavvisoRigettoEseguitoDaProvider(eseguitoDaUtente);
		}
		accreditamentoRepository.save(accreditamento);
	}

////	@Test
//	@Ignore
//	@Transactional //Aggiunto transactional per poter caricare il lazy accreditamento.getprovider()
//	public void creaPdf() throws Exception {
//		//String userName = "segreteria";
//		//CurrentUser currentUser = currentUserDetailsService.loadUserByUsername(userName);
//
//        List<String> listaCriticita = new ArrayList<String>();
//        listaCriticita.add("DENOMINAZIONE CAMPO 1 – DESCRIZIONE CRITICITA’");
//        listaCriticita.add("DENOMINAZIONE CAMPO 2 – DESCRIZIONE CRITICITA’");
//        listaCriticita.add("DENOMINAZIONE CAMPO 3 – DESCRIZIONE CRITICITA’");
//        listaCriticita.add("DENOMINAZIONE CAMPO 4 – DESCRIZIONE CRITICITA’");
//        PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo integrazioneInfo = new PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo("providerDenominazione", "providerIndirizzo", "providerCap", "providerComune", "providerProvincia", "providerNomeLegaleRappresentante", "providerCognomeLegaleRappresentante", "providerPec", LocalDate.now(), LocalDate.now(), listaCriticita, "Note seduta domanda con molto testo molto testo molto testo molto testo molto testo molto testo molto testo molto testo");
//        pdfService.creaPdfAccreditamentoProvvisiorioIntegrazione(integrazioneInfo);
//
//        List<String> listaCriticitaPreavvisoRigetto = new ArrayList<String>();
//        listaCriticita.add("DENOMINAZIONE CAMPO 1 – DESCRIZIONE CRITICITA’");
//        listaCriticita.add("DENOMINAZIONE CAMPO 2 – DESCRIZIONE CRITICITA’");
//        listaCriticita.add("DENOMINAZIONE CAMPO 3 – DESCRIZIONE CRITICITA’");
//        listaCriticita.add("DENOMINAZIONE CAMPO 4 – DESCRIZIONE CRITICITA’");
//        listaCriticita.add("DENOMINAZIONE CAMPO 5 – DESCRIZIONE CRITICITA’");
//        listaCriticita.add("DENOMINAZIONE CAMPO 6 – DESCRIZIONE CRITICITA’  DESCRIZIONE CRITICITA’  DESCRIZIONE CRITICITA’  DESCRIZIONE CRITICITA’  DESCRIZIONE CRITICITA’  DESCRIZIONE CRITICITA’");
//        PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo preavvisoRigettoInfo = new PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo("providerDenominazione", "providerIndirizzo", "providerCap", "providerComune", "providerProvincia", "providerNomeLegaleRappresentante", "providerCognomeLegaleRappresentante", "providerPec", LocalDate.now(), LocalDate.now(), listaCriticitaPreavvisoRigetto, "Note seduta domanda con molto testo molto testo molto testo molto testo molto testo molto testo molto testo molto testo");
//        pdfService.creaPdfAccreditamentoProvvisiorioPreavvisoRigetto(preavvisoRigettoInfo);
//
//        /*
//        List<String> listaMotivazioni = new ArrayList<String>();
//        listaMotivazioni.add("vedi motivazioni 10 bis 1");
//        listaMotivazioni.add("vedi Motivazioni 10 bis 2");
//        listaMotivazioni.add("vedi motivazioni 10 bis 3");
//        listaMotivazioni.add("vedi motivazioni 10 BIS 4");
//        listaMotivazioni.add("vedi motivazioni 10 bis 5");
//        */
//        PdfAccreditamentoProvvisorioRigettoInfo diniegoInfo = new PdfAccreditamentoProvvisorioRigettoInfo("providerDenominazione", "providerIndirizzo", "providerCap", "providerComune", "providerProvincia", "providerNomeLegaleRappresentante", "providerCognomeLegaleRappresentante", "providerPec", LocalDate.now(),              "numeroProtocolloIntegrazione", LocalDate.now(),           "verbaleNumeroIntegrazione", LocalDate.now(),                 true,                           "numeroProtocolloRigetto", LocalDate.now(),     "verbaleNumeroRigetto", LocalDate.now(),                 true,                   "Note seduta domanda");
//        pdfService.creaPdfAccreditamentoProvvisiorioDiniego(diniegoInfo);
//
//        List<String> listaMotivazioniAccreditamento = new ArrayList<String>();
//        listaMotivazioniAccreditamento.add("vedi motivazioni 10 bis 1");
//        listaMotivazioniAccreditamento.add("vedi Motivazioni 10 bis 2");
//        listaMotivazioniAccreditamento.add("vedi motivazioni 10 bis 3");
//        PdfAccreditamentoProvvisorioAccreditatoInfo accreditatoInfo = new PdfAccreditamentoProvvisorioAccreditatoInfo("providerDenominazione", "providerIndirizzo", "providerCap", "providerComune", "providerProvincia", "providerNomeLegaleRappresentante", "providerCognomeLegaleRappresentante", "providerPec", "providerId", LocalDate.now(),              "numeroProtocolloIntegrazione", LocalDate.now(),           "verbaleNumeroIntegrazione", LocalDate.now(),                 true,                           "numeroProtocolloRigetto", LocalDate.now(),     "verbaleNumeroRigetto", LocalDate.now(),                 true, LocalDate.now());
//        pdfService.creaPdfAccreditamentoProvvisiorioAccreditato(accreditatoInfo);
//        //pdfService.writePdfAccreditamentoProvvisiorioAccreditato(outputStreamAccreditata, accreditatoInfo);
//
//        System.out.println("FATTO");
//
//	}

	@Test
	@Ignore
	@Transactional //Aggiunto transactional per poter caricare il lazy accreditamento.getprovider()
	public void creaFile() throws Exception {
		it.tredi.ecm.dao.entity.File file = new it.tredi.ecm.dao.entity.File();

		byte[] byteArrayFile = null;
		file.setData(byteArrayFile);
		file.setDataCreazione(LocalDate.now());
		file.setNomeFile(FileEnum.FILE_ACCREDITAMENTO_PROVVISORIO_DECRETO_ACCREDITAMENTO.getNome() + ".pdf");
		file.setTipo(FileEnum.FILE_ACCREDITAMENTO_PROVVISORIO_DECRETO_ACCREDITAMENTO);
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

	@Test
	@Ignore
	@Transactional
	public void createIntegrazioneStandard() throws Exception{
		Long accreditamentoId = 218206L;
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);

		Long sedutaId = 401076L;
		Seduta seduta = sedutaService.getSedutaById(sedutaId);

		List<String> listaCriticita = new ArrayList<String>();
		listaCriticita.add("DENOMINAZIONE CAMPO 1 – DESCRIZIONE CRITICITA’");
		listaCriticita.add("DENOMINAZIONE CAMPO 2 – DESCRIZIONE CRITICITA’");
		listaCriticita.add("DENOMINAZIONE CAMPO 3 – DESCRIZIONE CRITICITA’");
		listaCriticita.add("DENOMINAZIONE CAMPO 4 – DESCRIZIONE CRITICITA’");
		listaCriticita.add("DENOMINAZIONE CAMPO 5 – DESCRIZIONE CRITICITA’");
		listaCriticita.add("DENOMINAZIONE CAMPO 6 – DESCRIZIONE CRITICITA’");
		listaCriticita.add("DENOMINAZIONE CAMPO 7 – DESCRIZIONE CRITICITA’");
		listaCriticita.add("DENOMINAZIONE CAMPO 8 – DESCRIZIONE CRITICITA’");
		listaCriticita.add("DENOMINAZIONE CAMPO 9 – DESCRIZIONE CRITICITA’");
		listaCriticita.add("DENOMINAZIONE CAMPO 10 – DESCRIZIONE CRITICITA’");
		listaCriticita.add("DENOMINAZIONE CAMPO 11 – DESCRIZIONE CRITICITA’");

		PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo integrazioneInfo = new PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo(accreditamento, seduta, listaCriticita);
		integrazioneInfo.setGiorniIntegrazionePreavvisoRigetto(accreditamento.getGiorniIntegrazione());
		File fileIntegrazione = null;
		File filePreavvisoRigetto = null;
		if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.PROVVISORIO){
			fileIntegrazione= pdfService.creaPdfAccreditamentoProvvisiorioIntegrazione(integrazioneInfo);
			filePreavvisoRigetto = pdfService.creaPdfAccreditamentoProvvisiorioPreavvisoRigetto(integrazioneInfo);
		}
		else if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.STANDARD){
			fileIntegrazione = pdfService.creaPdfAccreditamentoStandardIntegrazione(integrazioneInfo);
			filePreavvisoRigetto = pdfService.creaPdfAccreditamentoStandardIntegrazione(integrazioneInfo);
		}

		System.out.println("File Integrazione salvato: " + fileIntegrazione.getId());
		System.out.println("File PreavvisoRigetto salvato: " + filePreavvisoRigetto.getId());
	}

	@Test
	@Ignore
	public void testProtocolloInfo() throws Exception{
		boolean headerAndFooter = true;
		boolean infoProtocollo = true;
		String nomeLogo = "LogoRegioneVeneto.png";

		java.io.File file = java.io.File.createTempFile("test_protocollo", ".pdf");
		System.out.println(file.getAbsolutePath());
		Document document = new Document();
		PdfWriter writer = null;
		if(headerAndFooter){
			document = new Document(PageSize.A4, 36, 36, 80, 120);
			writer = PdfWriter.getInstance(document, new FileOutputStream(file.getPath()));
			writer.setPageEvent(new FooterWithInfo(nomeLogo));
		}else{
			writer = PdfWriter.getInstance(document, new FileOutputStream(file.getPath()));
		}

		document.open();

		if(infoProtocollo){
			createInfoProtocollo(writer.getDirectContent());
		}

		writer.close();
	}

	private void createInfoProtocollo(PdfContentByte cb){
		Font protocolloFont = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL);
        int top = 687;
        float left = 20;

        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Data:", protocolloFont), left, top, 0);
        left += 105;
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Protocollo N°:", protocolloFont), left, top, 0);
        left += 105;
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Class:", protocolloFont), left, top, 0);
        left += 105;
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Fasc:", protocolloFont), left, top, 0);
        left += 100;
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Allegati N°:", protocolloFont), left, top, 0);
	}

	private void saveFile(File file) throws IOException {
		OutputStream out = new FileOutputStream("/Users/3dinformatica-albania-minimac/Desktop/pdfs/"+file.getNomeFile());
		out.write(file.getData());
		out.close();
	}
	

}
