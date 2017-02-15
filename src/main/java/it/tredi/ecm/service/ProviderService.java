package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.service.bean.ProviderRegistrationWrapper;
import it.tredi.ecm.web.bean.ImpostazioniProviderWrapper;
import it.tredi.ecm.web.bean.RicercaProviderWrapper;

public interface ProviderService {
	public Provider getProvider();
	public Provider getProvider(Long id);
	public Provider getProviderByCodiceFiscale(String codiceFiscale);
	public Provider getProviderByPartitaIva(String partitaIva);
	public Set<Provider> getAll();
	public Set<Provider> getAllNotInserito();
	public Set<Provider> getAllAttivi();
	public void save(Provider provider);

	public ProviderRegistrationWrapper getProviderRegistrationWrapper();
	public void saveProviderRegistrationWrapper(ProviderRegistrationWrapper providerWrapper) throws Exception;

	public Long getProviderIdByAccountId(Long accountId);
	public boolean canInsertPianoFormativo(Long providerId);
	public boolean canInsertEvento(Long providerId);
	public boolean canInsertAccreditamentoStandard(Long providerId);
	public boolean canInsertAccreditamentoProvvisorio(Long providerId);
	public boolean canInsertRelazioneAnnuale(Long providerId);
	public boolean hasAlreadySedeLegaleProvider(Provider provider, Sede sede);

	public void saveFromIntegrazione(Provider provider);

	public void bloccaFunzionalitaForPagamento(Long providerId);
	public void abilitaFunzionalitaAfterPagamento(Long providerId);

	public String getCodiceFiscaleLegaleRappresentantePerVerificaFirmaDigitale(Long providerId);
	public String getCodiceFiscaleDelegatoLegaleRappresentantePerVerificaFirmaDigitale(Long providerId);

	public String getEmailLegaleRappresentante(Long providerId);
	public String getEmailDelegatoLegaleRappresentante(Long providerId);

	public List<Provider> cerca(RicercaProviderWrapper wrapper) throws Exception;

	public void abilitaCanInsertAccreditamentoStandard(Long providerId, LocalDate dataFine) throws Exception;
	public void disabilitaCanInsertAccreditamentoStandard(Long providerId) throws Exception;
	public void updateImpostazioni(Long providerId, ImpostazioniProviderWrapper wrapper) throws Exception;
	public void eseguiUpdateDataPianoFormativo();
	public void eseguiUpdateDataDomandaStandard();
	public void eseguiUpdateDataRelazioneAnnuale();
	public int countAllProviderInadempienti();

}
