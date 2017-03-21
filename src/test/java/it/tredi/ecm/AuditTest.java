package it.tredi.ecm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.SerializationUtils;
import org.javers.core.Javers;
import org.javers.core.changelog.SimpleTextChangeLog;
import org.javers.core.commit.CommitId;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import it.tredi.ecm.audit.AuditInfo;
import it.tredi.ecm.audit.EcmAuditInfoChangeLog;
import it.tredi.ecm.audit.EcmTextChangeLog;
import it.tredi.ecm.dao.entity.AnagraficaEvento;
import it.tredi.ecm.dao.entity.AnagraficaEventoBase;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoRES;
//import it.tredi.ecm.dao.entity.AnagraficaFullEvento;
import it.tredi.ecm.dao.entity.PersonaEvento;
//import it.tredi.ecm.dao.entity.PersonaFullEvento;
import it.tredi.ecm.dao.entity.PersonaFullEvento;
import it.tredi.ecm.dao.repository.EventoRepository;
import it.tredi.ecm.dao.repository.PersonaEventoRepository;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.service.AccountServiceImpl;
import it.tredi.ecm.service.AnagraficaEventoService;
import it.tredi.ecm.service.AuditService;
import it.tredi.ecm.service.AuditServiceImpl;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.ProviderService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
//@ActiveProfiles("dev")
@ActiveProfiles("abarducci")
//@WithUserDetails("provider")
@Rollback(false)
//@Ignore
public class AuditTest {

	@Autowired private EventoRepository eventoRepository;

	@Autowired private Javers javers;

	@Test
	@Ignore
	public void eventoDiff() throws Exception {
		EventoRES eventoOld = new EventoRES();
		EventoRES eventoNew = new EventoRES();

		eventoOld.setId(1L);
		eventoOld.setTitolo("Tit");
		PersonaEvento persOld = new PersonaEvento();
		persOld.setId(2L);
		persOld.setQualifica("qualifica");
		AnagraficaEventoBase anagraficaOld = new AnagraficaEventoBase();
		anagraficaOld.setNome("prova");
		persOld.setAnagrafica(anagraficaOld);
		eventoOld.getResponsabili().add(persOld);

		eventoNew.setId(1L);
		eventoNew.setTitolo("Tit mod");
		PersonaEvento persNew = new PersonaEvento();
		persNew.setId(2L);
		persNew.setQualifica("qualifica mod");
		AnagraficaEventoBase anagraficaNew = new AnagraficaEventoBase();
		anagraficaNew.setNome("prova");
		persNew.setAnagrafica(anagraficaNew);
		eventoNew.getResponsabili().add(persNew);

		Diff diff = javers.compare(eventoOld, eventoNew);
		String changeLog = javers.processChangeList(diff.getChanges(), new SimpleTextChangeLog());

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

		saveToFile("c:\\JAVERS_DIFF_" + LocalDateTime.now().format(dateTimeFormatter) + ".txt", changeLog);
	}

	@Test
	@Ignore
	public void eventoAuditForCommit() throws Exception {
		System.out.println("TypeMapping(PersonaEvento.class): " + javers.getTypeMapping(PersonaEvento.class).prettyPrint());

		Long entityId = 3750L;
		CommitId commitId = CommitId.valueOf("3.0");

		eventoAuditForCommit(entityId, commitId);

		System.out.println("FATTO");
	}

	@Test
	//@Ignore
	public void accreditamentoAuditToFile() throws Exception {
		Long entityId = 1225L;
		auditEntity("AccreditamentoAudit", entityId, false, null, null);

		System.out.println("accreditamentoCommitToFile FATTO");
	}

	private void eventoAuditForCommit(Long eventoId, CommitId commitId) throws Exception {
		if(commitId == null || eventoId == null) {
			System.out.println("eventoAuditForCommit - DATI MANCANTI");
			return;
		}

		Long entityId = eventoId;
		String fileName = "Evento_" + entityId;
		Evento e = eventoRepository.findOne(entityId);

		String changeLog = null;
		fileName += "-Commitid_" + commitId;
		List<Change> changes = javers.findChanges(QueryBuilder
				.byInstanceId(entityId,e.getClass())
				.withCommitId(commitId)
				.withNewObjectChanges(true)
				.withChildValueObjects()
				.build());
		changeLog = javers.processChangeList(changes, new EcmTextChangeLog(entityId, e.getClass()));
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
		fileName = "c:\\JAVERS_" + fileName + "_" + LocalDateTime.now().format(dateTimeFormatter) + ".txt";
		saveToFile(fileName, changeLog);
		System.out.println("eventoAuditForCommit - FATTO file: " + fileName);
	}

	@Test
	@Ignore
	public void eventoAudit() throws Exception {
		System.out.println("TypeMapping(PersonaEvento.class): " + javers.getTypeMapping(PersonaEvento.class).prettyPrint());

		Long entityId = 3750L;
		String fileName = "Evento_" + entityId;
		Evento e = eventoRepository.findOne(entityId);

		JqlQuery jqlQuerySnapshot = QueryBuilder
				.byInstanceId(entityId,e.getClass())
				.limit(1)
				.withNewObjectChanges(true)
				.withChildValueObjects()
				.build();
		List<CdoSnapshot> lastSnapshotEventoList = javers.findSnapshots(jqlQuerySnapshot);
		System.out.println("snapshots1.size() - " + lastSnapshotEventoList.size());
		CommitId commitId = null;
		if(lastSnapshotEventoList.size() != 0) {
			commitId = lastSnapshotEventoList.get(0).getCommitId();
			System.out.println("commitId - " + commitId);
		}
		String changeLog = null;
		if(commitId != null) {
			fileName += "-Commitid_" + commitId;
			List<Change> changes = javers.findChanges(QueryBuilder
					.byInstanceId(entityId,e.getClass())
					.withCommitId(commitId)
					.withNewObjectChanges(true)
					.withChildValueObjects()
					.build());
			changeLog = javers.processChangeList(changes, new EcmTextChangeLog(entityId, e.getClass()));
		} else {
			List<Change> changes = javers.findChanges(QueryBuilder
					.byInstanceId(entityId,e.getClass())
					.withNewObjectChanges(true)
					.withChildValueObjects()
					.build());
			changeLog = javers.processChangeList(changes, new EcmTextChangeLog(entityId, e.getClass()));
		}

//		List<CdoSnapshot> snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(entityId,e.getClass()).build());
//		System.out.println("snapshots.size() - " + snapshots.size());
//		if(snapshots.size() != 0) {
//			snapshots.get(0).getVersion();
//			snapshots.get(0).getCommitId();
//			List<Change> changes = javers.findChanges(QueryBuilder
//					.byInstanceId(entityId,e.getClass())
//					.withVersion(snapshots.get(0).getVersion())
//					.withNewObjectChanges(true)
//					.withChildValueObjects()
//					.build());
//			String changeLog = javers.processChangeList(changes, new SimpleTextChangeLog());
//
//			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
//
//			saveToFile("c:\\JAVERS_" + e.getId() + "_" + LocalDateTime.now().format(dateTimeFormatter) + ".txt", changeLog);
//		}

		System.out.println("FATTO");

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
		saveToFile("c:\\JAVERS_" + fileName + "_" + LocalDateTime.now().format(dateTimeFormatter) + ".txt", changeLog);
	}

	public void saveToFile(String fileName, String changeLog) throws FileNotFoundException, UnsupportedEncodingException{
        PrintWriter writer = new PrintWriter(fileName, "UTF-8");
        writer.println(changeLog);
        writer.close();
    }

	private void auditEntity(String entity, Long entityId, Boolean lastCommit, Long commitMajorId, Integer commitMinorId){
		//LOGGER.info(Utils.getLogMessage("GET /audit/entity/" + entity + "/id/" + entityId));

		boolean showLastCommit = false;
		if(lastCommit != null){
			showLastCommit = lastCommit.booleanValue();
		}

		try {
			Class entityClass = null;
			if(entity.contains(".")) {
				//Classe completa di package
				entityClass = Class.forName(entity);
			} else {
				//Classe senza package
				if(entity.contains("Audit"))
					entityClass = Class.forName("it.tredi.ecm.audit.entity." + entity);
				else
					entityClass = Class.forName("it.tredi.ecm.dao.entity." + entity);
			}
			//Audit entity
			CommitId commitId = null;
			if(showLastCommit) {
				JqlQuery jqlQuerySnapshot = QueryBuilder
						.byInstanceId(entityId,entityClass)
						.limit(1)
						.withNewObjectChanges(true)
						.withChildValueObjects()
						.build();
				List<CdoSnapshot> lastSnapshotEventoList = javers.findSnapshots(jqlQuerySnapshot);
				System.out.println("snapshots1.size() - " + lastSnapshotEventoList.size());
				if(lastSnapshotEventoList.size() != 0) {
					commitId = lastSnapshotEventoList.get(0).getCommitId();
					System.out.println("commitId - " + commitId);
				}
			}
			//per debug
			if(commitMajorId != null && commitMinorId != null)
				commitId = new CommitId(commitMajorId, commitMinorId);

			String changeLog = null;
			//AuditInfo auditInfo = null;
			if(commitId != null) {
				List<Change> changes = javers.findChanges(QueryBuilder
						.byInstanceId(entityId,entityClass)
						.withCommitId(commitId)
						.withNewObjectChanges(true)
						.withChildValueObjects()
						.build());
				changeLog = javers.processChangeList(changes, new EcmTextChangeLog(entityId, entityClass));
				//auditInfo = javers.processChangeList(changes, new EcmAuditInfoChangeLog(entityClass, entityId, javers, auditService, accountService));
			} else {
				List<Change> changes = javers.findChanges(QueryBuilder
						.byInstanceId(entityId,entityClass)
						.withNewObjectChanges(true)
						.withChildValueObjects()
						.build());
				changeLog = javers.processChangeList(changes, new EcmTextChangeLog(entityId, entityClass));
				//auditInfo = javers.processChangeList(changes, new EcmAuditInfoChangeLog(entityClass, entityId, javers, auditService, accountService));
			}

			//auditInfo.setFullText(changeLog);
			saveToFile("c:\\JAVERS_TXT_DIFF_BY_COMMIT" + entity + "-" + entityId + ".txt", changeLog);
			//printAuditInfo("c:\\JAVERS_ECM_DIFF_BY_COMMIT" + entity + "-" + entityId + ".csv", auditInfo);

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
