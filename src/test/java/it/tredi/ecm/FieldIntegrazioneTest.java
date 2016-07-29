package it.tredi.ecm;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.FieldEditabileAccreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazione;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;
import it.tredi.ecm.dao.repository.FieldEditabileAccreditamentoRepository;
import it.tredi.ecm.dao.repository.FieldIntegrazioneAccreditamentoRepository;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.DatiAccreditamentoService;

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
	@Autowired private FieldEditabileAccreditamentoRepository repo;
	@Autowired private FieldIntegrazioneAccreditamentoRepository repoIntegrazione;
	
	private Long providerId;
	private Long accreditamentoId;
	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}
	
	//@Before
	@Transactional
	public void init() {
	}
	
	@After
	public void clean(){
//		accreditamentoRepository.delete(this.accreditamentoId);
//		providerRepository.delete(this.providerId);
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
		
		setField(provider, field.getIdField().getNameRef(), field.getNewValue());
		
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
			Object newValue = getField(provider, field.getIdField().getNameRef());
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
			setField(provider, field.getIdField().getNameRef(), field.getNewValue());
		}
		
		print(provider, null);
	}
	
	@Test
	@Transactional
	//@Ignore
	public void createFieldIntegrazioneDatiAccreditamento() throws Exception{
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(304L);
		
		//Segreteria ha sbloccato gli id
		List<FieldEditabileAccreditamento> idEditabili = new ArrayList<FieldEditabileAccreditamento>();
		idEditabili.add(new FieldEditabileAccreditamento(IdFieldEnum.DATI_ACCREDITAMENTO__PROCEDURE_FORMATIVE,accreditamento));
		
		//PROVIDER modifica i campi
		DatiAccreditamento datiAccreditamento = datiAccreditamentoService.getDatiAccreditamento(400L);
		//DatiAccreditamento datiAccreditamento = new DatiAccreditamento();
		//datiAccreditamento.setProcedureFormative(new HashSet<ProceduraFormativa>(Arrays.asList(ProceduraFormativa.FAD,ProceduraFormativa.RES)));
		List<FieldIntegrazioneAccreditamento> idIntegrazione = new ArrayList<FieldIntegrazioneAccreditamento>();
		for(FieldEditabileAccreditamento field : idEditabili){
			Object newValue = getField(datiAccreditamento, field.getIdField().getNameRef());
			idIntegrazione.add(new FieldIntegrazioneAccreditamento(field.getIdField(), field.getAccreditamento(), newValue, TipoIntegrazioneEnum.MODIFICA));
		}
		
		repoIntegrazione.save(idIntegrazione);
	}
	
	@Test
	@Ignore
	public void applyFieldIntegrazioneDatiAccreditamento() throws Exception{
		Set<FieldIntegrazioneAccreditamento> idIntegrazione = repoIntegrazione.findAllByAccreditamentoId(304L);
		DatiAccreditamento dati = new DatiAccreditamento();
		for(FieldIntegrazioneAccreditamento field : idIntegrazione){
			setField(dati, field.getIdField().getNameRef(), field.getNewValue());
		}
		
		print(dati, IdFieldEnum.DATI_ACCREDITAMENTO__PROCEDURE_FORMATIVE.getNameRef());
	}
	
	private void setField(Object dst, String fieldName, Object fieldValue) throws Exception{
		Method method = getSetterMethodFor(dst.getClass(), fieldName);
		if(method != null)
			method.invoke(dst, fieldValue);
	}
	
	private Object getField(Object dst, String fieldName) throws Exception{
		boolean serializable = false;
		Method method = getGetterMethodFor(dst.getClass(), fieldName);
		if(method != null){
			Object newValue = method.invoke(dst);
			serializable = java.io.Serializable.class.isAssignableFrom(newValue.getClass());
			if(serializable)
				return method.invoke(dst);
			else{
				HashSet<?> newSet = new HashSet<>();
				newValue = method.invoke(dst);
				for(Object obj : ((Set<?>)newValue)){
					//newSet.add(obj);
					//TODO BARDUZZZ
				}
				return new HashSet<ProceduraFormativa>();
			}
		}
		return null;
	}
	
	private Method getSetterMethodFor(Class obj, String fieldName) throws IntrospectionException{
		BeanInfo info;
		info = Introspector.getBeanInfo(obj);
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			if(pd.getName().equals(fieldName))
				return pd.getWriteMethod();
		}
		return null;
	}
	
	private Method getGetterMethodFor(Class obj, String fieldName) throws IntrospectionException{
		BeanInfo info;
		info = Introspector.getBeanInfo(obj);
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			if(pd.getName().equals(fieldName))
				return pd.getReadMethod();
		}
		return null;
	}

	private void print(Object obj,String fieldName) throws Exception{
		BeanInfo info;
		info = Introspector.getBeanInfo(obj.getClass());
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			if((fieldName != null && pd.getName().equals(fieldName)) || fieldName == null)
				System.out.println(pd.getName() + ": " + pd.getReadMethod().invoke(obj));
		}
	}
}
