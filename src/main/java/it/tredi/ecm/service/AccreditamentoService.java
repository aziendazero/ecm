package it.tredi.ecm.service;

import java.util.Collection;
import java.util.Set;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.exception.AccreditamentoNotFoundException;
import it.tredi.ecm.service.bean.CurrentUser;

public interface AccreditamentoService{
	public Accreditamento getNewAccreditamentoForCurrentProvider(AccreditamentoTipoEnum tipoDomanda) throws Exception;
	public Accreditamento getNewAccreditamentoForProvider(Long providerId, AccreditamentoTipoEnum tipoDomanda) throws Exception;

	public Accreditamento getAccreditamento(Long id);
	public Set<Accreditamento> getAllAccreditamentiForProvider(Long providerId);
	public Set<Accreditamento> getAllAccreditamentiForProvider(Long providerId,AccreditamentoTipoEnum tipoDomanda);
	public Set<Accreditamento> getAccreditamentiAvviatiForProvider(Long providerId, AccreditamentoTipoEnum tipoTomanda);
	public Accreditamento getAccreditamentoAttivoForProvider(Long providerId) throws AccreditamentoNotFoundException;
	public AccreditamentoStatoEnum getStatoAccreditamento(Long accreditamentoId);


	public void save(Accreditamento accreditamento);

	public boolean canProviderCreateAccreditamento(Long providerId,AccreditamentoTipoEnum tipoTomanda);

	public void inviaDomandaAccreditamento(Long accreditamentoId);
	public void inserisciPianoFormativo(Long accreditamentoId);

	public DatiAccreditamento getDatiAccreditamentoForAccreditamento(Long accreditamentoId) throws Exception;
	public Long getProviderIdForAccreditamento(Long accreditamentoId);

	//Vaschetta segreteria
	public Set<Accreditamento> getAllAccreditamentiInviati();
	public int countAllAccreditamentiByStato(AccreditamentoStatoEnum stato);
	public Set<Accreditamento> getAllAccreditamentiByStato(AccreditamentoStatoEnum stato);
	public int countAllAccreditamentiByStatoAndProviderId(AccreditamentoStatoEnum stato, Long providerId);
	public Set<Accreditamento> getAllAccreditamentiByStatoAndProviderId(AccreditamentoStatoEnum stato, Long providerId);

	//Vaschetta generica
	public Set<Accreditamento> getAllAccreditamentiByStatoForAccountId(AccreditamentoStatoEnum stato, Long id);
	public int countAllAccreditamentiByStatoForAccountId(AccreditamentoStatoEnum stato, Long id);

	//Controlli valutazione
	public boolean canUserPrendiInCarica(Long accreditamentoId, CurrentUser currentUser);
	public boolean canUserValutaDomanda(Long accreditamentoId, CurrentUser currentUser);
	public boolean canUserValutaDomandaShow(Long id, CurrentUser authenticatedUser);
	public boolean canUserValutaDomandaShowRiepilogo(Long accreditamentoId, CurrentUser currentUser);

}
