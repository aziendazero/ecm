package it.tredi.ecm.web.bean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.ProgrammaGiornalieroRES;

public class EventoRESDateProgrammiGiornalieriWrapper {
	private EventoRES eventoRES;
	private Long index = 0L;
	private Long dataInizioKey = null;
	private Long dataFineKey = null;
	private Set<LocalDate> pgLocalDate = new HashSet<LocalDate>();
	
	public EventoRESDateProgrammiGiornalieriWrapper(EventoRES eventoRES) {
		this.eventoRES = eventoRES;
		//yengo i programmi giornalieri solo se esistono fra le date INIZIO FINE o INTERMEDIE
		for(ProgrammaGiornalieroRES pg : this.eventoRES.getProgramma()) {
			//Quelle senza giorno non vengono considerate, in realta' non dovrebbero neppure venire salvate su db
			if(pg.getGiorno() != null) {
				//Aggiungo solo se non gia' aggiunta
				if(pgLocalDate.contains(pg.getGiorno()))
					continue;
				if(this.eventoRES.getDataInizio() != null && pg.getGiorno().compareTo(this.eventoRES.getDataInizio()) == 0) {
					//DATAINIZIO
					index++;
					programmiGiornalieriMap.add(new AbstractMap.SimpleEntry<Long, EventoRESProgrammaGiornalieroWrapper>(index, new EventoRESProgrammaGiornalieroWrapper(EventoRESTipoDataProgrammaGiornalieroEnum.INIZIO, pg)));
					dataInizioKey = index;
					pgLocalDate.add(pg.getGiorno());
				} else if(this.eventoRES.getDataFine() != null && pg.getGiorno().compareTo(this.eventoRES.getDataFine()) == 0) {
					//DATAFINE
					index++;
					programmiGiornalieriMap.add(new AbstractMap.SimpleEntry<Long, EventoRESProgrammaGiornalieroWrapper>(index, new EventoRESProgrammaGiornalieroWrapper(EventoRESTipoDataProgrammaGiornalieroEnum.FINE, pg)));
					dataFineKey = index;
					pgLocalDate.add(pg.getGiorno());
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
			programmiGiornalieriMap.add(new AbstractMap.SimpleEntry<Long, EventoRESProgrammaGiornalieroWrapper>(index, new EventoRESProgrammaGiornalieroWrapper(EventoRESTipoDataProgrammaGiornalieroEnum.INIZIO, new ProgrammaGiornalieroRES())));
			dataInizioKey = index;
		}
		if(dataFineKey == null) {
			index++;
			programmiGiornalieriMap.add(new AbstractMap.SimpleEntry<Long, EventoRESProgrammaGiornalieroWrapper>(index, new EventoRESProgrammaGiornalieroWrapper(EventoRESTipoDataProgrammaGiornalieroEnum.FINE, new ProgrammaGiornalieroRES())));
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
		refreshSortedProgrammiGiornalieriMap();
	}
	
	//Metodo che riporta i dati dal wrapper all'evento
	public void updateEventoRES() {
		//Aggiorno nel caso le date inizio e fine siano state modificate
		aggiornaDati();
		Set<LocalDate> dateIntermedie = new HashSet<LocalDate>();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		Set<Long> oldIdProgrammaAncoraPresenti = new HashSet<Long>();
		for(EventoRESProgrammaGiornalieroWrapper evpgw : this.getSortedProgrammiGiornalieriMap().values()) {
			if(evpgw.getProgramma().getGiorno() == null) {
				//quelli non nuovi vanno elliminati se hanno la data null
				if(!evpgw.getProgramma().isNew()) {
					eventoRES.getProgramma().remove(evpgw.getProgramma());
				}
			} else {
				if(evpgw.getTipoData() == EventoRESTipoDataProgrammaGiornalieroEnum.INTERMEDIA)
					dateIntermedie.add(evpgw.getProgramma().getGiorno());
				if(evpgw.getProgramma().isNew()) {
					//aggiungo il programma all'evento, quelli non new ci sono gia' e sono aggiornati
					eventoRES.getProgramma().add(evpgw.getProgramma());
				} else {
					oldIdProgrammaAncoraPresenti.add(evpgw.getProgramma().getId());
				}
			}
		}
		eventoRES.setDateIntermedie(dateIntermedie);
		//per i programmi restano da sistemare eventuali programmi presenti prima ma che ora sono stati cancellati
		Iterator<ProgrammaGiornalieroRES> i = eventoRES.getProgramma().iterator();
		while (i.hasNext()) {
			ProgrammaGiornalieroRES prg = i.next();
			if(!prg.isNew() && !oldIdProgrammaAncoraPresenti.contains(prg.getId())) {
				i.remove();
			}
		}		
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
