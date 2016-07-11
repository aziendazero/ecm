# ECM - (Sviluppo sistema informatico per la gestione dei crediti informativi ECM)

## ISTRUZIONI DI AVVIO
* Alcune funzionalità del software prevedono l'invio automatico di email di notifica. Si consiglia di tenere sempre attivo un dummy smtp server (ad esempio [fakeSMTP](https://nilhcem.github.io/FakeSMTP/)) impostato per intercettare tutte le email inviate dall'applicazione. I parametri di configurazione sono settati nel file mail.properties.

* Il software si collega ad un database postgres i cui parametri sono configurabili nel file application-dev.properties

* 'dev' è anche il profile che si consiglia di utilizzare per la compilazione e l'avvio del progetto. 

* Per lo sviluppo del team di engineering sono state implementate 2 pagine di demo.

* All'avvio l'applicazione scrive sul database tutti i dati necessari per la corretta visualizzazione delle pagine di demo, creando anche un'utenza apposita per l'accesso al sistema.

* Le credenziali sono:
    * **username:** *engineering-dev*
    * **password:** *admin*

* Il progetto attualmente è in fase di sviluppo, quindi sorgenti ed esecuzione possono non essere completi. Si è concordato con il team di engineering di implementare le integrazioni nelle 2 pagine demo, documentare il tutto, commentare il codice, in modo tale che il team di 3D Informatica possa velocemente replicare le funzionalità in tutti i punti necessari.

## ISTRUZIONI DI SVILUPPO
* Per lo sviluppo lato java è stato utilizzato un plugin [Lombok](https://projectlombok.org/), che crea in automatico i metodi getter/setter attraverso annotazioni. Quindi per poter compilare correttamente il progetto è necessario installare il plugin nel proprio IDE.

* Per lo sviluppo del sistema le tecnologie utilizzate sono le seguenti:
    * framework Spring (MVC - JPA - Boot)
    * Hibernate per la persistenza dei dati
    * Thymeleafe con Bootstrap per la presentazione

In particolar modo per le integrazioni del team Engineering (relative alle pagine demo) sono già presenti:
* it.tredi.ecm.web.EngineeringController.java (controller Java)
* it.tredi.ecm.web.bean.EngineeringWrapper.java (wrapper di supporto per gestire la view)
* it.tredi.ecm.web.validator.EngineeringValidator.java (classe che implementa la validazione dei dati per il salvataggio)
* resources/templates/engineering sono presenti le due pagine di demo