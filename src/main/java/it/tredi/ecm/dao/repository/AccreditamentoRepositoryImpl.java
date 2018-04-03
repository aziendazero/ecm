package it.tredi.ecm.dao.repository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.tredi.ecm.dao.entity.Accreditamento;

public class AccreditamentoRepositoryImpl implements AccreditamentoRepositoryCustom {
	@PersistenceContext
	EntityManager entityManager;

	public Set<Accreditamento> findAllAccreditamentiInScadenzaNeiProssimiGiorni(int giorni) {
		return new HashSet<Accreditamento>(getQueryAccreditamentiInScadenzaNeiProssimiGiorni(giorni, false).getResultList());
	}

	@Override
	public long countAllAccreditamentiInScadenzaNeiProssimiGiorni(int giorni) {
		//Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM DOG WHERE ID =:id");
//		query.setParameter("id", 1);
//		int count = ((BigInteger) query.getSingleResult()).intValue();
		//long count = (long) query.getSingleResult();
		return (long) getQueryAccreditamentiInScadenzaNeiProssimiGiorni(giorni, true).getSingleResult();
	}
	
	private Query getQueryAccreditamentiInScadenzaNeiProssimiGiorni(int giorni, boolean forCount) {
		LocalDate inizio = LocalDate.now();
		LocalDate fine = inizio.plusDays(giorni);
		String queryStr = "";
		if(forCount) {
			queryStr = "SELECT count(*)";
		} else {
			queryStr = "SELECT a";
		}
		queryStr += " FROM Accreditamento a WHERE a.dataScadenza BETWEEN :inizio AND :fine OR (durataProcedimento IS NOT NULL AND (massimaDurataProcedimento - durataProcedimento) <= :giorni)";
		
		Query query;
		if(forCount) {
			query = entityManager.createQuery(queryStr);
		} else {
			query = entityManager.createQuery(queryStr, Accreditamento.class);
		}
		query.setParameter("inizio", inizio);
		query.setParameter("fine", fine);
		query.setParameter("giorni", giorni);
		
		return query;
	}

}
