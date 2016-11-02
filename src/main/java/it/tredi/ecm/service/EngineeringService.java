package it.tredi.ecm.service;

import it.tredi.ecm.dao.entity.Evento;

public interface EngineeringService {
	
	/**
	 * Effettua la richiesta di pagamento su MyPay
	 * @param idEvento
	 * @param backURL
	 * @return l'URL di MyPay verso cui reindirizzare l'utente per il pagamento.
	 * @throws Exception
	 */
	public String pagaEvento(Long idEvento, String backURL) throws Exception;
	
	/**
	 * Effettua la richiesta di pagamento su MyPay per la quota di un provider 
	 * @param idEvento
	 * @param annoRiferimento
	 * @param primoAnno
	 * @param backURL
	 * @return l'URL di MyPay verso cui reindirizzare l'utente per il pagamento.
	 * @throws Exception
	 */
	public String pagaQuotaProvider(Long idEvento, Integer annoRiferimento, boolean primoAnno, String backURL) throws Exception; 
	
	/**
	 * Recupera i pagamenti in sospeso e per ognuno di questi verifica su MyPay se e' disponibile l'esito.
	 * Questo metodo andrebbe chiamato con uno scheduler a intervalli regolari.
	 * @throws Exception
	 */
	public void esitoPagamenti() throws Exception;
	
	/**
	 * Metodo di utilità per resettare la situazione per poter ripetere i test.
	 * @param idProvider
	 * @throws Exception
	 */
	public void azzeraPagamenti(Long idProvider) throws Exception;

	
	public void saveFileFirmato(String xml) throws Exception;
}
