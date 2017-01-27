package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.web.bean.RichiestaIntegrazioneWrapper;

public interface IntegrazioneService {

	/**
	 * Effettua il detach dell'entity in modo tale da non rendere effetive le modifche fatte all'oggetto
	 * Il detach viene applicato a tutte le entity presenti all'interno attraverso l'introspezione e la reflection
	 * */
	public <T> void detach(T obj) throws Exception;

	/**
	 * Verifica sel'entity è in sessione hibernate o è detached
	 * Il controllo viene applicato a tutte le entity presenti all'interno attraverso l'introspezione e la reflection
	 * */
	public <T> void isManaged(T obj) throws Exception;

	/**
	 * Effettua l'attach dell'entity in modo tale da poter salvare l'entity che era stata precedentemente detachata
	 * Il detach viene applicato a tutte le entity presenti all'interno attraverso l'introspezione e la reflection
	 * */
	public <T> void attach(T obj) throws Exception;

	public void applyIntegrazioneAccreditamentoAndSave(Long accreditamentoId,
			Set<FieldIntegrazioneAccreditamento> fieldIntegrazioni) throws Exception;

	/**
	 * Applica le integrazioni all'oggetto passato
	 * la lista di integrazioni deve contenere SOLO i FIELD appartenenti all'oggetto
	 * le integrazioni di tipo ELIMINAZIONE vengono ignorate
	 * */
	public void applyIntegrazioneObject(Object dst, Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList)
			throws Exception;

	/**
	 * Recupero del valore specificato da <param>fieldName</param> dall'oggetto <param>dst</param>
	 * */
	public Object getField(Object dst, String fieldName) throws Exception;

	/**
	 * Setting del valore <param>fieldValue</param> specificato da <param>fieldName</param> nell'oggetto <param>dst</param>
	 * */
	public void setField(Object dst, String fieldName, Object fieldValue) throws Exception;

	public RichiestaIntegrazioneWrapper prepareRichiestaIntegrazioneWrapper(Long accreditamentoId, SubSetFieldEnum subset, Long objRef);
	public void saveEnableField(RichiestaIntegrazioneWrapper wrapper);

	/*
	 * Controlla se le integrazioni contengono effettivamente nuovi valori o sono conferma dei vecchi valori
	 * Aggiorna i fieldIntegrazioneAccreditamento settando il flag
	 * */
	public void checkIfFieldIntegrazioniConfirmedForAccreditamento(Long accreditamentoId, Set<FieldIntegrazioneAccreditamento> fieldIntegrazioni);

}