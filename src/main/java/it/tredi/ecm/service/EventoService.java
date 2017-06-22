package it.tredi.ecm.service;

import java.util.List;
import java.util.Set;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Sponsor;
import it.tredi.ecm.dao.enumlist.EventoStatoEnum;
import it.tredi.ecm.exception.AccreditamentoNotFoundException;
import it.tredi.ecm.web.bean.EventoWrapper;
import it.tredi.ecm.web.bean.ModificaOrarioAttivitaWrapper;
import it.tredi.ecm.web.bean.RicercaEventoWrapper;
import it.tredi.ecm.web.bean.ScadenzeEventoWrapper;

public interface EventoService {
	public Evento getEvento(Long id);
	public void save(Evento evento) throws Exception;
	public void delete(Long id);

	public void validaRendiconto(Long id, File rendiconto) throws Exception;
	public List<Evento> getAllEventi();
	public Set<Evento> getAllEventiForProviderId(Long providerId);
	public boolean canCreateEvento(Account account);
	public boolean canRieditEvento(Account account);
	public void inviaRendicontoACogeaps(Long id) throws Exception;
	public void statoElaborazioneCogeaps(Long id) throws Exception;
	public Evento handleRipetibiliAndAllegati(EventoWrapper eventoWrapper) throws Exception;
	public EventoWrapper prepareRipetibiliAndAllegati(EventoWrapper eventoWrapper);


	public void calculateAutoCompilingData(EventoWrapper eventoWrapper) throws Exception;
//	public float calcoloDurataEvento(EventoWrapper eventoWrapper);
//	public float calcoloCreditiEvento(EventoWrapper eventoWrapper);

	//TODO Questo metodo puo' diventare private
	public void retrieveProgrammaAndAddJoin(EventoWrapper eventoWrapper);
	public void aggiornaDati(EventoWrapper eventoWrapper);
	public Set<Evento> getAllEventiRieditabiliForProviderId(Long providerId) throws AccreditamentoNotFoundException;

	//TODO chiedere 1 mese di ferie almeno (joe19 mode on)
	public Evento prepareRiedizioneEvento(Evento evento) throws Exception;
	public int getLastEdizioneEventoByPrefix(String prefix);
	public Evento getEventoForRiedizione(Long eventoId);
	public Set<Evento> getEventiByProviderIdAndAnnoRiferimento(Long providerId, Integer annoRiferimento);
	public Set<Evento> getEventiRendicontatiByProviderIdAndAnnoRiferimento(Long providerId, Integer annoRiferimento);
	public Set<Evento> getEventiForRelazioneAnnualeByProviderIdAndAnnoRiferimento(Long providerId, Integer annoRiferimento);

	// detacha e clona l'Evento Padre da rieditare
//	public <T> void detachEvento(T obj) throws Exception;
	public Evento detachEvento(Evento evento) throws Exception;

	public Set<Evento> getEventiForProviderIdInScadenzaDiPagamento(Long providerId);
	public int countEventiForProviderIdInScadenzaDiPagamento(Long providerId);
	public Set<Evento> getEventiForProviderIdPagamentoScaduti(Long providerId);
	public int countEventiForProviderIdPagamentoScaduti(Long providerId);

	public List<Evento> cerca(RicercaEventoWrapper wrapper);

	public boolean isEditSemiBloccato(Evento evento);
	public boolean isEventoIniziato(Evento evento);
//	public boolean hasDataInizioRestrictions(Evento evento);
	public Sponsor getSponsorById(Long sponsorId);
	public void saveAndCheckContrattoSponsorEvento(File sponsorFile, Sponsor sponsor, Long eventoId, String mode) throws Exception;
	public Set<Evento> getEventiByProviderIdAndStato(Long id, EventoStatoEnum stato);
	public Integer countAllEventiByProviderIdAndStato(Long id, EventoStatoEnum stato);

	public Set<Evento> getEventiCreditiNonConfermati();
	public Integer countAllEventiCreditiNonConfermati();
	public void updateScadenze(Long eventoId, ScadenzeEventoWrapper wrapper) throws Exception;
	public Evento getEventoByPrefix(String idEventoLink);
	public Evento getEventoByPrefixAndEdizione(String prefix, int edizione);
	public Evento getEventoByCodiceIdentificativo(String codiceIdentificativo);

	public Integer countAllEventiAlimentazionePrimaInfanzia();
	public Set<Evento> getEventiAlimentazionePrimaInfanzia();
	public Integer countAllEventiMedicineNonConvenzionali();
	public Set<Evento> getEventiMedicineNonConvenzionali();
//	public boolean checkIfRESAndWorkshopOrCorsoAggiornamentoAndInterettivoSelected(Evento evento);
	public boolean checkIfFSCAndTrainingAndTutorPartecipanteRatioAlert(Evento evento);

	//MEV riedizioni
	public void updateOrariAttivita(ModificaOrarioAttivitaWrapper jsonObj, EventoWrapper eventoWrapper);
	public boolean existRiedizioniOfEventoId(Long eventoId);
	public Set<Evento> getRiedizioniOfEventoId(Long eventoId);
}
