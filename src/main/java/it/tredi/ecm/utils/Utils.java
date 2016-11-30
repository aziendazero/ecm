package it.tredi.ecm.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import it.tredi.ecm.dao.entity.Field;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.service.bean.CurrentUser;

public class Utils {
	private final static Logger LOGGER = Logger.getLogger(Utils.class);

	/**
	 * Recupero dell'utente loggato
	 * @return utente loggato oppure <code>NULL</code> se l'utente non ha fatto login
	 */
	public static CurrentUser getAuthenticatedUser(){
		CurrentUser currentUser = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if(authentication == null){
			LOGGER.info("AnonymousAuthentication found");
			return null;
		}else if(authentication instanceof AnonymousAuthenticationToken){
			LOGGER.info("AnonymousAuthentication found");
			return null;
		}

		currentUser = (CurrentUser) authentication.getPrincipal();
		return currentUser;
	}

	public static File convertFromMultiPart(MultipartFile multiPartFile){
		try{
			if(multiPartFile != null){
				File file = new File();
				file.setNomeFile(multiPartFile.getOriginalFilename());
				file.setData(multiPartFile.getBytes());
				file.setDataCreazione(LocalDate.now());
				return file;
			}
		}catch (IOException ioException){
			LOGGER.error("Errore durante lettura contenuto del file caricato oppure nessun file passato",ioException);
		}

		return null;
	}

	public static String getLogMessage(String message){
		CurrentUser currentUser = getAuthenticatedUser();
		return "[" + ((currentUser != null) ? currentUser.getAccount().getUsername() : "AnonymousAuthentication") + "] - " + message;
	}

	public static void logDebugErrorFields(org.slf4j.Logger LOGGER, Errors errors){
		if(LOGGER.isDebugEnabled())
			errors.getFieldErrors().forEach( fieldError -> LOGGER.debug(fieldError.getField() + ": '" + fieldError.getRejectedValue() + "' [" + fieldError.getCode()+ "]"));
	}

	/**
	 * Mi restituisce la lista di ENUM a partire da una lista di FieldEditabile o FieldIntegrazione o qualsiasi {@code <T extends Field>}
	 *
	 * @param src lista di {@code <T extends Field>}
	 * @param type SubSetFieldEnum
	 * */
	public static <T extends Field> Set<IdFieldEnum> getSubsetOfIdFieldEnum(Set<T> src, SubSetFieldEnum type){
		Set<IdFieldEnum> dst = new HashSet<IdFieldEnum>();

		src.forEach(f -> {
			if(f.getIdField().getSubSetField() == type)
				dst.add(f.getIdField());
		});

		return dst;
	}

	/**
	 * Mi restituisce la lista di ENUM a partire da una lista di FieldEditabile o FieldIntegrazione o qualsiasi {@code <T extends Field>}
	 *
	 * @param src lista di {@code <T extends Field>}
	 * @param objectReference id del multi-istanza di riferimento
	 * @param type SubSetFieldEnum
	 * */
	public static <T extends Field> Set<IdFieldEnum> getSubsetOfIdFieldEnum(Set<T> src, Long objectReference, SubSetFieldEnum type){
		Set<IdFieldEnum> dst = new HashSet<IdFieldEnum>();

		src.forEach(f -> {
			if(f.getIdField().getSubSetField() == type && f.getObjectReference() == objectReference)
				dst.add(f.getIdField());
		});

		return dst;
	}

	/**
	 * Mi restituisce la sottolista di {@code <T extends Field>} a partire da una lista di {@code <T extends Field>} fornendo il SubSetFieldEnum
	 *
	 * @param src lista di {@code <T extends Field>}
	 * @param type SubSetFieldEnum
	 * */
	public static <T extends Field> Set<T> getSubset(Set<T> src, SubSetFieldEnum type){
		Set<T> dst = new HashSet<T>();

		src.forEach(f -> {
			if(f.getIdField().getSubSetField() == type)
				dst.add(f);
		});

		return dst;
	}

	/**
	 * Mi restituisce la sottolista di {@code <T extends Field>} a partire da una lista di {@code <T extends Field>} fornendo il SubSetFieldEnum
	 *
	 * @param src lista di {@code <T extends Field>}
	 * @param objectReference id del multi-istanza di riferimento
	 * @param type SubSetFieldEnum
	 * */
	public static <T extends Field> Set<T> getSubset(Set<T> src, Long objectReference, SubSetFieldEnum type){
		Set<T> dst = new HashSet<T>();

		src.forEach(f -> {
			if(f.getIdField().getSubSetField() == type && f.getObjectReference() == objectReference)
				dst. add(f);
		});

		return dst;
	}

	/*
	 * Controllo se un determinato IdFieldEnum è presente nella lista di record
	 * */
	public static <T extends Field> T getField(Set<T> src, IdFieldEnum idEnum){
		for(T f : src){
			if(f.getIdField() == idEnum)
				return f;
		}
		return null;
	}

	/*
	 * Controllo se un determinato IdFieldEnum è presente nella lista di record
	 * */
	public static <T extends Field> T getField(Set<T> src, Long objectReference, IdFieldEnum idEnum){
		for(T f : src){
			if(f.getIdField() == idEnum && f.getObjectReference() == objectReference)
				return f;
		}
		return null;
	}

	public static SubSetFieldEnum getSubsetFromRuolo(Ruolo ruolo){
		switch (ruolo){
			case LEGALE_RAPPRESENTANTE: return SubSetFieldEnum.LEGALE_RAPPRESENTANTE;
			case DELEGATO_LEGALE_RAPPRESENTANTE: return SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE;
			case RESPONSABILE_SEGRETERIA: return SubSetFieldEnum.RESPONSABILE_SEGRETERIA;
			case RESPONSABILE_AMMINISTRATIVO: return SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO;
			case RESPONSABILE_SISTEMA_INFORMATICO: return SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO;
			case RESPONSABILE_QUALITA: return SubSetFieldEnum.RESPONSABILE_QUALITA;
			case COMPONENTE_COMITATO_SCIENTIFICO: return SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO;
			default: return null;
		}
	}

	public static IdFieldEnum getFullFromRuolo(Ruolo ruolo){
		switch (ruolo){
			case LEGALE_RAPPRESENTANTE: return IdFieldEnum.LEGALE_RAPPRESENTANTE__FULL;
			case DELEGATO_LEGALE_RAPPRESENTANTE: return IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__FULL;
			case RESPONSABILE_SEGRETERIA: return IdFieldEnum.RESPONSABILE_SEGRETERIA__FULL;
			case RESPONSABILE_AMMINISTRATIVO: return IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__FULL;
			case RESPONSABILE_SISTEMA_INFORMATICO: return IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__FULL;
			case RESPONSABILE_QUALITA: return IdFieldEnum.RESPONSABILE_QUALITA__FULL;
			case COMPONENTE_COMITATO_SCIENTIFICO: return IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__FULL;
			default: return null;
		}
	}

	public static Object copy(Object fromBean) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLEncoder out = new XMLEncoder(bos);
        out.writeObject(fromBean);
        out.close();
        ByteArrayInputStream bis = new
        ByteArrayInputStream(bos.toByteArray());
        XMLDecoder in = new XMLDecoder(bis);
        Object toBean = in.readObject();
        in.close();
        return toBean;
    }

	public static float getRoundedFloatValue(float value, int precision){
		BigDecimal bg = new BigDecimal(value).setScale(precision, RoundingMode.HALF_UP);
		return bg.floatValue();
	}

	public static float getRoundedFloatValue(BigDecimal value, int precision){
		BigDecimal bg = value.setScale(precision, RoundingMode.HALF_UP);
		return bg.floatValue();
	}

	public static Double getRoundedDoubleValue(Double value, int precision){
		BigDecimal bg = new BigDecimal(value).setScale(precision, RoundingMode.HALF_UP);
		return bg.doubleValue();
	}

	public static String formatOrario(float durata){
		int ore = (int) durata;
		int minuti = (int) ((durata*60) % 60);
		return ore + ":" + minuti;
	}

	public static String QUERY_AND(String query, String criteria){
		if(query.contains("WHERE"))
			return query+= " AND " + criteria;
		else
			return query+= " WHERE " + criteria;
	}

	//nobel per il workaround 2016 (in pratica fa una get di tutto | solo il primo livello della entity passata)
	//utile per i detach per evitare i lazy init ex
	public static <T> void touchFirstLevelOfEverything(T obj) throws Exception{
		BeanInfo info = Introspector.getBeanInfo(obj.getClass());
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			Method method = pd.getReadMethod();
			if(method != null) {
				Object innerEntity = method.invoke(obj);
				if(innerEntity != null)
					innerEntity.toString();
			}
		}
	}
}
