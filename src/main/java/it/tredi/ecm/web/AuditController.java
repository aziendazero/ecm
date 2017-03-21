package it.tredi.ecm.web;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.javers.core.Javers;
import org.javers.core.changelog.SimpleTextChangeLog;
import org.javers.core.commit.CommitId;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.audit.AuditInfo;
import it.tredi.ecm.audit.EcmAuditInfoChangeLog;
import it.tredi.ecm.audit.EcmTextChangeLog;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.service.AccountServiceImpl;
import it.tredi.ecm.service.AuditService;
import it.tredi.ecm.service.AuditServiceImpl;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;

@Controller
@SessionAttributes("eventoWrapper")
public class AuditController {
	public static final Logger LOGGER = LoggerFactory.getLogger(AuditController.class);

	@Autowired private AccountService accountService;

	@Autowired private Javers javers;
	@Autowired private AuditService auditService;

	private final String SHOW = "audit/auditShow";
	private final String ERROR = "fragments/errorsAjax";

	@RequestMapping(value= "/audit/entity/{entity}/entityId/{entityId}", method = RequestMethod.GET)
	public String auditEntity(@PathVariable String entity, @PathVariable Long entityId, @RequestParam(required = false) Boolean lastCommit
			, @RequestParam(required = false) Long commitMajorId, @RequestParam(required = false) Integer commitMinorId
			, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /audit/entity/" + entity + "/id/" + entityId));

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
			AuditInfo auditInfo = null;
			if(commitId != null) {
				List<Change> changes = javers.findChanges(QueryBuilder
						.byInstanceId(entityId,entityClass)
						.withCommitId(commitId)
						.withNewObjectChanges(true)
						.withChildValueObjects()
						.build());
				//changeLog = javers.processChangeList(changes, new EcmTextChangeLog(entityId, entityClass));
				auditInfo = javers.processChangeList(changes, new EcmAuditInfoChangeLog(entityClass, entityId, javers, auditService, accountService));
			} else {
				List<Change> changes = javers.findChanges(QueryBuilder
						.byInstanceId(entityId,entityClass)
						.withNewObjectChanges(true)
						.withChildValueObjects()
						.build());
				//changeLog = javers.processChangeList(changes, new EcmTextChangeLog(entityId, entityClass));
				auditInfo = javers.processChangeList(changes, new EcmAuditInfoChangeLog(entityClass, entityId, javers, auditService, accountService));
			}

			//auditInfo.setFullText(changeLog);

			model.addAttribute("auditInfo", auditInfo);
			return SHOW;
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /audit/entity/" + entity + "/id/" + entityId),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	@RequestMapping(value= "/auditvo/entity/{entity}/entityId/{entityId}", method = RequestMethod.GET)
	public String auditEntity(@PathVariable String entity, @PathVariable Long entityId, @RequestParam(required = true) String pathValueObject, @RequestParam(required = false) Boolean lastCommit
			, @RequestParam(required = false) Long commitMajorId, @RequestParam(required = false) Integer commitMinorId
			, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /audit/entity/" + entity + "/id/" + entityId + "/pathValueObject/" + pathValueObject));

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
						.byValueObjectId(entityId, entityClass, pathValueObject)
						.limit(1)
						.withNewObjectChanges(true)
						//.withChildValueObjects()
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
			AuditInfo auditInfo = null;
			if(commitId != null) {
				List<Change> changes = javers.findChanges(QueryBuilder
						.byValueObjectId(entityId, entityClass, pathValueObject)
						.withCommitId(commitId)
						.withNewObjectChanges(true)
						//.withChildValueObjects()
						.build());
				//changeLog = javers.processChangeList(changes, new EcmTextChangeLog(entityId, entityClass));
				auditInfo = javers.processChangeList(changes, new EcmAuditInfoChangeLog(entityClass, entityId, javers, auditService, accountService));
			} else {
				List<Change> changes = javers.findChanges(QueryBuilder
						.byValueObjectId(entityId, entityClass, pathValueObject)
						.withNewObjectChanges(true)
						//.withChildValueObjects()
						.build());
				//changeLog = javers.processChangeList(changes, new EcmTextChangeLog(entityId, entityClass));
				auditInfo = javers.processChangeList(changes, new EcmAuditInfoChangeLog(entityClass, entityId, javers, auditService, accountService));
			}

			//auditInfo.setFullText(changeLog);

			model.addAttribute("auditInfo", auditInfo);
			return SHOW;
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /audit/entity/" + entity + "/id/" + entityId + "/pathValueObject/" + pathValueObject),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	private void eventoAudit(Evento evento) throws Exception {
		System.out.println("TypeMapping(PersonaEvento.class): " + javers.getTypeMapping(PersonaEvento.class).prettyPrint());

		Long entityId = evento.getId();
		String fileName = "Evento_" + entityId;

		JqlQuery jqlQuerySnapshot = QueryBuilder
				.byInstanceId(entityId,evento.getClass())
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
					.byInstanceId(entityId,evento.getClass())
					.withCommitId(commitId)
					.withNewObjectChanges(true)
					.withChildValueObjects()
					.build());
			changeLog = javers.processChangeList(changes, new SimpleTextChangeLog());
		} else {
			List<Change> changes = javers.findChanges(QueryBuilder
					.byInstanceId(entityId,evento.getClass())
					.withNewObjectChanges(true)
					.withChildValueObjects()
					.build());
			changeLog = javers.processChangeList(changes, new SimpleTextChangeLog());
		}
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
		saveToFile("c:\\JAVERS_" + fileName + "_" + LocalDateTime.now().format(dateTimeFormatter) + ".txt", changeLog);
	}

	private void saveToFile(String fileName, String changeLog) throws FileNotFoundException, UnsupportedEncodingException{
        PrintWriter writer = new PrintWriter(fileName, "UTF-8");
        writer.println(changeLog);
        writer.close();
    }

}
