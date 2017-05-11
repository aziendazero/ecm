package it.tredi.ecm;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import it.tredi.ecm.cogeaps.XmlReportValidator;
import it.tredi.ecm.dao.entity.AnagrafeRegionaleCrediti;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.RendicontazioneInviata;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFADEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.dao.repository.AnagrafeRegionaleCreditiRepository;
import it.tredi.ecm.service.AnagrafeRegionaleCreditiService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.utils.Utils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("abarducci")
@WithUserDetails("segreteria1")
//@ActiveProfiles("dev")
//@WithUserDetails("LBENEDETTI")
@Rollback(false)
//@Ignore
public class AnagrafeRegionaleCreditiTest {

	@Autowired private EventoService eventoService;
	@PersistenceContext EntityManager entityManager;

	@Autowired private AnagrafeRegionaleCreditiRepository anagrafeRegionaleCreditiRepository;
	@Autowired private AnagrafeRegionaleCreditiService anagrafeRegionaleCreditiService;
	@Autowired private FileService fileService;


	@Test
	@Transactional
	@Ignore
	public void create() throws Exception {
		AnagrafeRegionaleCrediti a1 = new AnagrafeRegionaleCrediti();
		a1.setCognome("Pranteda");
		a1.setNome("Domenico");
		a1.setCodiceFiscale("PRNDNC86H23Z112R");
		anagrafeRegionaleCreditiRepository.save(a1);

		AnagrafeRegionaleCrediti a2 = new AnagrafeRegionaleCrediti();
		a2.setCognome("Iommi");
		a2.setNome("Thomas");
		a2.setCodiceFiscale("PRNDNC86H23Z112D");
		anagrafeRegionaleCreditiRepository.save(a2);

		Set<AnagrafeRegionaleCrediti> items = new HashSet<AnagrafeRegionaleCrediti>();
		items.add(a1);
		items.add(a2);

		Evento e = eventoService.getEvento(1357L);
		e.setAnagrafeRegionaleCrediti(items);
	}

	@Test
	@Transactional
	public void createFromFileXml() throws Exception {
		String fileName = "report-20170511-0953.xml";
		URL fileUrl = Thread.currentThread().getContextClassLoader().getResource("report-20170511-0953.xml");
		byte[] reportEventoXml = Files.readAllBytes(Paths.get(fileUrl.toURI()));
		Set<AnagrafeRegionaleCrediti> entities = anagrafeRegionaleCreditiService.extractAnagrafeRegionaleCreditiPartecipantiFromXml(fileName, reportEventoXml);
		anagrafeRegionaleCreditiRepository.save(entities);
	}

	@Test
	@Transactional
	@Ignore
	public void modify() throws Exception {
		AnagrafeRegionaleCrediti a3 = new AnagrafeRegionaleCrediti();
		a3.setCognome("Luconi");
		a3.setNome("Elisa");
		a3.setCodiceFiscale("PRNDNC86H23Z112E");
		anagrafeRegionaleCreditiRepository.save(a3);

		Set<AnagrafeRegionaleCrediti> items = new HashSet<AnagrafeRegionaleCrediti>();
		items.add(a3);

		Evento e = eventoService.getEvento(1357L);
		e.setAnagrafeRegionaleCrediti(items);
	}

	@Test
	@Transactional
	@Ignore
	public void extractInfo() throws Exception {
		Evento e = eventoService.getEvento(1357L);
		//RendicontazioneInviata r = e.getUltimaRendicontazioneInviata();

		File file = fileService.getFile(1359L);

		Set<AnagrafeRegionaleCrediti> items = anagrafeRegionaleCreditiService.extractAnagrafeRegionaleCreditiPartecipantiFromXml(file.getNomeFile(), file.getData());

		for(AnagrafeRegionaleCrediti a : items)
			System.out.println(a.getCodiceFiscale() + " : " + a.getCrediti() + " in data " + a.getData());

		e.setAnagrafeRegionaleCrediti(items);
	}

	@Test
	@Transactional
	@Ignore
	public void getProfessioni() throws Exception {
//		Set<Professione> professioni = anaService.getProfessioniAnagrafeAventeCrediti(37L, 2016);
//		System.out.println(professioni.size() + " che hanno avuto crediti");
//
//		for(Professione p : professioni){
//			System.out.println(p.getNome());
//		}

		System.out.println(anagrafeRegionaleCreditiService.getProfessioniAnagrafeAventeCrediti(37L, 2016) + " Professioni hanno avuto crediti");

	}

	@Test
	@Transactional
	@Ignore
	public void getAll() throws Exception {
		Set<AnagrafeRegionaleCrediti> items = anagrafeRegionaleCreditiService.getAll(2016);

		System.out.println(anagrafeRegionaleCreditiService.getProfessioniAnagrafeAventeCrediti(37L, 2016) + " Professioni hanno avuto crediti");

	}


}
