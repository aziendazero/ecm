package it.tredi.ecm;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.AccreditamentoDiff;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.PersonaDiff;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.repository.AccreditamentoRepository;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.DiffService;
import it.tredi.ecm.service.DiffServiceImpl;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.SedeService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
//@WithUserDetails("provider")
@Ignore
@Rollback(true)
public class AccreditamentoTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired AccreditamentoService accreditamentoService;
	@Autowired SedeService sedeService;
	@Autowired FileService fileService;
	@Autowired AccreditamentoRepository accreditamentoRepository;

	@Autowired DiffService diffService;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}

	@Test
	@Ignore
	public void testAccreditamentoQuery() throws Exception {

		System.out.println("pippo");

		Set<Accreditamento> set = accreditamentoService.getAllAccreditamentiByStatoAndTipoDomanda(AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA_ASSEGNAMENTO, AccreditamentoTipoEnum.STANDARD, false);

		for (Accreditamento a : set) {
			System.out.println("id: " + a.getId());
		}
	}

	@Test
	@Ignore
	public void testSgancioSede() {

		System.out.println("begin");

		Long sedeId = 1702L;

		Sede sedeVerbale = sedeService.getSede(sedeId);
		sedeVerbale.setProvider(null);
		sedeService.save(sedeVerbale);

		System.out.println("end");

	}

	@Test
	@Transactional
	@Ignore
	public void addDelibera(){
		Long accreditamentoId = 1352L;
		File fileFirmato = fileService.getFile(5990L);
		LocalDate dataDelibera = LocalDate.now();
		String numeroDelibera = "123";


		fileFirmato.setDataDelibera(dataDelibera);
		fileFirmato.setNumeroDelibera(numeroDelibera);
		//fileService.save(fileFirmato);

		Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
		accreditamento.setDecretoAccreditamento(fileFirmato);
		accreditamentoService.saveAndAudit(accreditamento);
	}

	@Test
	@Ignore
	public void testRepositoryModificaDataScadenzaAccreditamentiAvviatiForProvider() {

		System.out.println("begin");

		Long providerId = 160L;
		AccreditamentoTipoEnum tipoDomanda = AccreditamentoTipoEnum.STANDARD;

		Set<Accreditamento> accrs = accreditamentoRepository.getAccreditamentiAvviatiForProvider(providerId, tipoDomanda);
		System.out.println("accreditamentoRepository.getAccreditamentiAvviatiForProvider size: " + accrs.size());
		for(Accreditamento acc : accrs)
			System.out.println("acc id: " + acc.getId());


		System.out.println("end");

	}

	@Test
	@Ignore
	public void testRepositoryModificaDataScadenza() {

		System.out.println("testRepositoryModificaDataScadenza begin");

		List<Integer> listaGiorni = new ArrayList<Integer>();
		listaGiorni.add(4);
		listaGiorni.add(5);
		listaGiorni.add(30);

		for(Integer giorni : listaGiorni) {
			System.out.println("-- giorni: " + giorni + " --");
			Set<Accreditamento> accrs = accreditamentoRepository.findAllAccreditamentiInScadenzaNeiProssimiGiorni(giorni);
			System.out.println("\taccreditamentoRepository.findAllAccreditamentiInScadenzaNeiProssimiGiorni size: " + accrs.size());
			for(Accreditamento acc : accrs)
				System.out.println("\t\tacc id: " + acc.getId());

			long numAccrs = accreditamentoRepository.countAllAccreditamentiInScadenzaNeiProssimiGiorni(giorni);
			System.out.println("\taccreditamentoRepository.countAllAccreditamentiInScadenzaNeiProssimiGiorni: " + numAccrs);
		}
		System.out.println("testRepositoryModificaDataScadenza end");

	}

	@Test
	@Transactional
	@Rollback(true)
	@Ignore
	public void testConfrontaDiff() {
		Accreditamento accreditamento1 = accreditamentoService.getAccreditamento(4413L);
		AccreditamentoDiff diff1 = diffService.creaAllDiffAccreditamento(accreditamento1);

		Accreditamento accreditamento2 = accreditamentoService.getAccreditamento(143586L);
		AccreditamentoDiff diff2 = diffService.creaAllDiffAccreditamento(accreditamento2);

		Set<FieldValutazioneAccreditamento> valutazioniDiff = diffService.confrontaDiffAccreditamento(diff1, diff2);
		assertTrue(!valutazioniDiff.isEmpty());

	}

}
