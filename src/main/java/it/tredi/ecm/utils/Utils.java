package it.tredi.ecm.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.Source;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import it.tredi.ecm.dao.entity.Field;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.pdf.PdfRiepilogoPartecipantiInfo;
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
			LOGGER.debug("Authentication null found");
			return null;
		}else if(authentication instanceof AnonymousAuthenticationToken){
			LOGGER.debug("AnonymousAuthentication found");
			return null;
		}else if(authentication instanceof CasAuthenticationToken){
			LOGGER.debug("CasAuthentication found");
		}else if(authentication instanceof UsernamePasswordAuthenticationToken){
			LOGGER.debug("UsernamePasswordAuthenticationToken found");
		}

		currentUser = (CurrentUser) authentication.getPrincipal();
		return currentUser;
	}

	public static boolean isUserCasAuthenticated(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null)
			return false;
		if(authentication instanceof CasAuthenticationToken)
			return true;
		return false;
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
	 * Mi restituisce la sottolista di {@code <T extends Field>} a partire da una lista di idFieldEnum
	 *
	 * @param src lista di {@code <T extends Field>}
	 * @param ids lista di idFieldEnum
	 * */
	public static <T extends Field> Set<T> getIntersection(Set<T> src, Set<IdFieldEnum> ids){
		Set<T> dst = new HashSet<T>();

		src.forEach(f -> {
			if(ids.contains(f.getIdField()))
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

	public static int getRoundedHALFDOWNFloatValue(float value){
		BigDecimal bg = new BigDecimal(value).setScale(0, RoundingMode.HALF_DOWN);
		return (int) bg.floatValue();
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
		int hh = (int) durata;
		int mm = (int) (((durata*60) % 60) + 0.5f);
		//bugfix volante, caso in cui durata era 1.99999 (approssimazione di 2h), scriveva 1:60
		if(mm == 60) {
			mm = 0;
			hh++;
		}

		return ((hh < 10) ? ("0" + hh) : hh) + ":" + ((mm < 10) ? ("0" + mm) : mm);
	}

	public static String formatOrarioFromMinutes(long minuti){
		int hh = (int) minuti / 60;
		int mm = (int) ((minuti % 60) + 0.5f);

		return ((hh < 10) ? "0" + hh : hh) + ":" + ((mm < 10) ? "0" + mm : mm);
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

	public static LocalDateTime convertLocalDateToLocalDateTime(LocalDate l){
		if(l != null)
			return Timestamp.valueOf(l.atStartOfDay()).toLocalDateTime();
		return null;
	}

	public static String buildOggetto(FileEnum fileEnum, Provider provider) throws Exception {
		String oggetto = null;
		switch(fileEnum) {
		case FILE_ACCREDITAMENTO_PROVVISORIO_INTEGRAZIONE:
			oggetto = "Richiesta integrazione documentazione ai sensi della l. 241/90 e smi - Accreditamento provvisorio - " + provider.getDenominazioneLegale();
		break;
		case FILE_ACCREDITAMENTO_PROVVISORIO_PREAVVISO_RIGETTO:
			oggetto = "Comunicazione motivi ostativi all’accoglimento della domanda ex art. 10 bis l.241/90 e smi - Accreditamento provvisorio - " + provider.getDenominazioneLegale();
		break;
		case FILE_LETTERA_ACCOMPAGNATORIA_PROVVISORIO_DINIEGO:
		case FILE_ACCREDITAMENTO_PROVVISORIO_DECRETO_DINIEGO:
			oggetto = "Rigetto istanza di accreditamento provvisorio come provider regionale ECM ex DGR nn. 1969 del 02/10/12 e 1236 del 16/07/13 - " + provider.getDenominazioneLegale();
		break;
		case FILE_LETTERA_ACCOMPAGNATORIA_PROVVISORIO_ACCREDITAMENTO:
		case FILE_ACCREDITAMENTO_PROVVISORIO_DECRETO_ACCREDITAMENTO:
			oggetto = "Riconoscimento dell’accreditamento provvisorio come Provider regionale ECM ex DGR nn. 1969 del 02/10/12 e 1236 del 16/07/13 - " + provider.getDenominazioneLegale();
		break;
		case FILE_ACCREDITAMENTO_STANDARD_INTEGRAZIONE:
			oggetto = "Richiesta integrazione documentazione ai sensi della l. 241/90 e smi - Accreditamento standard - " + provider.getDenominazioneLegale();
		break;
		case FILE_ACCREDITAMENTO_STANDARD_PREAVVISO_RIGETTO:
			oggetto = "Comunicazione motivi ostativi all’accoglimento della domanda ex art. 10 bis della l.241/90 e smi - Accreditamento standard - " + provider.getDenominazioneLegale();
		break;
		case FILE_LETTERA_ACCOMPAGNATORIA_STANDARD_DINIEGO:
		case FILE_ACCREDITAMENTO_STANDARD_DECRETO_DINIEGO:
			oggetto = "Rigetto dell’istanza di accreditamento standard come Provider regionale ECM ai sensi della DGR n. 1247 del 28/09/2015 - " + provider.getDenominazioneLegale();
		break;
		case FILE_LETTERA_ACCOMPAGNATORIA_STANDARD_ACCREDITAMENTO:
		case FILE_ACCREDITAMENTO_STANDARD_DECRETO_ACCREDITAMENTO:
			oggetto = "Riconoscimento dell’accreditamento standard come Provider regionale ECM ai sensi della D.G.R n. 1247 del 28/09/2015 - " + provider.getDenominazioneLegale();
		break;
		case FILE_DICHIARAZIONE_LEGALE:
			oggetto = "Domanda di Accreditamento Provvisorio come Provider ECM";
		break;
		case FILE_RICHIESTA_ACCREDITAMENTO_STANDARD:
			oggetto = "Domanda di Accreditamento Standard come Provider ECM";
		break;
		case FILE_DECADENZA:
			oggetto = "Comunicazione di decadenza dell’accreditamento provvisorio del Provider regionale - " + provider.getDenominazioneLegale();
		break;
		}
		if(oggetto == null)
			throw new Exception("Impossibile generare Oggetto del file da protocollare");
		else
			return oggetto;
	}

}
