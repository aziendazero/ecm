package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.Set;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.RelazioneAnnuale;

public interface RelazioneAnnualeService {
	public RelazioneAnnuale getRelazioneAnnuale(Long relazioneAnnualeId);

	public Set<RelazioneAnnuale> getAllRelazioneAnnuale();

	public Set<RelazioneAnnuale> getAllRelazioneAnnualeByProviderId(Long providerId);

	public RelazioneAnnuale getRelazioneAnnualeForProviderIdAndAnnoRiferimento(Long providerId,
			Integer annoRiferimento);

	public Set<Provider> getAllProviderNotRelazioneAnnualeRegistrata(Integer annoRiferimento);

	public Set<Provider> getAllProviderNotRelazioneAnnualeRegistrataAllaScadenza();

	public int countProviderNotRelazioneAnnualeRegistrataAllaScadenza();

	public RelazioneAnnuale createRelazioneAnnuale(Long providerId);

	public void elaboraRelazioneAnnualeAndSave(RelazioneAnnuale relazioneAnnuale, File relazioneFinale,
			boolean asBozza);

	public boolean isLastRelazioneAnnualeInserita(Long providerId);

	public void aggiornaDataDiFineModificaPerRelazioneAnnualeForProviderIdAndAnnoRiferimento(Long providerId,
			Integer annoRiferimento, LocalDate dataFineModifca);
}
