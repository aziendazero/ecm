package it.tredi.ecm.dao.repository;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;

public interface AccreditamentoRepository extends CrudRepository<Accreditamento, Long> {
	public Set<Accreditamento> findByProviderId(Long providerId);
	public Set<Accreditamento> findAllByProviderIdAndTipoDomanda(Long providerId, AccreditamentoTipoEnum tipoDomanda);
	public Set<Accreditamento> findByProviderIdAndTipoDomandaAndDataScadenzaAfter(Long providerId, AccreditamentoTipoEnum tipoDomanda, LocalDate data);
	public Accreditamento findOneByProviderIdAndStatoAndDataFineAccreditamentoAfter(Long providerId, AccreditamentoStatoEnum stato, LocalDate data);
	@Query("SELECT a.stato FROM Accreditamento a WHERE a.id = :accreditamentoId")
	public AccreditamentoStatoEnum getStatoByAccreditamentoId(@Param("accreditamentoId") Long accreditamentoId);
	@Query("SELECT a.datiAccreditamento FROM Accreditamento a WHERE a.id = :accreditamentoId")
	public DatiAccreditamento getDatiAccreditamentoForAccreditamento(@Param("accreditamentoId") Long accreditamentoId);

	@Query("SELECT a.provider.id FROM Accreditamento a WHERE a.id = :accreditamentoId")
	public Long getProviderIdById(@Param("accreditamentoId") Long accreditamentoId);

	//QUERY VASCHETTE
		//query e count domande accreditmento a seconda dello stato
		public Set<Accreditamento> findAllByStato(AccreditamentoStatoEnum stato);
		public int countAllByStato(AccreditamentoStatoEnum stato);

		//query e count domande accreditamento a seconda dello stato e del tipo
		public Set<Accreditamento> findAllByStatoAndTipoDomanda(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipo);
		public int countAllByStatoAndTipoDomanda(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipo);

		//query e count domande accreditamento a seconda dello stato, non prese in carica
		@Query("SELECT a FROM Accreditamento a WHERE a.stato = :stato AND a.id NOT IN (SELECT v.accreditamento.id FROM Valutazione v)")
		public Set<Accreditamento> findAllByStatoNotTaken(@Param("stato") AccreditamentoStatoEnum stato);
		@Query("SELECT COUNT (a) FROM Accreditamento a WHERE a.stato = :stato AND a.id NOT IN (SELECT v.accreditamento.id FROM Valutazione v)")
		public int countAllByStatoNotTaken(@Param("stato") AccreditamentoStatoEnum stato);

		//query e count domande accreditamento a seconda dello stato e del tipo, non prese in carica
		@Query("SELECT a FROM Accreditamento a WHERE a.stato = :stato AND a.tipoDomanda = :tipo AND a.id NOT IN (SELECT v.accreditamento.id FROM Valutazione v)")
		public Set<Accreditamento> findAllByStatoAndTipoDomandaNotTaken(@Param("stato") AccreditamentoStatoEnum stato, @Param("tipo") AccreditamentoTipoEnum tipo);
		@Query("SELECT COUNT (a) FROM Accreditamento a WHERE a.stato = :stato AND a.tipoDomanda = :tipo AND a.id NOT IN (SELECT v.accreditamento.id FROM Valutazione v)")
		public int countAllByStatoAndTipoDomandaNotTaken(@Param("stato") AccreditamentoStatoEnum stato, @Param("tipo") AccreditamentoTipoEnum tipo);

		//query e count domande accreditamento a seconda dello stato assegnate al valutatore id
		@Query("SELECT v.accreditamento FROM Valutazione v WHERE v.account.id = :id AND v.accreditamento.stato = :stato")
		public Set<Accreditamento> findAllByStatoInValutazioneAssignedToAccountId(@Param("stato") AccreditamentoStatoEnum stato, @Param("id") Long id);
		@Query("SELECT COUNT(v.accreditamento) FROM Valutazione v WHERE v.account.id = :id AND v.accreditamento.stato = :stato")
		public int countAllByStatoInValutazioneAssignedToAccountId(@Param("stato") AccreditamentoStatoEnum stato, @Param("id") Long id);

		//query e count domande accreditamento a seconda dello stato e del tipo assegnate al valutatore id
		@Query("SELECT v.accreditamento FROM Valutazione v WHERE v.account.id = :id AND v.accreditamento.stato = :stato AND v.accreditamento.tipoDomanda = :tipo")
		public Set<Accreditamento> findAllByStatoAndTipoDomandaInValutazioneAssignedToAccountId(@Param("stato") AccreditamentoStatoEnum stato, @Param("tipo") AccreditamentoTipoEnum tipo, @Param("id") Long id);
		@Query("SELECT COUNT (v.accreditamento) FROM Valutazione v WHERE v.account.id = :id AND v.accreditamento.stato = :stato AND v.accreditamento.tipoDomanda = :tipo")
		public int countAllByStatoAndTipoDomandaInValutazioneAssignedToAccountId(@Param("stato") AccreditamentoStatoEnum stato, @Param("tipo") AccreditamentoTipoEnum tipo, @Param("id") Long id);

		//query e count domande accreditamento a seconda dello stato e del tipo assegnate al provider id
		public Set<Accreditamento> findAllByStatoAndTipoDomandaAndProviderId(@Param("stato") AccreditamentoStatoEnum stato, @Param("tipo") AccreditamentoTipoEnum tipo, @Param("id") Long id);
		public int countAllByStatoAndTipoDomandaAndProviderId(@Param("stato") AccreditamentoStatoEnum stato, @Param("tipo") AccreditamentoTipoEnum tipo, @Param("id") Long id);

		//query e count domande accreditamento a seconda dello stato assegnate al provider id
		public Set<Accreditamento> findAllByStatoAndProviderId(@Param("stato") AccreditamentoStatoEnum stato, @Param("id") Long id);
		public int countAllByStatoAndProviderId(@Param("stato") AccreditamentoStatoEnum stato, @Param("id") Long id);

		//query e count domande accreditamento in scadenza, prende in input la data odierna e la data successiva ai giorni considerati di scadenza
		//controlla se la data di scadenza sta in questo intervallo
		@Query("SELECT a FROM Accreditamento a WHERE a.dataScadenza BETWEEN :oggi AND :dateScadenza")
		public Set<Accreditamento> findAllByDataScadenzaProssima(@Param("oggi") LocalDate oggi, @Param("dateScadenza") LocalDate dateScadenza);
		@Query("SELECT COUNT (a) FROM Accreditamento a WHERE a.dataScadenza BETWEEN :oggi AND :dateScadenza")
		public int countAllByDataScadenzaProssima(@Param("oggi") LocalDate oggi, @Param("dateScadenza") LocalDate dateScadenza);
	//
}
