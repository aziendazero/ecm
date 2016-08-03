package it.tredi.ecm;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.id.IdentityGenerator.GetGeneratedKeysDelegate;
import org.junit.After;
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
import it.tredi.ecm.dao.entity.BaseEntity;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.FieldEditabileAccreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazione;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;
import it.tredi.ecm.dao.repository.FieldEditabileAccreditamentoRepository;
import it.tredi.ecm.dao.repository.FieldIntegrazioneAccreditamentoRepository;
import it.tredi.ecm.dao.repository.ProfessioneRepository;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.DatiAccreditamentoService;
import it.tredi.ecm.service.IntegrazioneUtils;
import it.tredi.ecm.service.PersonaService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@WithUserDetails("admin")
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING) // ordina i test in base al nome crescente
@Rollback(false)
public class FieldIntegrazioneTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private DatiAccreditamentoService datiAccreditamentoService;
	@Autowired private PersonaService personaService;
	@Autowired private FieldEditabileAccreditamentoRepository repo;
	@Autowired private FieldIntegrazioneAccreditamentoRepository repoIntegrazione;
	@Autowired private ProfessioneRepository professioneRepository;
	
	@Autowired private IntegrazioneUtils integrazioneUtils;

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

		integrazioneUtils.setField(provider, field.getIdField().getNameRef(), field.getNewValue());

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
			Object newValue = integrazioneUtils.getField(provider, field.getIdField().getNameRef());
			idIntegrazione.add(new FieldIntegrazioneAccreditamento(field.getIdField(), field.getAccreditamento(), newValue, TipoIntegrazioneEnum.MODIFICA));
		}
		repoIntegrazione.save(idIntegrazione);
	}

	@Test
	@Ignore
	public void applyFieldIntegrazioneProvider() throws Exception{
		Set<FieldIntegrazioneAccreditamento> idIntegrazione = repoIntegrazione.findAllByAccreditamentoId(304L);
		Provider provider = new Provider();
		for(FieldIntegrazioneAccreditamento field : idIntegrazione){
			integrazioneUtils.setField(provider, field.getIdField().getNameRef(), field.getNewValue());
		}

		print(provider, null);
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
			Object newValue = integrazioneUtils.getField(datiAccreditamento, field.getIdField().getNameRef());
			idIntegrazione.add(new FieldIntegrazioneAccreditamento(field.getIdField(), field.getAccreditamento(), newValue, TipoIntegrazioneEnum.MODIFICA));
		}

		repoIntegrazione.save(idIntegrazione);
	}

	@Test
	@Ignore
	public void applyFieldIntegrazioneDatiAccreditamento() throws Exception{
		Set<FieldIntegrazioneAccreditamento> idIntegrazione = repoIntegrazione.findAllByAccreditamentoId(416L);
		//DatiAccreditamento dati = datiAccreditamentoService.getDatiAccreditamento(396L);
		DatiAccreditamento dati = datiAccreditamentoService.getDatiAccreditamento(118L);
		
		for(FieldIntegrazioneAccreditamento field : idIntegrazione){
			integrazioneUtils.setField(dati, field.getIdField().getNameRef(), field.getNewValue());
		}

		datiAccreditamentoService.save(dati, dati.getAccreditamento().getId());
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
			Object newValue = integrazioneUtils.getField(persona, field.getIdField().getNameRef());
			idIntegrazione.add(new FieldIntegrazioneAccreditamento(field.getIdField(), field.getAccreditamento(), persona.getId(), newValue, TipoIntegrazioneEnum.MODIFICA));
		}

		repoIntegrazione.save(idIntegrazione);
	}

	@Test
	//@Ignore
	public void applyFieldPersona() throws Exception{
		Set<FieldIntegrazioneAccreditamento> idIntegrazione = repoIntegrazione.findAllByAccreditamentoId(416L);
		Persona persona = personaService.getPersona(534L);
		for(FieldIntegrazioneAccreditamento field : idIntegrazione){
			integrazioneUtils.setField(persona, field.getIdField().getNameRef(), field.getNewValue());
		}

		print(persona, IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__PROFESSIONE.getNameRef());

		personaService.save(persona);
	}

	private void print(Object obj,String fieldName) throws Exception{
		BeanInfo info;
		info = Introspector.getBeanInfo(obj.getClass());
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			if((fieldName != null && pd.getName().equals(fieldName)) || fieldName == null)
				System.out.println(pd.getName() + ": " + pd.getReadMethod().invoke(obj));
		}
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
