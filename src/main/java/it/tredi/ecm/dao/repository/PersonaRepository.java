package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.enumlist.Ruolo;

//public interface PersonaRepository extends CrudRepository<Persona, Long> {
public interface PersonaRepository extends JpaRepository<Persona, Long> {
	public Persona findOneByRuoloAndProviderId(Ruolo ruolo, Long providerId);
	public Persona findOneByRuoloAndAnagraficaIdAndProviderId(Ruolo ruolo, Long anagraficaId, Long providerId);
	public Persona findOneByRuoloAndCoordinatoreComitatoScientificoAndProviderIdAndDirtyFalse(Ruolo ruolo, boolean b, Long providerId);
	public Set<Persona> findAllByRuoloAndProviderId(Ruolo ruolo, Long providerId);

	public int countByRuoloAndProviderId(Ruolo ruolo, Long providerId);
	public int countByRuoloAndProviderIdAndProfessioneSanitaria(Ruolo ruolo, Long providerId, boolean sanitaria);
	@Query("select count(distinct p.professione) from Persona p where p.ruolo = :ruolo and p.provider.id = :providerId")
	public int countDistinctProfessioneByRuoloAndProviderId(@Param("ruolo")Ruolo ruolo, @Param("providerId")Long providerId);
	@Query("select count(distinct p.professione) from Persona p where p.ruolo = :ruolo and p.provider.id = :providerId and p.professione in :professioniSelezionate")
	public int countDistinctProfessioneByRuoloAndProviderIdInProfessioniSelezionate(@Param("ruolo")Ruolo ruolo, @Param("providerId") Long providerId,@Param("professioniSelezionate") Set<Professione> professioniSelezionate);
	@Query("select distinct p.professione from Persona p where p.ruolo = :ruolo and p.provider.id = :providerId")
	public Set<Professione> findDistinctProfessioneByRuoloAndProviderId(@Param("ruolo")Ruolo ruolo, @Param("providerId")Long providerId);

	//TODO @EntityGraph al momento non funziona...sarebbe utile investigare per capire perchè
	@EntityGraph(value = "graph.persona.files", type = EntityGraphType.FETCH)
	@Override
	public Persona findOne(Long id);
}
