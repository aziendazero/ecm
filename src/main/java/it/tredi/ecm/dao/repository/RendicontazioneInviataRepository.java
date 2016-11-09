package it.tredi.ecm.dao.repository;

import java.util.Set;
import org.springframework.data.repository.CrudRepository;
import it.tredi.ecm.dao.entity.RendicontazioneInviata;
import it.tredi.ecm.dao.enumlist.RendicontazioneInviataStatoEnum;

public interface RendicontazioneInviataRepository extends CrudRepository<RendicontazioneInviata, Long> {
	public Set<RendicontazioneInviata> findAllByStato(RendicontazioneInviataStatoEnum stato);
}
