package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import it.tredi.ecm.dao.entity.AzioneRuoliEventoFSC;
import it.tredi.ecm.dao.entity.DettaglioAttivitaFAD;
import it.tredi.ecm.dao.entity.DettaglioAttivitaRES;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.FaseAzioniRuoliEventoFSCTypeA;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Partner;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.entity.PersonaFullEvento;
import it.tredi.ecm.dao.entity.ProgrammaGiornalieroRES;
import it.tredi.ecm.dao.entity.RiepilogoRES;
import it.tredi.ecm.dao.entity.RiepilogoRuoliFSC;
import it.tredi.ecm.dao.entity.RuoloOreFSC;
import it.tredi.ecm.dao.entity.Sponsor;
import it.tredi.ecm.dao.entity.VerificaApprendimentoFAD;
import it.tredi.ecm.dao.enumlist.ContenutiEventoEnum;
import it.tredi.ecm.dao.enumlist.FaseDiLavoroFSCEnum;
import it.tredi.ecm.dao.enumlist.INomeEnum;
import it.tredi.ecm.dao.enumlist.MetodoDiLavoroEnum;
import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaFADEnum;
import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaRESEnum;
import it.tredi.ecm.dao.enumlist.ProgettiDiMiglioramentoFasiDaInserireFSCEnum;
import it.tredi.ecm.dao.enumlist.TipoMetodologiaEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.web.bean.EventoWrapper;

@Service
public class PdfEventoServiceImpl implements PdfEventoService {
	private static Logger LOGGER = LoggerFactory.getLogger(PdfEventoServiceImpl.class);

	/*
	@Override
	public File creaPdfEvento(EventoWrapper wrapper) throws Exception {
        ByteArrayOutputStream byteArrayOutputStreamPdf = new ByteArrayOutputStream();
        writePdfEvento(byteArrayOutputStreamPdf, wrapper);

		File file = new File();
		file.setData(byteArrayOutputStreamPdf.toByteArray());
		file.setDataCreazione(LocalDate.now());
		file.setNomeFile(FileEnum.FILE_ACCREDITAMENTO_PROVVISORIO_INTEGRAZIONE.getNome() + ".pdf");
		file.setTipo(FileEnum.FILE_ACCREDITAMENTO_PROVVISORIO_INTEGRAZIONE);
		//fileService.save(file);
		return file;
	}

	@Override
	public void creaPdfEvento(EventoWrapper wrapper, OutputStream outputStream) throws Exception {
        writePdfEvento(outputStream, wrapper);
	}
	*/

	@Override
	public ByteArrayOutputStream creaOutputStreamPdfEvento(EventoWrapper wrapper) throws Exception {
        ByteArrayOutputStream byteArrayOutputStreamPdf = new ByteArrayOutputStream();
        writePdfEvento(byteArrayOutputStreamPdf, wrapper);
		return byteArrayOutputStreamPdf;
	}

	@Autowired
	private MessageSource messageSource;

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private NumberStyleFormatter intFormatter = new NumberStyleFormatter("0");
	private NumberStyleFormatter longFormatter = new NumberStyleFormatter("0");
	private NumberStyleFormatter floatFormatter = new NumberStyleFormatter("0.0#");
	private NumberStyleFormatter valutaFormatter = new NumberStyleFormatter("0.00");

    //tipi font
	private int sizeTitolo = 14;
	private int sizeNomeCampo = 11;
	private int sizeValoreCampo = 11;
	private int sizeTitoloSubTable = 9;
	private int sizeEtichettaSubTable = 8;
	private int sizeNomeCampoSubTable = 9;
	private int sizeValoreCampoSubTable = 9;
	private Font.FontFamily fontFamily = Font.FontFamily.TIMES_ROMAN;
	private Font fontTitolo = new Font(fontFamily, sizeTitolo, Font.BOLD);
	private Font fontNomeCampo = new Font(fontFamily, sizeNomeCampo, Font.BOLD);
	private Font fontValoreCampo = new Font(fontFamily, sizeValoreCampo, Font.NORMAL);
	//Formati delle sotto tabelle
	private Font fontTitoloSubTable = new Font(fontFamily, sizeTitoloSubTable, Font.BOLD);
	private Font fontEtichettaSubTable = new Font(fontFamily, sizeEtichettaSubTable, Font.BOLD);
	private Font fontNomeCampoSubTable = new Font(fontFamily, sizeNomeCampoSubTable, Font.BOLD);
	private Font fontValoreCampoSubTable = new Font(fontFamily, sizeValoreCampoSubTable, Font.NORMAL);
	private Font fontValoreCampoSubTableBold = new Font(fontFamily, sizeValoreCampoSubTable, Font.BOLD);
	private float cellPadding = 5F;
	private float cellPaddingSubTable = 2F;
	private float spacingBefore = 10F;
	private float spacingAfter = 10F;


	private void writePdfEvento(OutputStream outputStream, EventoWrapper wrapper) throws Exception {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            //Info documento
            document.addAuthor("Ecm");
            document.addCreationDate();
            document.addCreator("Ecm");
            document.addTitle("Evento " + wrapper.getEvento().getCodiceIdentificativo());
        	if(wrapper.getEvento() instanceof EventoFAD) {
        		writePdfEventoFAD(document, wrapper);
			} else if(wrapper.getEvento() instanceof EventoFSC) {
        		writePdfEventoFSC(document, wrapper);
			} else if(wrapper.getEvento() instanceof EventoRES) {
        		writePdfEventoRES(document, wrapper);
			}
        } catch (Exception e) {
        	LOGGER.error("writePdfEvento impossibile creare il pdf", e);
            throw e;
        } finally {
            if(document.isOpen())
            	document.close();
            try {
            	outputStream.close();
            } catch (IOException ex) {

            }
		}
	}

	private void writePdfEventoFAD(Document document, EventoWrapper wrapper) throws Exception {
		Evento evento = wrapper.getEvento();
    	EventoFAD eventoFAD = (EventoFAD)evento;
		//<h2 th:text="#{label.visualizzazione_evento(${eventoWrapper.proceduraFormativa}, ${eventoWrapper.evento.getCodiceIdentificativo()})}"></h2>
        Object[] values = {wrapper.getProceduraFormativa(), evento.getCodiceIdentificativo()};
        Paragraph parTitolo = new Paragraph();
        parTitolo.setAlignment(Element.ALIGN_LEFT);
        parTitolo.setFont(fontTitolo);
        parTitolo.add(messageSource.getMessage("label.visualizzazione_evento", values, Locale.getDefault()));
        document.add(parTitolo);

		PdfPTable tableFields = getTableFields();
		addCellLabelCampoValore("label.denominazione_legale_provider", evento.getProvider().getDenominazioneLegale(), tableFields);
		addCellLabelCampoValore("label.id_provider", evento.getProvider().getId(), tableFields);
		addCellLabelCampoValoreEnum("label.procedure_formative_tipologia", evento.getProceduraFormativa(), tableFields);
		addCellLabelCampoValore("label.destinatari_evento", evento.getDestinatariEvento(), tableFields);
		addCellLabelCampoValore("label.evento_contenuti", evento.getContenutiEvento(), tableFields);
		addCellLabelCampoValore("label.titolo", evento.getTitolo(), tableFields);
		addCellLabelCampoValore("label.data_inizio", evento.getDataInizio(), tableFields);
		addCellLabelCampoValore("label.data_fine", evento.getDataFine(), tableFields);
		addCellLabelCampoValore("label.obiettivo_strategico_nazionale", evento.getObiettivoNazionale() == null ? null : evento.getObiettivoNazionale().getNome(), tableFields);
		addCellLabelCampoValore("label.obiettivo_strategico_regionale", evento.getObiettivoRegionale() == null ? null : evento.getObiettivoRegionale().getNome(), tableFields);
		addCellLabelCampoValoreDiscipline("label.professioni_discipline", evento.getDiscipline(), tableFields);
		addCellLabelCampoValore("label.numero_partecipanti", evento.getNumeroPartecipanti(), tableFields);
		addCellLabelCampoValore("label.tipologia_evento", eventoFAD.getTipologiaEventoFAD(), tableFields);
		addCellLabelCampoValorePersone("label.responsabili_scientifici", evento.getResponsabili(), tableFields, true, false, false);
		//<!-- DOCENTI/RELATORI/TUTOR -->
		addCellLabelCampoValorePersone("label.docente_tutor", eventoFAD.getDocenti(), tableFields, false, true, true);
		//<!-- BLOCCO PROGRAMMA ATTIVITÀ FORMATIVA -->
		addCellLabelSeparator("label.programma_attività_formativa", tableFields);
		addCellLabelCampoValore("label.razionale", eventoFAD.getRazionale(), tableFields);
		addCellLabelCampoValoriString("label.risultati_attesi", eventoFAD.getRisultatiAttesi(), tableFields);
		tableFields = addTableFieldsToDocumentAndGetNewTableField(tableFields, document);
		PdfPTable tableTemp = getTableProgrammaFAD(eventoFAD.getProgrammaFAD());
		if(tableTemp != null)
			document.add(tableTemp);
		addCellLabelCampoValore("label.brochure_evento", evento.getBrochureEvento(), tableFields);
		addCellLabelCampoValore("label.verifica_apprendimento_partecipanti", eventoFAD.getVerificaApprendimento(), tableFields);
		addCellLabelCampoValore("label.durata", evento.getDurata(), tableFields);
		addCellLabelCampoValore("label.tipologia_obiettivi_formativi", eventoFAD.getRiepilogoFAD().getObiettivi(), tableFields);
		addCellLabelCampoValore("label.metodologie_didattiche", eventoFAD.getRiepilogoFAD().getMetodologie(), tableFields);
		addCellLabelCampoValore("label.supporto_disciplinare_fad", eventoFAD.getSupportoSvoltoDaEsperto(), tableFields);
		addCellLabelCampoValoreZeroComeNonInserito("label.crediti_formativi_attribuiti_evento", evento.getCrediti(), tableFields);
		if(evento.getCrediti() != null && evento.getCrediti().floatValue() != 0)
			addCellLabelCampoValore("label.provider_confermato_crediti", evento.getConfermatiCrediti(), tableFields);
		//<!-- RESPONSABILE SEGRETERIA ORGANIZZATIVA -->
		addCellLabelCampoValore("label.responsabile_segreteria_organizzativa", evento.getResponsabileSegreteria(), tableFields);
		addCellLabelCampoValore("label.tipo_materiale_rilasciato", eventoFAD.getMaterialeDurevoleRilasciatoAiPratecipanti(), tableFields);
		addCellLabelCampoValoreValuta("label.quota_partecipazione", evento.getQuotaPartecipazione(), tableFields);
		addCellLabelCampoValore("label.dotazione_hardware_software", eventoFAD.getRequisitiHardwareSoftware(), tableFields);
		//<!-- BLOCCO ACCESSO ALLA PIATTAFORMA -->
		addCellLabelSeparator("label.info_accesso_piattaforma", tableFields);
		addCellLabelCampoValore("label.userId", eventoFAD.getUserId(), tableFields);
		addCellLabelCampoValore("label.password", eventoFAD.getPassword(), tableFields);
		addCellLabelCampoValore("label.url", eventoFAD.getUrl(), tableFields);

		addCellLabelCampoValore("label.evento_sponsorizzato_radio", evento.getEventoSponsorizzato(), tableFields);
		if(evento.getEventoSponsorizzato() != null && evento.getEventoSponsorizzato())
			addCellLabelCampoValoreSponsors("label.sponsors", evento.getSponsors(), tableFields);
		if(evento.getContenutiEvento() == ContenutiEventoEnum.ALIMENTAZIONE_PRIMA_INFANZIA) {
			//<!-- RADIO SPONSOR PRIMA INFANZIA -->
			addCellLabelCampoValore("label.evento_sponsorizzato_infanzia_radio", evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia(), tableFields);
			if(evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() != null) {
				if(evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia()) //<!-- ALLEGATO AUTOCERTIFICAZIONE AUTORIZZAZIONE MINISTERO SALUTE -->
					addCellLabelCampoValore("label.allegato_autocertificazione_autorizzazione_ministero", evento.getAutocertificazioneAutorizzazioneMinisteroSalute(), tableFields);
				else //<!-- ALLEGATO AUTOCERTIFICAZIONE ASSENZA PARTECIPAZIONE SPONSOR INFANZIA -->
					addCellLabelCampoValore("label.allegato_autocertificazione_assenza_finanziamenti", evento.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia(), tableFields);
			}
		}
		//<!-- FORME FINANZIAMENTO BLOCCO -->
		addCellLabelCampoValore("label.forme_finanziamento_radio", evento.getAltreFormeFinanziamento(), tableFields);
		if(evento.getAltreFormeFinanziamento() != null) {
			if(evento.getAltreFormeFinanziamento()) //<!-- ALLEGATO CONTRATTI/ACCORDI/CONVENZIONI -->
				addCellLabelCampoValore("label.allegato_contratti_accordi_convenzioni", evento.getContrattiAccordiConvenzioni(), tableFields);
			else //<!-- ALLEGATO AUTOCERTIFICAZIONE ASSENZA FINANZIAMENTI -->
				addCellLabelCampoValore("label.allegato_autocertificazione_assenza_finanziamenti", evento.getAutocertificazioneAssenzaFinanziamenti(), tableFields);
		}
		//<!-- BLOCCO PARTNER -->
		addCellLabelCampoValore("label.si_avvale_di_partner", evento.getEventoAvvalePartner(), tableFields);
		if(evento.getEventoAvvalePartner() != null && evento.getEventoAvvalePartner()) {
			addCellLabelCampoValorePartners("label.partners", evento.getPartners(), tableFields);
		}
		addCellLabelCampoValore("label.dichiarazione_assenza_conflitto_interesse", evento.getDichiarazioneAssenzaConflittoInteresse(), tableFields);
		addCellLabelCampoValore("label.procedura_verifica_qualita", evento.getProceduraVerificaQualitaPercepita(), tableFields);
		addCellLabelCampoValore("label.autorizzazione_privacy", evento.getAutorizzazionePrivacy(), tableFields);
		document.add(tableFields);
	}

	private void writePdfEventoFSC(Document document, EventoWrapper wrapper) throws Exception {
		Evento evento = wrapper.getEvento();
    	EventoFSC eventoFSC = (EventoFSC)evento;
		//<h2 th:text="#{label.visualizzazione_evento(${eventoWrapper.proceduraFormativa}, ${eventoWrapper.evento.getCodiceIdentificativo()})}"></h2>
        Object[] values = {wrapper.getProceduraFormativa(), evento.getCodiceIdentificativo()};
        Paragraph parTitolo = new Paragraph();
        parTitolo.setAlignment(Element.ALIGN_LEFT);
        parTitolo.setFont(fontTitolo);
        parTitolo.add(messageSource.getMessage("label.visualizzazione_evento", values, Locale.getDefault()));
        document.add(parTitolo);
		PdfPTable tableFields = getTableFields();
		addCellLabelCampoValore("label.denominazione_legale_provider", evento.getProvider().getDenominazioneLegale(), tableFields);
		addCellLabelCampoValore("label.id_provider", evento.getProvider().getId(), tableFields);
		addCellLabelCampoValoreEnum("label.procedure_formative_tipologia", evento.getProceduraFormativa(), tableFields);
		addCellLabelCampoValore("label.destinatari_evento", evento.getDestinatariEvento(), tableFields);
		addCellLabelCampoValore("label.evento_contenuti", evento.getContenutiEvento(), tableFields);
		addCellLabelCampoValore("label.titolo", evento.getTitolo(), tableFields);
		addCellLabelCampoValore("label.provincia", eventoFSC.getSedeEvento().getProvincia(), tableFields);
		addCellLabelCampoValore("label.comune", eventoFSC.getSedeEvento().getComune(), tableFields);
		addCellLabelCampoValore("label.indirizzo", eventoFSC.getSedeEvento().getIndirizzo(), tableFields);
		addCellLabelCampoValore("label.luogo", eventoFSC.getSedeEvento().getLuogo(), tableFields);
		addCellLabelCampoValore("label.data_inizio", evento.getDataInizio(), tableFields);
		addCellLabelCampoValore("label.data_fine", evento.getDataFine(), tableFields);
		addCellLabelCampoValore("label.obiettivo_strategico_nazionale", evento.getObiettivoNazionale() == null ? null : evento.getObiettivoNazionale().getNome(), tableFields);
		addCellLabelCampoValore("label.obiettivo_strategico_regionale", evento.getObiettivoRegionale() == null ? null : evento.getObiettivoRegionale().getNome(), tableFields);
		addCellLabelCampoValore("label.tipologia_evento", eventoFSC.getTipologiaEventoFSC(), tableFields);
		addCellLabelCampoValoreDiscipline("label.professioni_discipline", evento.getDiscipline(), tableFields);
		addCellLabelCampoValorePersone("label.responsabili_scientifici", evento.getResponsabili(), tableFields, true, false, false);
		addCellLabelCampoValore("label.descrizione_del_progetto", eventoFSC.getDescrizioneProgetto(), tableFields);
		addCellLabelSeparator("label.fasi_azioni_ruoli", tableFields);
		tableFields = addTableFieldsToDocumentAndGetNewTableField(tableFields, document);
		PdfPTable tableTemp = getTableFasiAzioniRuoliFSC(eventoFSC, "label.fasi_azioni_ruoli", eventoFSC.getFasiAzioniRuoli());
		if(tableTemp != null)
			document.add(tableTemp);
		addCellLabelCampoValore("label.brochure_evento", evento.getBrochureEvento(), tableFields);
		tableFields = addTableRiepilogoRuoli(document, tableFields, eventoFSC.getRiepilogoRuoli());
		addCellLabelCampoValore("label.numero_partecipanti", evento.getNumeroPartecipanti(), tableFields);
		addCellLabelCampoValore("label.durata", evento.getDurata(), tableFields);
		addCellLabelCampoValoreZeroComeNonInserito("label.crediti_formativi_attribuiti_evento", evento.getCrediti(), tableFields);
		addCellLabelCampoValore("label.verifica_presenza_partecipanti", eventoFSC.getVerificaPresenzaPartecipanti(), tableFields);
		addCellLabelCampoValore("label.verifica_apprendimento_partecipanti", eventoFSC.getVerificaApprendimento(), tableFields);
		addCellLabelCampoValore("label.indicatore_efficacia", eventoFSC.getIndicatoreEfficaciaFormativa(), tableFields);
		//<!-- RESPONSABILE SEGRETERIA ORGANIZZATIVA -->
		addCellLabelCampoValore("label.responsabile_segreteria_organizzativa", evento.getResponsabileSegreteria(), tableFields);
		addCellLabelCampoValoreValuta("label.quota_partecipazione", evento.getQuotaPartecipazione(), tableFields);
		addCellLabelCampoValore("label.evento_sponsorizzato_radio", evento.getEventoSponsorizzato(), tableFields);
		if(evento.getEventoSponsorizzato() != null && evento.getEventoSponsorizzato())
			addCellLabelCampoValoreSponsors("label.sponsors", evento.getSponsors(), tableFields);
		if(evento.getContenutiEvento() == ContenutiEventoEnum.ALIMENTAZIONE_PRIMA_INFANZIA) {
			//<!-- RADIO SPONSOR PRIMA INFANZIA -->
			addCellLabelCampoValore("label.evento_sponsorizzato_infanzia_radio", evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia(), tableFields);
			if(evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() != null) {
				if(evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia()) //<!-- ALLEGATO AUTOCERTIFICAZIONE AUTORIZZAZIONE MINISTERO SALUTE -->
					addCellLabelCampoValore("label.allegato_autocertificazione_autorizzazione_ministero", evento.getAutocertificazioneAutorizzazioneMinisteroSalute(), tableFields);
				else //<!-- ALLEGATO AUTOCERTIFICAZIONE ASSENZA PARTECIPAZIONE SPONSOR INFANZIA -->
					addCellLabelCampoValore("label.allegato_autocertificazione_assenza_finanziamenti", evento.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia(), tableFields);
			}
		}
		//<!-- FORME FINANZIAMENTO BLOCCO -->
		addCellLabelCampoValore("label.forme_finanziamento_radio", evento.getAltreFormeFinanziamento(), tableFields);
		if(evento.getAltreFormeFinanziamento() != null) {
			if(evento.getAltreFormeFinanziamento()) //<!-- ALLEGATO CONTRATTI/ACCORDI/CONVENZIONI -->
				addCellLabelCampoValore("label.allegato_contratti_accordi_convenzioni", evento.getContrattiAccordiConvenzioni(), tableFields);
			else //<!-- ALLEGATO AUTOCERTIFICAZIONE ASSENZA FINANZIAMENTI -->
				addCellLabelCampoValore("label.allegato_autocertificazione_assenza_finanziamenti", evento.getAutocertificazioneAssenzaFinanziamenti(), tableFields);
		}
		//<!-- BLOCCO PARTNER -->
		addCellLabelCampoValore("label.si_avvale_di_partner", evento.getEventoAvvalePartner(), tableFields);
		if(evento.getEventoAvvalePartner() != null && evento.getEventoAvvalePartner()) {
			addCellLabelCampoValorePartners("label.partners", evento.getPartners(), tableFields);
		}
		addCellLabelCampoValore("label.dichiarazione_assenza_conflitto_interesse", evento.getDichiarazioneAssenzaConflittoInteresse(), tableFields);
		addCellLabelCampoValore("label.procedura_verifica_qualita", evento.getProceduraVerificaQualitaPercepita(), tableFields);
		addCellLabelCampoValore("label.autorizzazione_privacy", evento.getAutorizzazionePrivacy(), tableFields);
		document.add(tableFields);
	}

	private void writePdfEventoRES(Document document, EventoWrapper wrapper) throws Exception {
		Evento evento = wrapper.getEvento();
    	EventoRES eventoRES = (EventoRES)evento;
		//<h2 th:text="#{label.visualizzazione_evento(${eventoWrapper.proceduraFormativa}, ${eventoWrapper.evento.getCodiceIdentificativo()})}"></h2>
        Object[] values = {wrapper.getProceduraFormativa(), evento.getCodiceIdentificativo()};
        Paragraph parTitolo = new Paragraph();
        parTitolo.setAlignment(Element.ALIGN_LEFT);
        parTitolo.setFont(fontTitolo);
        parTitolo.add(messageSource.getMessage("label.visualizzazione_evento", values, Locale.getDefault()));
        document.add(parTitolo);
		PdfPTable tableFields = getTableFields();
		addCellLabelCampoValore("label.denominazione_legale_provider", evento.getProvider().getDenominazioneLegale(), tableFields);
		addCellLabelCampoValore("label.id_provider", evento.getProvider().getId(), tableFields);
		addCellLabelCampoValoreEnum("label.procedure_formative_tipologia", evento.getProceduraFormativa(), tableFields);
		addCellLabelCampoValore("label.destinatari_evento", evento.getDestinatariEvento(), tableFields);
		addCellLabelCampoValore("label.evento_contenuti", evento.getContenutiEvento(), tableFields);
		addCellLabelCampoValore("label.titolo", evento.getTitolo(), tableFields);
		addCellLabelCampoValore("label.provincia", eventoRES.getSedeEvento().getProvincia(), tableFields);
		addCellLabelCampoValore("label.comune", eventoRES.getSedeEvento().getComune(), tableFields);
		addCellLabelCampoValore("label.indirizzo", eventoRES.getSedeEvento().getIndirizzo(), tableFields);
		addCellLabelCampoValore("label.luogo", eventoRES.getSedeEvento().getLuogo(), tableFields);
		addCellLabelCampoValore("label.data_inizio", evento.getDataInizio(), tableFields);
		addCellLabelCampoValore("label.data_fine", evento.getDataFine(), tableFields);
		addCellLabelCampoValore("label.date_intermedie", eventoRES.getDateIntermedie(), tableFields);
		addCellLabelCampoValore("label.obiettivo_strategico_nazionale", evento.getObiettivoNazionale() == null ? null : evento.getObiettivoNazionale().getNome(), tableFields);
		addCellLabelCampoValore("label.obiettivo_strategico_regionale", evento.getObiettivoRegionale() == null ? null : evento.getObiettivoRegionale().getNome(), tableFields);
		addCellLabelCampoValoreDiscipline("label.professioni_discipline", evento.getDiscipline(), tableFields);
		//<!-- BLOCCO TIPOLOGIA DI EVENTO -->
		addCellLabelCampoValore("label.tipologia_evento", eventoRES.getTipologiaEventoRES(), tableFields);
		//<!-- WORKSHOP/SEMINARI (attivo se selezionato convegno/congresso) -->
		if(eventoRES.getTipologiaEventoRES() == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO) {
			addCellLabelCampoValore("label.workshop_radio_text", eventoRES.getWorkshopSeminariEcm(), tableFields);
		}
		//<!-- TITOLO CONVEGNO (attivo se selezionato workshop/seminario) -->
		if(eventoRES.getTipologiaEventoRES() == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO) {
			addCellLabelCampoValore("label.titolo_convegno", eventoRES.getTitoloConvegno(), tableFields);
		}

		addCellLabelCampoValore("label.numero_partecipanti", evento.getNumeroPartecipanti(), tableFields);
		addCellLabelCampoValorePersone("label.responsabili_scientifici", evento.getResponsabili(), tableFields, true, false, false);
		//<!-- DOCENTI/RELATORI/TUTOR -->
		addCellLabelCampoValorePersone("label.docenti_relatori_tutor", eventoRES.getDocenti(), tableFields, false, true, true);
		//<!-- BLOCCO PROGRAMMA ATTIVITÀ FORMATIVA -->
		addCellLabelSeparator("label.programma_attività_formativa", tableFields);
		//<!-- RAZIONALE -->
		addCellLabelCampoValore("label.razionale", eventoRES.getRazionale(), tableFields);
		//<!-- RISULTATI ATTESI -->
		addCellLabelCampoValoriString("label.risultati_attesi", eventoRES.getRisultatiAttesi(), tableFields);
		//EventoRES programma
		tableFields = addTableProgrammaRES(document, tableFields, eventoRES.getProgramma());
		addCellLabelCampoValore("label.brochure_evento", evento.getBrochureEvento(), tableFields);
		addCellLabelCampoValore("label.verifica_apprendimento_partecipanti", eventoRES.getVerificaApprendimento(), tableFields);
		addCellLabelCampoValore("label.durata", evento.getDurata(), tableFields);
		addCellLabelCampoValore("label.tipologia_obiettivi_formativi", eventoRES.getRiepilogoRES().getObiettivi(), tableFields);
		//<!-- METODOLOGIE DIDATTICHE -->
		addCellLabelRiepilogoMetodologieRES("label.metodologie_didattiche", eventoRES.getRiepilogoRES(), tableFields);
		addCellLabelCampoValoreZeroComeNonInserito("label.crediti_formativi_attribuiti_evento", evento.getCrediti(), tableFields);
		if(evento.getCrediti() != null && evento.getCrediti().floatValue() != 0)
			addCellLabelCampoValore("label.provider_confermato_crediti", evento.getConfermatiCrediti(), tableFields);
		//<!-- RESPONSABILE SEGRETERIA ORGANIZZATIVA -->
		addCellLabelCampoValore("label.responsabile_segreteria_organizzativa", evento.getResponsabileSegreteria(), tableFields);
		addCellLabelCampoValore("label.tipo_materiale_rilasciato", eventoRES.getMaterialeDurevoleRilasciatoAiPratecipanti(), tableFields);
		addCellLabelCampoValoreValuta("label.quota_partecipazione", evento.getQuotaPartecipazione(), tableFields);
		//<!-- BLOCCO LINGUA -->
		addCellLabelCampoValore("label.solo_lingua_italiana", eventoRES.getSoloLinguaItaliana(), tableFields);
		if(eventoRES.getSoloLinguaItaliana() != null && eventoRES.getSoloLinguaItaliana() == false) {
			//<!-- LINGUA STRANIERA -->
			addCellLabelCampoValore("label.lingua_straniera_utilizzata", eventoRES.getLinguaStranieraUtilizzata(), tableFields);
			addCellLabelCampoValore("label.esiste_traduzione", eventoRES.getEsisteTraduzioneSimultanea(), tableFields);
		}
		//<!-- VERIFICA PRESENZA PARTECIPANTI -->
		addCellLabelCampoValore("label.verifica_presenza_partecipanti", eventoRES.getVerificaPresenzaPartecipanti(), tableFields);
		//<!-- VERIFICA A DISTANZA BLOCCO -->
		addCellLabelCampoValore("label.verifica_distanza_ricadute_formative", eventoRES.getVerificaRicaduteFormative(), tableFields);
		if(eventoRES.getVerificaRicaduteFormative() != null && eventoRES.getVerificaRicaduteFormative()) {
			addCellLabelCampoValore("label.descrizione_verifica_ricadute_formative", eventoRES.getDescrizioneVerificaRicaduteFormative(), tableFields);
			addCellLabelCampoValore("label.documento_verifica_ricadute_formative", eventoRES.getDocumentoVerificaRicaduteFormative(), tableFields);
		}

		addCellLabelCampoValore("label.evento_sponsorizzato_radio", evento.getEventoSponsorizzato(), tableFields);
		if(evento.getEventoSponsorizzato() != null && evento.getEventoSponsorizzato())
			addCellLabelCampoValoreSponsors("label.sponsors", evento.getSponsors(), tableFields);
		if(evento.getContenutiEvento() == ContenutiEventoEnum.ALIMENTAZIONE_PRIMA_INFANZIA) {
			//<!-- RADIO SPONSOR PRIMA INFANZIA -->
			addCellLabelCampoValore("label.evento_sponsorizzato_infanzia_radio", evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia(), tableFields);
			if(evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia() != null) {
				if(evento.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia()) //<!-- ALLEGATO AUTOCERTIFICAZIONE AUTORIZZAZIONE MINISTERO SALUTE -->
					addCellLabelCampoValore("label.allegato_autocertificazione_autorizzazione_ministero", evento.getAutocertificazioneAutorizzazioneMinisteroSalute(), tableFields);
				else //<!-- ALLEGATO AUTOCERTIFICAZIONE ASSENZA PARTECIPAZIONE SPONSOR INFANZIA -->
					addCellLabelCampoValore("label.allegato_autocertificazione_assenza_finanziamenti", evento.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia(), tableFields);
			}
		}
		//<!-- FORME FINANZIAMENTO BLOCCO -->
		addCellLabelCampoValore("label.forme_finanziamento_radio", evento.getAltreFormeFinanziamento(), tableFields);
		if(evento.getAltreFormeFinanziamento() != null) {
			if(evento.getAltreFormeFinanziamento()) //<!-- ALLEGATO CONTRATTI/ACCORDI/CONVENZIONI -->
				addCellLabelCampoValore("label.allegato_contratti_accordi_convenzioni", evento.getContrattiAccordiConvenzioni(), tableFields);
			else //<!-- ALLEGATO AUTOCERTIFICAZIONE ASSENZA FINANZIAMENTI -->
				addCellLabelCampoValore("label.allegato_autocertificazione_assenza_finanziamenti", evento.getAutocertificazioneAssenzaFinanziamenti(), tableFields);
		}
		//<!-- BLOCCO PARTNER -->
		addCellLabelCampoValore("label.si_avvale_di_partner", evento.getEventoAvvalePartner(), tableFields);
		if(evento.getEventoAvvalePartner() != null && evento.getEventoAvvalePartner()) {
			addCellLabelCampoValorePartners("label.partners", evento.getPartners(), tableFields);
		}

		addCellLabelCampoValore("label.dichiarazione_assenza_conflitto_interesse", evento.getDichiarazioneAssenzaConflittoInteresse(), tableFields);
		addCellLabelCampoValore("label.procedura_verifica_qualita", evento.getProceduraVerificaQualitaPercepita(), tableFields);
		addCellLabelCampoValore("label.autorizzazione_privacy", evento.getAutorizzazionePrivacy(), tableFields);
		document.add(tableFields);
	}

	private PdfPTable addTableFieldsToDocumentAndGetNewTableField(PdfPTable tableFields, Document document) throws DocumentException {
		tableFields.setSpacingAfter(0);
		document.add(tableFields);
		PdfPTable toRet = getTableFields();
		toRet.setSpacingBefore(0);
		return toRet;
	}

	private PdfPTable getTableFields() throws DocumentException {
		PdfPTable tableFields = new PdfPTable(2);
		tableFields.setWidthPercentage(100);
		tableFields.setWidths(new float[]{1, 3});
		tableFields.setSpacingBefore(spacingBefore);
		tableFields.setSpacingAfter(spacingAfter);
		//tableNoteTrasp.setTotalWidth(new float[]{520});
		//tableNoteTrasp.setLockedWidth(true);
		//tableFields.setHorizontalAlignment(Element.ALIGN_CENTER);
		return tableFields;
	}

	private PdfPTable addTableProgrammaRES(Document document, PdfPTable tableFields, List<ProgrammaGiornalieroRES> programma) throws DocumentException {
		//Creo le tabelle solo se ci sono dati
		PdfPTable newTableFields = tableFields;
		if(programma != null && programma.size() > 0) {
			//aggiungo la tableFields corrente al documento e ne inizializzo un'altra che restituisco alla fine e sulla quale aandranno aggiunti i prossimi campi
			newTableFields = addTableFieldsToDocumentAndGetNewTableField(tableFields, document);
			String docenti;
			boolean writeDocenti = false;
			for(ProgrammaGiornalieroRES dettPrg : programma) {
				PdfPTable tableProgrGior = new PdfPTable(2);
				tableProgrGior.setWidthPercentage(100);
				tableProgrGior.setWidths(new float[]{1, 4});
				tableProgrGior.setSpacingBefore(spacingBefore);
				tableProgrGior.setSpacingAfter(spacingAfter);

				addCellIntestaSubTableByLabel("label.programma_del", tableProgrGior, null, false, Element.ALIGN_RIGHT);
				addCellSubTable(dettPrg.getGiorno().format(dateTimeFormatter), tableProgrGior, null, false, Element.ALIGN_LEFT);
				addCellIntestaSubTableByLabel("label.sede", tableProgrGior, null, false, Element.ALIGN_RIGHT);
				addCellSubTable("", tableProgrGior, null, false, Element.ALIGN_LEFT);
				addCellIntestaSubTableByLabel("label.provincia", tableProgrGior, null, false, Element.ALIGN_RIGHT);
				addCellSubTable(dettPrg.getSede().getProvincia(), tableProgrGior, null, false, Element.ALIGN_LEFT);
				addCellIntestaSubTableByLabel("label.comune", tableProgrGior, null, false, Element.ALIGN_RIGHT);
				addCellSubTable(dettPrg.getSede().getComune(), tableProgrGior, null, false, Element.ALIGN_LEFT);
				addCellIntestaSubTableByLabel("label.indirizzo", tableProgrGior, null, false, Element.ALIGN_RIGHT);
				addCellSubTable(dettPrg.getSede().getIndirizzo(), tableProgrGior, null, false, Element.ALIGN_LEFT);
				addCellIntestaSubTableByLabel("label.luogo", tableProgrGior, null, false, Element.ALIGN_RIGHT);
				addCellSubTable(dettPrg.getSede().getLuogo(), tableProgrGior, null, false, Element.ALIGN_LEFT);

				document.add(tableProgrGior);

				PdfPTable tableDettaglioAttivita = new PdfPTable(8);
				tableDettaglioAttivita.setWidthPercentage(100);
				tableDettaglioAttivita.setWidths(new float[]{1.1F, 1.1F, 3, 2, 2, 3, 5, 1.2F});
				tableDettaglioAttivita.setSpacingBefore(0);
				tableDettaglioAttivita.setSpacingAfter(0);
				PdfPCell cellEtichetta = getCellEtichettaSubTable(messageSource.getMessage("label.dettaglio_attivita", null, Locale.getDefault()));
				cellEtichetta.setColspan(8);
				tableDettaglioAttivita.addCell(cellEtichetta);
				addCellIntestaSubTableByLabel("label.orario_inizio", tableDettaglioAttivita);
				addCellIntestaSubTableByLabel("label.orario_fine", tableDettaglioAttivita);
				addCellIntestaSubTableByLabel("label.argomento", tableDettaglioAttivita);
				addCellIntestaSubTableByLabel("label.docente", tableDettaglioAttivita);
				addCellIntestaSubTableByLabel("label.risultato_atteso", tableDettaglioAttivita);
				addCellIntestaSubTableByLabel("label.tipologia_obiettivi_formativi", tableDettaglioAttivita);
				addCellIntestaSubTableByLabel("label.metodologia_didattica", tableDettaglioAttivita);
				addCellIntestaSubTableByLabel("label.ore_attivita", tableDettaglioAttivita);
				if(dettPrg.getProgramma() != null && dettPrg.getProgramma().size() > 0) {
					for(DettaglioAttivitaRES dettAtt : dettPrg.getProgramma()) {
						writeDocenti = false;
						docenti = "";
						if(dettAtt.getOrarioInizio() != null)
							addCellSubTable(formatValue(dettAtt.getOrarioInizio()), tableDettaglioAttivita);
						else
							addCellSubTable("", tableDettaglioAttivita);
						if(dettAtt.getOrarioFine() != null)
							addCellSubTable(formatValue(dettAtt.getOrarioFine()), tableDettaglioAttivita);
						else
							addCellSubTable("", tableDettaglioAttivita);
						addCellSubTable(dettAtt.getArgomento(), tableDettaglioAttivita);
						//TODO ciclo sui docenti
//						if(dettAtt.getDocente() != null && dettAtt.getDocente().getAnagrafica() != null && dettAtt.getDocente().getAnagrafica().getCognome() != null)
//							addCellSubTable(dettAtt.getDocente().getAnagrafica().getCognome(), tableDettaglioAttivita);
//						else
//							addCellSubTable("", tableDettaglioAttivita);

						if(dettAtt.getDocenti() != null) {
							for(PersonaEvento docente : dettAtt.getDocenti()) {
								if(docente.getAnagrafica() != null && docente.getAnagrafica().getCognome() != null && !docente.getAnagrafica().getCognome().isEmpty()) {
									if(writeDocenti)
										docenti += "\n";
									docenti += docente.getAnagrafica().getCognome();
									writeDocenti = true;
								}
							}
						}
						addCellSubTable(docenti, tableDettaglioAttivita);

						addCellSubTable(dettAtt.getRisultatoAtteso(), tableDettaglioAttivita);
						if(dettAtt.getObiettivoFormativo() != null)
							addCellSubTable(dettAtt.getObiettivoFormativo().getNome(), tableDettaglioAttivita);
						else
							addCellSubTable("", tableDettaglioAttivita);
						if(dettAtt.getMetodologiaDidattica() != null)
							addCellSubTable(dettAtt.getMetodologiaDidattica().getNome(), tableDettaglioAttivita);
						else
							addCellSubTable("", tableDettaglioAttivita);
						addCellSubTable(floatFormatter.print(dettAtt.getOreAttivita(), Locale.getDefault()), tableDettaglioAttivita);
					}
				}
				document.add(tableDettaglioAttivita);
			}
		}
		return newTableFields;
	}

	private String formatValue(LocalTime time) {
		return time.format(timeFormatter);
	}

	private PdfPTable addTableRiepilogoRuoli(Document document, PdfPTable tableFields, List<RiepilogoRuoliFSC> riepilogoRuoli) throws Exception  {
		PdfPTable subTable = null;
		subTable = new PdfPTable(5);
		subTable.setWidthPercentage(100);
		subTable.setWidths(new float[]{1.5F, 1, 1, 1, 1});
		subTable.setSpacingBefore(5);
		subTable.setSpacingAfter(5);
		addCellIntestaSubTableByLabel("label.ruolo", subTable);
		addCellIntestaSubTableByLabel("label.codifica", subTable);
		addCellIntestaSubTableByLabel("label.numero_partecipanti", subTable);
		addCellIntestaSubTableByLabel("label.impegno_complessivo_ore", subTable);
		addCellIntestaSubTableByLabel("label.crediti", subTable);
		if(riepilogoRuoli != null) {
			for(RiepilogoRuoliFSC riepRu : riepilogoRuoli) {
				if(riepRu.getRuolo() != null) {
					addCellSubTable(riepRu.getRuolo().getNome(), subTable);
					if(riepRu.getRuolo().getRuoloBase() != null)
						addCellSubTable(riepRu.getRuolo().getRuoloBase().getCodifica(), subTable);
					else
						addCellSubTable("", subTable);
				} else {
					addCellSubTable("", subTable);
					addCellSubTable("", subTable);
				}

				addCellSubTable(intFormatter.print(riepRu.getNumeroPartecipanti(), Locale.getDefault()), subTable);
				addCellSubTable(floatFormatter.print(riepRu.getTempoDedicato(), Locale.getDefault()), subTable);
				addCellSubTable(floatFormatter.print(riepRu.getCrediti(), Locale.getDefault()), subTable);
			}
		}

		addCellLabelAndEmptyField("label.ruoli_coinvolti", tableFields);
		PdfPTable newTableField = addTableFieldsToDocumentAndGetNewTableField(tableFields, document);
		document.add(subTable);
		return newTableField;
	}

	private void addCellLabelAndEmptyField(String labelCampo, PdfPTable tableFields) {
		addCellCampoAndEmptyField(messageSource.getMessage(labelCampo, null, Locale.getDefault()), tableFields);
	}

	private void addCellCampoAndEmptyField(String nomeCampo, PdfPTable tableFields) {
		tableFields.addCell(getCellLabel(nomeCampo));
		tableFields.addCell(getValoreEmptyCell());
	}

	private PdfPTable getTableFasiAzioniRuoliFSC(EventoFSC eventoFSC, String labelCampo, List<FaseAzioniRuoliEventoFSCTypeA> programma) throws DocumentException {
		PdfPTable subTable = null;
		if(programma != null && programma.size() > 0) {
			String labelFase = messageSource.getMessage("label.fase", null, Locale.getDefault());
			String labelAzione = messageSource.getMessage("label.azione", null, Locale.getDefault());
			String labelFasi = messageSource.getMessage("label.fasi", null, Locale.getDefault());
			String labelAzioniRuoli = messageSource.getMessage("label.azioni", null, Locale.getDefault()) + "/" + messageSource.getMessage("label.ruoli", null, Locale.getDefault());

			subTable = new PdfPTable(6);
			subTable.setWidthPercentage(100);
			subTable.setWidths(new float[]{1, 1, 1, 1, 1, 1});
			subTable.setSpacingBefore(5);
			subTable.setSpacingAfter(5);

			String etichettaSubTable;
			String nomePrimaColonna;
			String tempString;
			String ruoloOreFSCVal;
			boolean write = false;
			PdfPCell cell;
			PdfPCell tableCell;
			for(FaseAzioniRuoliEventoFSCTypeA faseAzRu : programma) {
				if(eventoFSC.getTipologiaEventoFSC() != TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO || ProgettiDiMiglioramentoFasiDaInserireFSCEnum.faseAbilitata(eventoFSC.getFasiDaInserire(), faseAzRu.getFaseDiLavoro())) {
					etichettaSubTable = labelFasi;
					nomePrimaColonna = labelFase;
					//<th:block th:if="${fase != T(it.tredi.ecm.dao.enumlist.FaseDiLavoroFSCEnum).CAMPO_LIBERO}">
					if(faseAzRu.getFaseDiLavoro() != null && faseAzRu.getFaseDiLavoro() != FaseDiLavoroFSCEnum.CAMPO_LIBERO) {
						String titleSubTable = labelFase + "    " + faseAzRu.getFaseDiLavoro().getNome();
						cell = getCellTitleSubTable(titleSubTable.toUpperCase());
						cell.setColspan(6);
						subTable.addCell(cell);
						etichettaSubTable = labelAzioniRuoli;
						nomePrimaColonna = labelAzione;
					}

					cell = getCellEtichettaSubTable(etichettaSubTable);
					cell.setColspan(6);
					subTable.addCell(cell);

					addCellIntestaSubTableByString(nomePrimaColonna, subTable);
					addCellIntestaSubTableByLabel("label.obiettivo_formativo", subTable);
					addCellIntestaSubTableByLabel("label.risultati_attesi", subTable);
					addCellIntestaSubTableByLabel("label.metodi_di_lavoro", subTable);
					addCellIntestaSubTableByLabel("label.ruoli_coinvolti", subTable);
					addCellIntestaSubTableByLabel("label.ore_attivita", subTable);
					for(AzioneRuoliEventoFSC azRu : faseAzRu.getAzioniRuoli()) {
						//Azione o Fase
						addCellSubTable(azRu.getAzione(), subTable);
						//obiettivo_formativo
						if(azRu.getObiettivoFormativo() != null)
							addCellSubTable(azRu.getObiettivoFormativo().getNome(), subTable);
						else
							addCellSubTable("", subTable);
						//risultati_attesi
						addCellSubTable(azRu.getRisultatiAttesi(), subTable);
						//metodi_di_lavoro
						tempString = "";
						write = false;
						for(MetodoDiLavoroEnum metodoLavoro : azRu.getMetodiDiLavoro()) {
							if(write)
								tempString += "\n";
							tempString += metodoLavoro.getNome();
							write = true;
						}
						addCellSubTable(tempString, subTable);
						//ruoli_coinvolti
						tempString = "";
						write = false;
						for(RuoloOreFSC ruOre : azRu.getRuoli()) {
							ruoloOreFSCVal = getVal(ruOre);
							if(ruoloOreFSCVal != null) {
								if(write)
									tempString += "\n";
								tempString += ruoloOreFSCVal;
								write = true;
							}
						}
						addCellSubTable(tempString, subTable);
						//ore_attivita
						if(azRu.getTempoDedicato() != null)
							addCellSubTable(floatFormatter.print(azRu.getTempoDedicato(), Locale.getDefault()), subTable);
						else
							addCellSubTable("", subTable);
					}
				}
			}
		}
		return subTable;
	}

	private String getVal(RuoloOreFSC obj) {
		String toRet = null;
		if(obj.getRuolo() != null){
			toRet = obj.getRuolo().getNome();
			if(obj.getTempoDedicato() != null)
				toRet += " (" + floatFormatter.print(obj.getTempoDedicato(), Locale.getDefault()) + "h)";
		}
		return toRet;
	}

	private void addCellLabelCampoValorePartners(String labelCampo, Set<Partner> partners, PdfPTable table) throws DocumentException {
		PdfPTable subTable = null;
		if(partners != null && partners.size() > 0) {
			subTable = new PdfPTable(2);
			subTable.setWidthPercentage(100);
			subTable.setWidths(new float[]{4, 1});
			subTable.setSpacingBefore(0);
			subTable.setSpacingAfter(0);
			addCellIntestaSubTableByLabel("label.nome_partner", subTable);
			addCellIntestaSubTableByLabel("label.contratto", subTable);
			for(Partner partner : partners) {
				if(partner.getName() != null)
					addCellSubTable(partner.getName(), subTable);
				else
					addCellSubTable("", subTable);
				if(partner.getPartnerFile() != null)
					addCellSubTable(partner.getPartnerFile().getNomeFile(), subTable);
				else
					addCellSubTable("", subTable);
			}
		}
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), subTable, table, true);
	}


	private void addCellLabelCampoValore(String labelCampo, PersonaFullEvento persona, PdfPTable table) throws Exception  {
		PdfPTable tablePers = null;
		if(persona != null && persona.getAnagrafica() != null) {
			tablePers = new PdfPTable(6);
			tablePers.setWidthPercentage(100);
			tablePers.setWidths(new float[]{1, 1, 1.5F, 1, 1, 1});

			//tableDisc.setWidths(new float[]{1});
			tablePers.setSpacingBefore(0);
			tablePers.setSpacingAfter(0);

			addCellIntestaSubTableByLabel("label.cognome", tablePers);
			addCellIntestaSubTableByLabel("label.nome", tablePers);
			addCellIntestaSubTableByLabel("label.cf", tablePers);
			addCellIntestaSubTableByLabel("label.email", tablePers);
			addCellIntestaSubTableByLabel("label.telefono", tablePers);
			addCellIntestaSubTableByLabel("label.cellulare", tablePers);
			//if(persona.getAnagrafica() != null) {
				addCellSubTable(persona.getAnagrafica().getCognome(), tablePers);
				addCellSubTable(persona.getAnagrafica().getNome(), tablePers);
				addCellSubTable(persona.getAnagrafica().getCodiceFiscale(), tablePers);
				addCellSubTable(persona.getAnagrafica().getEmail(), tablePers);
				addCellSubTable(persona.getAnagrafica().getTelefono(), tablePers);
				addCellSubTable(persona.getAnagrafica().getCellulare(), tablePers);
			//}

		}
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), tablePers, table, true);
	}

	private void addCellLabelCampoValoreSponsors(String labelCampo, Set<Sponsor> sponsors, PdfPTable table) throws DocumentException {
		PdfPTable subTable = null;
		if(sponsors != null && sponsors.size() > 0) {
			subTable = new PdfPTable(2);
			subTable.setWidthPercentage(100);
			subTable.setWidths(new float[]{4, 1});
			subTable.setSpacingBefore(0);
			subTable.setSpacingAfter(0);
			addCellIntestaSubTableByLabel("label.nome_sponsor", subTable);
			addCellIntestaSubTableByLabel("label.contratto", subTable);
			for(Sponsor sponsor : sponsors) {
				if(sponsor.getName() != null)
					addCellSubTable(sponsor.getName(), subTable);
				else
					addCellSubTable("", subTable);
				if(sponsor.getSponsorFile() != null)
					addCellSubTable(sponsor.getSponsorFile().getNomeFile(), subTable);
				else
					addCellSubTable(getLabelAllegatoNonInserito(), subTable);
			}
		}
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), subTable, table, true);
	}

	private void addCellLabelCampoValore(String labelCampo, Map<MetodologiaDidatticaFADEnum, Float> metodologie, PdfPTable table) throws DocumentException {
		PdfPTable subTable = null;
		if(metodologie != null && metodologie.size() > 0) {
			subTable = new PdfPTable(2);
			subTable.setWidthPercentage(100);
			subTable.setWidths(new float[]{10, 1});
			subTable.setSpacingBefore(0);
			subTable.setSpacingAfter(0);
			addCellIntestaSubTableByLabel("label.metodologia_didattica", subTable);
			addCellIntestaSubTableByLabel("label.totale_ore", subTable);
			for(MetodologiaDidatticaFADEnum dettAtt : metodologie.keySet()) {
				if(dettAtt.getNome() != null)
					addCellSubTable(dettAtt.getNome(), subTable);
				else
					addCellSubTable("", subTable);
				if(metodologie.get(dettAtt) != null)
					addCellSubTable(floatFormatter.print(metodologie.get(dettAtt), Locale.getDefault()) + " h", subTable);
				else
					addCellSubTable("", subTable);
			}
		}
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), subTable, table, true);
	}

	//Map<MetodologiaDidatticaRESEnum, Float>
	private void addCellLabelRiepilogoMetodologieRES(String labelCampo, RiepilogoRES riepilogoRES, PdfPTable table) throws DocumentException {
		Map<MetodologiaDidatticaRESEnum, Float> metodologie = riepilogoRES.getMetodologie();
		PdfPTable subTable = null;
		if(metodologie != null && metodologie.size() > 0) {
			subTable = new PdfPTable(3);
			subTable.setWidthPercentage(100);
			subTable.setWidths(new float[]{8, 1.1F, 1.1F});
			subTable.setSpacingBefore(0);
			subTable.setSpacingAfter(0);
			addCellIntestaSubTableByLabel("label.metodologia_didattica", subTable);
			addCellIntestaSubTableByLabel("label.metodologia_frontale", subTable);
			addCellIntestaSubTableByLabel("label.metodologia_interattiva", subTable);
			for(MetodologiaDidatticaRESEnum dettAtt : metodologie.keySet()) {
				if(dettAtt.getNome() != null)
					addCellSubTable(dettAtt.getNome(), subTable);
				else
					addCellSubTable("", subTable);
				if(metodologie.get(dettAtt) != null)
					if(dettAtt.getMetodologia() == TipoMetodologiaEnum.FRONTALE) {
						addCellSubTable(floatFormatter.print(metodologie.get(dettAtt), Locale.getDefault()) + " h", subTable, null, true, Element.ALIGN_RIGHT);
						addCellSubTable("", subTable);
					} else {
						addCellSubTable("", subTable);
						addCellSubTable(floatFormatter.print(metodologie.get(dettAtt), Locale.getDefault()) + " h", subTable, null, true, Element.ALIGN_RIGHT);
					}
				else {
					addCellSubTable("", subTable);
					addCellSubTable("", subTable);
				}
			}
			//Totale
			//addCellIntestaSubTableByLabel("label.totale_ore", subTable);
			addCellIntestaSubTableByLabel("label.totale_ore", subTable, null, true, Element.ALIGN_LEFT);
			addCellSubTable(floatFormatter.print(riepilogoRES.getTotaleOreFrontali(), Locale.getDefault()) + " h", subTable, null, true, Element.ALIGN_RIGHT, true);
			addCellSubTable(floatFormatter.print(riepilogoRES.getTotaleOreInterattive(), Locale.getDefault()) + " h", subTable, null, true, Element.ALIGN_RIGHT, true);
		}
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), subTable, table, true);
	}

	private void addCellLabelCampoValore(String labelCampo, Collection<VerificaApprendimentoFAD> valoriCampo, PdfPTable table) {
		String valoreCampo = "";
		if(valoriCampo != null && valoriCampo.size() > 0) {
			boolean write = false;
			for(VerificaApprendimentoFAD val : valoriCampo) {
				if(write)
					valoreCampo += "\n";
				valoreCampo += val.getVerificaApprendimento().getNome() + " - (" + val.getVerificaApprendimentoInner().getNome() + ")";
				write = true;
			}
		}
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValore(String labelCampo, File fileCampo, PdfPTable table) {
    	addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), fileCampo, table);
    }

    private void addCellCampoValore(String nomeCampo, File fileCampo, PdfPTable table) {
		PdfPCell cell = getCellLabel(nomeCampo);
		table.addCell(cell);
		if(fileCampo == null)
			cell = getCellValore(messageSource.getMessage("label.allegato_non_inserito", null, Locale.getDefault()));
		else
			cell = getCellValore(fileCampo.getNomeFile());
		table.addCell(cell);
    }

	private PdfPTable getTableProgrammaFAD(List<DettaglioAttivitaFAD> programma) throws DocumentException {
		PdfPTable subTable = null;
		subTable = new PdfPTable(5);
		subTable.setWidthPercentage(100);
		subTable.setWidths(new float[]{3, 1, 1, 2, 3});
		subTable.setSpacingBefore(0);
		subTable.setSpacingAfter(0);
		PdfPCell cellEtichetta = getCellEtichettaSubTable(messageSource.getMessage("label.dettaglio_attivita", null, Locale.getDefault()));
		cellEtichetta.setColspan(5);
		subTable.addCell(cellEtichetta);
		if(programma != null && programma.size() > 0) {
			addCellIntestaSubTableByLabel("label.argomento", subTable);
			addCellIntestaSubTableByLabel("label.docente_tutor", subTable);
			addCellIntestaSubTableByLabel("label.risultato_atteso", subTable);
			addCellIntestaSubTableByLabel("label.tipologia_obiettivi_formativi", subTable);
			addCellIntestaSubTableByLabel("label.metodologia_didattica", subTable);
			String docenti;
			boolean writeDocenti = false;
			for(DettaglioAttivitaFAD dettAtt : programma) {
				docenti = "";
				writeDocenti = false;

				addCellSubTable(dettAtt.getArgomento(), subTable);
				//TODO ciclo sui docenti
//				if(dettAtt.getDocente() != null && dettAtt.getDocente().getAnagrafica() != null && dettAtt.getDocente().getAnagrafica().getCognome() != null)
//					addCellSubTable(dettAtt.getDocente().getAnagrafica().getCognome(), subTable);
//				else
//					addCellSubTable("", subTable);
				if(dettAtt.getDocenti() != null) {
					for(PersonaEvento docente : dettAtt.getDocenti()) {
						if(docente.getAnagrafica() != null && docente.getAnagrafica().getCognome() != null && !docente.getAnagrafica().getCognome().isEmpty()) {
							if(writeDocenti)
								docenti += "\n";
							docenti += docente.getAnagrafica().getCognome();
							writeDocenti = true;
						}
					}
				}
				addCellSubTable(docenti, subTable);

				addCellSubTable(dettAtt.getRisultatoAtteso(), subTable);
				if(dettAtt.getObiettivoFormativo() != null)
					addCellSubTable(dettAtt.getObiettivoFormativo().getNome(), subTable);
				else
					addCellSubTable("", subTable);
				if(dettAtt.getMetodologiaDidattica() != null)
					addCellSubTable(dettAtt.getMetodologiaDidattica().getNome(), subTable);
				else
					addCellSubTable("", subTable);
			}
		}
		return subTable;
	}

	private void addCellLabelCampoValorePersone(String labelCampo, List<PersonaEvento> persone, PdfPTable table
			, boolean mostraQualifica, boolean mostraRuolo, boolean mostraTitolareSostituto) throws Exception  {
		PdfPTable tablePers = null;
		if(persone != null && persone.size() > 0) {
			int numCols = 4;
			if(mostraQualifica)
				numCols++;
			if(mostraRuolo)
				numCols++;
			if(mostraTitolareSostituto)
				numCols++;

			tablePers = new PdfPTable(numCols);
			tablePers.setWidthPercentage(100);
			if(numCols==4)
				tablePers.setWidths(new float[]{1, 1, 1.2F, 1});
			else if(numCols==5)
				tablePers.setWidths(new float[]{1, 1, 1.4F, 1, 1});
			else if(numCols==6)
				tablePers.setWidths(new float[]{1, 1, 1.5F, 1, 1, 1});
			else if(numCols==7)
				tablePers.setWidths(new float[]{1, 1, 1.6F, 1, 1, 1, 1});
			else
				throw new Exception("Numero di colonne non gestito in addCellLabelCampoValorePersone numCols: " + numCols);

			//tableDisc.setWidths(new float[]{1});
			tablePers.setSpacingBefore(0);
			tablePers.setSpacingAfter(0);

			addCellIntestaSubTableByLabel("label.cognome", tablePers);
			addCellIntestaSubTableByLabel("label.nome", tablePers);
			addCellIntestaSubTableByLabel("label.cf", tablePers);
			if(mostraQualifica)
				addCellIntestaSubTableByLabel("label.qualifica", tablePers);
			if(mostraRuolo)
				addCellIntestaSubTableByLabel("label.ruolo", tablePers);
			if(mostraTitolareSostituto)
				addCellIntestaSubTableByString(messageSource.getMessage("label.titolare", null, Locale.getDefault()) + "/" + messageSource.getMessage("label.sostituto", null, Locale.getDefault()), tablePers);
			addCellIntestaSubTableByLabel("label.cv", tablePers);
			for(PersonaEvento pers : persone) {
				addCellSubTable(pers.getAnagrafica().getCognome(), tablePers);
				addCellSubTable(pers.getAnagrafica().getNome(), tablePers);
				addCellSubTable(pers.getAnagrafica().getCodiceFiscale(), tablePers);
				if(mostraQualifica)
					addCellSubTable(pers.getQualifica(), tablePers);
				if(mostraRuolo) {
					if(pers.getRuolo() == null)
						addCellSubTable("", tablePers);
					else
						addCellSubTable(pers.getRuolo().getNome(), tablePers);
				}
				if(mostraTitolareSostituto) {
					if(pers.getTitolare() == null)
						addCellSubTable("", tablePers);
					else
						addCellSubTable(pers.getTitolare(), tablePers);
				}
				if(pers.getAnagrafica().getCv() == null) {
					addCellSubTable(getLabelNessunCv(), tablePers);
				} else {
					addCellSubTable(pers.getAnagrafica().getCv().getNomeFile(), tablePers);
				}
			}

		}
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), tablePers, table, true);
	}

	private String getLabelNessunCv() {
		return messageSource.getMessage("label.nessun_cv", null, Locale.getDefault());
	}

	private String getLabelAllegatoNonInserito() {
		return 	messageSource.getMessage("label.allegato_non_inserito", null, Locale.getDefault());
	}

	private void addCellSeparator(PdfPTable table) {
		PdfPCell cell = getCellLabel("");
		cell.setColspan(2);
		cell.setFixedHeight(5);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
	}

	private void addCellLabelSeparator(String labelCampo, PdfPTable table) {
		addCellCampoSeparator(messageSource.getMessage(labelCampo, null, Locale.getDefault()), table);
	}

	private void addCellCampoSeparator(String nomeCampo, PdfPTable table) {
		PdfPCell cell = getCellLabel(nomeCampo);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(2);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);

		//Linea
//		Chunk linebreak = new Chunk(new DottedLineSeparator());
//		cell = new PdfPCell(new Phrase(linebreak));
//		cell.setBorder(PdfPCell.NO_BORDER);
//		cell.setColspan(2);
//		table.addCell(cell);
	}

	private void addCellIntestaSubTableByLabel(String labelCampo, PdfPTable table, BaseColor baseColor, boolean border, Integer elementAlign) {
		addCellIntestaSubTableByString(messageSource.getMessage(labelCampo, null, Locale.getDefault()), table, baseColor, border, elementAlign);
	}

	private void addCellIntestaSubTableByLabel(String labelCampo, PdfPTable table) {
		addCellIntestaSubTableByString(messageSource.getMessage(labelCampo, null, Locale.getDefault()), table, BaseColor.GRAY, true, null);
	}

	private void addCellIntestaSubTableByString(String stringLabelCampo, PdfPTable table) {
		addCellIntestaSubTableByString(stringLabelCampo, table, BaseColor.GRAY, true, null);
	}

	private void addCellIntestaSubTableByString(String stringLabelCampo, PdfPTable table, BaseColor baseColor, boolean border, Integer elementAlign) {
		PdfPCell cell = new PdfPCell(new Phrase(stringLabelCampo, fontNomeCampoSubTable));
		if(!border)
			cell.setBorder(PdfPCell.NO_BORDER);
		if(baseColor != null)
			cell.setBackgroundColor(baseColor);
		if(elementAlign != null)
			cell.setHorizontalAlignment(elementAlign);
		else
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setPadding(cellPaddingSubTable);
		table.addCell(cell);
	}

	private void addCellSubTable(String valoreCampo, PdfPTable table) {
		addCellSubTable(valoreCampo, table, null, true, null, false);
	}

	private void addCellSubTable(String valoreCampo, PdfPTable table, BaseColor baseColor, boolean border, Integer elementAlign) {
		addCellSubTable(valoreCampo, table, baseColor, border, elementAlign, false);
	}

	private void addCellSubTable(String valoreCampo, PdfPTable table, BaseColor baseColor, boolean border, Integer elementAlign, boolean bold) {
		PdfPCell cell = new PdfPCell(new Phrase(valoreCampo, bold ? fontValoreCampoSubTableBold : fontValoreCampoSubTable));
		if(!border)
			cell.setBorder(PdfPCell.NO_BORDER);
		if(baseColor != null)
			cell.setBackgroundColor(baseColor);
		if(elementAlign != null)
			cell.setHorizontalAlignment(elementAlign);
		else
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setPadding(cellPaddingSubTable);
		table.addCell(cell);
	}

	private PdfPCell getCellTitleSubTable(String title) {
		PdfPCell cell = new PdfPCell(new Phrase(title, fontTitoloSubTable));
		cell.setBorder(PdfPCell.NO_BORDER);
		//cell.setBackgroundColor(BaseColor.GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPadding(4F);
		return cell;
	}

	private PdfPCell getCellEtichettaSubTable(String etichetta) {
		PdfPCell cell = new PdfPCell(new Phrase(etichetta, fontEtichettaSubTable));
		cell.setBorder(PdfPCell.NO_BORDER);
		//cell.setBackgroundColor(BaseColor.GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setPadding(0);
		cell.setPaddingBottom(2);
		return cell;
	}

	private void addCellLabelCampoValoreDiscipline(String labelCampo, Set<Disciplina> discipline, PdfPTable table) throws DocumentException  {
		PdfPTable tableDisc = null;
		if(discipline != null && discipline.size() > 0) {
			Map<String, Set<String>> professioniDiscipline = new HashMap<String, Set<String>>();
			for(Disciplina disciplina : discipline) {
				Set<String> disciplineForProf = professioniDiscipline.get(disciplina.getProfessione().getNome());
				if(disciplineForProf == null) {
					disciplineForProf = new HashSet<String>();
					professioniDiscipline.put(disciplina.getProfessione().getNome(), disciplineForProf);
				}
				disciplineForProf.add(disciplina.getNome());
			}
			tableDisc = new PdfPTable(1);
			tableDisc.setWidthPercentage(100);
			tableDisc.setWidths(new float[]{1});
			tableDisc.setSpacingBefore(0);
			tableDisc.setSpacingAfter(0);
			for(String prof : professioniDiscipline.keySet()) {
				Set<String> discs = professioniDiscipline.get(prof);
				PdfPCell cell = new PdfPCell(new Phrase(prof, fontValoreCampo));
				//cell.setBorder(PdfPCell.NO_BORDER);
				cell.setBackgroundColor(BaseColor.GRAY);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPadding(cellPaddingSubTable);
				tableDisc.addCell(cell);
				for(String disc : discs) {
					cell = new PdfPCell(new Phrase(disc, fontValoreCampo));
					//cell.setBorder(PdfPCell.NO_BORDER);
					//cell.setBackgroundColor(BaseColor.GRAY);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setPadding(cellPaddingSubTable);
					tableDisc.addCell(cell);
				}
			}

		}
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), tableDisc, table, false);
	}

	private void addCellLabelCampoValore(String labelCampo, LocalDate valoreDateCampo, PdfPTable table) {
		String valoreCampo = null;
		if(valoreDateCampo != null)
			valoreCampo = valoreDateCampo.format(dateTimeFormatter);
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValore(String labelCampo, List<LocalDate> valoreDateCampo, PdfPTable table) {
		String valoreCampo = null;
		if(valoreDateCampo != null && valoreDateCampo.size() > 0) {
			valoreCampo = "";
			boolean write = false;
			for(LocalDate val : valoreDateCampo) {
				if(val != null) {
					if(write)
						valoreCampo += "\n";
					valoreCampo += val.format(dateTimeFormatter);
					write = true;
				}
			}
		}
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	//Mostra il toString() dell'enum
	private void addCellLabelCampoValoreEnum(String labelCampo, Enum valoreEnumCampo, PdfPTable table) {
		String valoreCampo = null;
		if(valoreEnumCampo != null)
			valoreCampo = valoreEnumCampo.toString();
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValoriString(String labelCampo, Collection<String> valoriCampo, PdfPTable table) {
		String valoreCampo = "";
		if(valoriCampo != null && valoriCampo.size() > 0) {
			boolean write = false;
			for(String val : valoriCampo) {
				if(write)
					valoreCampo += "\n";
				valoreCampo += val;
				write = true;
			}
		}
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValore(String labelCampo, Set<? extends INomeEnum> valoriEnumCampo, PdfPTable table) {
		String valoreCampo = "";
		if(valoriEnumCampo != null && valoriEnumCampo.size() > 0) {
			boolean write = false;
			for(INomeEnum printEnum : valoriEnumCampo) {
				if(write)
					valoreCampo += "\n";
				valoreCampo += printEnum.getNome();
				write = true;
			}
		}
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValore(String labelCampo, INomeEnum valoreEnumCampo, PdfPTable table) {
		String valoreCampo = null;
		if(valoreEnumCampo != null)
			valoreCampo = valoreEnumCampo.getNome();
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValore(String labelCampo, Float valoreFloatCampo, PdfPTable table) {
		String valoreCampo = null;
		if(valoreFloatCampo != null)
			valoreCampo = floatFormatter.print(valoreFloatCampo, Locale.getDefault());
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValoreZeroComeNonInserito(String labelCampo, Float valoreFloatCampo, PdfPTable table) {
		String valoreCampo = null;
		if(valoreFloatCampo != null && valoreFloatCampo.floatValue() != 0)
			valoreCampo = floatFormatter.print(valoreFloatCampo, Locale.getDefault());
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValore(String labelCampo, Integer valoreIntegerCampo, PdfPTable table) {
		String valoreCampo = null;
		if(valoreIntegerCampo != null)
			valoreCampo = intFormatter.print(valoreIntegerCampo, Locale.getDefault());
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValore(String labelCampo, Long valoreLongCampo, PdfPTable table) {
		String valoreCampo = null;
		if(valoreLongCampo != null)
			valoreCampo = longFormatter.print(valoreLongCampo, Locale.getDefault());
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValoreValuta(String labelCampo, BigDecimal valoreLongCampo, PdfPTable table) {
		String valoreCampo = null;
		if(valoreLongCampo != null)
			valoreCampo = valutaFormatter.print(valoreLongCampo, Locale.getDefault()) + "€";
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValore(String labelCampo, Boolean valoreBooleanCampo, PdfPTable table) {
		String valoreCampo = null;
		if(valoreBooleanCampo != null) {
			if(valoreBooleanCampo)
				valoreCampo = messageSource.getMessage("label.sì", null, Locale.getDefault());
			else
				valoreCampo = messageSource.getMessage("label.no", null, Locale.getDefault());
		}
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValore(String labelCampo, String valoreCampo, PdfPTable table) {
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

    private void addCellCampoValore(String nomeCampo, String valoreCampo, PdfPTable table) {
		PdfPCell cell = getCellLabel(nomeCampo);
		table.addCell(cell);
		if(valoreCampo == null || valoreCampo.isEmpty())
			cell = getCellValore(messageSource.getMessage("label.dato_non_inserito", null, Locale.getDefault()));
		else
			cell = getCellValore(valoreCampo);
		table.addCell(cell);
    }

    private PdfPCell getCellValore(String valoreCampo) {
    	PdfPCell cell = new PdfPCell(new Phrase(valoreCampo, fontValoreCampo));
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		cell.setPadding(cellPadding);
		//cell.setSpaceCharRatio(0);
		//cell.setPadding(5);
		//cell.setPaddingBottom(20);

//		Paragraph par = new Paragraph();
//        par.setAlignment(Element.ALIGN_JUSTIFIED);
//        par.setFont(fontNomeCampo);
//        par.add("valore");
//        par.setSpacingBefore(0);
//        par.setSpacingAfter(3);
//		cell.addElement(par);
		return cell;
    }

    private PdfPCell getCellLabel(String nomeCampo) {
    	PdfPCell cell = new PdfPCell(new Phrase(nomeCampo, fontNomeCampo));
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setPadding(cellPadding);
		return cell;
    }

    private PdfPCell getValoreEmptyCell() {
    	PdfPCell cell = new PdfPCell(new Phrase(""));
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		cell.setPadding(cellPadding);
		return cell;
    }

    private void addCellCampoValore(String nomeCampo, PdfPTable tableCampo, PdfPTable table, boolean tableInNewLine) {
		PdfPCell cell = getCellLabel(nomeCampo);
		table.addCell(cell);

		if(tableCampo == null)
			cell = getCellValore(messageSource.getMessage("label.dato_non_inserito", null, Locale.getDefault()));
			//cell = new PdfPCell(new Phrase(messageSource.getMessage("label.dato_non_inserito", null, Locale.getDefault()), fontValoreCampo));
		else {
			if(tableInNewLine) {
				cell = getValoreEmptyCell();
				table.addCell(cell);
				cell = getCellForTable(tableCampo);
				cell.setColspan(2);
			} else {
				cell = getCellForTable(tableCampo);
			}
		}
		table.addCell(cell);
    }

    private PdfPCell getCellForTable(PdfPTable table) {
    	PdfPCell cell = new PdfPCell(table);
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		cell.setPadding(cellPadding);
		//cell.setSpaceCharRatio(0);
		//cell.setPaddingBottom(20);
    	return cell;
    }

//	private void addCorpoParagraph(Document document, boolean addSpacingBefore, boolean addSpacingAfter, String testo) throws DocumentException  {
//		Paragraph par = new Paragraph();
//        par.setAlignment(Element.ALIGN_JUSTIFIED);
//        par.setFont(fontCorpo);
//        par.add(testo);
//        if(addSpacingBefore)
//        	par.setSpacingBefore(spacingBefore);
//        if(addSpacingAfter)
//        	par.setSpacingAfter(spacingAfter);
//
//        //Indentazione
//        //par.setIndentationLeft (18);
//        //par.setFirstLineIndent(-18);
//
//        //Interlinea OK
//        //parOggetto.setMultipliedLeading(5);
//        //Interlinea da provare
//        //parOggetto.setLeading(0F, 2F);
//        document.add(par);
//	}
//
//    private static ListItem getListItem(String testo, Font fontListItem) {
//        Paragraph parList = new Paragraph();
//        parList.setFont(fontListItem);
//        parList.add(testo);
//
//        //Da testare se richiesta indentazione diversa dal default
//        //parList.setIndentationLeft(40);
//        //parList.setFirstLineIndent(-18);
//
//        parList.setAlignment(Element.ALIGN_JUSTIFIED);
//        //parList.setSpacingAfter(spacingAfter);
//
//        ListItem listItem = new ListItem(parList);
//        //non serve a nulla
//        //listItem.setIndentationLeft(40);
//        return listItem;
//        //return new ListItem(parList);
//
//    }

}
