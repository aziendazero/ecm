package it.tredi.ecm.service;

import java.io.StringReader;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.cogeaps.Helper;
import it.tredi.ecm.cogeaps.XmlReportBuilder;
import it.tredi.ecm.cogeaps.XmlReportValidator;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoPianoFormativo;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.repository.PianoFormativoRepository;
import it.tredi.ecm.exception.EcmException;
import it.tredi.ecm.utils.Utils;

@Service
public class PianoFormativoServiceImpl implements PianoFormativoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PianoFormativoServiceImpl.class);
	private final static String CSV_REPORT_ENCODING = "CP1252";

	@Autowired private PianoFormativoRepository pianoFormativoRepository;
	@Autowired private ProviderService providerService;
	@Autowired private EventoPianoFormativoService eventoPianoFormativoService;
	@Autowired private ObiettivoService obiettivoService;
	@Autowired private AccreditamentoService accreditamentoService;

	@Override
	public boolean exist(Long providerId, Integer annoPianoFormativo){
		LOGGER.debug(Utils.getLogMessage("Check exist piano formativo " + annoPianoFormativo + " del Provider: " + providerId));
		PianoFormativo pianoFormativo = getPianoFormativoAnnualeForProvider(providerId, annoPianoFormativo);
		if(pianoFormativo == null)
			return false;
		else
			return true;
	}

	@Override
	@Transactional
	public PianoFormativo create(Long providerId, Integer annoPianoFormativo) {
		LOGGER.debug(Utils.getLogMessage("Inserimento Piano Formativo Anno " + annoPianoFormativo + " del Provider: " + providerId));
		Provider provider = providerService.getProvider();
		PianoFormativo pianoFormativo = new PianoFormativo();
		pianoFormativo.setAnnoPianoFormativo(annoPianoFormativo);
		pianoFormativo.setProvider(provider);
		//modificabile entro il 15 Dicembre dell'anno prima
		pianoFormativo.setDataFineModifca(LocalDate.parse(annoPianoFormativo-1 + "-12-15"));
		pianoFormativoRepository.save(pianoFormativo);
		return pianoFormativo;
	}

	@Override
	@Transactional
	public void save(PianoFormativo pianoFormativo) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio Piano Formativo Anno " + pianoFormativo.getAnnoPianoFormativo() + " del Provider: " + pianoFormativo.getProvider().getId()));
		pianoFormativoRepository.save(pianoFormativo);
	}

	@Override
	public PianoFormativo getPianoFormativo(Long pianoFormativoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero Piano Formativo: " + pianoFormativoId));
		return pianoFormativoRepository.findOne(pianoFormativoId);
	}

	@Override
	public Set<PianoFormativo> getAllPianiFormativiForProvider(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero Piani Formativi del Provider: " + providerId));
		return pianoFormativoRepository.findAllByProviderId(providerId);
	}

	@Override
	public PianoFormativo getPianoFormativoAnnualeForProvider(Long providerId, Integer annoPianoFormativo) {
		LOGGER.debug(Utils.getLogMessage("Recupero Piano Formativo Anno " + annoPianoFormativo + " del Provider: " + providerId));
		return pianoFormativoRepository.findOneByProviderIdAndAnnoPianoFormativo(providerId, annoPianoFormativo);
	}

	@Override
	public boolean isPianoModificabile(Long pianoFormativoId) {
		PianoFormativo pianoFormativo = pianoFormativoRepository.findOne(pianoFormativoId);
		if(pianoFormativo == null)
			return false;
		return pianoFormativo.isPianoModificabile();
	}

	@Override
	public Set<Long> getAllPianiFormativiIdInAccreditamentoForProvider(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero id di piani formativi dentro Accreditamento per Provider: " + providerId));
		return pianoFormativoRepository.findAllByProviderIdInAccreditamento(providerId);
	}
	
	@Override
	@Transactional
	public void importaEventiDaCSV(Long pianoFormativoId, File importEventiDaCsvFile) throws Exception {
		try {
			PianoFormativo pianoFormativo = getPianoFormativo(pianoFormativoId);
			Accreditamento accreditamento = accreditamentoService.getAccreditamentoAttivoForProvider(pianoFormativo.getProvider().getId());
			byte []csv = importEventiDaCsvFile.getData();
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withDelimiter(';').withFirstRecordAsHeader().parse(new StringReader(new String(csv, CSV_REPORT_ENCODING)));
			for (CSVRecord record : records) { //per ogni evento (riga del CSV) -> creo EventoPianoFormativo
				EventoPianoFormativo evento = new EventoPianoFormativo();
				evento.setTitolo(getEventoTitoloFromCSVRow(record));
				evento.setEdizione(0);
				evento.setProceduraFormativa(getEventoProceduraformativaFromCSVRow(record, accreditamento.getDatiAccreditamento()));
				evento.setPianoFormativo(pianoFormativo.getAnnoPianoFormativo());
				evento.setObiettivoNazionale(getEventoObiettivoNazionaleFromCVSRow(record));
				evento.setObiettivoRegionale(getEventoObiettivoRegionaleFromCVSRow(record));
				evento.setProfessioniEvento(getEventoGeneraleSettorialeFromCVSRow(record)); //Generale //Settoriale
				evento.setProvider(pianoFormativo.getProvider());
				evento.setAccreditamento(accreditamento);
				for (Disciplina disciplina: getEventoDisciplineFromCSVRow(record, accreditamento.getDatiAccreditamento())) {
					evento.getDiscipline().add(disciplina);	
				}
				eventoPianoFormativoService.save(evento);
				pianoFormativo.addEvento(evento);
			}
			save(pianoFormativo);
		}
		catch (Exception e) {
			throw new EcmException("error.csv_import_eventi_piano_formativo_error", e.getMessage(), e);
		}
	}

	private ProceduraFormativa getEventoProceduraformativaFromCSVRow(CSVRecord record, DatiAccreditamento datiAccreditamento) throws Exception {
		String procedura_formativa = record.get("RES/FAD/FSC");
		if (procedura_formativa == null)
			procedura_formativa = record.get(0);
		ProceduraFormativa proceduraFormativa = null;
		try {
			proceduraFormativa = ProceduraFormativa.valueOf(procedura_formativa.trim().toUpperCase());
		}
		catch (IllegalArgumentException e) {
			throw new Exception("Tipo di formazione non valido per il campo 'RES/FAD/FSC': " + procedura_formativa, e);
		}
		if (!datiAccreditamento.getProcedureFormative().contains(proceduraFormativa))
			throw new Exception("Tipo di formazione non compatibile con i dati di accreditamento: " + procedura_formativa);
		return proceduraFormativa;
	}
	
	private String getEventoTitoloFromCSVRow(CSVRecord record) throws Exception {
		String titolo = record.get("titolo");
		if (titolo == null)
			titolo = record.get(1);
		return titolo;
	}
	
	private String getEventoGeneraleSettorialeFromCVSRow(CSVRecord record) throws Exception {
		String cod_prof_disciplina = record.get("cod_prof.disciplina");
		if (cod_prof_disciplina == null)
			cod_prof_disciplina = record.get(4);
		return (cod_prof_disciplina.length() == 0)? "Generale" : "Settoriale";
	}
	
	private Obiettivo getEventoObiettivoNazionaleFromCVSRow(CSVRecord record) throws Exception {
		String obiettivo_nazionale = record.get("obiettivo_nazionale");
		if (obiettivo_nazionale == null)
			obiettivo_nazionale = record.get(2);
		Obiettivo obiettivo = obiettivoService.findOneByCodiceCogeaps(obiettivo_nazionale.trim(), true);
		if (obiettivo == null || !obiettivo.isNazionale())
			throw new Exception("Obiettivo nazionale non valido: " + obiettivo_nazionale);
		return obiettivo;
	}
	
	private Obiettivo getEventoObiettivoRegionaleFromCVSRow(CSVRecord record) throws Exception {
		String obiettivo_regionale = record.get("obiettivo_regionale");
		if (obiettivo_regionale == null)
			obiettivo_regionale = record.get(3);
		Obiettivo obiettivo = obiettivoService.findOneByCodiceCogeaps(obiettivo_regionale.trim(), false);
		if (obiettivo == null || obiettivo.isNazionale())
			throw new Exception("Obiettivo regionale non valido: " + obiettivo_regionale);
		return obiettivo;
	}
	
	private Set<Disciplina> getEventoDisciplineFromCSVRow(CSVRecord record, DatiAccreditamento datiAccreditamento) throws Exception {
		String cod_prof_disciplina = record.get("cod_prof.disciplina");
		if (cod_prof_disciplina == null)
			cod_prof_disciplina = record.get(4);
		cod_prof_disciplina = cod_prof_disciplina.trim();
		
		if (cod_prof_disciplina.length() == 0) { //generale -> prendo tutte le professioni (in realta tutte le discipline compatibili con i dati di accreditamento)
			return datiAccreditamento.getDiscipline();
		}
		else { //settoriale

			Set<Disciplina> disciplineAccreditamento = datiAccreditamento.getDiscipline(); //lista delle discipline per le quali si è accreditati
			Set<Professione> professioniAccreditamento = getProfessioniAccreditamentoByDiscipline(disciplineAccreditamento); //lista delle professioni per le quali si è abilitati
			
			Set<Disciplina> disciplineRet = new HashSet<Disciplina>();
			for (String professione_disciplina:cod_prof_disciplina.split(":")) { //per ogni sequenza di professioni.discipline
			    String []professione_disciplina_arr = professione_disciplina.split("\\.");
			    String professioneCodCog = professione_disciplina_arr[0];
			    
			    Professione professione = getProfessioneAccreditamentoByCodCog(professioneCodCog, professioniAccreditamento);
			    if (professione == null)
			    	throw new Exception("Professione non compatibile con i dati di accreditamento: " + professioneCodCog);
			    
			    if (professione_disciplina_arr.length == 1) { //se è stata specificata solo la professione -> si prendono tutte le discipline per le quali si è accreditati (che afferiscono a quella professione)
			    	disciplineRet.addAll(filterDisciplineAccreditamentoByProfessione(professione, disciplineAccreditamento));
			    }
			    else { //se sono state specificate le discipline si prendono solo quelle (controllando che siano compatibili con quelle accreditate per la professione)
				    for (int i=1; i<professione_disciplina_arr.length; i++) {
				    	String disciplinaCodGoc = professione_disciplina_arr[i];
				    	Disciplina disciplina = getDisciplinaAccreditamentoByCodGog(disciplinaCodGoc, disciplineAccreditamento);
				    	if (disciplina == null)
				    		throw new Exception("Disciplina non compatibile con i dati di accreditamento: " + disciplinaCodGoc);
				    	if (!disciplina.getProfessione().getCodiceCogeaps().equals(professioneCodCog))
				    		throw new Exception("Disciplina non compatibile con la professione indicata. Disciplina: " + disciplinaCodGoc + " Professione: " + professioneCodCog);
				    	disciplineRet.add(disciplina);
				    }			    	
			    }

			}
			return disciplineRet;
		}
	}
	
	private Set<Professione> getProfessioniAccreditamentoByDiscipline(Set<Disciplina> disciplineAccreditamento) {
		Set<Professione> professioniAccreditamento = new HashSet<Professione>();
		for (Disciplina disciplina:disciplineAccreditamento)
			if (!professioniAccreditamento.contains(disciplina.getProfessione()))
				professioniAccreditamento.add(disciplina.getProfessione());		
		return professioniAccreditamento;
	}
	
	private Professione getProfessioneAccreditamentoByCodCog(String professioneCodCog, Set<Professione> professioniAccreditamento) {
		for (Professione professione:professioniAccreditamento) {
			if (professione.getCodiceCogeaps().equals(professioneCodCog))
				return professione;
		}
		return null;
	}
	
	private Set<Disciplina> filterDisciplineAccreditamentoByProfessione(Professione professione, Set <Disciplina>disciplineAccreditamento) {
		Set<Disciplina> disciplineRet = new HashSet<Disciplina>();
		for (Disciplina disciplina:disciplineAccreditamento)
			if (disciplina.getProfessione().equals(professione))
				disciplineRet.add(disciplina);
		return disciplineRet;
	}

	private Disciplina getDisciplinaAccreditamentoByCodGog(String disciplinaCodGoc, Set<Disciplina> disciplineAccreditamento) {
		for (Disciplina disciplina:disciplineAccreditamento) {
			if (disciplina.getCodiceCogeaps().equals(disciplinaCodGoc))
				return disciplina;
		}
		return null;
	}
	
}
