package it.tredi.ecm;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.FieldEditabileAccreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneHistoryContainer;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;
import it.tredi.ecm.dao.repository.AnagraficaRepository;
import it.tredi.ecm.dao.repository.FieldEditabileAccreditamentoRepository;
import it.tredi.ecm.dao.repository.FieldIntegrazioneAccreditamentoRepository;
import it.tredi.ecm.dao.repository.ProfessioneRepository;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.DatiAccreditamentoService;
import it.tredi.ecm.service.FieldIntegrazioneAccreditamentoService;
import it.tredi.ecm.service.IntegrazioneService;
import it.tredi.ecm.service.PersonaService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("tom")
@WithUserDetails("segreteria1")
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING) // ordina i test in base al nome crescente
@Rollback(false)
//@Ignore
public class FieldIntegrazioneTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private DatiAccreditamentoService datiAccreditamentoService;
	@Autowired private PersonaService personaService;
	@Autowired private FieldEditabileAccreditamentoRepository repo;
	@Autowired private FieldIntegrazioneAccreditamentoRepository repoIntegrazione;
	@Autowired private ProfessioneRepository professioneRepository;
	@Autowired private AnagraficaRepository anagraficaRepository;
	@Autowired private FieldIntegrazioneAccreditamentoService fiaService;

	@Autowired private IntegrazioneService integrazioneService;

	@Autowired
	private ApplicationContext appContext;

	private Long providerId;
	private Long accreditamentoId;
	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}


	@Test
	@Ignore
	public void testSetFieldValue() throws Exception{
		FieldIntegrazioneAccreditamento field = new FieldIntegrazioneAccreditamento();
		field.setIdField(IdFieldEnum.PROVIDER__CODICE_FISCALE);
		field.setNewValue("bbbbbbb");

		Provider provider = new Provider();
		provider.setDenominazioneLegale("3DInformatica");
		provider.setCodiceFiscale("AAAAAAA");

		System.out.println("BEFORE: ");
		print(provider, null);

		integrazioneService.setField(provider, field.getIdField().getNameRef(), field.getNewValue());

		System.out.println("AFTER: ");
		print(provider, IdFieldEnum.PROVIDER__CODICE_FISCALE.getNameRef());
	}

	@Test
	@Transactional
	@Ignore
	public void createFieldIntegrazioneProvider() throws Exception{
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(304L);

		//Segreteria ha sbloccato gli id
		List<FieldEditabileAccreditamento> idEditabili = new ArrayList<FieldEditabileAccreditamento>();
		idEditabili.add(new FieldEditabileAccreditamento(IdFieldEnum.PROVIDER__CODICE_FISCALE, accreditamento));
		idEditabili.add(new FieldEditabileAccreditamento(IdFieldEnum.PROVIDER__DENOMINAZIONE_LEGALE, accreditamento));
		idEditabili.add(new FieldEditabileAccreditamento(IdFieldEnum.PROVIDER__RAGIONE_SOCIALE, accreditamento));
		idEditabili.add(new FieldEditabileAccreditamento(IdFieldEnum.PROVIDER__NO_PROFIT, accreditamento));

		//PROVIDER modifica i campi
		Provider provider = accreditamento.getProvider();
		List<FieldIntegrazioneAccreditamento> idIntegrazione = new ArrayList<FieldIntegrazioneAccreditamento>();
		for(FieldEditabileAccreditamento field : idEditabili){
			Object newValue = integrazioneService.getField(provider, field.getIdField().getNameRef());
			idIntegrazione.add(new FieldIntegrazioneAccreditamento(field.getIdField(), field.getAccreditamento(), newValue, TipoIntegrazioneEnum.MODIFICA));
		}
		repoIntegrazione.save(idIntegrazione);
	}

	@Test
	@Transactional
	@Ignore
	public void createFieldIntegrazioneDatiAccreditamento() throws Exception{
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(416L);

		//Segreteria ha sbloccato gli id
		List<FieldEditabileAccreditamento> idEditabili = new ArrayList<FieldEditabileAccreditamento>();
		idEditabili.add(new FieldEditabileAccreditamento(IdFieldEnum.DATI_ACCREDITAMENTO__PROCEDURE_FORMATIVE, accreditamento));
		idEditabili.add(new FieldEditabileAccreditamento(IdFieldEnum.DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI, accreditamento));
		idEditabili.add(new FieldEditabileAccreditamento(IdFieldEnum.DATI_ACCREDITAMENTO__DISCIPLINE, accreditamento));

		//PROVIDER modifica i campi
		DatiAccreditamento datiAccreditamento = accreditamento.getDatiAccreditamento();
		List<FieldIntegrazioneAccreditamento> idIntegrazione = new ArrayList<FieldIntegrazioneAccreditamento>();
		for(FieldEditabileAccreditamento field : idEditabili){
			Object newValue = integrazioneService.getField(datiAccreditamento, field.getIdField().getNameRef());
			idIntegrazione.add(new FieldIntegrazioneAccreditamento(field.getIdField(), field.getAccreditamento(), newValue, TipoIntegrazioneEnum.MODIFICA));
		}

		repoIntegrazione.save(idIntegrazione);
	}

	@Test
	@Transactional
	@Ignore
	public void createFieldPersona() throws Exception{
		Persona persona = personaService.getPersona(584L);
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(416L);

		//Segreteria ha sbloccato gli id
		List<FieldEditabileAccreditamento> idEditabili = new ArrayList<FieldEditabileAccreditamento>();
		idEditabili.add(new FieldEditabileAccreditamento(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__COGNOME,accreditamento,persona.getId()));
		idEditabili.add(new FieldEditabileAccreditamento(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__PROFESSIONE,accreditamento,persona.getId()));

		List<FieldIntegrazioneAccreditamento> idIntegrazione = new ArrayList<FieldIntegrazioneAccreditamento>();
		for(FieldEditabileAccreditamento field : idEditabili){
			Object newValue = integrazioneService.getField(persona, field.getIdField().getNameRef());
			idIntegrazione.add(new FieldIntegrazioneAccreditamento(field.getIdField(), field.getAccreditamento(), persona.getId(), newValue, TipoIntegrazioneEnum.MODIFICA));
		}

		repoIntegrazione.save(idIntegrazione);
	}

	@Ignore
	@Test
	public void test(){
		Anagrafica a = anagraficaRepository.findOne(585L);
		a.setCognome("Stronzo");

		Persona p = personaService.getPersona(699L);
		p.setRuolo(Ruolo.RICHIEDENTE);
		p.setAnagrafica(a);

		Accreditamento accreditamento = accreditamentoService.getAccreditamento(416L);
		FieldIntegrazioneAccreditamento d = new FieldIntegrazioneAccreditamento(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__ATTO_NOMINA, accreditamento, null, TipoIntegrazioneEnum.MODIFICA);
		repoIntegrazione.save(d);
	}

	private void print(Object obj,String fieldName) throws Exception{
		BeanInfo info;
		info = Introspector.getBeanInfo(obj.getClass());
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			if((fieldName != null && pd.getName().equals(fieldName)) || fieldName == null)
				System.out.println(pd.getName() + ": " + pd.getReadMethod().invoke(obj));
		}
	}

	@Ignore
	@Test
	public void queryProva() throws Exception {
		Long accreditamentoId = 1228L;
		accreditamentoService.controllaValidazioneIntegrazione(accreditamentoId);
	}

	@Test
	@Transactional
	@Ignore
	public void fittizioTest() {
		Long accreditamentoId = 1470L;
		Long workflowId = 44008L;
		AccreditamentoStatoEnum stato = AccreditamentoStatoEnum.INTEGRAZIONE;
		Set<FieldIntegrazioneAccreditamento> set = fiaService.getAllFieldIntegrazioneForAccreditamentoByContainer(accreditamentoId, stato, workflowId);
		System.out.println("NON FITTIZI:");
		for(FieldIntegrazioneAccreditamento fi : set)
			System.out.println(fi.getIdField().name());
		Set<FieldIntegrazioneAccreditamento> fittizi =  fiaService.getAllFieldIntegrazioneFittiziForAccreditamentoByContainer(accreditamentoId, stato, workflowId);
		System.out.println("Fittizi:");
		for(FieldIntegrazioneAccreditamento fi : fittizi)
			System.out.println(fi.getIdField().name());
	}

	@Test
	@Transactional
	@Ignore
	public void integrazioneFittiziaTest() {
		Long accreditamentoId = 1470L;
		Long workflowId = 44008L;
		AccreditamentoStatoEnum stato = AccreditamentoStatoEnum.INTEGRAZIONE;
		FieldIntegrazioneHistoryContainer container = fiaService.getContainer(accreditamentoId, stato, workflowId);
		FieldIntegrazioneAccreditamento fittizio1 = new FieldIntegrazioneAccreditamento(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__ATTO_NOMINA, accreditamentoService.getAccreditamento(accreditamentoId), -1,null);
		FieldIntegrazioneAccreditamento fittizio2 = new FieldIntegrazioneAccreditamento(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__COGNOME, accreditamentoService.getAccreditamento(accreditamentoId), -1,null);
		FieldIntegrazioneAccreditamento fittizio3 = new FieldIntegrazioneAccreditamento(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__CV, accreditamentoService.getAccreditamento(accreditamentoId), -1,null);
		List<FieldIntegrazioneAccreditamento> list = new ArrayList<FieldIntegrazioneAccreditamento>();
		list.add(fittizio1);
		list.add(fittizio2);
		list.add(fittizio3);
		fiaService.save(list);
		container.getIntegrazioni().addAll(list);
		fiaService.saveContainer(container);
		for(FieldIntegrazioneAccreditamento f : container.getIntegrazioni())
			System.out.println(f.getIdField().name());
		container = fiaService.getContainer(accreditamentoId, stato, workflowId);
		for(FieldIntegrazioneAccreditamento f : container.getIntegrazioni())
			System.out.println(f.getIdField().name());
	}


//	private Class getFirstGenericParameterTypesOfFirstParameter(Method method) throws Exception{
//		Type[] genericParameterTypes = method.getGenericParameterTypes();
//
//		if(genericParameterTypes[0] instanceof ParameterizedType){
//			ParameterizedType aType = (ParameterizedType) genericParameterTypes[0];
//			Type[] parameterArgTypes = aType.getActualTypeArguments();
//			return (Class) parameterArgTypes[0];
//		}
//
//		return null;
//	}
//
//	private Class getFirstGenericReturnType(Method method) throws Exception{
//		Type returnType  = method.getGenericReturnType();
//
//		if(returnType instanceof ParameterizedType){
//			ParameterizedType type = (ParameterizedType) returnType;
//			Type[] typeArguments = type.getActualTypeArguments();
//			return (Class) typeArguments[0];
//		}
//
//		return null;
//	}

}
