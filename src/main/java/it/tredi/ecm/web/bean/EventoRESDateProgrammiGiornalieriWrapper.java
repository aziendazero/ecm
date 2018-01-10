package it.tredi.ecm.web.bean;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.ProgrammaGiornalieroRES;
import it.tredi.ecm.dao.entity.SedeEvento;

public class EventoRESDateProgrammiGiornalieriWrapper {
	private EventoRES eventoRES;
	private SedeEvento sedeUltimoAggiornamento;
	private Long index = 0L;
	private Long dataInizioKey = null;
	private Long dataFineKey = null;
	private List<LocalDate> pgLocalDate = new ArrayList<LocalDate>();

	public EventoRESDateProgrammiGiornalieriWrapper(EventoRES eventoRES) {
		this.eventoRES = eventoRES;
		this.sedeUltimoAggiornamento = new SedeEvento();
		if(eventoRES.getSedeEvento() != null) {
			this.sedeUltimoAggiornamento.copiaDati(eventoRES.getSedeEvento());
		}
		boolean hasDataInizio = false;
		boolean hasDataFine = false;
		//tengo i programmi giornalieri solo se esistono fra le date INIZIO FINE o INTERMEDIE
		for(ProgrammaGiornalieroRES pg : this.eventoRES.getProgramma()) {
			//Quelle senza giorno non vengono considerate, in realta' non dovrebbero neppure venire salvate su db
			if(pg.getGiorno() != null) {
				//Aggiungo solo se non gia' aggiunta
//				if(pgLocalDate.contains(pg.getGiorno()))
//					continue;
				if(this.eventoRES.getDataInizio() != null && pg.getGiorno().compareTo(this.eventoRES.getDataInizio()) == 0 && hasDataInizio == false) {
					//DATAINIZIO
					index++;
					programmiGiornalieriMap.add(new AbstractMap.SimpleEntry<Long, EventoRESProgrammaGiornalieroWrapper>(index, new EventoRESProgrammaGiornalieroWrapper(EventoRESTipoDataProgrammaGiornalieroEnum.INIZIO, pg)));
					dataInizioKey = index;
					pgLocalDate.add(pg.getGiorno());
					hasDataInizio = true;
				} else if(this.eventoRES.getDataFine() != null && pg.getGiorno().compareTo(this.eventoRES.getDataFine()) == 0 && hasDataFine == false && !this.eventoRES.getDataFine().equals(this.eventoRES.getDataInizio())) {
					//DATAFINE
					index++;
					programmiGiornalieriMap.add(new AbstractMap.SimpleEntry<Long, EventoRESProgrammaGiornalieroWrapper>(index, new EventoRESProgrammaGiornalieroWrapper(EventoRESTipoDataProgrammaGiornalieroEnum.FINE, pg)));
					dataFineKey = index;
					pgLocalDate.add(pg.getGiorno());
					hasDataFine = true;
				} else {
					//POSSIBILE DATA INTERMEDIA
					if(eventoRES.getDateIntermedie().contains(pg.getGiorno())) {
						index++;
						programmiGiornalieriMap.add(new AbstractMap.SimpleEntry<Long, EventoRESProgrammaGiornalieroWrapper>(index, new EventoRESProgrammaGiornalieroWrapper(EventoRESTipoDataProgrammaGiornalieroEnum.INTERMEDIA, pg)));
						pgLocalDate.add(pg.getGiorno());
					}
				}
			}
		}
		if(dataInizioKey == null) {
			index++;
			ProgrammaGiornalieroRES progInizio = new ProgrammaGiornalieroRES();
			//Copio i dati della sede
			progInizio.setSede(new SedeEvento());
			progInizio.getSede().copiaDati(this.eventoRES.getSedeEvento());
			programmiGiornalieriMap.add(new AbstractMap.SimpleEntry<Long, EventoRESProgrammaGiornalieroWrapper>(index, new EventoRESProgrammaGiornalieroWrapper(EventoRESTipoDataProgrammaGiornalieroEnum.INIZIO, progInizio)));
			
			dataInizioKey = index;
		}
		if(dataFineKey == null) {
			index++;
			//E' possibile che la data fine corrisponda con la data inizio e quindi non sia stato creato
			ProgrammaGiornalieroRES progFine = new ProgrammaGiornalieroRES();
			progFine.setGiorno(eventoRES.getDataFine());
			//Copio i dati della sede
			progFine.setSede(new SedeEvento());
			progFine.getSede().copiaDati(this.eventoRES.getSedeEvento());
			programmiGiornalieriMap.add(new AbstractMap.SimpleEntry<Long, EventoRESProgrammaGiornalieroWrapper>(index, new EventoRESProgrammaGiornalieroWrapper(EventoRESTipoDataProgrammaGiornalieroEnum.FINE, progFine)));
			dataFineKey = index;
		}
		//Se tutto e' ok non dovrebbe servire ma in caso di dati sporchi ci potrebbero essere delle date intermedie non presenti fra i programmi giornalieri
		if(eventoRES.getDateIntermedie() != null && eventoRES.getDateIntermedie().size() > programmiGiornalieriMap.size() - 2) {
			//ciclo fra tutte le date intermedie e cerco quali non sono presenti nei programmi giornalieri
			for(LocalDate dataInt : eventoRES.getDateIntermedie()) {
				if(dataInt != null && !pgLocalDate.contains(dataInt)) {
					addProgrammaGiornalieroIntermedio(dataInt);
				}
			}
		}

		if(programmiGiornalieriMap.size()  == 2) {
			//Ci sono solo datainizio e data fine aggiungo una data intermedia null
			addProgrammaGiornalieroIntermedio(null);
		}

		refreshSortedProgrammiGiornalieriMap();
	}

	private void refreshSortedProgrammiGiornalieriMap() {
		sortedProgrammiGiornalieriMap = new LinkedHashMap<Long, EventoRESProgrammaGiornalieroWrapper>();
		for(Map.Entry<Long, EventoRESProgrammaGiornalieroWrapper> kv : programmiGiornalieriMap) {
			sortedProgrammiGiornalieriMap.put(kv.getKey(), kv.getValue());
		}
	}

	//LinkedHashMap mantiene l'ordine dell'inserimento
	private Map<Long, EventoRESProgrammaGiornalieroWrapper> sortedProgrammiGiornalieriMap = new LinkedHashMap<Long, EventoRESProgrammaGiornalieroWrapper>();

	//Mantengo internamente un Set ordinato rispetto a tipoData e giorno
	private SortedSet<Map.Entry<Long, EventoRESProgrammaGiornalieroWrapper>> programmiGiornalieriMap = new TreeSet<Map.Entry<Long, EventoRESProgrammaGiornalieroWrapper>>(
            new Comparator<Map.Entry<Long, EventoRESProgrammaGiornalieroWrapper>>() {
                @Override
                public int compare(Map.Entry<Long, EventoRESProgrammaGiornalieroWrapper> e1, Map.Entry<Long, EventoRESProgrammaGiornalieroWrapper> e2) {
                	//Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second
                    //return e1.getValue().compareTo(e2.getValue());
                	if(e1.getValue().getTipoData() == EventoRESTipoDataProgrammaGiornalieroEnum.INIZIO) {
                		if(e2.getValue().getTipoData() != EventoRESTipoDataProgrammaGiornalieroEnum.INIZIO)
                			return -1;
                	} else if(e1.getValue().getTipoData() == EventoRESTipoDataProgrammaGiornalieroEnum.FINE) {
                		if(e2.getValue().getTipoData() != EventoRESTipoDataProgrammaGiornalieroEnum.FINE)
                			return 1;
                	} else {
                		//e1 == INTERMEDIA
                		if(e2.getValue().getTipoData() == EventoRESTipoDataProgrammaGiornalieroEnum.INIZIO) {
                			return 1;
                		} else if(e2.getValue().getTipoData() == EventoRESTipoDataProgrammaGiornalieroEnum.FINE) {
                			return -1;
                		} else {
                			// entrambe INTERMEDIA
                			if(e1.getValue().getProgramma().getGiorno() == null) {
                				if(e2.getValue().getProgramma().getGiorno() != null) {
                					return 1;
                				}
                			} else {
                				//e1 != null
                				if(e2.getValue().getProgramma().getGiorno() == null) {
                    				//e1 != null AND e2 == null
                					return -1;
                				} else {
                					//Entrambe != null
                					if(e1.getValue().getProgramma().getGiorno().compareTo(e2.getValue().getProgramma().getGiorno()) == 0)
                						return e1.getKey().compareTo(e2.getKey());
                					else
                						return e1.getValue().getProgramma().getGiorno().compareTo(e2.getValue().getProgramma().getGiorno());
                				}
                			}
                		}
                	}
                	return e1.getKey().compareTo(e2.getKey());
                }
            });

	public Map<Long, EventoRESProgrammaGiornalieroWrapper> getSortedProgrammiGiornalieriMap() {
		return sortedProgrammiGiornalieriMap;
	}

	public void setSortedProgrammiGiornalieriMap(Map<Long, EventoRESProgrammaGiornalieroWrapper> sortedProgrammiGiornalieriMap) {
		this.sortedProgrammiGiornalieriMap = sortedProgrammiGiornalieriMap;
	}

	public void addProgrammaGiornalieroIntermedio(LocalDate dataInt) {
		ProgrammaGiornalieroRES pgAdd = new ProgrammaGiornalieroRES();
		pgAdd.setGiorno(dataInt);
		pgAdd.setSede(new SedeEvento());
		pgAdd.getSede().copiaDati(this.eventoRES.getSedeEvento());
		index++;
		programmiGiornalieriMap.add(new AbstractMap.SimpleEntry<Long, EventoRESProgrammaGiornalieroWrapper>(index, new EventoRESProgrammaGiornalieroWrapper(EventoRESTipoDataProgrammaGiornalieroEnum.INTERMEDIA, pgAdd)));
		pgLocalDate.add(dataInt);
		refreshSortedProgrammiGiornalieriMap();
	}

	public void removeProgrammaGiornalieroIntermedio(Long key) {
		EventoRESProgrammaGiornalieroWrapper erpgw = sortedProgrammiGiornalieriMap.get(key);
		if(erpgw != null) {
			programmiGiornalieriMap.remove(new AbstractMap.SimpleEntry<Long, EventoRESProgrammaGiornalieroWrapper>(key, erpgw));
			pgLocalDate.remove(erpgw.getProgramma().getGiorno());
		}
		refreshSortedProgrammiGiornalieriMap();
	}

	//Da chiamare per aggiornare i dati di data inizio e fine che sull'interfaccia non sono mappati ai programmi
	public void aggiornaDati() {
		sortedProgrammiGiornalieriMap.get(dataInizioKey).getProgramma().setGiorno(eventoRES.getDataInizio());
		sortedProgrammiGiornalieriMap.get(dataFineKey).getProgramma().setGiorno(eventoRES.getDataFine());

		if(!SedeEvento.compare(this.sedeUltimoAggiornamento, this.eventoRES.getSedeEvento())) {
			//la sede è cambiata rispetto all'originale
			for(EventoRESProgrammaGiornalieroWrapper eventoProgrGiorWrap : sortedProgrammiGiornalieriMap.values()){
				//se la sede del programma e' uguale alla sede originale la aggiorno con la sede dell'evento
				if(SedeEvento.compare(eventoProgrGiorWrap.getProgramma().getSede(), this.sedeUltimoAggiornamento)) {
					if(eventoProgrGiorWrap.getProgramma().getSede() == null)
						eventoProgrGiorWrap.getProgramma().setSede(new SedeEvento());
					eventoProgrGiorWrap.getProgramma().getSede().copiaDati(this.eventoRES.getSedeEvento());
				}
			}
			//
			this.sedeUltimoAggiornamento.copiaDati(this.eventoRES.getSedeEvento());
		}


		/*
		//Controllo se un dato è stato inserito nella sede
		if(!SedeEvento.isEmpty(this.eventoRES.getSedeEvento())) {
			for(EventoRESProgrammaGiornalieroWrapper eventoProgrGiorWrap : sortedProgrammiGiornalieriMap.values()){
				if(eventoProgrGiorWrap.getProgramma().getSede() == null)
					eventoProgrGiorWrap.getProgramma().setSede(this.eventoRES.getSedeEvento());
				else {
					if(eventoProgrGiorWrap.getProgramma().getSede().isEmpty()) {
						eventoProgrGiorWrap.getProgramma().setSede(this.eventoRES.getSedeEvento());
					}
				}
			}
		}
		*/

		refreshSortedProgrammiGiornalieriMap();
	}

	//Metodo che riporta i dati dal wrapper all'evento
	public void updateEventoRES() {
		//Aggiorno nel caso le date inizio e fine siano state modificate
		aggiornaDati();
		List<LocalDate> dateIntermedie = new ArrayList<LocalDate>();
		Set<Long> oldIdProgrammaAncoraPresenti = new HashSet<Long>();
		//Potrebbe essere gia' stata eseguita questa operazione (updateEventoRES, per esempio validazione non riuscita)
		//quindi in eventoRES.getProgramma() potrei avere gia' dei programmi nuovi inseriti li elimino e rieseguo
//		Iterator<ProgrammaGiornalieroRES> i = eventoRES.getProgramma().iterator();
//		while (i.hasNext()) {
//			ProgrammaGiornalieroRES prg = i.next();
//			if(prg.isNew()) {
//				i.remove();
//			}
//		}
//		for(EventoRESProgrammaGiornalieroWrapper evpgw : this.getSortedProgrammiGiornalieriMap().values()) {
//			if(evpgw.getProgramma().getGiorno() == null) {
//				//quelli non nuovi vanno eliminati se hanno la data null
//				if(!evpgw.getProgramma().isNew()) {
//					eventoRES.getProgramma().remove(evpgw.getProgramma());
//				}
//			} else {
//				//giorno != null
//				if(evpgw.getTipoData() == EventoRESTipoDataProgrammaGiornalieroEnum.INTERMEDIA)
//					dateIntermedie.add(evpgw.getProgramma().getGiorno());
//				if(evpgw.getProgramma().isNew()) {
//					if(evpgw.getTipoData() == EventoRESTipoDataProgrammaGiornalieroEnum.FINE && evpgw.getProgramma().getGiorno().equals(eventoRES.getDataInizio()))
//						continue;
//					//aggiungo il programma all'evento, se non gia' aggiunto, quelli non new ci sono gia' e sono aggiornati
//					if(!eventoRES.getProgramma().contains(evpgw.getProgramma()))
//						eventoRES.getProgramma().add(evpgw.getProgramma());
//				} else {
//					oldIdProgrammaAncoraPresenti.add(evpgw.getProgramma().getId());
//				}
//			}
//		}
		//### sovrascriviamo ###
		eventoRES.getProgramma().clear();
		for(EventoRESProgrammaGiornalieroWrapper evpgw : this.getSortedProgrammiGiornalieriMap().values()) {
			if(evpgw.getProgramma().getGiorno() != null) {
				if(evpgw.getTipoData() == EventoRESTipoDataProgrammaGiornalieroEnum.FINE
						&& evpgw.getProgramma().getGiorno().equals(this.eventoRES.getDataInizio())) {
							continue;
				}
				eventoRES.getProgramma().add(evpgw.getProgramma());
				if(evpgw.getTipoData() == EventoRESTipoDataProgrammaGiornalieroEnum.INTERMEDIA)
					dateIntermedie.add(evpgw.getProgramma().getGiorno());
			}
		}
		eventoRES.setDateIntermedie(dateIntermedie);
		//per i programmi restano da sistemare eventuali programmi presenti prima ma che ora sono stati cancellati
		//eventuali doppioni, per giorno
		//eventuali programmi con giorno non piu' impostato
//		i = eventoRES.getProgramma().iterator();
//		Set<LocalDate> dateDistinct = new HashSet<LocalDate>();
//		while (i.hasNext()) {
//			ProgrammaGiornalieroRES prg = i.next();
//			if(!prg.isNew() && (!oldIdProgrammaAncoraPresenti.contains(prg.getId()) || prg.getGiorno() == null)) {
//				i.remove();
//			} else {
//				if(dateDistinct.contains(prg.getGiorno())) {
//					i.remove();
//				} else {
//					dateDistinct.add(prg.getGiorno());
//				}
//			}
//		}

	}

	/*
	Comparator<Long, EventoRESProgrammaGiornalieroWrapper> EventoRESProgrammaGiornalieroWrapperComparator = new Comparator() {
	}<Long<Long, EventoRESProgrammaGiornalieroWrapper>() {
        @Override public int compare(String s1, String s2) {
            return s1.substring(1, 2).compareTo(s2.substring(1, 2));
        }
    };
    */
	/*
	static <K,V extends Comparable<? super V>>
	SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
	    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
	        new Comparator<Map.Entry<K,V>>() {
	            @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	                int res = e1.getValue().compareTo(e2.getValue());
	                return res != 0 ? res : 1;
	            }
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}
	*/
}
