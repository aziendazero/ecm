package it.tredi.ecm.service;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.File;

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
	 * @param pagamentoid
	 * @param backURL
	 * @return l'URL di MyPay verso cui reindirizzare l'utente per il pagamento.
	 * @throws Exception
	 */
	public String  pagaQuotaProvider(Long pagamentoId, String backURL) throws Exception;
	
	/**
	 * Recupera i pagamenti degli eventi in sospeso e per ognuno di questi verifica su MyPay se e' disponibile l'esito.
	 * Questo metodo andrebbe chiamato con uno scheduler a intervalli regolari.
	 * @throws Exception
	 */
	public void esitoPagamentiEventi() throws Exception;
	
	/**
	 * Recupera i pagamenti dei provider in sospeso e per ognuno di questi verifica su MyPay se e' disponibile l'esito.
	 * Questo metodo andrebbe chiamato con uno scheduler a intervalli regolari.
	 * @throws Exception
	 */
	public void esitoPagamentiQuoteAnnuali() throws Exception;
	
	/**
	 * Metodo di utilità per resettare la situazione per poter ripetere i test.
	 * @param idProvider
	 * @throws Exception
	 */
	public void azzeraPagamenti(Long idProvider) throws Exception;

	
	public File saveFileFirmato(String xml) throws Exception;
}
