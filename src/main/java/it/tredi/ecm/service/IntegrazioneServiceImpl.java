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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.collection.internal.PersistentSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.BaseEntity;
import it.tredi.ecm.dao.entity.FieldEditabileAccreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.RichiestaIntegrazioneWrapper;

@Service
public class IntegrazioneServiceImpl implements IntegrazioneService {

	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private PersonaService personaService;
	@Autowired private FieldEditabileAccreditamentoService fieldEditabileAccreditamentoService;

	@Autowired private ApplicationContext appContext;
	@PersistenceContext EntityManager entityManager;
	@Autowired private ObjectMapper jacksonObjectMapper;

	private static final Logger LOGGER = LoggerFactory.getLogger(IntegrazioneServiceImpl.class);

	/**
	 * Effettua il detach dell'entity in modo tale da non rendere effetive le modifche fatte all'oggetto
	 * Il detach viene applicato a tutte le entity presenti all'interno attraverso l'introspezione e la reflection
	 * */
	@Override
	public <T> void detach(T obj) throws Exception{
		if(obj instanceof BaseEntity){
			LOGGER.debug(Utils.getLogMessage("Detach object " + obj.getClass()));
			entityManager.detach(obj);
		}
		BeanInfo info;
		info = Introspector.getBeanInfo(obj.getClass());
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			Method method = pd.getReadMethod();
			Object innerEntity = method.invoke(obj);
			if(innerEntity instanceof BaseEntity){
				LOGGER.debug(Utils.getLogMessage("Detach object " + innerEntity.getClass() + ": " + ((BaseEntity) innerEntity).getId()));
				entityManager.detach(innerEntity);
			}else if(innerEntity instanceof PersistentSet){
				Class<?> clazz = getTypeByField(obj.getClass(),pd.getName());
				if(clazz != null && BaseEntity.class.isAssignableFrom(clazz)){
					for(Object o : (Set<?>)innerEntity){
						LOGGER.debug(Utils.getLogMessage("Detach object " + o.getClass() + ": " + ((BaseEntity) o).getId()));
						entityManager.detach(o);
					}
				}
			}
		}
	}

	/**
	 * Effettua l'attach dell'entity in modo tale da poter salvare l'entity che era stata precedentemente detachata
	 * Il detach viene applicato a tutte le entity presenti all'interno attraverso l'introspezione e la reflection
	 * */
	@Override
	public <T> void attach(T obj) throws Exception{
		if(obj instanceof BaseEntity){
			LOGGER.debug(Utils.getLogMessage("Attach object " + obj.getClass()));
			entityManager.merge(obj);
		}
	}

	@Override
	public void applyIntegrazioneAccreditamentoAndSave(Long accreditamentoId, Set<FieldIntegrazioneAccreditamento> fieldIntegrazioni) throws Exception{
		applyIntegrazioneAccreditamentoAndSave(accreditamentoService.getAccreditamento(accreditamentoId), fieldIntegrazioni);
	}

	private void applyIntegrazioneAccreditamentoAndSave(Accreditamento accreditamento, Set<FieldIntegrazioneAccreditamento> fieldIntegrazioni) throws Exception{
		if(!Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.PROVIDER).isEmpty())
			applyIntegrazioneAndSave(accreditamento.getProvider(), Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.PROVIDER));

//		if(!Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.SEDE_LEGALE).isEmpty())
//			applyIntegrazioneAndSave(accreditamento.getProvider().getSedeLegale(), Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.SEDE_LEGALE));
//
//		if(!Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.SEDE_OPERATIVA).isEmpty())
//			applyIntegrazioneAndSave(accreditamento.getProvider().getSedeOperativa(), Utils.getSubset(fieldIntegrazioni, SubSetFieldEnum.SEDE_OPERATIVA));

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
		//Set<Persona> componentiComitato = accreditamento.getProvider().getComponentiComitatoScientifico();
		Set<Persona> componentiComitato = personaService.getComponentiComitatoScientificoFromIntegrazione(accreditamento.getProvider().getId());
		componentiComitato.forEach( p -> {
			if(!Utils.getSubset(fieldIntegrazioni, p.getId(), SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO).isEmpty()){
				try {
					applyIntegrazioneAndSave(p, Utils.getSubset(fieldIntegrazioni, p.getId(), SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			if(!Utils.getSubset(fieldIntegrazioni, p.getId(), SubSetFieldEnum.FULL).isEmpty()){
				try {
					applyIntegrazioneAndSave(p, Utils.getSubset(fieldIntegrazioni, p.getId(), SubSetFieldEnum.FULL));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});

	}

	/**
	 * Applica le integrazioni all'oggetto passato
	 * la lista di integrazioni deve contenere SOLO i FIELD appartenenti all'oggetto
	 * le integrazioni di tipo ELIMINAZIONE vengono ignorate
	 * */
	@Override
	public void applyIntegrazioneObject(Object dst, Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList) throws Exception{
		for(FieldIntegrazioneAccreditamento field : fieldIntegrazioneList){
			if(field.getTipoIntegrazioneEnum() != TipoIntegrazioneEnum.ELIMINAZIONE)
				setField(dst,field.getIdField().getNameRef(),field.getNewValue());
		}
	}

	//applyIntegrazione + Salva oggetto su DB
	private void applyIntegrazioneAndSave(Object dst, Set<FieldIntegrazioneAccreditamento> fieldIntegrazioni) throws Exception{
		for(FieldIntegrazioneAccreditamento field :  fieldIntegrazioni){
			if(field.getTipoIntegrazioneEnum() != TipoIntegrazioneEnum.ELIMINAZIONE){
				setFieldAndSave(dst, field.getIdField().getNameRef(), field.getNewValue());
			}else{
				removeEntityFromRepo(dst.getClass(), field.getNewValue());
			}
		}

	}

	private void setFieldAndSave(Object dst, String fieldName, Object fieldValue) throws Exception{
		setField(dst, fieldName, fieldValue);
		saveEntity(dst.getClass(), dst);
	}

	/**
	 * Recupero del valore specificato da <param>fieldName</param> dall'oggetto <param>dst</param>
	 * */
	@Override
	public Object getField(Object dst, String fieldName) throws Exception{

		//se è un file restituisco l'id del file
		if(fieldName.startsWith("files")){
			Method getFiles = getGetterMethodFor(dst.getClass(), "files");
			@SuppressWarnings("unchecked")
			Set<File> files = (Set<File>) getFiles.invoke(dst);
			for(File f : files){
				if(f.getTipo() == FileEnum.valueOf(fieldName.substring(6)))
					return f.getId();
			}
		}

		//se fieldName è composto (campo1.camp2) applico ricorsivamente fino a ricondurmi al caso semplice
		//individuando l'oggetto campo1 su cui leggere campo2
		if(fieldName.contains(".")){
			//campo composto --> mi riconduco al caso semplice
			dst = getObject(dst, fieldName);
			fieldName = fieldName.substring(fieldName.lastIndexOf(".") + 1 , fieldName.length());
		}

		//caso semplice -> dst.getFieldName()
		//se fieldName e' un campo semplice -> restituisco il suo valore
		//se fieldName e' una BaseEntity -> restituisco il suo ID
		//se fieldName e' una Collection distinguo i 2 casi:
		//	caso1: collection di BaseEntity -> restituisco una Collecction<ID>
		//	caso2: collection di valori semplici -> restituisco la Collection
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

	private boolean isFull(String fieldName){
		return IdFieldEnum.isFull(fieldName);
	}


	/**
	 * Setting del valore <param>fieldValue</param> specificato da <param>fieldName</param> nell'oggetto <param>dst</param>
	 * */
	@Override
	public void setField(Object dst, String fieldName, Object fieldValue) throws Exception{
		if(isFull(fieldName)){
			/****	 se è un FULL	****/
			jacksonObjectMapper.readerForUpdating(dst).readValue((String)fieldValue);
		}else if(fieldName.startsWith("files")){
			/****	se è un file sostituisco il file 	****/
			Method getFiles = getGetterMethodFor(dst.getClass(), "files");
			@SuppressWarnings("unchecked")
			Set<File> files = (Set<File>) getFiles.invoke(dst);
			((Persona) dst).addFile((File) getEntityFromRepo(File.class, fieldValue));
		}else {
			/****	se è modifica a campi singoli 	****/
			//se fieldName è composto (campo1.camp2) applico ricorsivamente fino a ricondurmi al caso semplice
			//individuando l'oggetto campo1 su cui leggere campo2
			if(fieldName.contains(".")){
				//campo composto --> mi riconduco al caso semplice
				dst = getObject(dst, fieldName);
				fieldName = fieldName.substring(fieldName.lastIndexOf(".") + 1 , fieldName.length());
			}

			//caso semplice -> dst.setFieldName(fieldValue)
			//se fieldName e' un campo semplice -> assegno fieldValue
			//se fieldName e' una BaseEntity -> fieldValue è l'ID dell'oggetto da caricare e assegnare
			//se fieldName e' una Collection distinguo i 2 casi:
			//	caso1: collection di BaseEntity -> fieldValue contiene la lista di ID degli oggetti da caricare e assegnare
			//	caso2: collection di valori semplici -> assegno la Collection
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
	private Method getGetterMethodFor(Class<?> clazz, String fieldName) throws IntrospectionException{
		BeanInfo info;
		info = Introspector.getBeanInfo(clazz);
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			if(pd.getName().equals(fieldName))
				return pd.getReadMethod();
		}
		return null;
	}

	//recupero il metodo SETTER
	private Method getSetterMethodFor(Class<?> clazz, String fieldName) throws Exception{
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

	//	//salva l'oggetto su DB -> save(Object objectToSave)
	//	private void saveEntity(Class<?> clazz, Object objectToSave) throws Exception{
	//		Object repository = appContext.getBean(clazz.getSimpleName().substring(0,1).toLowerCase() + clazz.getSimpleName().substring(1) + "Repository");
	//		Class<?>[] cArg = new Class[1];
	//		cArg[0] = Object.class;
	//		Method save = repository.getClass().getDeclaredMethod("save", cArg);
	//		save.invoke(repository, objectToSave);
	//	}
	//
	//	//elimina l'oggetto dal DB -> delete(Long id)
	//	private void removeEntityFromRepo(Class<?> clazz, Object fieldValue) throws Exception{
	//		Object repository = appContext.getBean(clazz.getSimpleName().substring(0,1).toLowerCase() + clazz.getSimpleName().substring(1) + "Repository");
	//		Class<?>[] cArg = new Class[1];
	//		cArg[0] = java.io.Serializable.class;
	//		Method delete = repository.getClass().getDeclaredMethod("delete", cArg);
	//		delete.invoke(repository, fieldValue);
	//	}

	//salva l'oggetto su DB -> save(Object objectToSave)
	private void saveEntity(Class<?> clazz, Object objectToSave) throws Exception{
		Object service = appContext.getBean(clazz.getSimpleName().substring(0,1).toLowerCase() + clazz.getSimpleName().substring(1) + "ServiceImpl");
		Class<?>[] cArg = new Class[1];
		cArg[0] = clazz;
		Method save = service.getClass().getDeclaredMethod("saveFromIntegrazione", cArg);
		save.invoke(service, objectToSave);
	}

	//elimina l'oggetto dal DB -> delete(Long id)
	private void removeEntityFromRepo(Class<?> clazz, Object fieldValue) throws Exception{
		Object service = appContext.getBean(clazz.getSimpleName().substring(0,1).toLowerCase() + clazz.getSimpleName().substring(1) + "ServiceImpl");
		Class<?>[] cArg = new Class[1];
		cArg[0] = java.lang.Long.class;
		Method delete = service.getClass().getDeclaredMethod("deleteFromIntegrazione", cArg);
		delete.invoke(service, fieldValue);
	}

	//nel caso in cui il fieldName è a più livelli, significa che si vuole accedere ad un campo di un oggetto interno
	//attraverso la composizione dell'oggetto fino a recuperare l'oggetto reale
	//esempio;
	//angarfica.cognome passando un oggetto di tipo Persona
	//getAnagrafica() -> restituisce un oggetto di tipo it.tredi.ecm.dao.entity.Anagrafica che ha il campo cognome recuperabile con getCognome()
	private Object getObject(Object dst, String fieldName) throws Exception {
		int dot = fieldName.indexOf(".");
		if(dot > 1){
			String level = fieldName.substring(0,dot);
			Method method = getGetterMethodFor(dst.getClass(), level);
			return getObject(method.invoke(dst), fieldName.substring(dot + 1,fieldName.length()));
		}
		return dst;
	}

	private void removeDirty(Object dst) throws Exception{
		Method getter = getGetterMethodFor(dst.getClass(), "dirty");
		if(getter != null){
			boolean dirty = (boolean) getter.invoke(dst);
			if(dirty){
				Method setter = getSetterMethodFor(dst.getClass(), "dirty");
				setter.invoke(dst, false);
			}
		}
	}

	@Override
	public RichiestaIntegrazioneWrapper prepareRichiestaIntegrazioneWrapper(Long accreditamentoId, SubSetFieldEnum subset, Long objRef) {
		RichiestaIntegrazioneWrapper wrapper = new RichiestaIntegrazioneWrapper();

		Set<FieldEditabileAccreditamento> fullLista = null;
		if(objRef == null)
			fullLista = fieldEditabileAccreditamentoService.getAllFieldEditabileForAccreditamento(accreditamentoId);
		else
			fullLista = fieldEditabileAccreditamentoService.getAllFieldEditabileForAccreditamentoAndObject(accreditamentoId, objRef);

		wrapper.setAccreditamentoId(accreditamentoId);
		wrapper.setSubset(subset);
		wrapper.setObjRef(objRef);
		wrapper.setSelected(Utils.getSubsetOfIdFieldEnum(fullLista, subset));

		return wrapper;
	}

	@Override
	public void saveEnableField(RichiestaIntegrazioneWrapper wrapper) {
		Set<IdFieldEnum> listaDaView = wrapper.getSelected();

		Set<FieldEditabileAccreditamento> listaFull = fieldEditabileAccreditamentoService.getFullLista(wrapper.getAccreditamentoId(), wrapper.getObjRef());
		Set<FieldEditabileAccreditamento> listaSubset = Utils.getSubset(listaFull, wrapper.getSubset());

		listaSubset.forEach(f -> {
			if(listaDaView == null || !listaDaView.contains(f.getIdField())){
				fieldEditabileAccreditamentoService.delete(f);
			}
		});

		fieldEditabileAccreditamentoService.insertFieldEditabileForAccreditamento(wrapper.getAccreditamentoId(), wrapper.getObjRef(), wrapper.getSubset(), listaDaView);
	}
}
