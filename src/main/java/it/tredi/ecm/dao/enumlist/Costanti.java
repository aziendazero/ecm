package it.tredi.ecm.dao.enumlist;

import java.util.Arrays;
import java.util.List;

public class Costanti {
	public static final String PROFILO_PROVIDER = "PROVIDER";
	public static final String PROFILO_ADMIN = "ADMIN";

	public static final String SEDE_LEGALE = "SedeLegale";
	public static final String SEDE_OPERATIVA = "SedeOperativa";
	
	public static final String FILE_DELEGA = "Delega";
	public static final String FILE_CV = "CV";
	public static final String FILE_ATTO_NOMINA = "Atto di nomina";
	public static final String FILE_ESTRATTO_BILANCIO_FORMAZIONE = "Estratto del bilancio relativo alla formazione";
	public static final String FILE_BUDGET_PREVISIONALE = "Budget previsionale";
	public static final String FILE_FUNZIONIGRAMMA = "Funzionigramma";
	public static final String FILE_ORGANIGRAMMA = "Organigramma";
	public static final String FILE_ATTO_COSTITUTIVO = "Atto Costitutivo e statuto";
	public static final String FILE_ESPERIENZA_FORMAZIONE = "Esperienza formazione in ambito sanitario";
	public static final String FILE_UTILIZZO = "Utilizzo di sedi, strutture ed attrezzature di altro soggetto";
	public static final String FILE_SISTEMA_INFORMATICO = "Sistema informatico dedicato alla formazione";
	public static final String FILE_PIANO_QUALITA = "Piano di Qualità";
	public static final String FILE_DICHIARAZIONE_LEGALE = "Dichiarazione del Legale Rappresentante attestante la veridicità della documentazione";
	
	public static final String ACCREDITAMENTO_PROVVISORIO = "Provvisorio";
	public static final String ACCREDITAMENTO_STANDARD = "Standard";
	public static final String ACCREDITAMENTO_STATO_BOZZA = "Bozza";
	public static final String ACCREDITAMENTO_STATO_INVIATO = "Inviato";
	
	
	public static final List<Integer> IDS_PROVIDER = Arrays.asList(1,2,5,6,7);
	public static final List<Integer> IDS_SEDE_LEGALE = Arrays.asList(8,9,10,11,12,13,14);
	public static final List<Integer> IDS_SEDE_OPERATIVA = Arrays.asList(15,16,17,18,19,20,21);
	public static final List<Integer> IDS_LEGALE_RAPPRESENTANTE = Arrays.asList(22,23,24,24,25,26,27,28,29);
	public static final List<Integer> IDS_DELEGATO_LEGALE_RAPPRESENTANTE = Arrays.asList(30,31,32,33,34,35,36,37,38);
	public static final List<Integer> IDS_DATI_ACCREDITAMENTO = Arrays.asList(39,40,41,42,43,44,45,46,47,48,49,50);
	public static final List<Integer> IDS_RESPONSABILE_SEGRETERIA = Arrays.asList(51,52,53,54,55,56,57);
	public static final List<Integer> IDS_RESPONSABILE_AMMINISTRATIVO = Arrays.asList(58,59,60,61,62,63,64,65);
	public static final List<Integer> IDS_COMPONENTE_COMITATO_SCIENTIFICO = Arrays.asList(66,67,68,69,70,71,72,73,74,75);
	public static final List<Integer> IDS_RESPONSABILE_SISTEMA_INFORMATICO = Arrays.asList(76,77,78,79,80,81,82,83);
	public static final List<Integer> IDS_RESPONSABILE_QUALITA = Arrays.asList(84,85,86,87,88,89,89,90);
	public static final List<Integer> IDS_ALLEGATI = Arrays.asList(90,91,92,93,94,95);
	
}
