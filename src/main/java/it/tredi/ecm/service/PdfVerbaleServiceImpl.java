package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DelegatoValutazioneSulCampo;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.entity.VerbaleValutazioneSulCampo;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.pdf.FooterWithInfo;

@Service
public class PdfVerbaleServiceImpl implements PdfVerbaleService {
	private static Logger LOGGER = LoggerFactory.getLogger(PdfVerbaleServiceImpl.class);

	@Override
	public ByteArrayOutputStream creaOutputSteramPdfVerbale(Accreditamento accreditamento) throws Exception {
		ByteArrayOutputStream byteArrayOutputStreamPdf = new ByteArrayOutputStream();
	    writePdfVerbale(byteArrayOutputStreamPdf, accreditamento);
		return byteArrayOutputStreamPdf;
	}

	@Autowired private MessageSource messageSource;
	@Autowired private ValutazioneService valutazioneService;
	@Autowired private FieldValutazioneAccreditamentoService fieldValutazioneAccreditamentoService;

	//formatters
	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private DateTimeFormatter dataOraTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
	private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private NumberStyleFormatter intFormatter = new NumberStyleFormatter("0");
	private NumberStyleFormatter longFormatter = new NumberStyleFormatter("0");
	private NumberStyleFormatter floatFormatter = new NumberStyleFormatter("0.0#");
	private NumberStyleFormatter valutaFormatter = new NumberStyleFormatter("0.00");

    //tipi font
	private int sizeTitolo = 15;
	private int sizeParTitolo = 13;
	private int sizeNomeCampo = 11;
	private int sizeValoreCampo = 11;
	private int sizeTitoloSubTable = 9;
	private int sizeEtichettaSubTable = 8;
	private int sizeNomeCampoSubTable = 9;
	private int sizeValoreCampoSubTable = 9;
	private Font.FontFamily fontFamily = Font.FontFamily.TIMES_ROMAN;
	private Font fontTitolo = new Font(fontFamily, sizeTitolo, Font.BOLD);
	private Font fontParTitolo = new Font(fontFamily, sizeParTitolo, Font.BOLD);
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

	private final String LOGO_AZIENDA_ZERO = "LogoAziendaZero.png";

    Image imgCheck = null;
    Image imgRemove = null;
    Image imgQuestion = null;

	private void writePdfVerbale(OutputStream outputStream, Accreditamento accreditamento) throws Exception {
		if(accreditamento.getVerbaleValutazioneSulCampo() == null)
			throw new Exception("Nessun Verbale trovato!");

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            //Info documento
            document.addAuthor("Ecm");
            document.addCreationDate();
            document.addCreator("Ecm");
            document.addTitle("Valutazione formale della domanda di accreditamento standard del Provider: " + accreditamento.getProvider().getDenominazioneLegale());

            Image img = FooterWithInfo.createLogo(LOGO_AZIENDA_ZERO);
            if(img != null)
            	document.add(img);

            //TODO sostituire
        	writePdf(document, accreditamento);
        } catch (Exception ex) {
        	LOGGER.error("writePdfVerbale impossibile creare il pdf", ex);
            throw ex;
        } finally {
            if(document.isOpen())
            	document.close();
            try {
            	outputStream.close();
            } catch (IOException ex) {

            }
		}
	}

	private void writePdf(Document document, Accreditamento accreditamento) throws Exception {

        Object[] values = {accreditamento.getProvider().getCodiceIdentificativoUnivoco(), accreditamento.getProvider().getDenominazioneLegale()};
        VerbaleValutazioneSulCampo verbale = accreditamento.getVerbaleValutazioneSulCampo();
        Provider provider = accreditamento.getProvider();


        //primo paragrafo INFO
        Paragraph parTitolo = new Paragraph();
        parTitolo.setAlignment(Element.ALIGN_LEFT);
        parTitolo.setFont(fontTitolo);
        parTitolo.add(messageSource.getMessage("label.accreditamento_standard_verbale_visita_in_loco", values, Locale.getDefault()));
        document.add(parTitolo);

		PdfPTable tableFields = getTableFields();
		//addCellLabelCampoValoreData("label.data", verbale.getGiorno(), tableFields);
		addCellLabelCampoValoreDataOra("label.data_ora", verbale.getDataoraVisita(), tableFields);
		addCellLabelCampoValoreString("label.sede", verbale.getSede().getAddressNameFull(), tableFields);
		addCellLabelCampoValoreString("label.componente_crec_team_leader", verbale.getTeamLeader().getFullNameBase(), tableFields);
		addCellLabelCampoValoreString("label.osservatore_regionale", verbale.getOsservatoreRegionale().getFullNameBase(), tableFields);
		for(Account a : verbale.getComponentiSegreteria()) {
			addCellLabelCampoValoreStringWithParam("label.componente_segreteria_ecm", null, a.getFullNameBase(), tableFields);
		}
		if(verbale.getReferenteInformatico() != null)
			addCellLabelCampoValoreString("label.referente_informatico", verbale.getReferenteInformatico().getFullNameBase(), tableFields);
		if(verbale.getIsPresenteLegaleRappresentante() != null) {
			if(verbale.getIsPresenteLegaleRappresentante())
				addCellLabelCampoValoreLegaleRappr("label.sottoscrivente", accreditamento.getProvider().getLegaleRappresentante(), tableFields);
			else
				addCellLabelCampoValoreDelegato("label.sottoscrivente", verbale.getDelegato(), tableFields);
		}
		document.add(tableFields);

		document.add(Chunk.NEWLINE);

		//FONT AWESOME IMAGES
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String checkPath = classLoader.getResource("check.png").getPath();
        String removePath = classLoader.getResource("remove.png").getPath();
        String questionPath = classLoader.getResource("question.png").getPath();
        imgCheck = Image.getInstance(checkPath);
        imgCheck.scaleAbsolute(16f, 16f);
        imgRemove = Image.getInstance(removePath);
        imgRemove.scaleAbsolute(15f, 15f);
        imgQuestion = Image.getInstance(questionPath);
        imgQuestion.scaleAbsolute(15f, 15f);

		//secondo paragrafo VALUTAZIONI
        Valutazione valutazioneSulCampo = valutazioneService.getValutazioneSegreteriaForAccreditamentoIdNotStoricizzato(accreditamento.getId());
        Set<FieldValutazioneAccreditamento> valutazioniNonDefault = fieldValutazioneAccreditamentoService.getValutazioniNonDefault(valutazioneSulCampo);

        List<FieldValutazioneAccreditamento> verbaleVal = getOrderedFieldValutazioneBySubset(verbale.getDatiValutazioneSulCampo().getValutazioniSulCampo(), SubSetFieldEnum.VALUTAZIONE_SUL_CAMPO);
        if(!valutazioniNonDefault.isEmpty() && !verbaleVal.isEmpty()) {
    		Paragraph par2Titolo = new Paragraph();
    		par2Titolo.setAlignment(Element.ALIGN_LEFT);
            par2Titolo.setFont(fontTitolo);
            par2Titolo.add(messageSource.getMessage("label.esito_valutazioni", values, Locale.getDefault()));
            document.add(par2Titolo);

            document.add(Chunk.NEWLINE);

        }

        //LEGALE RAPPRESENTANTE
        List<FieldValutazioneAccreditamento> legaleRapprVal = getOrderedFieldValutazioneBySubset(valutazioniNonDefault, SubSetFieldEnum.LEGALE_RAPPRESENTANTE);
	    if(!legaleRapprVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.legale_rappresentante", null, Locale.getDefault()) + ": " + provider.getLegaleRappresentante().getAnagrafica().getFullName());
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(legaleRapprVal, table);
	        document.add(table);
        }

	    //DELEGATO LEGALE RAPPRESENTANTE
        List<FieldValutazioneAccreditamento> delegatoLegaleRapprVal = getOrderedFieldValutazioneBySubset(valutazioniNonDefault, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE);
	    if(!delegatoLegaleRapprVal.isEmpty() && provider.getDelegatoLegaleRappresentante() != null) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.delegato_legale_rappresentante", null, Locale.getDefault()) + ": " + provider.getDelegatoLegaleRappresentante().getAnagrafica().getFullName());
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(delegatoLegaleRapprVal, table);
	        document.add(table);
        }

	    //DATI ECONOMICI
        List<FieldValutazioneAccreditamento> datiEconomiciVal = getOrderedFieldValutazioneTipologiaFormativa(valutazioniNonDefault, IdFieldEnum.getDatiAccreditamentoSplitBySezione(2));
	    if(!datiEconomiciVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.dati_economici", null, Locale.getDefault()));
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(datiEconomiciVal, table);
	        document.add(table);
        }

	    //DATI STRUTTURA
        List<FieldValutazioneAccreditamento> datiStrutturaVal = getOrderedFieldValutazioneTipologiaFormativa(valutazioniNonDefault, IdFieldEnum.getDatiAccreditamentoSplitBySezione(3));
	    if(!datiStrutturaVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.dati_struttura", null, Locale.getDefault()));
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(datiStrutturaVal, table);
	        document.add(table);
        }

	    //RESPONSABILE SEGRETERIA
	    List<FieldValutazioneAccreditamento> respSegreteriaVal = getOrderedFieldValutazioneBySubset(valutazioniNonDefault, SubSetFieldEnum.RESPONSABILE_SEGRETERIA);
	    if(!respSegreteriaVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.responsabile_segreteria", null, Locale.getDefault()) + ": " + provider.getPersonaByRuolo(Ruolo.RESPONSABILE_SEGRETERIA).getAnagrafica().getFullName());
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(respSegreteriaVal, table);
	        document.add(table);
        }

	    //RESPONSABILE AMMINISTRATIVO
	    List<FieldValutazioneAccreditamento> respAmministrativoVal = getOrderedFieldValutazioneBySubset(valutazioniNonDefault, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO);
	    if(!respAmministrativoVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.responsabile_amministrativo", null, Locale.getDefault()) + ": " + provider.getPersonaByRuolo(Ruolo.RESPONSABILE_AMMINISTRATIVO).getAnagrafica().getFullName());
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(respAmministrativoVal, table);
	        document.add(table);
        }

	    //RESPONSABILE SISTEMA INFORMATICO
	    List<FieldValutazioneAccreditamento> respSistemaInfoVal = getOrderedFieldValutazioneBySubset(valutazioniNonDefault, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO);
	    if(!respSistemaInfoVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.responsabile_sistema_informatico", null, Locale.getDefault()) + ": " + provider.getPersonaByRuolo(Ruolo.RESPONSABILE_SISTEMA_INFORMATICO).getAnagrafica().getFullName());
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(respSistemaInfoVal, table);
	        document.add(table);
        }

	    //RESPONSABILE QUALITA
	    List<FieldValutazioneAccreditamento> respQualitaVal = getOrderedFieldValutazioneBySubset(valutazioniNonDefault, SubSetFieldEnum.RESPONSABILE_QUALITA);
	    if(!respQualitaVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.responsabile_qualita", null, Locale.getDefault()) + ": " + provider.getPersonaByRuolo(Ruolo.RESPONSABILE_QUALITA).getAnagrafica().getFullName());
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(respQualitaVal, table);
	        document.add(table);
        }

	    //COMPONENTI COMITATO SCIENTIFICO
        Paragraph parComitato = new Paragraph();
        parComitato.setAlignment(Element.ALIGN_LEFT);
        parComitato.setFont(fontParTitolo);
        parComitato.add(messageSource.getMessage("label.componenti_comitato_scientifico", null, Locale.getDefault()));
        document.add(parComitato);
        //COORDINATORE
        Persona coordinatore = accreditamento.getProvider().getCoordinatoreComitatoScientifico();
        if(coordinatore != null) {
	        List<FieldValutazioneAccreditamento> coordinatoreVal = getOrderedFieldValutazioneBySubsetAndObjectRef(valutazioniNonDefault, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, coordinatore.getId());
		    if(!coordinatoreVal.isEmpty()) {
		    	Object[] valuesCoordinatore = {coordinatore.getAnagrafica().getFullName()};
		        Paragraph par = new Paragraph();
		        par.setAlignment(Element.ALIGN_LEFT);
		        par.setFont(fontNomeCampo);
		        par.add(messageSource.getMessage("label.coordinatore_withName", valuesCoordinatore, Locale.getDefault()));
		        document.add(par);
		        PdfPTable table = getTableFieldsValutazione();
		        addTableValutazione(coordinatoreVal, table);
		        document.add(table);
	        }
        }
        //COMPONENTI
        for(Persona p : accreditamento.getProvider().getComponentiComitatoScientifico()) {
        	if(!p.isCoordinatoreComitatoScientifico()) {
                List<FieldValutazioneAccreditamento> componenteVal = getOrderedFieldValutazioneBySubsetAndObjectRef(valutazioniNonDefault, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, p.getId());
    		    if(!componenteVal.isEmpty()) {
    		    	Object[] valSede = {p.getAnagrafica().getFullName()};
    		        Paragraph par = new Paragraph();
    		        par.setAlignment(Element.ALIGN_LEFT);
    		        par.setFont(fontNomeCampo);
    		        par.add(messageSource.getMessage("label.componente_withName", valSede, Locale.getDefault()));
    		        document.add(par);
    		        PdfPTable table = getTableFieldsValutazione();
    		        addTableValutazione(componenteVal, table);
    		        document.add(table);
    	        }
        	}
        }

        //ALLEGATI
        List<FieldValutazioneAccreditamento> allegatiVal = getOrderedFieldValutazioneBySubset(valutazioniNonDefault, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO);
	    if(!allegatiVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.allegati", null, Locale.getDefault()));
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(allegatiVal, table);
	        document.add(table);
        }


	    //VALUTAZIONI SUL CAMPO
        if(!verbaleVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.valutazioni_sul_campo", null, Locale.getDefault()));
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(verbaleVal, table);
	        document.add(table);
        }

        document.add(Chunk.NEWLINE);

        //OSSERVAZIONI DEL TEAM DI VALUTAZIONE
        String osservazioniTeamValutazione =  verbale.getDatiValutazioneSulCampo().getOsservazioniTeamValutazione();
        if(osservazioniTeamValutazione != null && !osservazioniTeamValutazione.isEmpty()) {
        	Paragraph parNoteTeamTitolo = new Paragraph();
        	parNoteTeamTitolo.setAlignment(Element.ALIGN_LEFT);
        	parNoteTeamTitolo.setFont(fontParTitolo);
        	parNoteTeamTitolo.add(messageSource.getMessage("label.osservazioni_team_valutazione", null, Locale.getDefault()));
            document.add(parNoteTeamTitolo);

            Paragraph parNoteTeam = new Paragraph();
            parNoteTeam.setAlignment(Element.ALIGN_LEFT);
            parNoteTeam.setFont(fontValoreCampo);
            parNoteTeam.add(osservazioniTeamValutazione);
	        document.add(parNoteTeam);
        }

        document.add(Chunk.NEWLINE);

        //OSSERVAZIONI PROVIDER
        String osservazioniProvider =  verbale.getDatiValutazioneSulCampo().getOsservazioniDelProvider();
        if(osservazioniProvider != null && !osservazioniProvider.isEmpty()) {
        	Paragraph parNoteProviderTitolo = new Paragraph();
        	parNoteProviderTitolo.setAlignment(Element.ALIGN_LEFT);
        	parNoteProviderTitolo.setFont(fontParTitolo);
        	parNoteProviderTitolo.add(messageSource.getMessage("label.osservazioni_del_provider", null, Locale.getDefault()));
            document.add(parNoteProviderTitolo);

            Paragraph parNoteProvider = new Paragraph();
            parNoteProvider.setAlignment(Element.ALIGN_LEFT);
            parNoteProvider.setFont(fontValoreCampo);
            parNoteProvider.add(osservazioniProvider);
	        document.add(parNoteProvider);
        }

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        //ENDING
        Paragraph parFirma = new Paragraph();
        parFirma.setAlignment(Element.ALIGN_LEFT);
        parFirma.setFont(fontValoreCampo);
        parFirma.add(messageSource.getMessage("label.ending_verbale", null, Locale.getDefault()));
        document.add(parFirma);

        //FIRMA CRECM
        Paragraph parFirmaCrecm = new Paragraph();
        parFirmaCrecm.setAlignment(Element.ALIGN_LEFT);
        parFirmaCrecm.setFont(fontValoreCampo);
        String[] valuesFirmaCrecm = {messageSource.getMessage("label.componente_crecm", null, Locale.getDefault()), verbale.getTeamLeader().getFullNameBase()};
        parFirmaCrecm.add(messageSource.getMessage("label.firma_nomeFirmatario", valuesFirmaCrecm, Locale.getDefault()));
        document.add(parFirmaCrecm);

        //FIRMA OSSERVATORIO REGIONALE
        Paragraph parFirmaOssRegionale = new Paragraph();
        parFirmaOssRegionale.setAlignment(Element.ALIGN_LEFT);
        parFirmaOssRegionale.setFont(fontValoreCampo);
        String[] valuesFirmaOssRegionale = {messageSource.getMessage("label.componente_osservatorio_regionale", null, Locale.getDefault()), verbale.getOsservatoreRegionale().getFullNameBase()};
        parFirmaOssRegionale.add(messageSource.getMessage("label.firma_nomeFirmatario", valuesFirmaOssRegionale, Locale.getDefault()));
        document.add(parFirmaOssRegionale);

        //FIRMA COMPONENTI SEGRETERIA
        for(Account a : verbale.getComponentiSegreteria()) {
        	Paragraph parFirmaECM = new Paragraph();
        	parFirmaECM.setAlignment(Element.ALIGN_LEFT);
        	parFirmaECM.setFont(fontValoreCampo);
            String[] valuesFirmaECM = {messageSource.getMessage("label.componente_segreteria_ecm", null, Locale.getDefault()), a.getFullNameBase()};
            parFirmaECM.add(messageSource.getMessage("label.firma_nomeFirmatario", valuesFirmaECM, Locale.getDefault()));
            document.add(parFirmaECM);
        }

        //FIRMA LEGALE RAPPRESENTANTE
        if(verbale.getIsPresenteLegaleRappresentante()) {
        	Paragraph parFirmaLegale = new Paragraph();
        	parFirmaLegale.setAlignment(Element.ALIGN_LEFT);
        	parFirmaLegale.setFont(fontValoreCampo);
            String[] valuesFirmaLegale = {messageSource.getMessage("label.legale_rappresentante_del_provider", null, Locale.getDefault()), accreditamento.getProvider().getLegaleRappresentante().getAnagrafica().getFullName()};
            parFirmaLegale.add(messageSource.getMessage("label.firma_nomeFirmatario", valuesFirmaLegale, Locale.getDefault()));
            document.add(parFirmaLegale);
        }
        else {
        	Paragraph parFirmaDelegato = new Paragraph();
        	parFirmaDelegato.setAlignment(Element.ALIGN_LEFT);
        	parFirmaDelegato.setFont(fontValoreCampo);
            String[] valuesFirmaDelegato = {messageSource.getMessage("label.delegato_legale_rappresentante_del_provider", null, Locale.getDefault()), verbale.getDelegato().getNome() + verbale.getDelegato().getCognome()};
            parFirmaDelegato.add(messageSource.getMessage("label.firma_nomeFirmatario", valuesFirmaDelegato, Locale.getDefault()));
            document.add(parFirmaDelegato);
        }


	}


	private void addCellLabelCampoValoreDelegato(String labelCampo, DelegatoValutazioneSulCampo delegato, PdfPTable table) {
		if(delegato != null)
			addCellLabelCampoValoreString(labelCampo, delegato.getNome() + " " + delegato.getCognome(), table);
	}

	private void addCellLabelCampoValoreLegaleRappr(String labelCampo, Persona legaleRappresentante, PdfPTable table) {
		addCellLabelCampoValoreString(labelCampo, legaleRappresentante.getAnagrafica().getNome() + " " + legaleRappresentante.getAnagrafica().getCognome(), table);
	}

	private void addCellLabelCampoValoreStringWithParam(String labelCampo, Object[] counter, String valoreCampo, PdfPTable table) {
		addCellCampoValore(messageSource.getMessage(labelCampo, counter, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValoreData(String labelCampo, LocalDate valoreDateCampo, PdfPTable table) {
		String valoreCampo = null;
		if(valoreDateCampo != null)
			valoreCampo = valoreDateCampo.format(dateTimeFormatter);
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValoreOra(String labelCampo, LocalTime valoreOraCampo, PdfPTable table) {
		String valoreCampo = null;
		if(valoreOraCampo != null)
			valoreCampo = valoreOraCampo.format(timeFormatter);
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValoreDataOra(String labelCampo, LocalDateTime valoreDataOraCampo, PdfPTable table) {
		String valoreCampo = null;
		if(valoreDataOraCampo != null)
			valoreCampo = valoreDataOraCampo.format(dataOraTimeFormatter);
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValoreString(String labelCampo, String valoreCampo, PdfPTable table) {
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

	private PdfPCell getCellLabel(String nomeCampo) {
    	PdfPCell cell = new PdfPCell(new Phrase(nomeCampo, fontNomeCampo));
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setPadding(cellPadding);
		return cell;
    }

	private PdfPCell getCellValore(String valoreCampo) {
    	PdfPCell cell = new PdfPCell(new Phrase(valoreCampo, fontValoreCampo));
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		cell.setPadding(cellPadding);
		return cell;
    }

	private PdfPTable getTableFields() throws DocumentException {
		PdfPTable tableFields = new PdfPTable(2);
		tableFields.setWidthPercentage(100);
		tableFields.setWidths(new float[]{1, 3});
		tableFields.setSpacingBefore(spacingBefore);
		tableFields.setSpacingAfter(spacingAfter);
		return tableFields;
	}

	private PdfPTable getTableFieldsValutazione() throws DocumentException {
		PdfPTable tableFields = new PdfPTable(4);
		tableFields.setWidthPercentage(100);
		tableFields.setWidths(new float[]{0.5f, 2, 0.8f, 4});
		tableFields.setSpacingBefore(spacingBefore);
		tableFields.setSpacingAfter(spacingAfter);
		addCellIntestaSubTableByString(messageSource.getMessage("label.id", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.descrizione", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.valutazione", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.note", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		return tableFields;
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

	private void addTableValutazione(List<FieldValutazioneAccreditamento> orderedVal, PdfPTable table) throws Exception {
		for(FieldValutazioneAccreditamento fva : orderedVal) {
			addCellSubTable(intFormatter.print(fva.getIdField().getIdEcm(), Locale.getDefault()), table);
			addCellSubTable(messageSource.getMessage("IdFieldEnum_valutazione." + fva.getIdField().name(), null, Locale.getDefault()), table);
			addCellSubTable(getIconForValutazione(fva.getEsito()), table);
			addCellSubTable(getNoteForValutazione(fva.getNote()), table);
		}
	}

	private void addCellSubTable(String valoreCampo, PdfPTable table) {
		addCellSubTable(valoreCampo, table, null, true, null, false);
	}

	private void addCellSubTable(Image valoreCampo, PdfPTable table) {
		addCellSubTable(valoreCampo, table, null, true, null, false);
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

	private void addCellSubTable(Image img, PdfPTable table, BaseColor baseColor, boolean border, Integer elementAlign, boolean bold) {
		//boolean scala l'immagine
		PdfPCell cell = new PdfPCell(img, false);
		if(!border)
			cell.setBorder(PdfPCell.NO_BORDER);
		if(baseColor != null)
			cell.setBackgroundColor(baseColor);
		if(elementAlign != null)
			cell.setHorizontalAlignment(elementAlign);
		else {
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		}
		cell.setPadding(cellPaddingSubTable);
		table.addCell(cell);
	}

	private Image getIconForValutazione(Boolean esito) throws Exception {
		if(imgQuestion == null || imgCheck == null || imgRemove == null)
			throw new Exception("Imgages not initialized");
		if(esito == null)
			return imgQuestion;
		if(esito.booleanValue() == true)
			return imgCheck;
		if(esito.booleanValue() == false)
			return imgRemove;
		else
			throw new Exception("Error in finding icon");
	}

	private String getNoteForValutazione(String note) {
		if(note == null)
			return " ";
		else return note;
	}

	private List<FieldValutazioneAccreditamento> getOrderedFieldValutazioneBySubset(Set<FieldValutazioneAccreditamento> valutazioni, SubSetFieldEnum subset) {
		List<FieldValutazioneAccreditamento> result = new ArrayList<FieldValutazioneAccreditamento>();
		for(FieldValutazioneAccreditamento fva : valutazioni) {
			if(fva.getIdField().getSubSetField() == subset  && fva.getIdField().getIdEcm() != -1)
				result.add(fva);
		}
		result.sort((fva1, fva2) -> Integer.compare(fva1.getIdField().getIdEcm(), fva2.getIdField().getIdEcm()));
		return result;
	}

	private List<FieldValutazioneAccreditamento> getOrderedFieldValutazioneBySubsetAndObjectRef(Set<FieldValutazioneAccreditamento> valutazioni, SubSetFieldEnum subset, Long id) {
		List<FieldValutazioneAccreditamento> result = new ArrayList<FieldValutazioneAccreditamento>();
		for(FieldValutazioneAccreditamento fva : valutazioni) {
			if(fva.getIdField().getSubSetField() == subset && fva.getObjectReference() == id.longValue() && fva.getIdField().getIdEcm() != -1)
				result.add(fva);
		}
		result.sort((fva1, fva2) -> Integer.compare(fva1.getIdField().getIdEcm(), fva2.getIdField().getIdEcm()));
		return result;
	}

	private List<FieldValutazioneAccreditamento> getOrderedFieldValutazioneTipologiaFormativa(Set<FieldValutazioneAccreditamento> valutazioni, Set<IdFieldEnum> setIdField) {
		List<FieldValutazioneAccreditamento> result = new ArrayList<FieldValutazioneAccreditamento>();
		for(FieldValutazioneAccreditamento fva : valutazioni) {
			if(setIdField.contains(fva.getIdField()))
				result.add(fva);
		}
		result.sort((fva1, fva2) -> Integer.compare(fva1.getIdField().getIdEcm(), fva2.getIdField().getIdEcm()));
		return result;
	}


}
