package it.tredi.ecm.service;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.collection.internal.PersistentSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.BaseEntity;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.repository.FieldIntegrazioneAccreditamentoRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class IntegrazioneUtils {

	@Autowired private FieldIntegrazioneAccreditamentoRepository integrazioneRepo;
	@Autowired private AccreditamentoService accreditamentoService;

	@Autowired
	private ApplicationContext appContext;

	public Object getField(Object dst, String fieldName) throws Exception{

		//se è un file restituisco l'id del file
		if(fieldName.startsWith("files")){
			Method getFiles = getGetterMethodFor(dst.getClass(), "files");
			Set<File> files = (Set<File>) getFiles.invoke(dst);
			for(File f : files){
				if(f.getTipo() == FileEnum.valueOf(fieldName.substring(6)))
					return f.getId();
			}
		}

		if(fieldName.contains(".")){
			//campo composto --> mi riconduco al caso semplice
			dst = getObject(dst, fieldName);
			fieldName = fieldName.substring(fieldName.lastIndexOf(".") + 1 , fieldName.length());
		}

		Method method = getGetterMethodFor(dst.getClass(), fieldName);
		if(method != null){
			Object newValue = method.invoke(dst);
			if(newValue instanceof BaseEntity){
				return ((BaseEntity) newValue).getId();
			}
			else if(newValue instanceof PersistentSet)
			{
				Set<Object> newSet = new HashSet<Object>();
				Class<?> clazz = getTypeByField(dst.getClass(),fieldName);
				if(clazz != null && BaseEntity.class.isAssignableFrom(clazz)){
					for(Object obj : (Set<?>)newValue){
						newSet.add(((BaseEntity)obj).getId());
					}
					return newSet;
				}else{
					newSet.addAll((Set<?>)newValue);
					return newSet;
				}
			}else{
				return newValue;
			}
		}
		return null;
	}

	public void setField(Object dst, String fieldName, Object fieldValue) throws Exception{
		//se è un file restituisco carico sostituisco il file
		if(fieldName.startsWith("files")){
			Method getFiles = getGetterMethodFor(dst.getClass(), "files");
			Set<File> files = (Set<File>) getFiles.invoke(dst);
			for(File f : files){
				if(f.getTipo() == FileEnum.valueOf(fieldName.substring(6)))
					f = (File) getEntityFromRepo(f.getClass(), fieldValue);
			}
		}


		if(fieldName.contains(".")){
			//campo composto --> mi riconduco al caso semplice
			dst = getObject(dst, fieldName);
			fieldName = fieldName.substring(fieldName.lastIndexOf(".") + 1 , fieldName.length());
		}

		Method method = getSetterMethodFor(dst.getClass(), fieldName);
		if(method != null){
			Class<?> clazz = method.getParameterTypes()[0];

			if(BaseEntity.class.isAssignableFrom(clazz)){
				Object object = getEntityFromRepo(clazz, fieldValue);
				method.invoke(dst, object);
			}else if(Collection.class.isAssignableFrom(clazz)){
				//verifico se la collection contiene Entity oppure Altri tipi di dato
				clazz = getTypeByField(dst.getClass(),fieldName);
				if(BaseEntity.class.isAssignableFrom(clazz)){
					Set<Object> newSet = new HashSet<Object>();
					for(Object obj :  (Set<?>)fieldValue){
						newSet.add(getEntityFromRepo(clazz,obj)); 
					}
					method.invoke(dst, newSet);
				}else{
					method.invoke(dst, fieldValue);
				}
			}
			else{
				method.invoke(dst, fieldValue);
			}
		}
	}

	public void setFieldAndSave(Object dst, String fieldName, Object fieldValue) throws Exception{
		setField(dst, fieldName, fieldValue);
		saveEntity(dst.getClass(), dst);
	}

	//recupero il tipo <generic> di un campo
	private Class<?> getTypeByField(Class<?> clazz, String fieldName) throws Exception{
		Field field = null;
		//Si fa cosi, ma se l'oggetto è caricato da repository, hibernate lo wrappa in un suo oggetto e quindi la reflection non funge più
		//Sembrerebbe che gli oggetti di hibernate extends le Entity originali e quindi tentiamo la reflection sulla superClasse
		try{
			field = clazz.getDeclaredField(fieldName);
		}catch (Exception ex){
			field = clazz.getSuperclass().getDeclaredField(fieldName);
		}

		Type genericFieldType = field.getGenericType();

		if(genericFieldType instanceof ParameterizedType){
			ParameterizedType aType = (ParameterizedType) genericFieldType;
			Type[] fieldArgTypes = aType.getActualTypeArguments();
			return (Class<?>) fieldArgTypes[0];
		}

		return (Class<?>)genericFieldType;
	}

	//recupero il metodo GETTER
	public Method getGetterMethodFor(Class<?> clazz, String fieldName) throws IntrospectionException{
		BeanInfo info;
		info = Introspector.getBeanInfo(clazz);
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			if(pd.getName().equals(fieldName))
				return pd.getReadMethod();
		}
		return null;
	}

	//recupero il metodo SETTER
	public Method getSetterMethodFor(Class<?> clazz, String fieldName) throws Exception{
		BeanInfo info;
		info = Introspector.getBeanInfo(clazz);
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			if(pd.getName().equals(fieldName))
				return pd.getWriteMethod();
		}
		return null;
	}

	//carica l'oggetto dal DB -> findOne(Long id)
	private Object getEntityFromRepo(Class<?> clazz, Object fieldValue) throws Exception{
		Object repository = appContext.getBean(clazz.getSimpleName().substring(0,1).toLowerCase() + clazz.getSimpleName().substring(1) + "Repository");
		Class<?>[] cArg = new Class[1];
		cArg[0] = java.io.Serializable.class;
		Method findOne = repository.getClass().getDeclaredMethod("findOne", cArg);
		return findOne.invoke(repository, fieldValue);
	}

	//carica l'oggetto dal DB -> save(Object objectToSave)
	private void saveEntity(Class<?> clazz, Object objectToSave) throws Exception{
		Object repository = appContext.getBean(clazz.getSimpleName().substring(0,1).toLowerCase() + clazz.getSimpleName().substring(1) + "Repository");
		Class<?>[] cArg = new Class[1];
		cArg[0] = Object.class;
		Method save = repository.getClass().getDeclaredMethod("save", cArg);
		save.invoke(repository, objectToSave);
	}

	//elimina l'oggetto dal DB -> delete(Long id)
	public void removeEntityFromRepo(Class<?> clazz, Object fieldValue) throws Exception{
		Object repository = appContext.getBean(clazz.getSimpleName().substring(0,1).toLowerCase() + clazz.getSimpleName().substring(1) + "Repository");
		Class<?>[] cArg = new Class[1];
		cArg[0] = java.io.Serializable.class;
		Method delete = repository.getClass().getDeclaredMethod("delete", cArg);
		delete.invoke(repository, fieldValue);
	}

	//nel caso in cui il fieldName è a più livelli, significa che si vuole accedere ad un campo di un oggetto interno
	//attraverso la composizione dell'oggetto fino a recuperare l'oggetto reale
	//esempio;
	//angarfica.cognome passando un oggetto di tipo Persona
	//getAnagrafica() -> it.tredi.ecm.dao.entity.Anagrafica che ha il campo cognome recuperabile con getCognome()
	private Object getObject(Object dst, String fieldName) throws Exception {
		int dot = fieldName.indexOf(".");
		if(dot > 1){
			String level = fieldName.substring(0,dot);
			Method method = getGetterMethodFor(dst.getClass(), level);
			return getObject(method.invoke(dst), fieldName.substring(dot + 1,fieldName.length()));
		}
		return dst;
	}


	public void applyIntegrazioniAccreditamentoAndSave(Long accreditamentoId, Set<FieldIntegrazioneAccreditamento> fieldIntegrazioni) throws Exception{
		applyIntegrazioniAndSave(accreditamentoService.getAccreditamento(accreditamentoId), fieldIntegrazioni);
	}

	private void applyIntegrazioniAndSave(Accreditamento accreditamento, Set<FieldIntegrazioneAccreditamento> fieldIntegrazioni) throws Exception{
		if(!Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.PROVIDER).isEmpty())
			applyIntegrazioneAndSave(accreditamento.getProvider(), Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.PROVIDER));

		if(!Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.SEDE_LEGALE).isEmpty())
			applyIntegrazioneAndSave(accreditamento.getProvider().getSedeLegale(), Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.SEDE_LEGALE));

		if(!Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.SEDE_OPERATIVA).isEmpty())
			applyIntegrazioneAndSave(accreditamento.getProvider().getSedeOperativa(), Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.SEDE_OPERATIVA));

		if(!Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.LEGALE_RAPPRESENTANTE).isEmpty())
			applyIntegrazioneAndSave(accreditamento.getProvider().getPersonaByRuolo(Ruolo.LEGALE_RAPPRESENTANTE), Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.LEGALE_RAPPRESENTANTE));

		if(!Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE).isEmpty())
			applyIntegrazioneAndSave(accreditamento.getProvider().getPersonaByRuolo(Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE), Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE));

		if(!Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.DATI_ACCREDITAMENTO).isEmpty())
			applyIntegrazioneAndSave(accreditamento.getDatiAccreditamento(), Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.DATI_ACCREDITAMENTO));

		if(!Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.RESPONSABILE_SEGRETERIA).isEmpty()){
			applyIntegrazioneAndSave(accreditamento.getProvider().getPersonaByRuolo(Ruolo.RESPONSABILE_SEGRETERIA), Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.RESPONSABILE_SEGRETERIA));
		}

		if(!Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO).isEmpty()){
			applyIntegrazioneAndSave(accreditamento.getProvider().getPersonaByRuolo(Ruolo.RESPONSABILE_AMMINISTRATIVO), Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO));
		}

		if(!Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO).isEmpty()){
			applyIntegrazioneAndSave(accreditamento.getProvider().getPersonaByRuolo(Ruolo.RESPONSABILE_SISTEMA_INFORMATICO), Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO));
		}

		if(!Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.RESPONSABILE_QUALITA).isEmpty()){
			applyIntegrazioneAndSave(accreditamento.getProvider().getPersonaByRuolo(Ruolo.RESPONSABILE_QUALITA), Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.RESPONSABILE_QUALITA));
		}

		//fieldIntegrazione per i multi-istanza
		Set<Persona> componentiComitato = accreditamento.getProvider().getComponentiComitatoScientifico();
		componentiComitato.forEach( p -> {
			if(!Utils.getSubset(fieldIntegrazioni, p.getId(), SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO).isEmpty()){
				try {
					applyIntegrazioneAndSave(p, Utils.getSubset(fieldIntegrazioni, p.getId(), SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});

	}

	//oggetto + nomeCampo + valore
	//esempio: (persona, "anagrafica.nome", "Pippo")
	private void applyIntegrazioneAndSave(Object dst, Set<FieldIntegrazioneAccreditamento> fieldIntegrazioni) throws Exception{
		for(FieldIntegrazioneAccreditamento field :  fieldIntegrazioni)
			setFieldAndSave(dst, field.getIdField().getNameRef(), field.getNewValue());
	}

	public void applyIntegrazione(Object dst, Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList) throws Exception{
		for(FieldIntegrazioneAccreditamento field : fieldIntegrazioneList){
			setField(dst,field.getIdField().getNameRef(),field.getNewValue());
		}
	}
}
