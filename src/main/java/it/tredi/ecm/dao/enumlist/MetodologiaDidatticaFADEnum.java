package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum MetodologiaDidatticaFADEnum {

	//OBV1_1
	_1(1,"Lettura e studio di testi scritti digitali (dispense, slide interattive, ipertesti)"),
	_2(2,"Lettura e studio di testi cartacei (libri autorevoli, riviste scientifiche; sono escluse stampe di presentazioni e fotocopie)"),
	_3(3,"Visione e studio di lezioni magistrali videoregistrate"),
	_4(4,"Ascolto e studio di lezioni in modalità audio"),
	_5(5,"Fruizione e studio di tavole rotonde o grand rounds video o audio registrati"),
	_6(6,"Studio di sussidi didattici multimediali integrati con risorse audiovisive (lezioni interattive con commenti audio/video, risorse audiovisive illustrative di concetti)"),
	_7(7,"Test di comprensione con correzione automatizzata"),
	_8(8,"Esercitazioni formative interattive per lo studio di concetti e di procedure"),
	_9(9,"Fruizione di tutoriali multimediali interattivi"),
	_10(10,"Dimostrazioni tecniche in videoconferenze o trasmesse in broadcasting"),
	//OBV2_1
	_11(11,"Uso di simulatori"),
	_12(12,"Esercitazioni con esecuzione diretta di procedure tecniche relative a strumentazione abilitata all’utilizzo in modalità telematica (apparecchiature per la telemedicina)"),
	//OBV3_1
	_13(13,"Role playing con uso di simulatori"),
	_14(14,"Role playing basati su giochi interattivi"),
	//OBV4_1
	_15(15,"Esercitazione formativa con produzione di un elaborato individuale (analisi di un caso clinico, saggio breve, documentazione di sintesi di ricerca di fonti autorevoli, ecc.)"),
	_16(16,"Esercitazione formativa interattiva per l’analisi/risoluzione di problemi Simulatori (percorsi diagnostici o decisionali)"),

	//OBV1_2
	_17(17,"Lettura e studio di testi scritti digitali (dispense, slide interattive, ipertesti)"),
	_18(18,"Lettura e studio di testi cartacei (libri autorevoli, riviste scientifiche; sono escluse stampe di presentazioni e fotocopie)"),
	_19(19,"Visione e studio di lezioni magistrali videoregistrate"),
	_20(20,"Ascolto e studio di lezioni in modalità audio"),
	_21(21,"Fruizione e studio di tavole rotonde o grand rounds video o audio registrati"),
	_22(22,"Studio di sussidi didattici multimediali integrati con risorse audiovisive (lezioni interattive con commenti audio/video, risorse audiovisive illustrative di concetti)"),
	_23(23,"Test di comprensione con correzione automatizzata"),
	_24(24,"Esercitazioni formative interattive per lo studio di concetti e di procedure"),
	_25(25,"Lezioni frontali in modalità broadcasting (trasmissione in diretta via internet, via tv, via satellite ecc.)"),
	_26(26,"Lezioni frontali con dibattito moderato dal docente in videoconferenza o in aula virtuale sincrona"),
	_27(27,"Discussione teorica strutturata e moderata dal docente o da un esperto disciplinare con uso di strumenti per la comunicazione asincrona (forum, e-mail, ecc.)"),
	_28(28,"Seminari online: Partecipazione a sessioni di tavole rotonde on line o grand rounds online in videoconferenza o in aula virtuale sincrona"),
	_29(29,"Seminari interattivi in aula virtuale sincrona integrati con materiale audiovisivo, questionari, white board interattiva, chat, ecc."),
	_30(30,"Fruizione di tutoriali multimediali interattivi"),
	_31(31,"Dimostrazioni tecniche in videoconferenze o trasmesse in broadcasting"),
	//OBV2_2
	_32(32,"Uso di simulatori"),
	_33(33,"Esercitazioni con esecuzione diretta di procedure tecniche relative a strumentazione abilitata all’utilizzo in modalità telematica (apparecchiature per la telemedicina)."),
	//OBV3_2
	_34(34,"Role playing con uso di simulatori"),
	_35(35,"Role playing basati su giochi interattivi"),
	_36(36,"Role playing in aula virtuale sincrona o videoconferenza"),
	_37(37,"Attività di role playing online in ambiente di collaborazione asincrona"),
	_38(38,"Lavoro a piccoli gruppi su problemi, indagini e casi didattici con produzione di un elaborato collaborativo in ambiente sincrono"),
	_39(39,"Lavori a piccoli gruppi su problemi, indagini e casi didattici con produzione di un elaborato collaborativo in ambiente asincrono"),
	_40(40,"Presentazione e discussione di problemi o di casi didattici in grande gruppo, in videoconferenza o in aula virtuale sincrona"),
	_41(41,"Presentazione e discussione di problemi o di casi didattici in grande gruppo, moderata da esperto disciplinare in modalità asincrona"),
	//OBV4_2
	_42(42,"Esercitazione formativa con produzione di un elaborato individuale (analisi di un caso clinico, saggio breve, documentazione di sintesi di ricerca di fonti autorevoli, ecc.)"),
	_43(43,"Esercitazione formativa interattiva per l’analisi/risoluzione di problemi"),
	_44(44,"Simulatori (percorsi diagnostici o decisionali)"),
	_45(45,"Presentazione e discussione di problemi o di casi didattici in grande gruppo, in videoconferenza o in aula virtuale sincrona"),
	_46(46,"Presentazione e discussione di problemi o di casi didattici in grande gruppo, moderata da esperto disciplinare in modalità asincrona"),
	_47(47,"Seminari interattivi online integrati con materiale audiovisivo, questionari, in aula virtuale sincrona, white board interattiva,ecc."),
	_48(48,"Lavoro a piccoli gruppi su problemi, indagini e casi didattici con produzione di un elaborato collaborativo sincrono o asincrono");

	private int id;
	private String nome;

	private MetodologiaDidatticaFADEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
	
	
	
}
