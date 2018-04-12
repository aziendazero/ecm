package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.DatiEconomici;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.EventoPianoFormativo;
import it.tredi.ecm.dao.entity.FieldEditabileAccreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.INomeEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;

@Service
public class PdfRiepiloghiServiceImpl implements PdfRiepiloghiService {
	private static Logger LOGGER = LoggerFactory.getLogger(PdfRiepiloghiServiceImpl.class);

	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private ProviderService providerService;
	@Autowired private ValutazioneService valutazioneService;
	@Autowired private MessageSource messageSource;
	//@Autowired private FieldIntegrazioneAccreditamentoService fieldIntegrazioneAccreditamentoService;
	@Autowired private FieldEditabileAccreditamentoService fieldEditabileAccreditamentoService;

	//formatters
	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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

    Image imgCheck = null;
    Image imgRemove = null;
    Image imgQuestion = null;
    Image imgIntegrazioneAbilitata = null;


	@Override
	public ByteArrayOutputStream creaOutputStreamPdfRiepilogoDomanda(Long accreditamentoId, String argument, Long valutazioneId) throws Exception {
		LOGGER.debug("Inizio procedura scrittura PDF del riepilogo della Domanda: " + accreditamentoId);

		Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
		Set<FieldEditabileAccreditamento> fieldsEditabileAccreditamento = null;
		if("riepilogovariazionedati".equals(argument)) {
			//AccreditamentoStatoEnum stato = accreditamento.getStatoUltimaIntegrazione();
			fieldsEditabileAccreditamento = fieldEditabileAccreditamentoService.getAllFieldEditabileForAccreditamento(accreditamentoId);
		}

		ByteArrayOutputStream byteArrayOutputStreamPdf = new ByteArrayOutputStream();
        writePdfRiepilogo(byteArrayOutputStreamPdf, accreditamento, argument, valutazioneId, fieldsEditabileAccreditamento);

        return byteArrayOutputStreamPdf;
	}

	private void writePdfRiepilogo(OutputStream outputStream, Accreditamento accreditamento, String argument, Long valutazioneId, Set<FieldEditabileAccreditamento> fieldsEditabileAccreditamento) throws Exception {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            //Info documento
            document.addAuthor("Ecm");
            document.addCreationDate();
            document.addCreator("Ecm");

            switch(argument) {
            case "domanda":
            	document.addTitle("Riepilogo della Domanda di Accreditamento " + accreditamento.getId());
            	writePdfRiepilogoDomanda(document, accreditamento, false, fieldsEditabileAccreditamento);
            	break;
            case "riepilogovariazionedati":
            	document.addTitle("Riepilogo della variazione dati della Domanda di Accreditamento " + accreditamento.getId());
            	writePdfRiepilogoDomanda(document, accreditamento, true, fieldsEditabileAccreditamento);
            	break;
            case "pianoFormativo":
            	document.addTitle("Riepilogo del Piano Formativo della Domanda di Accreditamento " + accreditamento.getId());
            	writePdfRiepilogoPianoFormativoDomanda(document, accreditamento);
            	break;
            case "valutazione":
            	document.addTitle("Riepilogo della Valutazione della Domanda di Accreditamento " + accreditamento.getId());
            	writePdfRiepilogoValutazioneDomanda(document, accreditamento, valutazioneId);
            	break;
            }
        } catch (Exception e) {
        	LOGGER.error("Impossibile creare il PDF " + argument + " della Domanda " + accreditamento.getId(), e);
            throw e;
        } finally {
            if(document.isOpen())
            	document.close();
            try {
            	outputStream.close();
            } catch (IOException ex) {}
		}
	}

	private boolean integrazioneAbilitata(boolean showIntegrazioneInfo, Set<FieldEditabileAccreditamento> fieldsEditabileAccreditamento, IdFieldEnum idFieldEnum, Long objectReference) {
		if(showIntegrazioneInfo) {
			for(FieldEditabileAccreditamento fEdit : fieldsEditabileAccreditamento) {
				if(fEdit.getIdField() == idFieldEnum) {
					if(objectReference == null || objectReference == fEdit.getObjectReference()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Chiamato sia per il riepilogo della domanda sia per il riepilogo della domanda in variazione dati con le info sui campi da integrare
	 * @param document
	 * @param accreditamento
	 * @param fieldsIntegrazioneAccreditamento
	 * @throws DocumentException
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	private void writePdfRiepilogoDomanda(Document document, Accreditamento accreditamento, boolean showIntegrazioneInfo, Set<FieldEditabileAccreditamento> fieldsEditabileAccreditamento) throws DocumentException, MalformedURLException, IOException {
		Provider provider = accreditamento.getProvider();
		DatiAccreditamento dati = accreditamento.getDatiAccreditamento();
		DatiEconomici datiEconomici = dati.getDatiEconomici();

		if(showIntegrazioneInfo) {
	        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	        String imgPath = classLoader.getResource("IntegrazioneAbilitata.png").getPath();
	        imgIntegrazioneAbilitata = Image.getInstance(imgPath);
	        imgIntegrazioneAbilitata.scaleAbsolute(16f, 16f);
		}
		
		//TITOLO
		Object[] values = {accreditamento.getTipoDomanda().getNome(), provider.getDenominazioneLegale(), longFormatter.print(provider.getId(), Locale.getDefault())};
        Paragraph parTitolo = new Paragraph();
        parTitolo.setAlignment(Element.ALIGN_LEFT);
        parTitolo.setFont(fontTitolo);
        parTitolo.add(messageSource.getMessage("label.riepilogo_domandaTipo_providerDenominazione_providerId", values, Locale.getDefault()));
        document.add(parTitolo);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        
        boolean inIntegrazione = false;

        //INFORMAZIONI DEL PROVIDER
        Paragraph parInfoProvider = new Paragraph();
        parInfoProvider.setAlignment(Element.ALIGN_LEFT);
        parInfoProvider.setFont(fontParTitolo);
        parInfoProvider.add(messageSource.getMessage("label.info_provider", null, Locale.getDefault()));
        document.add(parInfoProvider);
        PdfPTable providerFields = getTableFields(showIntegrazioneInfo);
        addCellLabelCampoValore("label.tipo_organizzatore", provider.getTipoOrganizzatore().getNome(), providerFields, showIntegrazioneInfo, 
        		integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.PROVIDER__TIPO_ORGANIZZATORE, null));
		addCellLabelCampoValore("label.denominazione_legale", provider.getDenominazioneLegale(), providerFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.PROVIDER__DENOMINAZIONE_LEGALE, null));
		addCellLabelCampoValore("label.partita_iva", provider.getPartitaIva(), providerFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.PROVIDER__PARTITA_IVA, null));
		addCellLabelCampoValore("label.codice_fiscale", provider.getCodiceFiscale(), providerFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.PROVIDER__CODICE_FISCALE, null));
		addCellLabelCampoValoreEnum("label.ragione_sociale", provider.getRagioneSociale(), providerFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.PROVIDER__RAGIONE_SOCIALE, null));
		addCellLabelCampoValore("label.email", provider.getEmailStruttura(), providerFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.PROVIDER__EMAIL_STRUTTURA, null));
		addCellLabelCampoValore("label.natura_organizzazione", provider.getNaturaOrganizzazione(), providerFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.PROVIDER__NATURA_ORGANIZZAZIONE, null));
		String noProfit = provider.isNoProfit() ? (messageSource.getMessage("label.sì", null, Locale.getDefault())) : (messageSource.getMessage("label.no", null, Locale.getDefault()));
		addCellLabelCampoValore("label.no_profit", noProfit, providerFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.PROVIDER__NO_PROFIT, null));
		document.add(providerFields);

		document.add(Chunk.NEWLINE);

		//LEGALE RAPPRESENTANTE
		Paragraph parLegaleRappresentante = new Paragraph();
		parLegaleRappresentante.setAlignment(Element.ALIGN_LEFT);
		parLegaleRappresentante.setFont(fontParTitolo);
		parLegaleRappresentante.add(messageSource.getMessage("label.legale_rappresentante", null, Locale.getDefault()));
        document.add(parLegaleRappresentante);
        PdfPTable legaleFields = getTableFields(showIntegrazioneInfo);
        addCellLabelIntegrazioneSostAggRimoz("label.sostituisci_anagrafica", legaleFields, showIntegrazioneInfo, 
        		integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.LEGALE_RAPPRESENTANTE__FULL, null));
        addAllCellsPersonaByRuolo(provider.getLegaleRappresentante(), legaleFields, Ruolo.LEGALE_RAPPRESENTANTE, showIntegrazioneInfo, fieldsEditabileAccreditamento);
        document.add(legaleFields);

        document.add(Chunk.NEWLINE);

        //DELEGATO LEGALE RAPPRESENTANTE
        if(provider.getDelegatoLegaleRappresentante() != null) {
        	Paragraph parDelegatoLegaleRappresentante = new Paragraph();
        	parDelegatoLegaleRappresentante.setAlignment(Element.ALIGN_LEFT);
        	parDelegatoLegaleRappresentante.setFont(fontParTitolo);
        	parDelegatoLegaleRappresentante.add(messageSource.getMessage("label.delegato_legale_rappresentante", null, Locale.getDefault()));
            document.add(parDelegatoLegaleRappresentante);
            PdfPTable delegatoLegaleFields = getTableFields(showIntegrazioneInfo);
            addCellLabelIntegrazioneSostAggRimoz("label.sostituisci_anagrafica", delegatoLegaleFields, showIntegrazioneInfo, 
            		integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__FULL, null));
            addAllCellsPersonaByRuolo(provider.getDelegatoLegaleRappresentante(), delegatoLegaleFields, Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, showIntegrazioneInfo, fieldsEditabileAccreditamento);
            document.add(delegatoLegaleFields);

            document.add(Chunk.NEWLINE);
        }

        //SEDI DEL PROVIDER
        Paragraph parSediProvider = new Paragraph();
        parSediProvider.setAlignment(Element.ALIGN_LEFT);
        parSediProvider.setFont(fontParTitolo);
        parSediProvider.add(messageSource.getMessage("label.sedi_provider", null, Locale.getDefault()));
        document.add(parSediProvider);
        if(showIntegrazioneInfo) {
	        PdfPTable sediAddRemove = getTableFields(showIntegrazioneInfo);
	        addCellLabelIntegrazioneSostAggRimoz("label.modifica_sedi", sediAddRemove, showIntegrazioneInfo, 
	        		integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.SEDE__FULL, null));
	        document.add(sediAddRemove);
        }
        //SEDE LEGALE
        Paragraph parSedeLegale = new Paragraph();
        parSedeLegale.setAlignment(Element.ALIGN_LEFT);
        parSedeLegale.setFont(fontNomeCampo);
        parSedeLegale.add(messageSource.getMessage("label.sede_legale", null, Locale.getDefault()));
        document.add(parSedeLegale);
        PdfPTable sedeLegaleFields = getTableFields(showIntegrazioneInfo);
        addAllCellsSede(provider.getSedeLegale(), sedeLegaleFields, showIntegrazioneInfo, fieldsEditabileAccreditamento);
        document.add(sedeLegaleFields);
        //SEDI OPERATIVE
        for(Sede s : provider.getSedi()) {
        	if(!s.isSedeLegale()) {
        		Paragraph parSedeOperativa = new Paragraph();
        		parSedeOperativa.setAlignment(Element.ALIGN_LEFT);
        		parSedeOperativa.setFont(fontNomeCampo);
        		parSedeOperativa.add(messageSource.getMessage("label.sede_operativa", null, Locale.getDefault()));
                document.add(parSedeOperativa);
                PdfPTable sedeOperativaFields = getTableFields(showIntegrazioneInfo);
                addAllCellsSede(s, sedeOperativaFields, showIntegrazioneInfo, fieldsEditabileAccreditamento);
                document.add(sedeOperativaFields);
        	}
        }

        document.add(Chunk.NEWLINE);

        //TIPOLOGIA FORMATIVA
        Paragraph parTipologiaFormativa = new Paragraph();
        parTipologiaFormativa.setAlignment(Element.ALIGN_LEFT);
        parTipologiaFormativa.setFont(fontParTitolo);
        parTipologiaFormativa.add(messageSource.getMessage("label.tipologia_formativa", null, Locale.getDefault()));
        document.add(parTipologiaFormativa);
        PdfPTable tipologiaFormativaFields = getTableFields(showIntegrazioneInfo);
        addCellLabelCampoValore("label.tipologiaAccreditamento", dati.getTipologiaAccreditamento(), tipologiaFormativaFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__TIPOLOGIA_ACCREDITAMENTO, null));
        addCellLabelCampoValore("label.procedure_formative_tipologia", dati.getProcedureFormative(), tipologiaFormativaFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__PROCEDURE_FORMATIVE, null));
        addCellLabelCampoValore("label.professioniAccreditamento", dati.getProfessioniAccreditamento(), tipologiaFormativaFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__PROFESSIONI_ACCREDITAMENTO, null));
        if(dati.getProfessioniAccreditamento().equals("Generale")) {
        	addCellLabelCampoLabel("label.professioni_discipline", "label.tutte_le_professioni", tipologiaFormativaFields, showIntegrazioneInfo, 
    				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__DISCIPLINE, null));
        	
        } else {
        	addCellLabelCampoValoreDiscipline("label.professioni_discipline", dati.getDiscipline(), tipologiaFormativaFields, showIntegrazioneInfo, 
    				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__DISCIPLINE, null));
        }
        document.add(tipologiaFormativaFields);

        document.add(Chunk.NEWLINE);

        //DATI ECONOMICI
        File estrattoBilancioComplessivo = null;
		File estrattoBilancioFormazione = null;
		File organigramma = null;
		File funzionigramma = null;
		for(File f : dati.getFiles()) {
			switch(f.getTipo()) {
			case FILE_ESTRATTO_BILANCIO_COMPLESSIVO:
				estrattoBilancioComplessivo = f;
				break;
			case FILE_ESTRATTO_BILANCIO_FORMAZIONE:
				estrattoBilancioFormazione = f;
				break;
			case FILE_ORGANIGRAMMA:
				organigramma = f;
				break;
			case FILE_FUNZIONIGRAMMA:
				funzionigramma = f;
				break;
			default:
				break;
			}
		}
        Paragraph parDatiEconomici = new Paragraph();
        parDatiEconomici.setAlignment(Element.ALIGN_LEFT);
        parDatiEconomici.setFont(fontParTitolo);
        parDatiEconomici.add(messageSource.getMessage("label.dati_economici", null, Locale.getDefault()));
        document.add(parDatiEconomici);
        //FATTURATO COMPLESSIVO
        Paragraph parFatturatoComplessivo = new Paragraph();
        parFatturatoComplessivo.setAlignment(Element.ALIGN_LEFT);
        parFatturatoComplessivo.setFont(fontNomeCampo);
        parFatturatoComplessivo.add(messageSource.getMessage("label.fatturato_complessivo_title", null, Locale.getDefault()));
        document.add(parFatturatoComplessivo);
        PdfPTable fatturatoComplessivoFields = getTableFields(showIntegrazioneInfo);
        if(datiEconomici.getFatturatoComplessivoValoreUno() != null) {
        	addCellValoreCampoValoreValuta(Integer.toString(datiEconomici.getFatturatoComplessivoAnnoUno()), datiEconomici.getFatturatoComplessivoValoreUno(), fatturatoComplessivoFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_UNO, null));
        } else {
        	addCellValoreCampoValore(Integer.toString(datiEconomici.getFatturatoComplessivoAnnoUno()), "--", fatturatoComplessivoFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_UNO, null));
        }
        if(datiEconomici.getFatturatoComplessivoValoreDue() != null) {
        	addCellValoreCampoValoreValuta(Integer.toString(datiEconomici.getFatturatoComplessivoAnnoDue()), datiEconomici.getFatturatoComplessivoValoreDue(), fatturatoComplessivoFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_DUE, null));
        } else {
        	addCellValoreCampoValore(Integer.toString(datiEconomici.getFatturatoComplessivoAnnoDue()), "--", fatturatoComplessivoFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_DUE, null));
        }
        if(datiEconomici.getFatturatoComplessivoValoreTre() != null) {
        	addCellValoreCampoValoreValuta(Integer.toString(datiEconomici.getFatturatoComplessivoAnnoTre()), datiEconomici.getFatturatoComplessivoValoreTre(), fatturatoComplessivoFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_TRE, null));
        } else {
        	addCellValoreCampoValore(Integer.toString(datiEconomici.getFatturatoComplessivoAnnoTre()), "--", fatturatoComplessivoFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_TRE, null));
        }
    	if(estrattoBilancioComplessivo != null) {
			addCellLabelCampoValore("label.estrattoBilancioComplessivo", estrattoBilancioComplessivo.getNomeFile(), fatturatoComplessivoFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_COMPLESSIVO, null));
    	} else {
			addCellLabelCampoValore("label.estrattoBilancioComplessivo", getLabelAllegatoNonInserito(), fatturatoComplessivoFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_COMPLESSIVO, null));
    	}
    	document.add(fatturatoComplessivoFields);
    	//FATTURATO FORMAZIONE
    	Paragraph parFatturatoFormazione = new Paragraph();
    	parFatturatoFormazione.setAlignment(Element.ALIGN_LEFT);
    	parFatturatoFormazione.setFont(fontNomeCampo);
    	parFatturatoFormazione.add(messageSource.getMessage("label.fatturato_formazione_title", null, Locale.getDefault()));
        document.add(parFatturatoFormazione);
        PdfPTable fatturatoFormazioneFields = getTableFields(showIntegrazioneInfo);
        if(datiEconomici.getFatturatoFormazioneValoreUno() != null) {
        	addCellValoreCampoValoreValuta(Integer.toString(datiEconomici.getFatturatoFormazioneAnnoUno()), datiEconomici.getFatturatoFormazioneValoreUno(), fatturatoFormazioneFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_UNO, null));
        } else {
        	addCellValoreCampoValore(Integer.toString(datiEconomici.getFatturatoFormazioneAnnoUno()), "--", fatturatoFormazioneFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_UNO, null));
        }
        if(datiEconomici.getFatturatoFormazioneValoreDue() != null) {
        	addCellValoreCampoValoreValuta(Integer.toString(datiEconomici.getFatturatoFormazioneAnnoDue()), datiEconomici.getFatturatoFormazioneValoreDue(), fatturatoFormazioneFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_DUE, null));
        } else {
        	addCellValoreCampoValore(Integer.toString(datiEconomici.getFatturatoFormazioneAnnoDue()), "--", fatturatoFormazioneFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_DUE, null));
        }
        if(datiEconomici.getFatturatoFormazioneValoreTre() != null) {
        	addCellValoreCampoValoreValuta(Integer.toString(datiEconomici.getFatturatoFormazioneAnnoTre()), datiEconomici.getFatturatoFormazioneValoreTre(), fatturatoFormazioneFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_TRE, null));
        } else {
        	addCellValoreCampoValore(Integer.toString(datiEconomici.getFatturatoFormazioneAnnoTre()), "--", fatturatoFormazioneFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_TRE, null));
        }
    	if(estrattoBilancioFormazione != null) {
			addCellLabelCampoValore("label.estrattoBilancioFormazione", estrattoBilancioFormazione.getNomeFile(), fatturatoFormazioneFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_FORMAZIONE, null));
    	} else {
			addCellLabelCampoValore("label.estrattoBilancioFormazione", getLabelAllegatoNonInserito(), fatturatoFormazioneFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_FORMAZIONE, null));
    	}
    	document.add(fatturatoFormazioneFields);

    	document.add(Chunk.NEWLINE);

    	//DATI DELLA STRUTTURA
    	Paragraph parDatiStruttura = new Paragraph();
    	parDatiStruttura.setAlignment(Element.ALIGN_LEFT);
    	parDatiStruttura.setFont(fontParTitolo);
    	parDatiStruttura.add(messageSource.getMessage("label.dati_struttura", null, Locale.getDefault()));
        document.add(parDatiStruttura);
        //NUMERO DIPENDENTI
        Paragraph parNumeroDipendenti = new Paragraph();
        parNumeroDipendenti.setAlignment(Element.ALIGN_LEFT);
        parNumeroDipendenti.setFont(fontNomeCampo);
        parNumeroDipendenti.add(messageSource.getMessage("label.numero_dipendenti_title", null, Locale.getDefault()));
        document.add(parNumeroDipendenti);
        PdfPTable dipendentiFields = getTableFields(showIntegrazioneInfo);
        if(dati.getNumeroDipendentiFormazioneTempoIndeterminato() != null) {
        	addCellLabelCampoValore("label.tempo_indeterminato", intFormatter.print(dati.getNumeroDipendentiFormazioneTempoIndeterminato(), Locale.getDefault()), dipendentiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_TEMPO_INDETERMINATO, null));
        } else {
        	addCellLabelCampoValore("label.tempo_indeterminato", "--", dipendentiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_TEMPO_INDETERMINATO, null));
        }
        if(dati.getNumeroDipendentiFormazioneAltro() != null) {
        	addCellLabelCampoValore("label.altro_personale", intFormatter.print(dati.getNumeroDipendentiFormazioneAltro(), Locale.getDefault()), dipendentiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_ALTRO, null));
        } else {
        	addCellLabelCampoValore("label.altro_personale", "--", dipendentiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_ALTRO, null));
        }
    	if(organigramma != null) {
			addCellLabelCampoValore("label.organigramma", organigramma.getNomeFile(), dipendentiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__ORGANIGRAMMA, null));
    	} else {
			addCellLabelCampoValore("label.organigramma", getLabelAllegatoNonInserito(), dipendentiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__ORGANIGRAMMA, null));
    	}
    	if(funzionigramma != null) {
			addCellLabelCampoValore("label.funzionigramma", funzionigramma.getNomeFile(), dipendentiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__FUNZIONIGRAMMA, null));
    	} else {
			addCellLabelCampoValore("label.funzionigramma", getLabelAllegatoNonInserito(), dipendentiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.DATI_ACCREDITAMENTO__FUNZIONIGRAMMA, null));
    	}
    	document.add(dipendentiFields);

    	document.add(Chunk.NEWLINE);

    	//RESPONSABILE SEGRETERIA
		Paragraph parResponsabileSegreteria = new Paragraph();
		parResponsabileSegreteria.setAlignment(Element.ALIGN_LEFT);
		parResponsabileSegreteria.setFont(fontParTitolo);
		parResponsabileSegreteria.add(messageSource.getMessage("label.responsabile_segreteria", null, Locale.getDefault()));
        document.add(parResponsabileSegreteria);
        PdfPTable respSegreteriaFields = getTableFields(showIntegrazioneInfo);
        addCellLabelIntegrazioneSostAggRimoz("label.sostituisci_anagrafica", respSegreteriaFields, showIntegrazioneInfo, 
        		integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.RESPONSABILE_SEGRETERIA__FULL, null));
        addAllCellsPersonaByRuolo(provider.getPersonaByRuolo(Ruolo.RESPONSABILE_SEGRETERIA), respSegreteriaFields, Ruolo.RESPONSABILE_SEGRETERIA, showIntegrazioneInfo, fieldsEditabileAccreditamento);
        document.add(respSegreteriaFields);

        document.add(Chunk.NEWLINE);

        //RESPONSABILE AMMINISTRATIVO
		Paragraph parResponsabileAmministrativo = new Paragraph();
		parResponsabileAmministrativo.setAlignment(Element.ALIGN_LEFT);
		parResponsabileAmministrativo.setFont(fontParTitolo);
		parResponsabileAmministrativo.add(messageSource.getMessage("label.responsabile_amministrativo", null, Locale.getDefault()));
	    document.add(parResponsabileAmministrativo);
	    PdfPTable respAmministrativoFields = getTableFields(showIntegrazioneInfo);
        addCellLabelIntegrazioneSostAggRimoz("label.sostituisci_anagrafica", respAmministrativoFields, showIntegrazioneInfo, 
        		integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__FULL, null));
	    addAllCellsPersonaByRuolo(provider.getPersonaByRuolo(Ruolo.RESPONSABILE_AMMINISTRATIVO), respAmministrativoFields, Ruolo.RESPONSABILE_AMMINISTRATIVO, showIntegrazioneInfo, fieldsEditabileAccreditamento);
	    document.add(respAmministrativoFields);

	    document.add(Chunk.NEWLINE);

	    //RESPONSABILE SISTEMA INFORMATICO
		Paragraph parResponsabileSistemaInformatico = new Paragraph();
		parResponsabileSistemaInformatico.setAlignment(Element.ALIGN_LEFT);
		parResponsabileSistemaInformatico.setFont(fontParTitolo);
		parResponsabileSistemaInformatico.add(messageSource.getMessage("label.responsabile_sistema_informatico", null, Locale.getDefault()));
	    document.add(parResponsabileSistemaInformatico);
	    PdfPTable respSistemaInformaticoFields = getTableFields(showIntegrazioneInfo);
        addCellLabelIntegrazioneSostAggRimoz("label.sostituisci_anagrafica", respSistemaInformaticoFields, showIntegrazioneInfo, 
        		integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__FULL, null));
	    addAllCellsPersonaByRuolo(provider.getPersonaByRuolo(Ruolo.RESPONSABILE_SISTEMA_INFORMATICO), respSistemaInformaticoFields, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, showIntegrazioneInfo, fieldsEditabileAccreditamento);
	    document.add(respSistemaInformaticoFields);

	    document.add(Chunk.NEWLINE);

	    //RESPONSABILE QUALITA
		Paragraph parResponsabileQualita = new Paragraph();
		parResponsabileQualita.setAlignment(Element.ALIGN_LEFT);
		parResponsabileQualita.setFont(fontParTitolo);
		parResponsabileQualita.add(messageSource.getMessage("label.responsabile_qualita", null, Locale.getDefault()));
	    document.add(parResponsabileQualita);
	    PdfPTable respQualitaFields = getTableFields(showIntegrazioneInfo);
        addCellLabelIntegrazioneSostAggRimoz("label.sostituisci_anagrafica", respQualitaFields, showIntegrazioneInfo, 
        		integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.RESPONSABILE_QUALITA__FULL, null));
	    addAllCellsPersonaByRuolo(provider.getPersonaByRuolo(Ruolo.RESPONSABILE_QUALITA), respQualitaFields, Ruolo.RESPONSABILE_QUALITA, showIntegrazioneInfo, fieldsEditabileAccreditamento);
	    document.add(respQualitaFields);

	    document.add(Chunk.NEWLINE);

	    //COMITATO SCIENTIFICO
        Paragraph parComponenti = new Paragraph();
        parComponenti.setAlignment(Element.ALIGN_LEFT);
        parComponenti.setFont(fontParTitolo);
        parComponenti.add(messageSource.getMessage("label.componenti_comitato_scientifico", null, Locale.getDefault()));
        document.add(parComponenti);
        
        if(showIntegrazioneInfo) {
	        PdfPTable comSciAddRemove = getTableFields(showIntegrazioneInfo);
	        addCellLabelIntegrazioneSostAggRimoz("label.modifica_comitato", comSciAddRemove, showIntegrazioneInfo, 
	        		integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.SEDE__FULL, null));
	        document.add(comSciAddRemove);
        }
        
        //COORDINATORE
        Paragraph parCoordinatore = new Paragraph();
        parCoordinatore.setAlignment(Element.ALIGN_LEFT);
        parCoordinatore.setFont(fontNomeCampo);
        parCoordinatore.add(messageSource.getMessage("label.coordinatore", null, Locale.getDefault()));
        document.add(parCoordinatore);
        PdfPTable coordinatoreFields = getTableFields(showIntegrazioneInfo);
        addAllCellsPersonaByRuolo(provider.getCoordinatoreComitatoScientifico(), coordinatoreFields, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, showIntegrazioneInfo, fieldsEditabileAccreditamento);
        document.add(coordinatoreFields);
        //COMPONENTI
        for(Persona p : provider.getComponentiComitatoScientifico()) {
        	if(!p.isCoordinatoreComitatoScientifico()) {
        		Paragraph parComponente = new Paragraph();
        		parComponente.setAlignment(Element.ALIGN_LEFT);
        		parComponente.setFont(fontNomeCampo);
        		parComponente.add(messageSource.getMessage("label.componente", null, Locale.getDefault()));
                document.add(parComponente);
                PdfPTable componenteFields = getTableFields(showIntegrazioneInfo);
                addAllCellsPersonaByRuolo(p, componenteFields, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, showIntegrazioneInfo, fieldsEditabileAccreditamento);
                document.add(componenteFields);
        	}
        }

        document.add(Chunk.NEWLINE);

        //ALLEGATI
        File attoCostitutivo = null;
		File dichiarazioneEsclusione = null;
		File esperienzaFormazione = null;
		File utilizzoSedi = null;
		File sistemaInformatico = null;
		File pianoQualita = null;
		File dichiarazioneLegaleRappresentante = null;
		File richiestaAccreditamentoStandard = null;
		File relazioneAttivitaFormativa = null;
		for(File f : dati.getFiles()) {
			switch(f.getTipo()) {
			case FILE_ATTO_COSTITUTIVO:
				attoCostitutivo = f;
				break;
			case FILE_DICHIARAZIONE_ESCLUSIONE:
				dichiarazioneEsclusione = f;
				break;
			case FILE_ESPERIENZA_FORMAZIONE:
				esperienzaFormazione = f;
				break;
			case FILE_UTILIZZO:
				utilizzoSedi = f;
				break;
			case FILE_SISTEMA_INFORMATICO:
				sistemaInformatico = f;
				break;
			case FILE_PIANO_QUALITA:
				pianoQualita = f;
				break;
			case FILE_DICHIARAZIONE_LEGALE:
				dichiarazioneLegaleRappresentante = f;
				break;
			case FILE_RICHIESTA_ACCREDITAMENTO_STANDARD:
				richiestaAccreditamentoStandard = f;
				break;
			case FILE_RELAZIONE_ATTIVITA_FORMATIVA:
				relazioneAttivitaFormativa = f;
				break;
			default:
				break;
			}
		}
		Paragraph parAllegati = new Paragraph();
		parAllegati.setAlignment(Element.ALIGN_LEFT);
		parAllegati.setFont(fontParTitolo);
		parAllegati.add(messageSource.getMessage("label.allegati", null, Locale.getDefault()));
        document.add(parAllegati);
		PdfPTable allegatiFields = getTableFields(showIntegrazioneInfo);
		if(attoCostitutivo != null) {
			addCellLabelCampoValore("label.attoCostitutivo", attoCostitutivo.getNomeFile(), allegatiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ATTO_COSTITUIVO, null));
		} else {
			addCellLabelCampoValore("label.attoCostitutivo", getLabelAllegatoNonInserito(), allegatiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ATTO_COSTITUIVO, null));
		}
		if(dichiarazioneEsclusione != null) {
			addCellLabelCampoValore("label.dichiarazioneEsclusione", dichiarazioneEsclusione.getNomeFile(), allegatiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_ESCLUSIONE, null));
		} else {
			addCellLabelCampoValore("label.dichiarazioneEsclusione", getLabelAllegatoNonInserito(), allegatiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_ESCLUSIONE, null));
		}
		if(esperienzaFormazione != null) {
			addCellLabelCampoValore("label.esperienzaFormazione", esperienzaFormazione.getNomeFile(), allegatiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ESPERIENZA_FORMAZIONE, null));
		} else {
			addCellLabelCampoValore("label.esperienzaFormazione", getLabelAllegatoNonInserito(), allegatiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ESPERIENZA_FORMAZIONE, null));
		}
		if(utilizzoSedi != null) {
			addCellLabelCampoValore("label.utilizzo", utilizzoSedi.getNomeFile(), allegatiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__UTILIZZO, null));
		} else {
			addCellLabelCampoValore("label.utilizzo", getLabelAllegatoNonInserito(), allegatiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__UTILIZZO, null));
		}
		if(sistemaInformatico != null) {
			addCellLabelCampoValore("label.sistemaInformatico", sistemaInformatico.getNomeFile(), allegatiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__SISTEMA_INFORMATICO, null));
		} else {
			addCellLabelCampoValore("label.sistemaInformatico", getLabelAllegatoNonInserito(), allegatiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__SISTEMA_INFORMATICO, null));
		}
		if(pianoQualita != null) {
			addCellLabelCampoValore("label.pianoQualita", pianoQualita.getNomeFile(), allegatiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__PIANO_QUALITA, null));
		} else {
			addCellLabelCampoValore("label.pianoQualita", getLabelAllegatoNonInserito(), allegatiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__PIANO_QUALITA, null));
		}
		if(dichiarazioneLegaleRappresentante != null) { 
			if(dichiarazioneLegaleRappresentante.getProtocollo() != null) {
				Object[] valuesProtocollo = {intFormatter.print(dichiarazioneLegaleRappresentante.getProtocollo().getNumero(), Locale.getDefault()), dateTimeFormatter.format(dichiarazioneLegaleRappresentante.getProtocollo().getData())};
				addCellLabelCampoValore("label.dichiarazioneLegale", dichiarazioneLegaleRappresentante.getNomeFile() + " - " + messageSource.getMessage("label.info_protocollo", valuesProtocollo, Locale.getDefault()), allegatiFields, 
	        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_LEGALE, null));
			}
			else {
				addCellLabelCampoValore("label.dichiarazioneLegale", dichiarazioneLegaleRappresentante.getNomeFile(), allegatiFields, 
	        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_LEGALE, null));
			}
		}
		else {
			addCellLabelCampoValore("label.dichiarazioneLegale", getLabelAllegatoNonInserito(), allegatiFields, 
        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_LEGALE, null));
		}
		if(accreditamento.isStandard()) {
			if(richiestaAccreditamentoStandard != null) {
				if(richiestaAccreditamentoStandard.getProtocollo() != null) {
					Object[] valuesProtocollo = {intFormatter.print(richiestaAccreditamentoStandard.getProtocollo().getNumero(), Locale.getDefault()), dateTimeFormatter.format(richiestaAccreditamentoStandard.getProtocollo().getData())};
					addCellLabelCampoValore("label.richiestaAccreditamentoStandard", richiestaAccreditamentoStandard.getNomeFile() + " - " + messageSource.getMessage("label.info_protocollo", valuesProtocollo, Locale.getDefault()), allegatiFields, 
		        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RICHIESTA_ACCREDITAMENTO_STANDARD, null));
				}
				else {
					addCellLabelCampoValore("label.richiestaAccreditamentoStandard", richiestaAccreditamentoStandard.getNomeFile(), allegatiFields, 
		        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RICHIESTA_ACCREDITAMENTO_STANDARD, null));
				}
			}
			else {
				addCellLabelCampoValore("label.richiestaAccreditamentoStandard", getLabelAllegatoNonInserito(), allegatiFields, 
	        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RICHIESTA_ACCREDITAMENTO_STANDARD, null));
			}
			if(relazioneAttivitaFormativa != null) {
				addCellLabelCampoValore("label.relazioneAttivitaFormativa", relazioneAttivitaFormativa.getNomeFile(), allegatiFields, 
	        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RELAZIONE_ATTIVITA_FORMATIVA, null));
			} else {
				addCellLabelCampoValore("label.relazioneAttivitaFormativa", getLabelAllegatoNonInserito(), allegatiFields, 
	        			showIntegrazioneInfo, integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RELAZIONE_ATTIVITA_FORMATIVA, null));
			}
		}
		document.add(allegatiFields);

	}

	private void writePdfRiepilogoPianoFormativoDomanda(Document document, Accreditamento accreditamento) throws DocumentException {
		Provider provider = accreditamento.getProvider();

		//TITOLO
		Object[] values = {intFormatter.print(accreditamento.getPianoFormativo().getAnnoPianoFormativo(), Locale.getDefault()), accreditamento.getTipoDomanda().getNome(), provider.getDenominazioneLegale(), longFormatter.print(provider.getId(), Locale.getDefault())};
        Paragraph parTitolo = new Paragraph();
        parTitolo.setAlignment(Element.ALIGN_LEFT);
        parTitolo.setFont(fontTitolo);
        parTitolo.add(messageSource.getMessage("label.riepilogo_pf_domandaTipo_providerDenominazione_providerId", values, Locale.getDefault()));
        document.add(parTitolo);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        //EVENTI DEL PIANO FORMATIVO
        for(EventoPianoFormativo e : accreditamento.getPianoFormativo().getEventiPianoFormativo()) {
        	Object[] id = {e.getCodiceIdentificativo()};
    		Paragraph parEvento = new Paragraph();
    		parEvento.setAlignment(Element.ALIGN_LEFT);
    		parEvento.setFont(fontNomeCampo);
    		parEvento.add(messageSource.getMessage("label.evento_idEvento", id, Locale.getDefault()));
            document.add(parEvento);
            PdfPTable eventoFields = getTableFields();
            addAllCellsEventoPF(e, eventoFields);
            document.add(eventoFields);
        }

	}

	private void writePdfRiepilogoValutazioneDomanda(Document document, Accreditamento accreditamento, Long valutazioneId) throws Exception {
		if(valutazioneId == null)
			throw new Exception("Id valutazione riepilogo non valido");

		Valutazione valutazione = valutazioneService.getValutazione(valutazioneId);
		if(valutazione == null)
			throw new Exception("Valutazione " +  valutazioneId + "non trovata");

		Provider provider = accreditamento.getProvider();

		//TITOLO
		Object[] values = {valutazione.getAccount().getFullName(), accreditamento.getTipoDomanda().getNome(), provider.getDenominazioneLegale(), longFormatter.print(provider.getId(), Locale.getDefault())};
        Paragraph parTitolo = new Paragraph();
        parTitolo.setAlignment(Element.ALIGN_LEFT);
        parTitolo.setFont(fontTitolo);
        parTitolo.add(messageSource.getMessage("label.riepilogo_valutazione_valutatoreFullNome_domandaTipo_providerDenominazione_providerId", values, Locale.getDefault()));
        document.add(parTitolo);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        //VALUTAZIONE COMPLESSIVA
        Paragraph parValComplessiva = new Paragraph();
        parValComplessiva.setAlignment(Element.ALIGN_LEFT);
        parValComplessiva.setFont(fontParTitolo);
        parValComplessiva.add(messageSource.getMessage("label.valutazione_complessiva", null, Locale.getDefault()));
        document.add(parValComplessiva);
        String valutazioneComplessiva = valutazione.getValutazioneComplessiva();
        if(valutazioneComplessiva == null || valutazioneComplessiva.isEmpty())
        	valutazioneComplessiva = messageSource.getMessage("label.dato_non_inserito", null, Locale.getDefault());
        Paragraph parValComplessivaVal = new Paragraph();
        parValComplessivaVal.setAlignment(Element.ALIGN_LEFT);
        parValComplessivaVal.setFont(fontNomeCampo);
        parValComplessivaVal.add(valutazioneComplessiva);
        document.add(parValComplessivaVal);

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

        //INFORMAZIONI DEL PROVIDER
        List<FieldValutazioneAccreditamento> infoProviderVal = getOrderedFieldValutazioneBySubset(valutazione.getValutazioni(), SubSetFieldEnum.PROVIDER);
	    if(!infoProviderVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.info_provider", null, Locale.getDefault()));
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(infoProviderVal, table);
	        document.add(table);
        }

	    //LEGALE RAPPRESENTANTE
        List<FieldValutazioneAccreditamento> legaleRapprVal = getOrderedFieldValutazioneBySubset(valutazione.getValutazioni(), SubSetFieldEnum.LEGALE_RAPPRESENTANTE);
	    if(!legaleRapprVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.legale_rappresentante", null, Locale.getDefault()));
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(legaleRapprVal, table);
	        document.add(table);
        }

	    //DELEGATO LEGALE RAPPRESENTANTE
        List<FieldValutazioneAccreditamento> delegatoLegaleRapprVal = getOrderedFieldValutazioneBySubset(valutazione.getValutazioni(), SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE);
	    if(!delegatoLegaleRapprVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.delegato_legale_rappresentante", null, Locale.getDefault()));
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(delegatoLegaleRapprVal, table);
	        document.add(table);
        }

	    //SEDI DEL PROVIDER
        Paragraph parSediProvider = new Paragraph();
        parSediProvider.setAlignment(Element.ALIGN_LEFT);
        parSediProvider.setFont(fontParTitolo);
        parSediProvider.add(messageSource.getMessage("label.sedi_provider", null, Locale.getDefault()));
        document.add(parSediProvider);
        //SEDE LEGALE
        Sede sedeLegale = provider.getSedeLegale();
        if(sedeLegale != null) {
	        List<FieldValutazioneAccreditamento> sedeLegaleVal = getOrderedFieldValutazioneBySubsetAndObjectRef(valutazione.getValutazioni(), SubSetFieldEnum.SEDE, sedeLegale.getId());
		    if(!sedeLegaleVal.isEmpty()) {
		    	Object[] valSedeLegale = {sedeLegale.getAddressNameFull()};
		        Paragraph par = new Paragraph();
		        par.setAlignment(Element.ALIGN_LEFT);
		        par.setFont(fontNomeCampo);
		        par.add(messageSource.getMessage("label.sede_legale_withAddress", valSedeLegale, Locale.getDefault()));
		        document.add(par);
		        PdfPTable table = getTableFieldsValutazione();
		        addTableValutazione(sedeLegaleVal, table);
		        document.add(table);
	        }
        }
        //SEDI OPERATIVE
        for(Sede s : provider.getSedi()) {
        	if(!s.isSedeLegale()) {
                List<FieldValutazioneAccreditamento> sedeVal = getOrderedFieldValutazioneBySubsetAndObjectRef(valutazione.getValutazioni(), SubSetFieldEnum.SEDE, s.getId());
    		    if(!sedeVal.isEmpty()) {
    		    	Object[] valSede = {s.getAddressNameFull()};
    		        Paragraph par = new Paragraph();
    		        par.setAlignment(Element.ALIGN_LEFT);
    		        par.setFont(fontNomeCampo);
    		        par.add(messageSource.getMessage("label.sede_operativa_withAddress", valSede, Locale.getDefault()));
    		        document.add(par);
    		        PdfPTable table = getTableFieldsValutazione();
    		        addTableValutazione(sedeVal, table);
    		        document.add(table);
    	        }
        	}
        }

        //TIPOLOGIA FORMATIVA
        List<FieldValutazioneAccreditamento> tipologiaFormativaVal = getOrderedFieldValutazioneTipologiaFormativa(valutazione.getValutazioni(), IdFieldEnum.getDatiAccreditamentoSplitBySezione(1));
	    if(!tipologiaFormativaVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.tipologia_formativa", null, Locale.getDefault()));
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(tipologiaFormativaVal, table);
	        document.add(table);
        }

	    //DATI ECONOMICI
        List<FieldValutazioneAccreditamento> datiEconomiciVal = getOrderedFieldValutazioneTipologiaFormativa(valutazione.getValutazioni(), IdFieldEnum.getDatiAccreditamentoSplitBySezione(2));
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
        List<FieldValutazioneAccreditamento> datiStrutturaVal = getOrderedFieldValutazioneTipologiaFormativa(valutazione.getValutazioni(), IdFieldEnum.getDatiAccreditamentoSplitBySezione(3));
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
	    List<FieldValutazioneAccreditamento> respSegreteriaVal = getOrderedFieldValutazioneBySubset(valutazione.getValutazioni(), SubSetFieldEnum.RESPONSABILE_SEGRETERIA);
	    if(!respSegreteriaVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.responsabile_segreteria", null, Locale.getDefault()));
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(respSegreteriaVal, table);
	        document.add(table);
        }

	    //RESPONSABILE AMMINISTRATIVO
	    List<FieldValutazioneAccreditamento> respAmministrativoVal = getOrderedFieldValutazioneBySubset(valutazione.getValutazioni(), SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO);
	    if(!respAmministrativoVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.responsabile_amministrativo", null, Locale.getDefault()));
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(respAmministrativoVal, table);
	        document.add(table);
        }

	    //RESPONSABILE SISTEMA INFORMATICO
	    List<FieldValutazioneAccreditamento> respSistemaInfoVal = getOrderedFieldValutazioneBySubset(valutazione.getValutazioni(), SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO);
	    if(!respSistemaInfoVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.responsabile_sistema_informatico", null, Locale.getDefault()));
	        document.add(par);
	        PdfPTable table = getTableFieldsValutazione();
	        addTableValutazione(respSistemaInfoVal, table);
	        document.add(table);
        }

	    //RESPONSABILE QUALITA
	    List<FieldValutazioneAccreditamento> respQualitaVal = getOrderedFieldValutazioneBySubset(valutazione.getValutazioni(), SubSetFieldEnum.RESPONSABILE_QUALITA);
	    if(!respQualitaVal.isEmpty()) {
	        Paragraph par = new Paragraph();
	        par.setAlignment(Element.ALIGN_LEFT);
	        par.setFont(fontParTitolo);
	        par.add(messageSource.getMessage("label.responsabile_qualita", null, Locale.getDefault()));
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
        Persona coordinatore = provider.getCoordinatoreComitatoScientifico();
        if(coordinatore != null) {
	        List<FieldValutazioneAccreditamento> coordinatoreVal = getOrderedFieldValutazioneBySubsetAndObjectRef(valutazione.getValutazioni(), SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, coordinatore.getId());
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
        for(Persona p : provider.getComponentiComitatoScientifico()) {
        	if(!p.isCoordinatoreComitatoScientifico()) {
                List<FieldValutazioneAccreditamento> componenteVal = getOrderedFieldValutazioneBySubsetAndObjectRef(valutazione.getValutazioni(), SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, p.getId());
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
        List<FieldValutazioneAccreditamento> allegatiVal = getOrderedFieldValutazioneBySubset(valutazione.getValutazioni(), SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO);
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

	    //VALUTAZIONI SUL CAMPO (SE STANDARD)
	    if(accreditamento.isStandard()) {
	    	List<FieldValutazioneAccreditamento> valutazioniCampoVal = getOrderedFieldValutazioneBySubset(valutazione.getValutazioni(), SubSetFieldEnum.VALUTAZIONE_SUL_CAMPO);
		    if(!valutazioniCampoVal.isEmpty()) {
		        Paragraph par = new Paragraph();
		        par.setAlignment(Element.ALIGN_LEFT);
		        par.setFont(fontParTitolo);
		        par.add(messageSource.getMessage("label.valutazioni_sul_campo", null, Locale.getDefault()));
		        document.add(par);
		        PdfPTable table = getTableFieldsValutazione();
		        addTableValutazione(valutazioniCampoVal, table);
		        document.add(table);
	        }
	    }
	}

	private PdfPTable getTableFields() throws DocumentException {
		return getTableFields(false);
	}
	
	private PdfPTable getTableFields(boolean showIntegrazioneInfo) throws DocumentException {
		PdfPTable tableFields = null;
		if(showIntegrazioneInfo) {
			tableFields = new PdfPTable(3);
			tableFields.setWidthPercentage(100);
			tableFields.setWidths(new float[]{1, 5, 18});
			
		} else {
			tableFields = new PdfPTable(2);
			tableFields.setWidthPercentage(100);
			tableFields.setWidths(new float[]{1, 3});			
		}
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
	
	private void addCellLabelIntegrazioneSostAggRimoz(String labelCampo, PdfPTable table, boolean showIntegrazioneInfo, boolean inIntegrazione) {
		addCellIntegrazioneSostAggRimoz(messageSource.getMessage(labelCampo, null, Locale.getDefault()), table, showIntegrazioneInfo, inIntegrazione);
	}
	
	private void addCellIntegrazioneSostAggRimoz(String nomeCampo, PdfPTable table, boolean showIntegrazioneInfo, boolean inIntegrazione) {
		if(showIntegrazioneInfo) {
			addCellShowIntegrazione(table, showIntegrazioneInfo, inIntegrazione);
			
			PdfPCell cell = getCellLabel(nomeCampo);
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(cell);
		}
	}

	private void addCellLabelCampoValore(String labelCampo, String valoreCampo, PdfPTable table, boolean showIntegrazioneInfo, boolean inIntegrazione) {
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table, showIntegrazioneInfo, inIntegrazione);
	}
	
	private void addCellLabelCampoValore(String labelCampo, String valoreCampo, PdfPTable table) {
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoLabel(String labelCampo, String valoreCampo, PdfPTable table) {
		addCellLabelCampoLabel(labelCampo, valoreCampo, table, false, false);
	}
	
	private void addCellLabelCampoLabel(String labelCampo, String valoreCampo, PdfPTable table, boolean showIntegrazioneInfo, boolean inIntegrazione) {
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), messageSource.getMessage(valoreCampo, null, Locale.getDefault()), table, showIntegrazioneInfo, inIntegrazione);
	}

	private void addCellValoreCampoValoreValuta(String labelCampo, BigDecimal valoreLongCampo, PdfPTable table) {
		addCellValoreCampoValoreValuta(labelCampo, valoreLongCampo, table, false, false);
	}
	
	private void addCellValoreCampoValoreValuta(String labelCampo, BigDecimal valoreLongCampo, PdfPTable table, boolean showIntegrazioneInfo, boolean inIntegrazione) {
		String valoreCampo = null;
		if(valoreLongCampo != null)
			valoreCampo = valutaFormatter.print(valoreLongCampo, Locale.getDefault()) + "€";
		addCellCampoValore(labelCampo, valoreCampo, table, showIntegrazioneInfo, inIntegrazione);
	}

	private void addCellShowIntegrazione(PdfPTable table, boolean showIntegrazioneInfo, boolean inIntegrazione) {
		if(showIntegrazioneInfo) {
			if(inIntegrazione) {
				PdfPCell cellIntInfo = new PdfPCell(imgIntegrazioneAbilitata);
				cellIntInfo.setBorder(PdfPCell.NO_BORDER);
				cellIntInfo.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellIntInfo.setPadding(cellPadding);
				table.addCell(cellIntInfo);				
			} else {
				PdfPCell cellIntInfo = new PdfPCell();
				cellIntInfo.setBorder(PdfPCell.NO_BORDER);
				table.addCell(cellIntInfo);
			}
		}
	}
	
	private void addCellValoreCampoValore(String labelCampo, String valoreCampo, PdfPTable table, boolean showIntegrazioneInfo, boolean inIntegrazione) {
		addCellCampoValore(labelCampo, valoreCampo, table, showIntegrazioneInfo, inIntegrazione);
	}

	private void addCellCampoValore(String nomeCampo, String valoreCampo, PdfPTable table, boolean showIntegrazioneInfo, boolean inIntegrazione) {
		addCellShowIntegrazione(table, showIntegrazioneInfo, inIntegrazione);
		
		PdfPCell cell = getCellLabel(nomeCampo);
		table.addCell(cell);
		if(valoreCampo == null || valoreCampo.isEmpty())
			cell = getCellValore(messageSource.getMessage("label.dato_non_inserito", null, Locale.getDefault()));
		else
			cell = getCellValore(valoreCampo);
		table.addCell(cell);
	}
	
	private void addCellCampoValore(String nomeCampo, String valoreCampo, PdfPTable table) {
		addCellCampoValore(nomeCampo, valoreCampo, table, false, false);
	}

	private void addCellLabelCampoValore(String labelCampo, Set<? extends INomeEnum> valoriEnumCampo, PdfPTable table) {
		addCellLabelCampoValore(labelCampo, valoriEnumCampo, table, false, false);
	}
	
	private void addCellLabelCampoValore(String labelCampo, Set<? extends INomeEnum> valoriEnumCampo, PdfPTable table, boolean showIntegrazioneInfo, boolean inIntegrazione) {
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
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table, showIntegrazioneInfo, inIntegrazione);
	}

	private void addCellCampoValore(String nomeCampo, PdfPTable tableCampo, PdfPTable table, boolean tableInNewLine) {
		addCellCampoValore(nomeCampo, tableCampo, table, tableInNewLine, false, false);
	}
	
	private void addCellCampoValore(String nomeCampo, PdfPTable tableCampo, PdfPTable table, boolean tableInNewLine, boolean showIntegrazioneInfo, boolean inIntegrazione) {
		addCellShowIntegrazione(table, showIntegrazioneInfo, inIntegrazione);
		
		PdfPCell cell = getCellLabel(nomeCampo);
		table.addCell(cell);

		if(tableCampo == null)
			cell = getCellValore(messageSource.getMessage("label.dato_non_inserito", null, Locale.getDefault()));
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

	private void addCellLabelCampoValoreDiscipline(String labelCampo, Set<Disciplina> discipline, PdfPTable table) throws DocumentException  {
		addCellLabelCampoValoreDiscipline(labelCampo, discipline, table, false, false);
	}
	
	private void addCellLabelCampoValoreDiscipline(String labelCampo, Set<Disciplina> discipline, PdfPTable table, boolean showIntegrazioneInfo, boolean inIntegrazione) throws DocumentException  {
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
			List<String> professioniList = new ArrayList<String>();
			professioniList.addAll(professioniDiscipline.keySet());
			professioniList.sort(String::compareTo);
			for(String prof : professioniList) {
				Set<String> discs = professioniDiscipline.get(prof);
				List<String> disciplineList = new ArrayList<String>();
				disciplineList.addAll(discs);
				disciplineList.sort(String::compareTo);
				PdfPCell cell = new PdfPCell(new Phrase(prof, fontValoreCampo));
				cell.setBackgroundColor(BaseColor.GRAY);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPadding(cellPaddingSubTable);
				tableDisc.addCell(cell);
				for(String disc : disciplineList) {
					cell = new PdfPCell(new Phrase(disc, fontValoreCampo));
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setPadding(cellPaddingSubTable);
					tableDisc.addCell(cell);
				}
			}

		}
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), tableDisc, table, false);
	}

	private PdfPCell getValoreEmptyCell() {
    	PdfPCell cell = new PdfPCell(new Phrase(""));
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		cell.setPadding(cellPadding);
		return cell;
    }

	private PdfPCell getCellForTable(PdfPTable table) {
    	PdfPCell cell = new PdfPCell(table);
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		cell.setPadding(cellPadding);
    	return cell;
    }

	private void addCellLabelCampoValoreEnum(String labelCampo, Enum valoreEnumCampo, PdfPTable table) {
		addCellLabelCampoValoreEnum(labelCampo, valoreEnumCampo, table, false, false);
	}
	
	//Mostra il toString() dell'enum
	private void addCellLabelCampoValoreEnum(String labelCampo, Enum valoreEnumCampo, PdfPTable table, boolean showIntegrazioneInfo, boolean inIntegrazione) {
		String valoreCampo = null;
		if(valoreEnumCampo != null)
			valoreCampo = valoreEnumCampo.toString();
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table, showIntegrazioneInfo, inIntegrazione);
	}

	private PdfPCell getCellLabel(String nomeCampo) {
    	PdfPCell cell = new PdfPCell(new Phrase(nomeCampo, fontNomeCampo));
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setPadding(cellPadding);
		return cell;
    }

	private PdfPCell getCellLabelTODELETE(String nomeCampo, boolean showIntegrazioneInfo, boolean inIntegrazione) {
		PdfPCell cell;
		if(showIntegrazioneInfo && inIntegrazione) {
			//cell = new PdfPCell(imgIntegrazioneAbilitata, false);
			
			
			/*
Paragraph p = new Paragraph();
p.Add(new Phrase("Test "));
p.Add(new Chunk(image, 0, 0));
p.Add(new Phrase(" more text "));
p.Add(new Chunk(image, 0, 0));
p.Add(new Chunk(image, 0, 0));
p.Add(new Phrase(" end."));
cell.AddElement(p);
			 */
			Paragraph p = new Paragraph();
			p.add(new Chunk(imgIntegrazioneAbilitata, 0, 0));
			p.add(new Phrase(" " + nomeCampo, fontNomeCampo));
			//p.setAlignment(Element.ALIGN_MIDDLE);

			cell = new PdfPCell(p);
//			cell.addElement(imgIntegrazioneAbilitata);
			//cell.addElement(p);
//			cell.addElement(new Phrase(nomeCampo, fontNomeCampo));
			/*
PdfPCell cell = new PdfPCell();
cell.AddElement(image128);
Paragraph p = new Paragraph("Student name");
p.Alignment = Element.ALIGN_CENTER;
cell.AddElement(p);
BarCodeTable.AddCell(cell);
			 */
			
			//Con tabella ew 2 colonne
			PdfPTable tableLabel = new PdfPTable(2);
			tableLabel.setWidthPercentage(100);
			try {
				tableLabel.setWidths(new float[]{1, 5});
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tableLabel.setSpacingBefore(0);
			tableLabel.setSpacingAfter(0);
			PdfPCell cellLabel = new PdfPCell(imgIntegrazioneAbilitata);
			cellLabel.setPadding(0);
			tableLabel.addCell(cellLabel);
			cellLabel = new PdfPCell(new Phrase(nomeCampo, fontNomeCampo));
			cellLabel.setPadding(0);
			tableLabel.addCell(cellLabel);
			cell = new PdfPCell(tableLabel);
			
		} else {
			cell = new PdfPCell(new Phrase(nomeCampo, fontNomeCampo));
		}
		//cell.setBorder(PdfPCell.NO_BORDER);
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

	private String getLabelAllegatoNonInserito() {
		return 	messageSource.getMessage("label.allegato_non_inserito", null, Locale.getDefault());
	}

	private void addAllCellsPersonaByRuolo(Persona persona, PdfPTable tableFields, Ruolo ruolo) {
		addAllCellsPersonaByRuolo(persona, tableFields, ruolo, false, null);
	}
	
	private void addAllCellsPersonaByRuolo(Persona persona, PdfPTable tableFields, Ruolo ruolo, boolean showIntegrazioneInfo, Set<FieldEditabileAccreditamento> fieldsEditabileAccreditamento) {
		Anagrafica anagrafica = persona.getAnagrafica();
		File attoNomina = null;
		File cv = null;
		File delega = null;
		for(File f : persona.getFiles()) {
			switch(f.getTipo()) {
			case FILE_ATTO_NOMINA:
				attoNomina = f;
				break;
			case FILE_CV:
				cv = f;
				break;
			case FILE_DELEGA:
				delega = f;
				break;
			default:
				break;
			}
		}
		
//		addCellLabelCampoValore("label.no_profit", noProfit, providerFields, showIntegrazioneInfo, 
//				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.PROVIDER__NO_PROFIT, null));
		
		
/*
	LEGALE_RAPPRESENTANTE__FULL ("persona.anagrafica",-1,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "full", false),
	LEGALE_RAPPRESENTANTE__COGNOME ("persona.anagrafica.cognome",9,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.cognome", true),
	LEGALE_RAPPRESENTANTE__NOME ("persona.anagrafica.nome",10,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.nome", true),
	LEGALE_RAPPRESENTANTE__CODICEFISCALE ("persona.anagrafica.codiceFiscale",11,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.codiceFiscale", true),
	LEGALE_RAPPRESENTANTE__TELEFONO ("persona.anagrafica.telefono",12,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.telefono", true),
	LEGALE_RAPPRESENTANTE__CELLULARE ("persona.anagrafica.cellulare",13,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.cellulare", true),
	LEGALE_RAPPRESENTANTE__EMAIL ("persona.anagrafica.email",14,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.email", true),
	LEGALE_RAPPRESENTANTE__PEC ("persona.anagrafica.pec",15,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.pec", true),
	LEGALE_RAPPRESENTANTE__ATTO_NOMINA ("attoNomina",16,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "files.FILE_ATTO_NOMINA", false),
	LEGALE_RAPPRESENTANTE__CV ("cv",17,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "files.FILE_CV", false),
 
 	DELEGATO_LEGALE_RAPPRESENTANTE__FULL ("persona.anagrafica",-1,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "full", false),
	DELEGATO_LEGALE_RAPPRESENTANTE__COGNOME ("persona.anagrafica.cognome",18,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.cognome", true),
	DELEGATO_LEGALE_RAPPRESENTANTE__NOME ("persona.anagrafica.nome",19,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.nome", true),
	DELEGATO_LEGALE_RAPPRESENTANTE__CODICEFISCALE ("persona.anagrafica.codiceFiscale",20,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.codiceFiscale", true),
	DELEGATO_LEGALE_RAPPRESENTANTE__TELEFONO ("persona.anagrafica.telefono",21,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.telefono", true),
	DELEGATO_LEGALE_RAPPRESENTANTE__CELLULARE ("persona.anagrafica.cellulare",22,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.cellulare", true),
	DELEGATO_LEGALE_RAPPRESENTANTE__EMAIL ("persona.anagrafica.email",23,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.email", true),
	DELEGATO_LEGALE_RAPPRESENTANTE__DELEGA ("delega",24,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "files.FILE_DELEGA", false),
	DELEGATO_LEGALE_RAPPRESENTANTE__CV ("cv",25,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "files.FILE_CV", false),

 */
		Long idPersona = null;
		if(ruolo == Ruolo.COMPONENTE_COMITATO_SCIENTIFICO)
			idPersona = persona.getId();
		
		addCellLabelCampoValore("label.cognome", anagrafica.getCognome(), tableFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.getIdField("persona.anagrafica.cognome", ruolo), idPersona));
		addCellLabelCampoValore("label.nome", anagrafica.getNome(), tableFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.getIdField("persona.anagrafica.nome", ruolo), idPersona));
		addCellLabelCampoValore("label.codice_fiscale", anagrafica.getCodiceFiscale(), tableFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.getIdField("persona.anagrafica.codiceFiscale", ruolo), idPersona));
		addCellLabelCampoValore("label.telefono", anagrafica.getTelefono(), tableFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.getIdField("persona.anagrafica.telefono", ruolo), idPersona));
		if(ruolo == Ruolo.LEGALE_RAPPRESENTANTE || ruolo == Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE) {
			addCellLabelCampoValore("label.cellulare", anagrafica.getCellulare(), tableFields, showIntegrazioneInfo, 
					integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.getIdField("persona.anagrafica.cellulare", ruolo), idPersona));
		}
		addCellLabelCampoValore("label.email", anagrafica.getEmail(), tableFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.getIdField("persona.anagrafica.email", ruolo), idPersona));
		if(ruolo == Ruolo.LEGALE_RAPPRESENTANTE) {
			addCellLabelCampoValore("label.pec", anagrafica.getPec(), tableFields, showIntegrazioneInfo, 
					integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.getIdField("persona.anagrafica.pec", ruolo), idPersona));
		}
		if(ruolo == Ruolo.COMPONENTE_COMITATO_SCIENTIFICO) {
			addCellLabelCampoValore("label.professione", persona.getProfessione().getNome(), tableFields, showIntegrazioneInfo, 
					integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.getIdField("persona.professione", ruolo), idPersona));
		}
		if(ruolo != Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE) {
			if(attoNomina != null) {
				addCellLabelCampoValore("label.attoNomina", attoNomina.getNomeFile(), tableFields, showIntegrazioneInfo, 
						integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.getIdField("attoNomina", ruolo), idPersona));
			} else {
				addCellLabelCampoValore("label.attoNomina", getLabelAllegatoNonInserito(), tableFields, showIntegrazioneInfo, 
						integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.getIdField("attoNomina", ruolo), idPersona));
			}
		}
		else {
			if(delega != null) {
				addCellLabelCampoValore("label.delega", delega.getNomeFile(), tableFields, showIntegrazioneInfo, 
						integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.getIdField("delega", ruolo), idPersona));
			} else {
				addCellLabelCampoValore("label.delega", getLabelAllegatoNonInserito(), tableFields, showIntegrazioneInfo, 
						integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.getIdField("delega", ruolo), idPersona));
			}
		}
		if(cv != null) {
			addCellLabelCampoValore("label.cv", cv.getNomeFile(), tableFields, showIntegrazioneInfo, 
					integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.getIdField("cv", ruolo), idPersona));
		} else {
			addCellLabelCampoValore("label.cv", getLabelAllegatoNonInserito(), tableFields, showIntegrazioneInfo, 
					integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.getIdField("cv", ruolo), idPersona));
		}

	}

//	private void addAllCellsPersonaByRuolo(Persona persona, PdfPTable tableFields, Ruolo ruolo, boolean showIntegrazioneInfo, Set<FieldEditabileAccreditamento> fieldsEditabileAccreditamento) {
	private void addAllCellsSede(Sede sede, PdfPTable tableFields) {
		addAllCellsSede(sede, tableFields, false, null);
	}

	
	private void addAllCellsSede(Sede sede, PdfPTable tableFields, boolean showIntegrazioneInfo, Set<FieldEditabileAccreditamento> fieldsEditabileAccreditamento) {
		addCellLabelCampoValore("label.provincia", sede.getProvincia(), tableFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.SEDE__PROVINCIA, sede.getId()));
		addCellLabelCampoValore("label.comune", sede.getComune(), tableFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.SEDE__COMUNE, sede.getId()));
		addCellLabelCampoValore("label.indirizzo", sede.getIndirizzo(), tableFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.SEDE__INDIRIZZO, sede.getId()));
		addCellLabelCampoValore("label.cap", sede.getCap(), tableFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.SEDE__CAP, sede.getId()));
		addCellLabelCampoValore("label.telefono", sede.getTelefono(), tableFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.SEDE__TELEFONO, sede.getId()));
		addCellLabelCampoValore("label.fax", sede.getFax(), tableFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.SEDE__FAX, sede.getId()));
		addCellLabelCampoValore("label.email", sede.getEmail(), tableFields, showIntegrazioneInfo, 
				integrazioneAbilitata(showIntegrazioneInfo, fieldsEditabileAccreditamento, IdFieldEnum.SEDE__EMAIL, sede.getId()));
	}

	private void addAllCellsEventoPF(EventoPianoFormativo evento, PdfPTable tableFields) throws DocumentException {
		addCellLabelCampoValore("label.procedure_formative_tipologia", evento.getProceduraFormativa().getNome(), tableFields);
		addCellLabelCampoValore("label.titolo", evento.getTitolo(), tableFields);
		addCellLabelCampoValore("label.obiettivo_strategico_nazionale", evento.getObiettivoNazionale().getNome(), tableFields);
		addCellLabelCampoValore("label.obiettivo_strategico_regionale", evento.getObiettivoRegionale().getNome(), tableFields);
		addCellLabelCampoValore("label.professioni_cui_evento_si_riferisce", evento.getProfessioniEvento(), tableFields);
		if(evento.getProfessioniEvento().equals("Generale"))
        	addCellLabelCampoLabel("label.professioni_discipline", "label.tutte_le_professioni", tableFields);
        else
        	addCellLabelCampoValoreDiscipline("label.professioni_discipline", evento.getDiscipline(), tableFields);
	}

	private void addTableValutazione(List<FieldValutazioneAccreditamento> orderedVal, PdfPTable table) throws Exception {
		for(FieldValutazioneAccreditamento fva : orderedVal) {
			addCellSubTable(intFormatter.print(fva.getIdField().getIdEcm(), Locale.getDefault()), table);
			addCellSubTable(messageSource.getMessage("IdFieldEnum_valutazione." + fva.getIdField().name(), null, Locale.getDefault()), table);
			addCellSubTable(getIconForValutazione(fva.getEsito()), table);
			addCellSubTable(getNoteForValutazione(fva.getNote()), table);
		}
	}

	private List<FieldValutazioneAccreditamento> getOrderedFieldValutazioneBySubset(Set<FieldValutazioneAccreditamento> valutazioni, SubSetFieldEnum subset) {
		List<FieldValutazioneAccreditamento> result = new ArrayList<FieldValutazioneAccreditamento>();
		for(FieldValutazioneAccreditamento fva : valutazioni) {
			if(fva.getIdField().getSubSetField() == subset && fva.getIdField().getIdEcm() != -1)
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
			if(setIdField.contains(fva.getIdField()) && fva.getIdField().getIdEcm() != -1)
				result.add(fva);
		}
		result.sort((fva1, fva2) -> Integer.compare(fva1.getIdField().getIdEcm(), fva2.getIdField().getIdEcm()));
		return result;
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
}
